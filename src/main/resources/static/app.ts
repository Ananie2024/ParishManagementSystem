import './styles.css';

// ==========================================
// INTERFACES - DATA SHAPES
// ==========================================

/**
 * Generic API response interface for consistent error handling
 */
interface ApiResponse<T> {
    status: 'success' | 'error';
    message?: string;
    data?: T;
}

/**
 * Welcome message response from the home page API
 */
interface WelcomeResponse {
    welcomeMessage: string;
}

/**
 * Greeting response for personalized welcome messages
 */
interface GreetingResponse {
    greeting: string;
    name: string;
}

/**
 * Complete faithful record as returned from the API
 */
interface FaithfulSacramentInfo {
    // Personal Information
    firstname: string;
    name: string;
    fatherName?: string;
    motherName: string;
    godparentName: string;
    dateOfBirth: string;

    // Address Information
    diocese: string;
    parish: string;
    subparish: string;
    basicEcclesialCommunity: string;

    // Baptism
    dateOfBaptism: string;
    baptismId: string;
    baptismMinister: string;

    // First Communion
    dateOfFirstCommunion?: string;

    // Confirmation
    dateOfConfirmation?: string;
    confirmationId?: string;

    // Matrimony
    dateOfMatrimony?: string;
    matrimonyId?: string;
    spouseName?: string;
    spouseBaptismId?: string;
}

/**
 * Data structure for creating new faithful records via form submission
 */
interface CreateFaithfulRequest {
    // --- BASIC PERSONAL INFO (Form Grid) ---
    firstname: string; // From id="firstname"
    name: string;      // From id="name"
    fatherName: string;
    motherName: string;
    godparentName?: string;

    // --- DATES & SACRAMENT IDs ---
    dateOfBirth?: string;
    dateOfBaptism?: string;
    baptismId?: string;
    dateOfFirstCommunion?: string;
    dateOfConfirmation?: string;
    confirmationId?: string;
    dateOfMatrimony?: string;
    matrimonyId?: string;
    spouseName?: string;
    spouseBaptismId?: string;
    baptismMinister?: string;

    // --- ORDINATION (Priesthood/Deaconate) ---
    level_diaconate?: string;   // Checkbox value "true"
    date_diaconate?: string;
    level_priesthood?: string;  // Checkbox value "true"
    date_priesthood?: string;
    level_episcopate?: string;  // Checkbox value "true"
    date_episcopate?: string;

    // --- RELIGIOUS PROFESSION ---
    congregationName?: string;
    hasTemporalProfession?: string; // Checkbox value "true"
    dateTemporalProfession?: string;
    hasPermanentProfession?: string; // Checkbox value "true"
    datePermanentProfession?: string;

    // --- MINISTRY / SERVICE ---
    // Note: Backend maps multiple 'name="ministry"' inputs to List<String>
    ministry?: string[];
    otherMinistryDetails?: string;

    // --- LAPSE HISTORY ---
    // Complex data structure for tracking periods of inactivity
    lapseHistory?: LapseEvent[];

    // --- RELOCATION & DEATH STATUS ---
    hasRelocated?: string; // Checkbox value "true"
    newParishName?: string;
    isDeceased?: string; // Checkbox value "true"
    dateOfDeath?: string;

    // --- CHURCH TERRITORY ADDRESS ---
    diocese?: string;
    parish?: string;
    subparish?: string;
    basicEcclesialCommunity?: string;
}
/**
 * Complete Faithful entity as returned from the API
 */
interface Faithful {
    id: number;

    // Basic Personal Info
    firstname?: string;
    name: string;
    fatherName?: string;
    motherName?: string;
    godparentName?: string;

    // Dates & Sacrament IDs
    dateOfBirth?: string;
    dateOfBaptism?: string;
    baptismId?: string;
    baptismMinister?: string;
    dateOfFirstCommunion?: string;
    dateOfConfirmation?: string;
    confirmationId?: string;
    dateOfMatrimony?: string;
    matrimonyId?: string;
    spouseName?: string;
    spouseBaptismId?: string;

    // Ordination
    level_diaconate?: string;
    date_diaconate?: string;
    level_priesthood?: string;
    date_priesthood?: string;
    level_episcopate?: string;
    date_episcopate?: string;

    // Religious Profession
    congregationName?: string;
    hasTemporalProfession?: string;
    dateTemporalProfession?: string;
    hasPermanentProfession?: string;
    datePermanentProfession?: string;

