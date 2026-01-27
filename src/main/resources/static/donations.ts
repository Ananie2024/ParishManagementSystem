// ==================== INTERFACES ====================
interface Faithful {
    id: number;
    name: string;
    firstname?: string;
    phone?: string;
    subparish?: string;
    basicEcclesialCommunity?: string;
    dateOfBirth?: string;
    occupation?: string;
}

interface Donation {
    id: number;
    faithfulId: number;
    faithfulName: string;
    year: number;
    amount: number;
    date: string;
    contributionType?: string;
    paymentMethod?: string;
    referenceNumber?: string;
    notes?: string;
    recordedBy?: string;
}

interface DonationRequestDTO {
    faithfulId: number;
    year: number;
    amount: number;
    date: string;
    contributionType?: string;
    paymentMethod?: string;
    referenceNumber?: string;
    notes?: string;
    recordedBy?: string;
}

// ==================== MAIN MANAGER CLASS ====================
class FaithfulDonationsManager {
    // API Base URL
    private readonly API_BASE = 'http://localhost:8080/api';

    // Filter elements
    private filterSubParishComboBox: HTMLSelectElement;
    private filterBecComboBox: HTMLSelectElement;
    private searchFaithfulTextField: HTMLInputElement;
    private clearFiltersButton: HTMLButtonElement;

    // Navigation buttons
    private homeButton: HTMLButtonElement;
    private showSummaryButton: HTMLButtonElement;
    private overallSummaryButton: HTMLButtonElement;

    // Faithful table elements
    private faithfulsTable: HTMLTableElement;
    private faithfulsTableBody: HTMLTableSectionElement;

    // Faithful action buttons
    private addNewFaithfulBtn: HTMLButtonElement;
    private editFaithfulBtn: HTMLButtonElement;
    private deleteFaithfulBtn: HTMLButtonElement;

    // Faithful details labels
    private detailNameLabel: HTMLElement;
    private detailContactLabel: HTMLElement;
    private detailAddressLabel: HTMLElement;
    private detailBecLabel: HTMLElement;
    private detailSubParishLabel: HTMLElement;
    private detailBaptismYearLabel: HTMLElement;
    private detailOccupationLabel: HTMLElement;

    // Contributions table elements
    private contributionsTable: HTMLTableElement;
    private contributionsTableBody: HTMLTableSectionElement;

    // Contribution action buttons
    private addContributionBtn: HTMLButtonElement;
    private editContributionBtn: HTMLButtonElement;
    private deleteContributionBtn: HTMLButtonElement;
    private generatePDFBtn: HTMLButtonElement;

    // Modal elements
    private donationModal: HTMLElement;
    private modalTitle: HTMLElement;
    private modalFaithfulName: HTMLElement;
    private donationForm: HTMLFormElement;
    private modalAmount: HTMLInputElement;
    private modalYear: HTMLSelectElement;
    private modalDate: HTMLInputElement;
    private modalContributionType: HTMLSelectElement;
    private modalPaymentMethod: HTMLSelectElement;
    private modalNotes: HTMLTextAreaElement;
    private modalCancelBtn: HTMLButtonElement;
    private modalSaveBtn: HTMLButtonElement;
    private modalStatus: HTMLElement;

    // Status message
    private statusMessage: HTMLElement;

    // Data storage
    private faithfulList: Faithful[] = [];
    private filteredFaithfuls: Faithful[] = [];
    private donationsList: Donation[] = [];
    private selectedFaithful: Faithful | null = null;
    private selectedDonation: Donation | null = null;
    private editingDonationId: number | null = null;

    constructor() {
        // Initialize all DOM elements
        this.initializeElements();

        // Setup event listeners
        this.setupEventListeners();

        // Load initial data
        this.initialize();
    }

