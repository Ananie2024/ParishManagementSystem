// ==================== INTERFACES ====================
interface SubParish {
    name: string;
    total: number;
}

interface BEC {
    name: string;
    total: number;
}

// ==================== CONTRIBUTION SUMMARY CONTROLLER ====================
class DonationSummaryController {
    // API Base URL
    private readonly API_BASE = 'http://localhost:8080/api';

    // Filter elements
    private yearFilterComboBox: HTMLSelectElement;
    private refreshSummaryButton: HTMLButtonElement;
    private showAllYearsButton: HTMLButtonElement;
    private subParishFilterComboBox: HTMLSelectElement;
    private clearSubParishFilterButton: HTMLButtonElement;

    // Content elements
    private summaryContentVBox: HTMLElement;
    private overallTotalTitleLabel: HTMLElement;
    private overallTotalAmountLabel: HTMLElement;
    private dynamicSummaryVBox: HTMLElement;
    private summaryTitleLabel: HTMLElement;
    private summaryItemsContainer: HTMLElement;

    // Action button
    private closeButton: HTMLButtonElement;

    // Loading indicator
    private loadingIndicator: HTMLElement;

    // Data storage
    private selectedYear: number | null = null;
    private selectedSubParish: string | null = null;
    private availableYears: number[] = [];
    private subParishList: string[] = [];

    constructor() {
        this.initializeElements();
        this.setupEventListeners();
        this.initialize();
    }

    // ==================== INITIALIZATION ====================
    private initializeElements(): void {
        // Filters
        this.yearFilterComboBox = document.getElementById('yearFilterComboBox') as HTMLSelectElement;
        this.refreshSummaryButton = document.getElementById('refreshSummaryButton') as HTMLButtonElement;
        this.showAllYearsButton = document.getElementById('showAllYearsButton') as HTMLButtonElement;
        this.subParishFilterComboBox = document.getElementById('subParishFilterComboBox') as HTMLSelectElement;
        this.clearSubParishFilterButton = document.getElementById('clearSubParishFilterButton') as HTMLButtonElement;

        // Content
        this.summaryContentVBox = document.getElementById('summaryContentVBox') as HTMLElement;
        this.overallTotalTitleLabel = document.getElementById('overallTotalTitleLabel') as HTMLElement;
        this.overallTotalAmountLabel = document.getElementById('overallTotalAmountLabel') as HTMLElement;
        this.dynamicSummaryVBox = document.getElementById('dynamicSummaryVBox') as HTMLElement;
        this.summaryTitleLabel = document.getElementById('summaryTitleLabel') as HTMLElement;
        this.summaryItemsContainer = document.getElementById('summaryItemsContainer') as HTMLElement;

        // Actions
        this.closeButton = document.getElementById('closeButton') as HTMLButtonElement;

        // Loading
        this.loadingIndicator = document.getElementById('loadingIndicator') as HTMLElement;
    }

    private async initialize(): Promise<void> {
        console.log('Donation Summary View initialized');

        try {
            // Load available years
            await this.loadAvailableYears();

            // Set current year as default
            const currentYear = new Date().getFullYear();
            this.yearFilterComboBox.value = currentYear.toString();
            this.selectedYear = currentYear;

            // Load subparishes
            await this.loadSubParishes();

            // Load initial summary
            await this.refreshSummary();

        } catch (error) {
            console.error('Initialization error:', error);
            this.showError('Ikosa mu gutangiza');
        }
    }

    // ==================== EVENT LISTENERS ====================
    private setupEventListeners(): void {
        // Year filter change
        this.yearFilterComboBox.addEventListener('change', () => this.refreshCurrentView());

        // Refresh button
        this.refreshSummaryButton.addEventListener('click', () => {
            console.log('Refreshing summary data');
            this.refreshSummary();
        });

        // Show all years button
        this.showAllYearsButton.addEventListener('click', () => {
            console.log('Showing all years summary');
            this.showAllYearsSummary();
        });

        // SubParish filter change
        this.subParishFilterComboBox.addEventListener('change', () => {
            const selectedSubParish = this.subParishFilterComboBox.value;
            if (selectedSubParish) {
                console.log('Selected sub-parish filter:', selectedSubParish);
                this.showBecSummaryForSubParish(selectedSubParish);
            } else {
                this.showSubParishSummary();
            }
        });

        // Clear subparish filter
        this.clearSubParishFilterButton.addEventListener('click', () => {
            console.log('Clearing sub-parish filter');
            this.clearSubParishFilter();
        });

        // Close button
        this.closeButton.addEventListener('click', () => {
            console.log('Closing summary view');
            this.closeDialog();
        });
    }

