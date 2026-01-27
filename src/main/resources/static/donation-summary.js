// ==================== CONTRIBUTION SUMMARY CONTROLLER ====================
class DonationSummaryController {
    constructor() {
        // API Base URL
        this.API_BASE = 'http://localhost:8080/api';
        // Data storage
        this.selectedYear = null;
        this.selectedSubParish = null;
        this.availableYears = [];
        this.subParishList = [];
        this.initializeElements();
        this.setupEventListeners();
        this.initialize();
    }
    // ==================== INITIALIZATION ====================
    initializeElements() {
        // Filters
        this.yearFilterComboBox = document.getElementById('yearFilterComboBox');
        this.refreshSummaryButton = document.getElementById('refreshSummaryButton');
        this.showAllYearsButton = document.getElementById('showAllYearsButton');
        this.subParishFilterComboBox = document.getElementById('subParishFilterComboBox');
        this.clearSubParishFilterButton = document.getElementById('clearSubParishFilterButton');
        // Content
        this.summaryContentVBox = document.getElementById('summaryContentVBox');
        this.overallTotalTitleLabel = document.getElementById('overallTotalTitleLabel');
        this.overallTotalAmountLabel = document.getElementById('overallTotalAmountLabel');
        this.dynamicSummaryVBox = document.getElementById('dynamicSummaryVBox');
        this.summaryTitleLabel = document.getElementById('summaryTitleLabel');
        this.summaryItemsContainer = document.getElementById('summaryItemsContainer');
        // Actions
        this.closeButton = document.getElementById('closeButton');
        // Loading
        this.loadingIndicator = document.getElementById('loadingIndicator');
    }
    async initialize() {
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
        }
        catch (error) {
            console.error('Initialization error:', error);
            this.showError('Ikosa mu gutangiza');
        }
    }
    // ==================== EVENT LISTENERS ====================
    setupEventListeners() {
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
            }
            else {
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
    async loadAvailableYears() {
        try {
            const response = await fetch(`${this.API_BASE}/donations/statistics/available-years`);
            if (!response.ok)
                throw new Error('Failed to load years');
            this.availableYears = await response.json();
            // Populate year dropdown
            this.yearFilterComboBox.innerHTML = '<option value="">Hitamo umwaka...</option>';
            this.availableYears.forEach(year => {
                const option = document.createElement('option');
                option.value = year.toString();
                option.textContent = year.toString();
                this.yearFilterComboBox.appendChild(option);
            });
        }
        catch (error) {
            console.error('Error loading years:', error);
        }
    }
    async loadSubParishes() {
        try {
            // Get unique subparishes from donations
            const response = await fetch(`${this.API_BASE}/donations/statistics/by-subparish`);
            if (!response.ok)
                throw new Error('Failed to load subparishes');
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
        }
        catch (error) {
            console.error('Error loading subparishes:', error);
        }
    }
    // ==================== SUMMARY METHODS ====================
    async refreshSummary() {
        const selectedYear = this.yearFilterComboBox.value;
        console.log('Refreshing summary with year filter:', selectedYear || 'All years');
        if (selectedYear) {
            this.selectedYear = parseInt(selectedYear);
            await this.loadSummaryForYear(this.selectedYear);
        }
        else {
            this.selectedYear = null;
            await this.loadSummaryForYear(null);
        }
    }
    async showAllYearsSummary() {
        console.log('Displaying summary for all years');
        this.yearFilterComboBox.value = '';
        this.selectedYear = null;
        await this.loadSummaryForYear(null);
    }
    async loadSummaryForYear(year) {
        this.showLoading(true);
        try {
            // Clear subparish filter
            this.subParishFilterComboBox.value = '';
            console.log('Loading summary data for year:', year || 'All years');
            // Update title based on filter
            if (year) {
                this.overallTotalTitleLabel.textContent = `ITURO RYA PARUWASE YOSE UMWAKA WA ${year} NI`;
            }
            else {
                this.overallTotalTitleLabel.textContent = 'ITURO RYOSE RYA PARUWASE KUGEZA UBU NI';
            }
            // Get overall total
            let overallTotal;
            if (year) {
                const response = await fetch(`${this.API_BASE}/donations/statistics/year/${year}/total`);
                if (!response.ok)
                    throw new Error('Failed to load total');
                const data = await response.json();
                overallTotal = parseFloat(data.total);
            }
            else {
                const response = await fetch(`${this.API_BASE}/donations/statistics/total`);
                if (!response.ok)
                    throw new Error('Failed to load total');
                const data = await response.json();
                overallTotal = parseFloat(data.total);
            }
            console.log('Calculated overall total:', this.formatCurrency(overallTotal));
            this.overallTotalAmountLabel.textContent = this.formatCurrency(overallTotal);
            // Show subparish summary
            await this.showSubParishSummary();
        }
        catch (error) {
            console.error('Error loading summary:', error);
            this.showError('Ikosa mu gusoma amaturo');
        }
        finally {
            this.showLoading(false);
        }
    }
    async showSubParishSummary() {
        try {
            const year = this.selectedYear;
            console.log('Displaying sub-parish summary for:', year || 'all years');
            // Clear previous content
            this.summaryItemsContainer.innerHTML = '';
            // Update title with year information
            let titleText = 'IGITERANYO CY\'ITURO RYA SANTARALI:';
            if (year) {
                titleText += ` (${year})`;
            }
            else {
                titleText += ' (IMYAKA YOSE)';
            }
            this.summaryTitleLabel.textContent = titleText;
            // Get totals by SubParish
            const url = year
                ? `${this.API_BASE}/donations/statistics/by-subparish?year=${year}`
                : `${this.API_BASE}/donations/statistics/by-subparish`;
            const response = await fetch(url);
            if (!response.ok)
                throw new Error('Failed to load subparish totals');
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
                const row = this.createSummaryRow(subParish, this.formatCurrency(total), 'bg-gray-100');
                this.summaryItemsContainer.appendChild(row);
            }
        }
        catch (error) {
            console.error('Error displaying subparish summary:', error);
            this.showError('Ikosa mu gusoma santarali');
        }
    }
    async showBecSummaryForSubParish(subParish) {
        try {
            const year = this.selectedYear;
            console.log('Showing BEC summary for:', subParish, '(', year || 'all years', ')');
            // Clear previous content
            this.summaryItemsContainer.innerHTML = '';
            // Update title
            let titleText = `IGITERANYO CY'ITURO RYA MPUZA ZA ${subParish.toUpperCase()}`;
            if (year) {
                titleText += ` (${year})`;
            }
            else {
                titleText += ' (IMYAKA YOSE)';
            }
            this.summaryTitleLabel.textContent = titleText;
            // Get BEC totals for the selected SubParish
            const url = year
                ? `${this.API_BASE}/donations/statistics/by-bec?subParish=${encodeURIComponent(subParish)}&year=${year}`
                : `${this.API_BASE}/donations/statistics/by-bec?subParish=${encodeURIComponent(subParish)}`;
            const response = await fetch(url);
            if (!response.ok)
                throw new Error('Failed to load BEC totals');
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
                const row = this.createSummaryRow(bec, this.formatCurrency(total), 'bg-blue-50');
                this.summaryItemsContainer.appendChild(row);
            }
        }
        catch (error) {
            console.error('Error displaying BEC summary:', error);
            this.showError('Ikosa mu gusoma mpuza');
        }
    }
    refreshCurrentView() {
        const selectedYear = this.yearFilterComboBox.value;
        this.selectedYear = selectedYear ? parseInt(selectedYear) : null;
        // Update overall title
        if (this.selectedYear) {
            this.overallTotalTitleLabel.textContent = `ITURO RYA PARUWASE YOSE MU MWAKA WA ${this.selectedYear} NI`;
        }
        else {
            this.overallTotalTitleLabel.textContent = 'ITURO RYOSE RYA PARUWASE KUGEZA UBU NI';
        }
        // Smart refresh based on current state
        const selectedSubParish = this.subParishFilterComboBox.value;
        if (selectedSubParish) {
            // User is viewing BEC details - refresh BEC view with new year
            this.showBecSummaryForSubParish(selectedSubParish);
        }
        else {
            // User is viewing SubParish summary - refresh SubParish view with new year
            this.refreshSummary();
        }
    }
    clearSubParishFilter() {
        this.subParishFilterComboBox.value = '';
        this.showSubParishSummary();
    }
    // ==================== UI HELPERS ====================
    createSummaryRow(label, amount, bgColor) {
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
    formatCurrency(amount) {
        try {
            if (amount == null) {
                amount = 0;
            }
            return new Intl.NumberFormat('rw-RW', {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0
            }).format(amount) + ' RWF';
        }
        catch (error) {
            console.error('Currency format error:', error);
            return ' RWF ???';
        }
    }
    showLoading(show) {
        if (show) {
            this.loadingIndicator.classList.remove('hidden');
            this.loadingIndicator.classList.add('flex');
        }
        else {
            this.loadingIndicator.classList.add('hidden');
            this.loadingIndicator.classList.remove('flex');
        }
    }
    showError(message) {
        console.error(message);
        alert(message); // Simple alert for errors
    }
    closeDialog() {
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