    // ==================== INITIALIZATION ====================
    private initializeElements(): void {
        // Filters
        this.filterSubParishComboBox = document.getElementById('filterSubParishComboBox') as HTMLSelectElement;
        this.filterBecComboBox = document.getElementById('filterBecComboBox') as HTMLSelectElement;
        this.searchFaithfulTextField = document.getElementById('searchFaithfulTextField') as HTMLInputElement;
        this.clearFiltersButton = document.getElementById('clearFiltersButton') as HTMLButtonElement;

        // Navigation
        this.homeButton = document.getElementById('homeButton') as HTMLButtonElement;
        this.showSummaryButton = document.getElementById('showSummaryButton') as HTMLButtonElement;
        this.overallSummaryButton = document.getElementById('overallSummaryButton') as HTMLButtonElement;

        // Faithful table
        this.faithfulsTable = document.getElementById('faithfulsTable') as HTMLTableElement;
        this.faithfulsTableBody = document.getElementById('faithfulsTableBody') as HTMLTableSectionElement;

        // Faithful buttons
        this.addNewFaithfulBtn = document.getElementById('addNewFaithfulBtn') as HTMLButtonElement;
        this.editFaithfulBtn = document.getElementById('editFaithfulBtn') as HTMLButtonElement;
        this.deleteFaithfulBtn = document.getElementById('deleteFaithfulBtn') as HTMLButtonElement;

        // Details labels
        this.detailNameLabel = document.getElementById('detailNameLabel') as HTMLElement;
        this.detailContactLabel = document.getElementById('detailContactLabel') as HTMLElement;
        this.detailAddressLabel = document.getElementById('detailAddressLabel') as HTMLElement;
        this.detailBecLabel = document.getElementById('detailBecLabel') as HTMLElement;
        this.detailSubParishLabel = document.getElementById('detailSubParishLabel') as HTMLElement;
        this.detailBaptismYearLabel = document.getElementById('detailBaptismYearLabel') as HTMLElement;
        this.detailOccupationLabel = document.getElementById('detailOccupationLabel') as HTMLElement;

        // Contributions table
        this.contributionsTable = document.getElementById('contributionsTable') as HTMLTableElement;
        this.contributionsTableBody = document.getElementById('contributionsTableBody') as HTMLTableSectionElement;

        // Contribution buttons
        this.addContributionBtn = document.getElementById('addContributionBtn') as HTMLButtonElement;
        this.editContributionBtn = document.getElementById('editContributionBtn') as HTMLButtonElement;
        this.deleteContributionBtn = document.getElementById('deleteContributionBtn') as HTMLButtonElement;
        this.generatePDFBtn = document.getElementById('generatePDFBtn') as HTMLButtonElement;

        // Modal
        this.donationModal = document.getElementById('donationModal') as HTMLElement;
        this.modalTitle = document.getElementById('modalTitle') as HTMLElement;
        this.modalFaithfulName = document.getElementById('modalFaithfulName') as HTMLElement;
        this.donationForm = document.getElementById('donationForm') as HTMLFormElement;
        this.modalAmount = document.getElementById('modalAmount') as HTMLInputElement;
        this.modalYear = document.getElementById('modalYear') as HTMLSelectElement;
        this.modalDate = document.getElementById('modalDate') as HTMLInputElement;
        this.modalContributionType = document.getElementById('modalContributionType') as HTMLSelectElement;
        this.modalPaymentMethod = document.getElementById('modalPaymentMethod') as HTMLSelectElement;
        this.modalNotes = document.getElementById('modalNotes') as HTMLTextAreaElement;
        this.modalCancelBtn = document.getElementById('modalCancelBtn') as HTMLButtonElement;
        this.modalSaveBtn = document.getElementById('modalSaveBtn') as HTMLButtonElement;
        this.modalStatus = document.getElementById('modalStatus') as HTMLElement;

        // Status
        this.statusMessage = document.getElementById('statusMessage') as HTMLElement;
    }

    private async initialize(): Promise<void> {
        try {
            // Populate year dropdown in modal
            this.populateYears();

            // Load faithfuls from API
            await this.loadAllFaithfuls();

            // Load filter options
            await this.populateFilterSubParishes();

            console.log('Faithful Donations Manager initialized successfully');
        } catch (error) {
            console.error('Initialization error:', error);
            this.showStatus('Ikosa mu gutangiza: ' + (error as Error).message, 'error');
        }
    }

