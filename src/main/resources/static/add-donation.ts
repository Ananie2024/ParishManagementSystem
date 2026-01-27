// ==================== INTERFACES ====================
interface Faithful {
    id: number;
    name: string;
    firstname?: string;
}

interface DonationRequestDTO {
    faithfulId: number;
    year: number;
    amount: number;
    date: string;
    contributionType?: string;
    paymentMethod?: string;
    notes?: string;
    recordedBy?: string;
}

// ==================== ADD DONATION CONTROLLER ====================
class AddDonationController {
    // API Base URL
    private readonly API_BASE = 'http://localhost:8080/api';

    // Form elements
    private form: HTMLFormElement;
    private formTitleLabel: HTMLElement;
    private faithfulNameLabel: HTMLElement;
    private amountTextField: HTMLInputElement;
    private yearTextField: HTMLInputElement;
    private datePicker: HTMLInputElement;
    private contributionType: HTMLSelectElement;
    private paymentMethod: HTMLSelectElement;
    private notesTextArea: HTMLTextAreaElement;
    private saveContributionButton: HTMLButtonElement;
    private cancelButton: HTMLButtonElement;
    private statusLabel: HTMLElement;

    // Data
    private selectedFaithful: Faithful | null = null;
    private currentDonationId: number | null = null;
    private isSubmitting = false;

    constructor() {
        this.initializeElements();
        this.initialize();
        this.setupEventListeners();
    }

    // ==================== INITIALIZATION ====================
    private initializeElements(): void {
        this.form = document.getElementById('donationForm') as HTMLFormElement;
        this.formTitleLabel = document.getElementById('formTitleLabel') as HTMLElement;
        this.faithfulNameLabel = document.getElementById('faithfulNameLabel') as HTMLElement;
        this.amountTextField = document.getElementById('amountTextField') as HTMLInputElement;
        this.yearTextField = document.getElementById('yearTextField') as HTMLInputElement;
        this.datePicker = document.getElementById('datePicker') as HTMLInputElement;
        this.contributionType = document.getElementById('contributionType') as HTMLSelectElement;
        this.paymentMethod = document.getElementById('paymentMethod') as HTMLSelectElement;
        this.notesTextArea = document.getElementById('notesTextArea') as HTMLTextAreaElement;
        this.saveContributionButton = document.getElementById('saveContributionButton') as HTMLButtonElement;
        this.cancelButton = document.getElementById('cancelButton') as HTMLButtonElement;
        this.statusLabel = document.getElementById('statusLabel') as HTMLElement;
    }

    private initialize(): void {
        // Set default date to today
        const today = new Date().toISOString().split('T')[0];
        this.datePicker.value = today;

        // Set default year to current year
        const currentYear = new Date().getFullYear();
        this.yearTextField.value = currentYear.toString();

        // Get faithful info from URL parameters or session storage
        this.loadFaithfulFromContext();

        console.log('Add Donation Controller initialized');
    }

    private loadFaithfulFromContext(): void {
        // Method 1: Check URL parameters
        const urlParams = new URLSearchParams(window.location.search);
        const faithfulId = urlParams.get('faithfulId');
        const donationId = urlParams.get('donationId');

        if (faithfulId) {
            this.loadFaithful(parseInt(faithfulId));
        }

        if (donationId) {
            // Edit mode
            this.currentDonationId = parseInt(donationId);
            this.formTitleLabel.textContent = 'HINDURA ITURO';
            this.loadDonationForEdit(parseInt(donationId));
        }

        // Method 2: Check session storage (fallback)
        if (!faithfulId) {
            const storedFaithful = sessionStorage.getItem('selectedFaithful');
            if (storedFaithful) {
                this.selectedFaithful = JSON.parse(storedFaithful);
                this.displayFaithfulName();
            } else {
                this.showError('Nta mukristu wahisemo. Subira inyuma uhitemo.');
                this.saveContributionButton.disabled = true;
            }
        }
    }

    private async loadFaithful(faithfulId: number): Promise<void> {
        try {
            const response = await fetch(`${this.API_BASE}/faithfuls/${faithfulId}`);
            if (!response.ok) throw new Error('Failed to load faithful');

            this.selectedFaithful = await response.json();
            this.displayFaithfulName();
        } catch (error) {
            console.error('Error loading faithful:', error);
            this.showError('Ikosa mu gusoma umwirondoro w\'umukristu');
        }
    }