    // Ministry
    ministries?: Ministry[];
    otherMinistryDetails?: string;

    // Lapse History
    lapseEvents?: LapseEvent[];

    // Relocation & Death Status
    hasRelocated?: string;
    newParishName?: string;
    isDeceased?: string;
    dateOfDeath?: string;

    // Church Territory Address
    diocese?: string;
    parish?: string;
    subparish?: string;
    basicEcclesialCommunity?: string;

    // Metadata
    createdAt?: string;
    updatedAt?: string;
}

/**
 * Ministry entity - matches backend Ministry.java
 */
interface Ministry {
    id?: number;
    ministryType: string;
}
/**
 * Individual lapse event for tracking periods of inactivity from the church
 * LapseEvent entity - matches backend LapseEvent.java
 */
interface LapseEvent {
    id?: number;
    lapseType: string;
    lapseDate?: string;
    lapseReason?: string;
    returnDate?: string;
}

// ==========================================
// CONSTANTS
// ==========================================

/**
 * Base URL for all API endpoints - adjust for production
 */
const API_BASE_URL: string = 'http://localhost:8080';

// ==========================================
// UTILITY FUNCTIONS
// ==========================================

/**
 * Generic helper to get element by ID with proper TypeScript typing
 */
function getElement<T extends HTMLElement>(id: string): T | null {
    return document.getElementById(id) as T | null;
}

/**
 * Checks if current page is the faithful management page
 */
function isFaithfulPage(): boolean {
    return window.location.pathname === '/faithful' || window.location.pathname.includes('faithful');
}

/**
 * Checks if current page is the home page
 */
function isHomePage(): boolean {
    return window.location.pathname === '/' || window.location.pathname === '/index.html';
}

// ==========================================
// FORM LOGIC FUNCTIONS
// ==========================================

/**
 * Toggles visibility and requirements for ordination date inputs
 * Enforces hierarchical sequence: Episcopate requires Priesthood requires Diaconate
 */
export function toggleDateInputs(): void {
    // Get checkbox elements for each ordination level
    const diaconateBox = getElement<HTMLInputElement>('diaconate');
    const priesthoodBox = getElement<HTMLInputElement>('priesthood');
    const episcopateBox = getElement<HTMLInputElement>('episcopate');

    // Get container groups for date inputs
    const dateDiaconateGroup = getElement<HTMLElement>('date_diaconate_group');
    const datePriesthoodGroup = getElement<HTMLElement>('date_priesthood_group');
    const dateEpiscopateGroup = getElement<HTMLElement>('date_episcopate_group');

    // Get actual date input elements
    const dateDiaconate = getElement<HTMLInputElement>('date_diaconate');
    const datePriesthood = getElement<HTMLInputElement>('date_priesthood');
    const dateEpiscopate = getElement<HTMLInputElement>('date_episcopate');

    // Validate all elements exist before proceeding
    if (!diaconateBox || !priesthoodBox || !episcopateBox ||
        !dateDiaconateGroup || !datePriesthoodGroup || !dateEpiscopateGroup ||
        !dateDiaconate || !datePriesthood || !dateEpiscopate) {
        console.error("Missing elements for toggleDateInputs");
        return;
    }

    // --- ENFORCE HIERARCHICAL SEQUENCE ---

    // Episcopate requires Priesthood
    if (episcopateBox.checked && !priesthoodBox.checked) {
        priesthoodBox.checked = true;
    } else if (!episcopateBox.checked) {
        dateEpiscopateGroup.style.display = 'none';
        dateEpiscopate.required = false;
        dateEpiscopate.value = '';
    }

    // Priesthood requires Diaconate
    if (priesthoodBox.checked && !diaconateBox.checked) {
        diaconateBox.checked = true;
    } else if (!priesthoodBox.checked) {
        episcopateBox.checked = false;
        dateEpiscopateGroup.style.display = 'none';
        dateEpiscopate.required = false;
        dateEpiscopate.value = '';

        datePriesthoodGroup.style.display = 'none';
        datePriesthood.required = false;
        datePriesthood.value = '';
    }

    // Clear higher levels if diaconate is unchecked
    if (!diaconateBox.checked) {
        priesthoodBox.checked = false;
        episcopateBox.checked = false;

        dateDiaconateGroup.style.display = 'none';
        dateDiaconate.required = false;
        dateDiaconate.value = '';
    }

    // --- UPDATE VISIBILITY AND REQUIREMENTS BASED ON CURRENT STATE ---
    dateDiaconateGroup.style.display = diaconateBox.checked ? 'block' : 'none';
    dateDiaconate.required = diaconateBox.checked;

    datePriesthoodGroup.style.display = priesthoodBox.checked ? 'block' : 'none';
    datePriesthood.required = priesthoodBox.checked;

    dateEpiscopateGroup.style.display = episcopateBox.checked ? 'block' : 'none';
    dateEpiscopate.required = episcopateBox.checked;
}