    // ==================== EVENT LISTENERS ====================
    private setupEventListeners(): void {
        // Filter listeners
        this.filterSubParishComboBox.addEventListener('change', () => this.handleSubParishChange());
        this.filterBecComboBox.addEventListener('change', () => this.filterFaithfuls());
        this.searchFaithfulTextField.addEventListener('input', () => this.filterFaithfuls());
        this.clearFiltersButton.addEventListener('click', () => this.clearFilters());

        // Navigation listeners
        this.homeButton.addEventListener('click', () => this.handleGoHome());
        this.showSummaryButton.addEventListener('click', () => this.handleShowSummary());
        this.overallSummaryButton.addEventListener('click', () => this.handleOverallSummary());

        // Faithful action listeners
        this.addNewFaithfulBtn.addEventListener('click', () => this.handleAddFaithful());
        this.editFaithfulBtn.addEventListener('click', () => this.handleEditFaithful());
        this.deleteFaithfulBtn.addEventListener('click', () => this.handleDeleteFaithful());

        // Contribution action listeners
        this.addContributionBtn.addEventListener('click', () => this.openDonationModal(null));
        this.editContributionBtn.addEventListener('click', () => this.handleEditDonation());
        this.deleteContributionBtn.addEventListener('click', () => this.handleDeleteDonation());
        this.generatePDFBtn.addEventListener('click', () => this.handleGeneratePDF());

        // Modal listeners removed - using separate page
        // No longer needed: modal form submit, cancel, outside click
    }

    // ==================== DATA LOADING ====================
    private async loadAllFaithfuls(): Promise<void> {
        try {
            const response = await fetch(`${this.API_BASE}/faithfuls`);
            if (!response.ok) throw new Error('Failed to load faithfuls');

            this.faithfulList = await response.json();
            this.filteredFaithfuls = [...this.faithfulList];
            this.renderFaithfulsTable();
        } catch (error) {
            console.error('Error loading faithfuls:', error);
            this.showStatus('Ikosa mu gusoma abakristu', 'error');
        }
    }

    private async loadDonationsByFaithful(faithfulId: number): Promise<void> {
        try {
            const response = await fetch(`${this.API_BASE}/donations/faithful/${faithfulId}`);
            if (!response.ok) throw new Error('Failed to load donations');

            this.donationsList = await response.json();
            this.renderDonationsTable();
        } catch (error) {
            console.error('Error loading donations:', error);
            this.showStatus('Ikosa mu gusoma amaturo', 'error');
            this.donationsList = [];
            this.renderDonationsTable();
        }
    }

    // ==================== FILTER METHODS ====================
    private async populateFilterSubParishes(): Promise<void> {
        // Get unique subparishes from faithful list
        const subparishes = [...new Set(this.faithfulList.map(f => f.subparish).filter(Boolean))];

        this.filterSubParishComboBox.innerHTML = '<option value="">Hitamo Santarali...</option>';
        subparishes.forEach(sp => {
            const option = document.createElement('option');
            option.value = sp!;
            option.textContent = sp!;
            this.filterSubParishComboBox.appendChild(option);
        });
    }

    private handleSubParishChange(): void {
        const selectedSubParish = this.filterSubParishComboBox.value;

        // Clear and disable BEC filter
        this.filterBecComboBox.innerHTML = '<option value="">Hitamo impuza...</option>';
        this.filterBecComboBox.disabled = !selectedSubParish;

        if (selectedSubParish) {
            // Get unique BECs for selected subparish
            const becs = [...new Set(
                this.faithfulList
                    .filter(f => f.subparish === selectedSubParish)
                    .map(f => f.basicEcclesialCommunity)
                    .filter(Boolean)
            )];

            becs.forEach(bec => {
                const option = document.createElement('option');
                option.value = bec!;
                option.textContent = bec!;
                this.filterBecComboBox.appendChild(option);
            });
        }

        this.filterFaithfuls();
    }