    // ==================== DATA LOADING ====================
    private async loadAvailableYears(): Promise<void> {
        try {
            const response = await fetch(`${this.API_BASE}/donations/statistics/available-years`);
            if (!response.ok) throw new Error('Failed to load years');

            this.availableYears = await response.json();

            // Populate year dropdown
            this.yearFilterComboBox.innerHTML = '<option value="">Hitamo umwaka...</option>';
            this.availableYears.forEach(year => {
                const option = document.createElement('option');
                option.value = year.toString();
                option.textContent = year.toString();
                this.yearFilterComboBox.appendChild(option);
            });

        } catch (error) {
            console.error('Error loading years:', error);
        }
    }

    private async loadSubParishes(): Promise<void> {
        try {
            // Get unique subparishes from donations
            const response = await fetch(`${this.API_BASE}/donations/statistics/by-subparish`);
            if (!response.ok) throw new Error('Failed to load subparishes');

            const data = await response.json();
            this.subParishList = Object.keys(data);

            // Populate subparish dropdown
            this.subParishFilterComboBox.innerHTML = '<option value="">Hitamo santarali...</option>';
            this.subParishList.forEach(sp => {
                const option = document.createElement('option');
                option.value = sp;
                option.textContent = sp;
                this.subParishFilterComboBox.appendChild(option);
            });

        } catch (error) {
            console.error('Error loading subparishes:', error);
        }
    }

    // ==================== SUMMARY METHODS ====================
    private async refreshSummary(): Promise<void> {
        const selectedYear = this.yearFilterComboBox.value;
        console.log('Refreshing summary with year filter:', selectedYear || 'All years');

        if (selectedYear) {
            this.selectedYear = parseInt(selectedYear);
            await this.loadSummaryForYear(this.selectedYear);
        } else {
            this.selectedYear = null;
            await this.loadSummaryForYear(null);
        }
    }

    private async showAllYearsSummary(): Promise<void> {
        console.log('Displaying summary for all years');
        this.yearFilterComboBox.value = '';
        this.selectedYear = null;
        await this.loadSummaryForYear(null);
    }

    private async loadSummaryForYear(year: number | null): Promise<void> {
        this.showLoading(true);

        try {
            // Clear subparish filter
            this.subParishFilterComboBox.value = '';

            console.log('Loading summary data for year:', year || 'All years');

            // Update title based on filter
            if (year) {
                this.overallTotalTitleLabel.textContent = `ITURO RYA PARUWASE YOSE UMWAKA WA ${year} NI`;
            } else {
                this.overallTotalTitleLabel.textContent = 'ITURO RYOSE RYA PARUWASE KUGEZA UBU NI';
            }

            // Get overall total
            let overallTotal: number;
            if (year) {
                const response = await fetch(`${this.API_BASE}/donations/statistics/year/${year}/total`);
                if (!response.ok) throw new Error('Failed to load total');
                const data = await response.json();
                overallTotal = parseFloat(data.total);
            } else {
                const response = await fetch(`${this.API_BASE}/donations/statistics/total`);
                if (!response.ok) throw new Error('Failed to load total');
                const data = await response.json();
                overallTotal = parseFloat(data.total);
            }

            console.log('Calculated overall total:', this.formatCurrency(overallTotal));
            this.overallTotalAmountLabel.textContent = this.formatCurrency(overallTotal);

            // Show subparish summary
            await this.showSubParishSummary();

        } catch (error) {
            console.error('Error loading summary:', error);
            this.showError('Ikosa mu gusoma amaturo');
        } finally {
            this.showLoading(false);
        }
    }

    private async showSubParishSummary(): Promise<void> {
        try {
            const year = this.selectedYear;
            console.log('Displaying sub-parish summary for:', year || 'all years');

            // Clear previous content
            this.summaryItemsContainer.innerHTML = '';

            // Update title with year information
            let titleText = 'IGITERANYO CY\'ITURO RYA SANTARALI:';
            if (year) {
                titleText += ` (${year})`;
            } else {
                titleText += ' (IMYAKA YOSE)';
            }
            this.summaryTitleLabel.textContent = titleText;

            // Get totals by SubParish
            const url = year
                ? `${this.API_BASE}/donations/statistics/by-subparish?year=${year}`
                : `${this.API_BASE}/donations/statistics/by-subparish`;

            const response = await fetch(url);
            if (!response.ok) throw new Error('Failed to load subparish totals');

            const subParishTotals = await response.json();
            console.log('Found sub-parishes with contributions:', Object.keys(subParishTotals).length);

            if (Object.keys(subParishTotals).length === 0) {
                let noDataMessage = 'Nta maturo aboneka';
                if (year) {
                    noDataMessage += ` mu mwaka wa ${year}`;
                }
                this.summaryItemsContainer.innerHTML = `
                    <p class="text-gray-500 italic text-center py-4">${noDataMessage}</p>
                `;
                return;
            }

            // Display each subparish
            for (const [subParish, total] of Object.entries(subParishTotals)) {
                const row = this.createSummaryRow(
                    subParish,
                    this.formatCurrency(total as number),
                    'bg-gray-100'
                );
                this.summaryItemsContainer.appendChild(row);
            }

        } catch (error) {
            console.error('Error displaying subparish summary:', error);
            this.showError('Ikosa mu gusoma santarali');
        }
    }