    private async loadDonationForEdit(donationId: number): Promise<void> {
        try {
            const response = await fetch(`${this.API_BASE}/donations/${donationId}`);
            if (!response.ok) throw new Error('Failed to load donation');

            const donation = await response.json();
            this.populateForm(donation);
        } catch (error) {
            console.error('Error loading donation:', error);
            this.showError('Ikosa mu gusoma ituro');
        }
    }

    private displayFaithfulName(): void {
        if (this.selectedFaithful) {
            this.faithfulNameLabel.textContent = this.selectedFaithful.name;
            this.faithfulNameLabel.classList.remove('text-gray-400');
            this.faithfulNameLabel.classList.add('text-gray-900');
        }
    }

    private populateForm(donation: any): void {
        this.amountTextField.value = donation.amount.toString();
        this.yearTextField.value = donation.year.toString();
        this.datePicker.value = donation.date;
        this.contributionType.value = donation.contributionType || 'TITHE';
        this.paymentMethod.value = donation.paymentMethod || 'CASH';
        this.notesTextArea.value = donation.notes || '';
    }

    // ==================== EVENT LISTENERS ====================
    private setupEventListeners(): void {
        // Form submission
        this.form.addEventListener('submit', (e) => this.handleSubmit(e));

        // Cancel button
        this.cancelButton.addEventListener('click', () => this.handleCancel());

        // Real-time validation
        this.amountTextField.addEventListener('input', () => this.validateAmount());
        this.yearTextField.addEventListener('input', () => this.validateYear());
        this.datePicker.addEventListener('change', () => this.validateDate());

        // Amount formatting on blur
        this.amountTextField.addEventListener('blur', () => this.formatAmount());
    }

    // ==================== VALIDATION ====================
    private validateAmount(): boolean {
        const value = parseFloat(this.amountTextField.value);

        if (!this.amountTextField.value || isNaN(value) || value <= 0) {
            this.showFieldError(this.amountTextField, 'Shyiramo amafaranga atanze');
            return false;
        }

        this.clearFieldError(this.amountTextField);
        return true;
    }

    private validateYear(): boolean {
        const value = parseInt(this.yearTextField.value);
        const currentYear = new Date().getFullYear();

        if (!this.yearTextField.value || isNaN(value)) {
            this.showFieldError(this.yearTextField, 'Shyiramo umwaka');
            return false;
        }

        if (value < 1900 || value > currentYear + 5) {
            this.showFieldError(this.yearTextField, `Umwaka ugomba kuba hagati ya 1900 na ${currentYear + 5}`);
            return false;
        }

        this.clearFieldError(this.yearTextField);
        return true;
    }

    private validateDate(): boolean {
        if (!this.datePicker.value) {
            this.showFieldError(this.datePicker, 'Shyiramo itariki yo gutanga');
            return false;
        }

        this.clearFieldError(this.datePicker);
        return true;
    }

    private validateForm(): boolean {
        let isValid = true;

        if (!this.validateAmount()) isValid = false;
        if (!this.validateYear()) isValid = false;
        if (!this.validateDate()) isValid = false;

        if (!this.selectedFaithful) {
            this.showError('Nta mukristu wahisemo');
            isValid = false;
        }

        return isValid;
    }

    private showFieldError(element: HTMLElement, message: string): void {
        // Remove existing error
        this.clearFieldError(element);

        // Add error styling
        element.classList.add('border-red-500', 'ring-2', 'ring-red-200');

        // Create error message
        const errorDiv = document.createElement('div');
        errorDiv.className = 'text-red-600 text-sm mt-1';
        errorDiv.textContent = message;
        errorDiv.id = `${element.id}-error`;

        // Insert after the element
        element.parentNode?.appendChild(errorDiv);
    }

    private clearFieldError(element: HTMLElement): void {
        element.classList.remove('border-red-500', 'ring-2', 'ring-red-200');

        const errorDiv = document.getElementById(`${element.id}-error`);
        if (errorDiv) {
            errorDiv.remove();
        }
    }

    private clearAllErrors(): void {
        const errorElements = document.querySelectorAll('[id$="-error"]');
        errorElements.forEach(el => el.remove());

        const inputs = [this.amountTextField, this.yearTextField, this.datePicker];
        inputs.forEach(input => {
            input.classList.remove('border-red-500', 'ring-2', 'ring-red-200');
        });
    }