    private filterFaithfuls(): void {
        const subParish = this.filterSubParishComboBox.value;
        const bec = this.filterBecComboBox.value;
        const searchText = this.searchFaithfulTextField.value.toLowerCase();

        this.filteredFaithfuls = this.faithfulList.filter(f => {
            // Filter by subparish
            if (subParish && f.subparish !== subParish) return false;

            // Filter by BEC
            if (bec && f.basicEcclesialCommunity !== bec) return false;

            // Filter by search text
            if (searchText && !f.name.toLowerCase().includes(searchText)) return false;

            return true;
        });

        this.renderFaithfulsTable();
    }

    private clearFilters(): void {
        this.filterSubParishComboBox.value = '';
        this.filterBecComboBox.value = '';
        this.filterBecComboBox.disabled = true;
        this.searchFaithfulTextField.value = '';

        this.filteredFaithfuls = [...this.faithfulList];
        this.renderFaithfulsTable();
    }

    // ==================== TABLE RENDERING ====================
    private renderFaithfulsTable(): void {
        this.faithfulsTableBody.innerHTML = '';

        if (this.filteredFaithfuls.length === 0) {
            const row = this.faithfulsTableBody.insertRow();
            const cell = row.insertCell(0);
            cell.colSpan = 3;
            cell.className = 'text-center py-4 text-gray-500';
            cell.textContent = 'Nta bakristu bagaragara';
            return;
        }

        this.filteredFaithfuls.forEach(faithful => {
            const row = this.faithfulsTableBody.insertRow();
            row.className = 'hover:bg-blue-50 cursor-pointer transition';
            row.dataset.faithfulId = faithful.id.toString();

            // Add click listener
            row.addEventListener('click', () => this.selectFaithful(faithful));

            // Name
            const nameCell = row.insertCell(0);
            nameCell.className = 'px-3 py-2';
            nameCell.textContent = faithful.name;

            // BEC
            const becCell = row.insertCell(1);
            becCell.className = 'px-3 py-2';
            becCell.textContent = faithful.basicEcclesialCommunity || 'N/A';

            // SubParish
            const subParishCell = row.insertCell(2);
            subParishCell.className = 'px-3 py-2';
            subParishCell.textContent = faithful.subparish || 'N/A';
        });
    }

    private renderDonationsTable(): void {
        this.contributionsTableBody.innerHTML = '';

        if (this.donationsList.length === 0) {
            const row = this.contributionsTableBody.insertRow();
            const cell = row.insertCell(0);
            cell.colSpan = 3;
            cell.className = 'text-center py-4 text-gray-500';
            cell.textContent = 'Nta maturo agaragara';
            return;
        }

        this.donationsList.forEach(donation => {
            const row = this.contributionsTableBody.insertRow();
            row.className = 'hover:bg-gray-50 cursor-pointer transition';
            row.dataset.donationId = donation.id.toString();

            // Add click listener
            row.addEventListener('click', () => this.selectDonation(donation));

            // Year
            const yearCell = row.insertCell(0);
            yearCell.className = 'px-3 py-2';
            yearCell.textContent = donation.year.toString();

            // Amount
            const amountCell = row.insertCell(1);
            amountCell.className = 'px-3 py-2 font-semibold text-green-600';
            amountCell.textContent = this.formatCurrency(donation.amount);

            // Date
            const dateCell = row.insertCell(2);
            dateCell.className = 'px-3 py-2';
            dateCell.textContent = new Date(donation.date).toLocaleDateString('rw-RW');
        });
    }