/**
 * Toggles religious profession date inputs and enforces sequence
 * Permanent profession requires temporal profession
 */
export function toggleProfessionInputs(): void {
    const temporalBox = getElement<HTMLInputElement>('temporal_prof');
    const permanentBox = getElement<HTMLInputElement>('permanent_prof');

    const dateTemporalGroup = getElement<HTMLElement>('date_temporal_prof_group');
    const datePermanentGroup = getElement<HTMLElement>('date_permanent_prof_group');

    const dateTemporalInput = getElement<HTMLInputElement>('date_temporal_prof');
    const datePermanentInput = getElement<HTMLInputElement>('date_permanent_prof');

    if (!temporalBox || !permanentBox || !dateTemporalGroup || !datePermanentGroup || !dateTemporalInput || !datePermanentInput) {
        console.error("Missing elements for toggleProfessionInputs");
        return;
    }

    // --- ENFORCE SEQUENCE: Permanent requires Temporal ---
    if (permanentBox.checked && !temporalBox.checked) {
        temporalBox.checked = true;
    }

    if (!temporalBox.checked) {
        permanentBox.checked = false;
    }

    // --- UPDATE VISIBILITY AND REQUIREMENTS ---
    // Temporal Profession
    if (temporalBox.checked) {
        dateTemporalGroup.style.display = 'block';
        dateTemporalInput.required = true;
    } else {
        dateTemporalGroup.style.display = 'none';
        dateTemporalInput.required = false;
        dateTemporalInput.value = '';
        permanentBox.checked = false; // Ensure permanent is off if temporal is cleared
    }

    // Permanent Profession
    if (permanentBox.checked) {
        datePermanentGroup.style.display = 'block';
        datePermanentInput.required = true;
    } else {
        datePermanentGroup.style.display = 'none';
        datePermanentInput.required = false;
        datePermanentInput.value = '';
    }
}

/**
 * Shows/hides relocation details based on checkbox state
 */
export function toggleRelocationDetails(): void {
    const relocatedBox = getElement<HTMLInputElement>('relocated_status');
    const parishGroup = getElement<HTMLElement>('new_parish_group');
    const parishInput = getElement<HTMLInputElement>('new_parish_name');

    if (!relocatedBox || !parishGroup || !parishInput) {
        console.error("Missing elements for toggleRelocationDetails");
        return;
    }

    const isRelocated = relocatedBox.checked;

    // Toggle visibility and requirements
    if (isRelocated) {
        parishGroup.style.display = 'block';
        parishInput.required = true;
    } else {
        parishGroup.style.display = 'none';
        parishInput.required = false;
        parishInput.value = '';
    }
}

/**
 * Shows/hides death date input based on deceased status
 */
export function toggleDeathDate(): void {
    const deceasedBox = getElement<HTMLInputElement>('faithful_deceased');
    const dateGroup = getElement<HTMLElement>('death_date_group');
    const dateInput = getElement<HTMLInputElement>('date_of_death');

    if (!deceasedBox || !dateGroup || !dateInput) {
        console.error("Missing elements for toggleDeathDate");
        return;
    }

    const isDeceased = deceasedBox.checked;

    // Toggle visibility and requirements
    if (isDeceased) {
        dateGroup.style.display = 'block';
        dateInput.required = true;
    } else {
        dateGroup.style.display = 'none';
        dateInput.required = false;
        dateInput.value = '';
    }
}

// Global counter for generating unique IDs for dynamic lapse entries
let lapseCounter: number = 0;

/**
 * Adds a new lapse entry to the form using the template
 */