    private async showBecSummaryForSubParish(subParish: string): Promise<void> {
        try {
            const year = this.selectedYear;
            console.log('Showing BEC summary for:', subParish, '(', year || 'all years', ')');

            // Clear previous content
            this.summaryItemsContainer.innerHTML = '';

            // Update title
            let titleText = `IGITERANYO CY'ITURO RYA MPUZA ZA ${subParish.toUpperCase()}`;
            if (year) {
                titleText += ` (${year})`;
            } else {
                titleText += ' (IMYAKA YOSE)';
            }
            this.summaryTitleLabel.textContent = titleText;

            // Get BEC totals for the selected SubParish
            const url = year
                ? `${this.API_BASE}/donations/statistics/by-bec?subParish=${encodeURIComponent(subParish)}&year=${year}`
                : `${this.API_BASE}/donations/statistics/by-bec?subParish=${encodeURIComponent(subParish)}`;

            const response = await fetch(url);
            if (!response.ok) throw new Error('Failed to load BEC totals');

            const becTotals = await response.json();
            console.log('Found BECs with contributions:', Object.keys(becTotals).length);

            if (Object.keys(becTotals).length === 0) {
                let noDataMessage = 'Nta maturo aboneka kuri ino santarali';
                if (year) {
                    noDataMessage += ` mu mwaka wa ${year}`;
                }
                this.summaryItemsContainer.innerHTML = `
                    <p class="text-gray-500 italic text-center py-4">${noDataMessage}</p>
                `;
                return;
            }

            // Display each BEC
            for (const [bec, total] of Object.entries(becTotals)) {
                const row = this.createSummaryRow(
                    bec,
                    this.formatCurrency(total as number),
                    'bg-blue-50'
                );
                this.summaryItemsContainer.appendChild(row);
            }

        } catch (error) {
            console.error('Error displaying BEC summary:', error);
            this.showError('Ikosa mu gusoma mpuza');
        }
    }

    private refreshCurrentView(): void {
        const selectedYear = this.yearFilterComboBox.value;
        this.selectedYear = selectedYear ? parseInt(selectedYear) : null;

        // Update overall title
        if (this.selectedYear) {
            this.overallTotalTitleLabel.textContent = `ITURO RYA PARUWASE YOSE MU MWAKA WA ${this.selectedYear} NI`;
        } else {
            this.overallTotalTitleLabel.textContent = 'ITURO RYOSE RYA PARUWASE KUGEZA UBU NI';
        }

        // Smart refresh based on current state
        const selectedSubParish = this.subParishFilterComboBox.value;
        if (selectedSubParish) {
            // User is viewing BEC details - refresh BEC view with new year
            this.showBecSummaryForSubParish(selectedSubParish);
        } else {
            // User is viewing SubParish summary - refresh SubParish view with new year
            this.refreshSummary();
        }
    }

    private clearSubParishFilter(): void {
        this.subParishFilterComboBox.value = '';
        this.showSubParishSummary();
    }

    // ==================== UI HELPERS ====================
    private createSummaryRow(label: string, amount: string, bgColor: string): HTMLElement {
        const row = document.createElement('div');
        row.className = `flex justify-between items-center p-3 ${bgColor} rounded border border-gray-200`;

        const nameLabel = document.createElement('span');
        nameLabel.className = 'font-bold text-gray-800';
        nameLabel.textContent = label;

        const amountLabel = document.createElement('span');
        amountLabel.className = 'font-bold text-green-600';
        amountLabel.textContent = amount;

        row.appendChild(nameLabel);
        row.appendChild(amountLabel);

        return row;
    }

    private formatCurrency(amount: number): string {
        try {
            if (amount == null) {
                amount = 0;
            }

            return new Intl.NumberFormat('rw-RW', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount) + ' RWF';
        } catch (error) {
            console.error('Currency format error:', error);
            return ' RWF ???';
        }
    }

    private showLoading(show: boolean): void {
        if (show) {
            this.loadingIndicator.classList.remove('hidden');
            this.loadingIndicator.classList.add('flex');
        } else {
            this.loadingIndicator.classList.add('hidden');
            this.loadingIndicator.classList.remove('flex');
        }
    }

    private showError(message: string): void {
        console.error(message);
        alert(message); // Simple alert for errors
    }

    private closeDialog(): void {
        // Go back to previous page
        window.history.back();
    }
}

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    new DonationSummaryController();
    console.log('Donation Summary Controller initialized');
});

export { DonationSummaryController };