    // ==================== SELECTION HANDLERS ====================
    private selectFaithful(faithful: Faithful): void {
        this.selectedFaithful = faithful;

        // Highlight selected row
        const rows = this.faithfulsTableBody.querySelectorAll('tr');
        rows.forEach(row => row.classList.remove('bg-blue-100'));
        const selectedRow = this.faithfulsTableBody.querySelector(`tr[data-faithful-id="${faithful.id}"]`);
        if (selectedRow) selectedRow.classList.add('bg-blue-100');

        // Display details
        this.displayFaithfulDetails(faithful);

        // Load donations
        this.loadDonationsByFaithful(faithful.id);

        // Enable buttons
        this.editFaithfulBtn.disabled = false;
        this.deleteFaithfulBtn.disabled = false;
        this.addContributionBtn.disabled = false;
        this.generatePDFBtn.disabled = false;
    }

    private selectDonation(donation: Donation): void {
        this.selectedDonation = donation;

        // Highlight selected row
        const rows = this.contributionsTableBody.querySelectorAll('tr');
        rows.forEach(row => row.classList.remove('bg-gray-100'));
        const selectedRow = this.contributionsTableBody.querySelector(`tr[data-donation-id="${donation.id}"]`);
        if (selectedRow) selectedRow.classList.add('bg-gray-100');

        // Enable buttons
        this.editContributionBtn.disabled = false;
        this.deleteContributionBtn.disabled = false;
    }

    private displayFaithfulDetails(faithful: Faithful): void {
        this.detailNameLabel.textContent = faithful.name || 'N/A';
        this.detailContactLabel.textContent = faithful.phone || 'N/A';
        this.detailAddressLabel.textContent = 'N/A'; // Add address field if available
        this.detailBecLabel.textContent = faithful.basicEcclesialCommunity || 'N/A';
        this.detailSubParishLabel.textContent = faithful.subparish || 'N/A';
        this.detailBaptismYearLabel.textContent = faithful.dateOfBirth ? new Date(faithful.dateOfBirth).getFullYear().toString() : 'N/A';
        this.detailOccupationLabel.textContent = faithful.occupation || 'N/A';
    }

    private clearFaithfulDetails(): void {
        this.detailNameLabel.textContent = '-';
        this.detailContactLabel.textContent = '-';
        this.detailAddressLabel.textContent = '-';
        this.detailBecLabel.textContent = '-';
        this.detailSubParishLabel.textContent = '-';
        this.detailBaptismYearLabel.textContent = '-';
        this.detailOccupationLabel.textContent = '-';
    }

    // ==================== DONATION MODAL ====================
    private openDonationModal(donation: Donation | null): void {
        if (!this.selectedFaithful && !donation) {
            this.showStatus('Hitamo umukristu mbere', 'error');
            return;
        }

        // Store selected faithful in session storage for the form page
        if (this.selectedFaithful) {
            sessionStorage.setItem('selectedFaithful', JSON.stringify(this.selectedFaithful));
        }

        // Navigate to add/edit donation page
        if (donation) {
            // Edit mode - pass both faithfulId and donationId
            window.location.href = `/donations/add?faithfulId=${this.selectedFaithful?.id}&donationId=${donation.id}`;
        } else {
            // Add mode - pass only faithfulId
            window.location.href = `/donations/add?faithfulId=${this.selectedFaithful?.id}`;
        }
    }

    // Modal methods removed - using separate page now

    // ==================== ACTION HANDLERS ====================
    private handleGoHome(): void {
        // Navigate to home page
        window.location.href = '/';
    }

    private handleShowSummary(): void {
        // Navigate to summary page
        console.log('Navigating to donation summary');
        window.location.href = '/donation-summary.html';
    }

    private handleOverallSummary(): void {
        // Navigate to overall summary page
        window.location.href = '/donation-summary.html';
    }

    private handleAddFaithful(): void {
        // Navigate to add faithful page
        window.location.href = '/faithfuls.html';
    }

    private handleEditFaithful(): void {
        if (!this.selectedFaithful) {
            this.showStatus('Hitamo umukristu mbere', 'error');
            return;
        }
        window.location.href = `/faithfuls/edit/${this.selectedFaithful.id}`;
    }