export function addLapseEntry(): void {
    const template = getElement<HTMLElement>('lapse_template');
    const container = getElement<HTMLElement>('lapses_container');

    if (!template || !container || !template.firstElementChild) {
        console.error("Missing lapse template or container elements");
        return;
    }

    // Clone the template and generate unique IDs
    const newEntry = template.firstElementChild.cloneNode(true) as HTMLElement;
    lapseCounter++;

    // Replace template placeholders with unique identifiers
    newEntry.innerHTML = newEntry.innerHTML.replace(/TEMPLATE/g, lapseCounter.toString());

    // Make the new entry visible
    newEntry.style.display = 'block';

    // Add to the container
    container.appendChild(newEntry);
}

/**
 * Removes a lapse entry from the form
 * @param buttonElement The remove button that was clicked
 */
export function removeLapseEntry(buttonElement: HTMLButtonElement): void {
    const entryDiv = buttonElement.closest('.lapse-entry');
    if (entryDiv) {
        entryDiv.remove();
    }
}

// ==========================================
// API FUNCTIONS
// ==========================================

/**
 * Generic GET request to API endpoints
 */
async function fetchFromAPI<T>(endpoint: string): Promise<ApiResponse<T>> {
    try {
        const response: Response = await fetch(`${API_BASE_URL}${endpoint}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data: ApiResponse<T> = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        return {
            status: 'error',
            message: error instanceof Error ? error.message : 'Unknown error occurred'
        };
    }
}

/**
 * GET request with query parameters
 */
async function fetchWithParams<T>(
    endpoint: string,
    params: Record<string, string>
): Promise<ApiResponse<T>> {
    try {
        const queryString: string = new URLSearchParams(params).toString();
        const url: string = `${API_BASE_URL}${endpoint}?${queryString}`;
        const response: Response = await fetch(url);

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data: ApiResponse<T> = await response.json();
        return data;
    } catch (error) {
        console.error('API Error:', error);
        return {
            status: 'error',
            message: error instanceof Error ? error.message : 'Unknown error occurred'
        };
    }
}

/**
 * POST request to create or update data
 */
async function postToAPI<T>(endpoint: string, data: any): Promise<ApiResponse<T>> {
    try {
        const response: Response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result: ApiResponse<T> = await response.json();
        return result;
    } catch (error) {
        console.error('API Error:', error);
        return {
            status: 'error',
            message: error instanceof Error ? error.message : 'Unknown error occurred'
        };
    }
}

/**
 * DELETE request to remove data
 */
async function deleteFromAPI<T>(endpoint: string): Promise<ApiResponse<T>> {
    try {
        const response: Response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE'
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const result: ApiResponse<T> = await response.json();
        return result;
    } catch (error) {
        console.error('API Error:', error);
        return {
            status: 'error',
            message: error instanceof Error ? error.message : 'Unknown error occurred'
        };
    }
}

// ==========================================
// FAITHFUL MANAGEMENT FUNCTIONS
// ==========================================

/**
 * Creates a new faithful record via API
 */
async function createFaithful(faithfulData: CreateFaithfulRequest): Promise<void> {
    try {
        const response: ApiResponse<Faithful> = await postToAPI<Faithful>('/api/faithful', faithfulData);

        if (response.status === 'success' && response.data) {
            showFormSuccess('Faithful created successfully!');
            (document.getElementById('faithful-form') as HTMLFormElement).reset();
            loadAllFaithful();
        } else {
            showFormError(response.message || 'Failed to create faithful');
        }
    } catch (error) {
        showFormError('Error creating faithful: ' + (error instanceof Error ? error.message : 'Unknown error'));
    }
}

/**
 * Loads all faithful records and displays them
 */
async function loadAllFaithful(): Promise<void> {
    // Only run on faithful management page
    if (!isFaithfulPage()) return;

    try {
        const response: ApiResponse<Faithful[]> = await fetchFromAPI<Faithful[]>('/api/faithful');

        if (response.status === 'success' && response.data) {
            displayFaithfulList(response.data);
        } else {
            const container = document.getElementById('faithful-list');
            if (container) {
                container.innerHTML = '<p class="text-gray-500">Error loading faithful records</p>';
            }
        }
    } catch (error) {
        const container = document.getElementById('faithful-list');
        if (container) {
            container.innerHTML = '<p class="text-red-500">Error loading faithful records: ' +
                (error instanceof Error ? error.message : 'Unknown error') + '</p>';
        }
    }
}

/**
 * Searches faithful by name and displays results
 */
async function searchFaithful(): Promise<void> {
    // Only run on faithful management page
    if (!isFaithfulPage()) return;

    const searchInput = document.getElementById('search-input') as HTMLInputElement;
    const searchTerm: string = searchInput.value.trim();

    if (!searchTerm) {
        alert('Please enter a name to search');
        return;
    }

    try {
        const response: ApiResponse<Faithful[]> = await fetchWithParams<Faithful[]>('/api/faithful/search/name', { name: searchTerm });

        if (response.status === 'success' && response.data) {
            displayFaithfulList(response.data);
        } else {
            const container = document.getElementById('faithful-list');
            if (container) {
                container.innerHTML = '<p class="text-gray-500">No faithful found with that name</p>';
            }
        }
    } catch (error) {
        const container = document.getElementById('faithful-list');
        if (container) {
            container.innerHTML = '<p class="text-red-500">Error searching faithful: ' +
                (error instanceof Error ? error.message : 'Unknown error') + '</p>';
        }
    }
}

/**
 * Renders the list of faithful records in the UI
 */
function displayFaithfulList(faithfulArray: Faithful[]): void {
    const container: HTMLElement | null = document.getElementById('faithful-list');
    if (!container) return;

    if (!faithfulArray || faithfulArray.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-center py-4">No faithful records found</p>';
        return;
    }

    const html: string = faithfulArray.map(faithful => `
        <div class="faithful-card">
            <div class="faithful-header">
                <h3 class="faithful-name">${faithful.name}</h3>
                <div class="faithful-actions">
                    <button onclick="viewFaithful(${faithful.id})" class="btn btn-outline btn-sm">View</button>
                    <button onclick="deleteFaithful(${faithful.id})" class="btn btn-danger btn-sm">Delete</button>
                </div>
            </div>
            <div class="faithful-details">
                <div class="faithful-detail">
                    <span class="detail-label">Parents</span>
                    <span class="detail-value">${faithful.fatherName} & ${faithful.motherName}</span>
                </div>
                <div class="faithful-detail">
                    <span class="detail-label">Parish</span>
                    <span class="detail-value">${faithful.parish || 'Not specified'}</span>
                </div>
                <div class="faithful-detail">
                    <span class="detail-label">Baptism ID</span>
                    <span class="detail-value">${faithful.baptismId || 'Not specified'}</span>
                </div>
                <div class="faithful-detail">
                    <span class="detail-label">Date of Birth</span>
                    <span class="detail-value">${faithful.dateOfBirth || 'Not specified'}</span>
                </div>
            </div>
        </div>
    `).join('');

    container.innerHTML = html;
}

/**
 * Placeholder for viewing faithful details
 */
function viewFaithful(id: number): void {
    alert('View faithful details for ID: ' + id);
    // TODO: Implement detailed view modal or page navigation
}

/**
 * Deletes a faithful record after confirmation
 */
async function deleteFaithful(id: number): Promise<void> {
    if (!confirm('Are you sure you want to delete this faithful record?')) {
        return;
    }

    try {
        const response: ApiResponse<void> = await deleteFromAPI<void>(`/api/faithful/${id}`);

        if (response.status === 'success') {
            alert('Faithful record deleted successfully');
            loadAllFaithful();
        } else {
            alert('Error deleting faithful: ' + response.message);
        }
    } catch (error) {
        alert('Error deleting faithful: ' + (error instanceof Error ? error.message : 'Unknown error'));
    }
}

/**
 * Shows success message in the form
 */
function showFormSuccess(message: string): void {
    const successElement: HTMLElement | null = document.getElementById('form-success');
    const successText: HTMLElement | null = document.getElementById('form-success-text');
    const errorElement: HTMLElement | null = document.getElementById('form-error');

    if (successElement && successText && errorElement) {
        successText.textContent = message;
        successElement.style.display = 'block';
        errorElement.style.display = 'none';
    }
}

/**
 * Shows error message in the form
 */
function showFormError(message: string): void {
    const errorElement: HTMLElement | null = document.getElementById('form-error');
    const errorText: HTMLElement | null = document.getElementById('form-error-text');
    const successElement: HTMLElement | null = document.getElementById('form-success');

    if (errorElement && errorText && successElement) {
        errorText.textContent = message;
        errorElement.style.display = 'block';
        successElement.style.display = 'none';
    }
}

// ==========================================
// HOME PAGE FUNCTIONS
// ==========================================

/**
 * Loads and displays the welcome message from the API
 */
async function loadWelcomeMessage(): Promise<void> {
    const welcomeElement: HTMLElement | null = document.getElementById('welcome-message');
    if (!welcomeElement) return;

    const response: ApiResponse<WelcomeResponse> = await fetchFromAPI<WelcomeResponse>('/api/hello');

    if (response.status === 'success' && response.data) {
        welcomeElement.textContent = response.data.welcomeMessage;
    } else {
        welcomeElement.textContent = 'Failed to load welcome message';
    }
}

/**
 * Handles greeting form submission
 */
async function handleGreetingSubmit(event: Event): Promise<void> {
    event.preventDefault();

    const nameInput: HTMLInputElement = document.getElementById('name-input') as HTMLInputElement;
    const name: string = nameInput.value.trim();

    if (!name) {
        showError('Please enter your name');
        return;
    }

    hideGreeting();
    hideError();

    const response: ApiResponse<GreetingResponse> = await fetchWithParams<GreetingResponse>(
        '/api/hello/greet',
        { name: name }
    );

    if (response.status === 'success' && response.data) {
        showGreeting(response.data.greeting);
    } else {
        showError(response.message || 'Failed to get greeting');
    }
}

/**
 * Displays personalized greeting
 */
function showGreeting(message: string): void {
    const greetingResponse: HTMLElement | null = document.getElementById('greeting-response');
    const greetingText: HTMLElement | null = document.getElementById('greeting-text');

    if (!greetingResponse || !greetingText) return;

    greetingText.textContent = message;
    greetingResponse.style.display = 'block';
}

/**
 * Hides the greeting display
 */
function hideGreeting(): void {
    const greetingResponse: HTMLElement | null = document.getElementById('greeting-response');
    if (greetingResponse) {
        greetingResponse.style.display = 'none';
    }
}

/**
 * Shows error message on home page
 */
function showError(message: string): void {
    const errorMessage: HTMLElement | null = document.getElementById('error-message');
    const errorText: HTMLElement | null = document.getElementById('error-text');

    if (!errorMessage || !errorText) return;

    errorText.textContent = message;
    errorMessage.style.display = 'block';
}

/**
 * Hides error message on home page
 */
function hideError(): void {
    const errorMessage: HTMLElement | null = document.getElementById('error-message');
    if (errorMessage) {
        errorMessage.style.display = 'none';
    }
}

// ==========================================
// HELPER FUNCTIONS
// ==========================================

/**
 * Collects dynamic lapse history data from form arrays
 */
function collectLapseHistory(formData: FormData): LapseEvent[] {
    // Collect all arrays for the dynamic fields using .getAll()
    const lapseDates = formData.getAll('lapseDate[]');
    const lapseTypes = formData.getAll('lapseType[]');
    const lapseReasons = formData.getAll('lapseReason[]');
    const returnDates = formData.getAll('returnDate[]');

    const count = lapseTypes.length; // All arrays should have the same length

    const lapseEvents: LapseEvent[] = [];

    for (let i = 0; i < count; i++) {
        // Only collect entries that have a selected type (non-empty)
        if (lapseTypes[i]) {
            lapseEvents.push({
                lapseType: (lapseTypes[i] as string),
                lapseDate: (lapseDates[i] as string) || undefined,
                lapseReason: (lapseReasons[i] as string) || undefined,
                returnDate: (returnDates[i] as string) || undefined,
            });
        }
    }

    return lapseEvents;
}

// ==========================================
// PAGE-SPECIFIC INITIALIZATION
// ==========================================

/**
 * Initializes home page specific functionality
 */
function initializeHomePage(): void {
    console.log('Initializing Home Page...');
    loadWelcomeMessage();

    const greetingForm: HTMLElement | null = document.getElementById('greeting-form');
    if (greetingForm) {
        greetingForm.addEventListener('submit', handleGreetingSubmit);
    }
}

/**
 * Initializes faithful management page with all event listeners
 */
function initializeFaithfulPage(): void {
    console.log('Initializing Faithful Management Page...');

    // Form submission handler
    const faithfulForm = getElement<HTMLFormElement>('faithful-form');
    if (faithfulForm) {
        faithfulForm.addEventListener('submit', (event: Event) => {
            event.preventDefault();

            const formData: FormData = new FormData(faithfulForm);

            // Construct the data object from form inputs
            const faithfulData: CreateFaithfulRequest = {
                // BASIC INFO
                firstname: formData.get('firstname') as string,
                name: formData.get('name') as string,
                fatherName: formData.get('fatherName') as string,
                motherName: formData.get('motherName') as string,
                godparentName: (formData.get('godparentName') as string) || undefined,
                baptismMinister: (formData.get('baptismMinister') as string) || undefined,

                // DATES & SACRAMENTS
                dateOfBirth: (formData.get('dateOfBirth') as string) || undefined,
                dateOfBaptism: (formData.get('dateOfBaptism') as string) || undefined,
                baptismId: (formData.get('baptismId') as string) || undefined,
                dateOfFirstCommunion: (formData.get('dateOfFirstCommunion') as string) || undefined,
                dateOfConfirmation: (formData.get('dateOfConfirmation') as string) || undefined,
                confirmationId: (formData.get('confirmationId') as string) || undefined,
                dateOfMatrimony: (formData.get('dateOfMatrimony') as string) || undefined,
                matrimonyId: (formData.get('matrimonyId') as string) || undefined,
                spouseName: (formData.get('spouseName') as string) || undefined,
                spouseBaptismId: (formData.get('spouseBaptismId') as string) || undefined,

                // ORDINATION
                level_diaconate: (formData.get('level_diaconate') as string) || undefined,
                date_diaconate: (formData.get('date_diaconate') as string) || undefined,
                level_priesthood: (formData.get('level_priesthood') as string) || undefined,
                date_priesthood: (formData.get('date_priesthood') as string) || undefined,
                level_episcopate: (formData.get('level_episcopate') as string) || undefined,
                date_episcopate: (formData.get('date_episcopate') as string) || undefined,

                // RELIGIOUS PROFESSION
                congregationName: (formData.get('congregationName') as string) || undefined,
                hasTemporalProfession: (formData.get('hasTemporalProfession') as string) || undefined,
                dateTemporalProfession: (formData.get('dateTemporalProfession') as string) || undefined,
                hasPermanentProfession: (formData.get('hasPermanentProfession') as string) || undefined,
                datePermanentProfession: (formData.get('datePermanentProfession') as string) || undefined,

                // RELOCATION & DEATH
                hasRelocated: (formData.get('hasRelocated') as string) || undefined,
                newParishName: (formData.get('newParishName') as string) || undefined,
                isDeceased: (formData.get('isDeceased') as string) || undefined,
                dateOfDeath: (formData.get('dateOfDeath') as string) || undefined,

                // CHURCH TERRITORY ADDRESS
                diocese: (formData.get('diocese') as string) || undefined,
                parish: (formData.get('parish') as string) || undefined,
                subparish: (formData.get('subparish') as string) || undefined,
                basicEcclesialCommunity: (formData.get('basicEcclesialCommunity') as string) || undefined,

                // ARRAY FIELDS
                ministry: formData.getAll('ministry').map(val => val as string),
                otherMinistryDetails: (formData.get('otherMinistryDetails') as string) || undefined,

                // COMPLEX ARRAY FIELDS
                lapseHistory: collectLapseHistory(formData)
            };

            // Clean up empty arrays before sending to API
            if (faithfulData.ministry && faithfulData.ministry.length === 0) {
                delete faithfulData.ministry;
            }
            if (faithfulData.lapseHistory && faithfulData.lapseHistory.length === 0) {
                delete faithfulData.lapseHistory;
            }

            createFaithful(faithfulData);
        });
    }

    // Search and load buttons
    const searchButton: HTMLElement | null = document.getElementById('search-button');
    if (searchButton) {
        searchButton.addEventListener('click', searchFaithful);
    }

    const loadAllButton: HTMLElement | null = document.getElementById('load-all-button');
    if (loadAllButton) {
        loadAllButton.addEventListener('click', loadAllFaithful);
    }

    // Form field event listeners
    // Ordinations
    getElement<HTMLInputElement>('diaconate')?.addEventListener('change', toggleDateInputs);
    getElement<HTMLInputElement>('priesthood')?.addEventListener('change', toggleDateInputs);
    getElement<HTMLInputElement>('episcopate')?.addEventListener('change', toggleDateInputs);

    // Professions
    getElement<HTMLInputElement>('temporal_prof')?.addEventListener('change', toggleProfessionInputs);
    getElement<HTMLInputElement>('permanent_prof')?.addEventListener('change', toggleProfessionInputs);

    // Relocation
    getElement<HTMLInputElement>('relocated_status')?.addEventListener('change', toggleRelocationDetails);

    // Deceased Status
    getElement<HTMLInputElement>('faithful_deceased')?.addEventListener('change', toggleDeathDate);

    // Lapse management
    getElement<HTMLButtonElement>('add_lapse_button')?.addEventListener('click', addLapseEntry);

    // Initialize all conditional fields to be hidden on page load
    initializeConditionalFields();

    // Load initial data
    loadAllFaithful();
}

/**
 * Initializes all conditional form fields to be hidden by default
 */
function initializeConditionalFields(): void {
    console.log('Initializing conditional fields...');

    // Hide all ordination date groups by default
    const dateDiaconateGroup = getElement<HTMLElement>('date_diaconate_group');
    const datePriesthoodGroup = getElement<HTMLElement>('date_priesthood_group');
    const dateEpiscopateGroup = getElement<HTMLElement>('date_episcopate_group');

    if (dateDiaconateGroup) dateDiaconateGroup.style.display = 'none';
    if (datePriesthoodGroup) datePriesthoodGroup.style.display = 'none';
    if (dateEpiscopateGroup) dateEpiscopateGroup.style.display = 'none';

    // Hide all religious profession date groups by default
    const dateTemporalProfGroup = getElement<HTMLElement>('date_temporal_prof_group');
    const datePermanentProfGroup = getElement<HTMLElement>('date_permanent_prof_group');

    if (dateTemporalProfGroup) dateTemporalProfGroup.style.display = 'none';
    if (datePermanentProfGroup) datePermanentProfGroup.style.display = 'none';

    // Hide relocation parish group by default
    const newParishGroup = getElement<HTMLElement>('new_parish_group');
    if (newParishGroup) newParishGroup.style.display = 'none';

    // Hide death date group by default
    const deathDateGroup = getElement<HTMLElement>('death_date_group');
    if (deathDateGroup) deathDateGroup.style.display = 'none';

    // Remove required attributes from hidden fields initially
    const dateDiaconate = getElement<HTMLInputElement>('date_diaconate');
    const datePriesthood = getElement<HTMLInputElement>('date_priesthood');
    const dateEpiscopate = getElement<HTMLInputElement>('date_episcopate');
    const dateTemporalProf = getElement<HTMLInputElement>('date_temporal_prof');
    const datePermanentProf = getElement<HTMLInputElement>('date_permanent_prof');
    const newParishName = getElement<HTMLInputElement>('new_parish_name');
    const dateOfDeath = getElement<HTMLInputElement>('date_of_death');

    if (dateDiaconate) dateDiaconate.required = false;
    if (datePriesthood) datePriesthood.required = false;
    if (dateEpiscopate) dateEpiscopate.required = false;
    if (dateTemporalProf) dateTemporalProf.required = false;
    if (datePermanentProf) datePermanentProf.required = false;
    if (newParishName) newParishName.required = false;
    if (dateOfDeath) dateOfDeath.required = false;

    console.log('Conditional fields initialized and hidden');
}

// ==========================================
// MAIN INITIALIZATION
// ==========================================

/**
 * Main application initialization function
 * Routes to appropriate page-specific initialization
 */
function initializeApp(): void {
    console.log('Initializing Parish Management System...');

    if (isHomePage()) {
        initializeHomePage();
    } else if (isFaithfulPage()) {
        initializeFaithfulPage();
    }

    console.log('Application initialized successfully!');
}

// ==========================================
// GLOBAL FUNCTION EXPORTS
// ==========================================

/**
 * Export functions to global scope for HTML onclick handlers
 * This is necessary for functions called directly from HTML attributes
 */
(window as any).searchFaithful = searchFaithful;
(window as any).loadAllFaithful = loadAllFaithful;
(window as any).viewFaithful = viewFaithful;
(window as any).deleteFaithful = deleteFaithful;
(window as any).addLapseEntry = addLapseEntry;
(window as any).removeLapseEntry = removeLapseEntry;

// ==========================================
// APPLICATION START
// ==========================================

/**
 * Start the application when DOM is fully loaded
 */
document.addEventListener('DOMContentLoaded', initializeApp);