    private formatAmount(): void {
        const value = this.amountTextField.value;
        if (value) {
            const number = parseFloat(value);
            if (!isNaN(number)) {
                // Format with thousand separators
                this.amountTextField.value = number.toLocaleString('en-US', {
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 2
                });
            }
        }
    }

    // ==================== FORM SUBMISSION ====================
    private async handleSubmit(event: Event): Promise<void> {
        event.preventDefault();

        // Clear previous status
        this.statusLabel.classList.add('hidden');
        this.statusLabel.textContent = '';

        // Validate form
        if (!this.validateForm()) {
            this.showError('Ugomba kuzuza ibisabwa byose');
            return;
        }

        // Prevent double submission
        if (this.isSubmitting) return;

        this.isSubmitting = true;
        this.saveContributionButton.disabled = true;
        this.saveContributionButton.innerHTML = 'BIKA... <span class="inline-block animate-spin">↻</span>';

        try {
            // Prepare request data
            const donationData: DonationRequestDTO = {
                faithfulId: this.selectedFaithful!.id,
                year: parseInt(this.yearTextField.value),
                amount: parseFloat(this.amountTextField.value.replace(/,/g, '')),
                date: this.datePicker.value,
                contributionType: this.contributionType.value,
                paymentMethod: this.paymentMethod.value,
                notes: this.notesTextArea.value.trim() || undefined
            };

            // Send to API
            let response;
            if (this.currentDonationId) {
                // Update existing donation
                response = await fetch(`${this.API_BASE}/donations/${this.currentDonationId}`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(donationData)
                });
            } else {
                // Create new donation
                response = await fetch(`${this.API_BASE}/donations`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(donationData)
                });
            }

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.error || 'Failed to save donation');
            }

            const result = await response.json();

            // Show success message
            const message = this.currentDonationId
                ? `Ituro rya ${this.selectedFaithful!.name} rw'umwaka ${donationData.year} ryahinduwe neza`
                : `Ituro rya ${this.selectedFaithful!.name} rw'umwaka ${donationData.year} ryanditswe neza`;

            this.showSuccess(message);

            // Clear form after short delay
            setTimeout(() => {
                if (!this.currentDonationId) {
                    this.clearForm();
                }
            }, 2000);

            // Redirect after 3 seconds
            setTimeout(() => {
                this.handleCancel();
            }, 3000);

        } catch (error) {
            console.error('Submission error:', error);
            this.showError('Ikosa mu kohereza: ' + (error as Error).message);
        } finally {
            this.isSubmitting = false;
            this.saveContributionButton.disabled = false;
            this.saveContributionButton.textContent = 'Bika';
        }
    }

    // ==================== NAVIGATION ====================
    private handleCancel(): void {
        if (confirm('Ushaka guhagarika? Ibyanditswe byose birasibwa.')) {
            // Clear session storage
            sessionStorage.removeItem('selectedFaithful');

            // Go back to previous page
            window.history.back();
        }
    }

    // ==================== UTILITY METHODS ====================
    private clearForm(): void {
        this.amountTextField.value = '';
        this.yearTextField.value = new Date().getFullYear().toString();
        this.datePicker.value = new Date().toISOString().split('T')[0];
        this.contributionType.value = 'TITHE';
        this.paymentMethod.value = 'CASH';
        this.notesTextArea.value = '';
        this.clearAllErrors();
        this.statusLabel.classList.add('hidden');
    }

    private showSuccess(message: string): void {
        this.statusLabel.textContent = message;
        this.statusLabel.className = 'text-center mt-4 font-bold p-3 rounded bg-green-100 text-green-700 border border-green-300';
        this.statusLabel.classList.remove('hidden');
    }

    private showError(message: string): void {
        this.statusLabel.textContent = message;
        this.statusLabel.className = 'text-center mt-4 font-bold p-3 rounded bg-red-100 text-red-700 border border-red-300';
        this.statusLabel.classList.remove('hidden');
    }
}

// ==================== INITIALIZATION ====================
document.addEventListener('DOMContentLoaded', () => {
    new AddDonationController();
    console.log('Add Donation Form initialized');
});

export { AddDonationController };