    private async handleDeleteFaithful(): Promise<void> {
        if (!this.selectedFaithful) {
            this.showStatus('Hitamo umukristu mbere', 'error');
            return;
        }

        if (!confirm(`Ushaka gusiba ${this.selectedFaithful.name}?\nUzi neza ko biribusibe n'amaturo yose?`)) {
            return;
        }

        try {
            const response = await fetch(`${this.API_BASE}/faithfuls/${this.selectedFaithful.id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Failed to delete faithful');

            this.showStatus(`${this.selectedFaithful.name} yasibwe neza`, 'success');
            this.selectedFaithful = null;
            this.clearFaithfulDetails();
            await this.loadAllFaithfuls();
        } catch (error) {
            console.error('Error deleting faithful:', error);
            this.showStatus('Ikosa mu gusiba', 'error');
        }
    }

    private handleEditDonation(): void {
        if (!this.selectedDonation) {
            this.showStatus('Hitamo ituro mbere', 'error');
            return;
        }
        this.openDonationModal(this.selectedDonation);
    }

    private async handleDeleteDonation(): Promise<void> {
        if (!this.selectedDonation) {
            this.showStatus('Hitamo ituro mbere', 'error');
            return;
        }

        if (!confirm(`Ushaka gusiba ituro rya ${this.formatCurrency(this.selectedDonation.amount)}?`)) {
            return;
        }

        try {
            const response = await fetch(`${this.API_BASE}/donations/${this.selectedDonation.id}`, {
                method: 'DELETE'
            });

            if (!response.ok) throw new Error('Failed to delete donation');

            this.showStatus('Ituro ryasibwe neza', 'success');
            this.selectedDonation = null;

            if (this.selectedFaithful) {
                await this.loadDonationsByFaithful(this.selectedFaithful.id);
            }
        } catch (error) {
            console.error('Error deleting donation:', error);
            this.showStatus('Ikosa mu gusiba ituro', 'error');
        }
    }

    private handleGeneratePDF(): void {
        if (!this.selectedFaithful) {
            this.showStatus('Hitamo umukristu mbere', 'error');
            return;
        }
        // TODO: Implement PDF generation
        this.showStatus('PDF generation - coming soon!', 'info');
    }

    // ==================== UTILITY METHODS ====================
    private populateYears(): void {
        const currentYear = new Date().getFullYear();
        this.modalYear.innerHTML = '';

        for (let year = currentYear - 15; year <= currentYear + 5; year++) {
            const option = document.createElement('option');
            option.value = year.toString();
            option.textContent = year.toString();
            this.modalYear.appendChild(option);
        }
    }

    private formatCurrency(amount: number): string {
        return new Intl.NumberFormat('rw-RW', {
            minimumFractionDigits: 0,
            maximumFractionDigits: 0
        }).format(amount) + ' RWF';
    }

    private showStatus(message: string, type: 'success' | 'error' | 'info'): void {
        this.statusMessage.textContent = message;
        this.statusMessage.classList.remove('hidden', 'bg-green-100', 'text-green-700', 'bg-red-100', 'text-red-700', 'bg-blue-100', 'text-blue-700');

        if (type === 'success') {
            this.statusMessage.classList.add('bg-green-100', 'text-green-700');
        } else if (type === 'error') {
            this.statusMessage.classList.add('bg-red-100', 'text-red-700');
        } else {
            this.statusMessage.classList.add('bg-blue-100', 'text-blue-700');
        }

        setTimeout(() => {
            this.statusMessage.classList.add('hidden');
        }, 5000);
    }

    private showModalStatus(message: string, type: 'success' | 'error'): void {
        this.modalStatus.textContent = message;
        this.modalStatus.classList.remove('hidden', 'bg-green-100', 'text-green-700', 'bg-red-100', 'text-red-700');

        if (type === 'success') {
            this.modalStatus.classList.add('bg-green-100', 'text-green-700');
        } else {
            this.modalStatus.classList.add('bg-red-100', 'text-red-700');
        }
    }
}

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    new FaithfulDonationsManager();
    console.log('Faithful Donations Manager initialized');
});

export { FaithfulDonationsManager };