import './styles.css';
// ==========================================
// CONSTANTS
// ==========================================
/**
 * Base URL for all API endpoints - adjust for production
 */
const API_BASE_URL = 'http://localhost:8080';
// ==========================================
// UTILITY FUNCTIONS
// ==========================================
/**
 * Generic helper to get element by ID with proper TypeScript typing
 */
function getElement(id) {
    return document.getElementById(id);
}
/**
 * Checks if current page is the faithful management page
 */
function isFaithfulPage() {
    return window.location.pathname === '/faithful' || window.location.pathname.includes('faithful');
}
/**
 * Checks if current page is the home page
 */
function isHomePage() {
    return window.location.pathname === '/' || window.location.pathname === '/index.html';
}
// ==========================================
// FORM LOGIC FUNCTIONS
// ==========================================
/**
 * Toggles visibility and requirements for ordination date inputs
 * Enforces hierarchical sequence: Episcopate requires Priesthood requires Diaconate
 */
export function toggleDateInputs() {
    // Get checkbox elements for each ordination level
    const diaconateBox = getElement('diaconate');
    const priesthoodBox = getElement('priesthood');
    const episcopateBox = getElement('episcopate');
    // Get container groups for date inputs
    const dateDiaconateGroup = getElement('date_diaconate_group');
    const datePriesthoodGroup = getElement('date_priesthood_group');
    const dateEpiscopateGroup = getElement('date_episcopate_group');
    // Get actual date input elements
    const dateDiaconate = getElement('date_diaconate');
    const datePriesthood = getElement('date_priesthood');
    const dateEpiscopate = getElement('date_episcopate');
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
    }
    else if (!episcopateBox.checked) {
        dateEpiscopateGroup.style.display = 'none';
        dateEpiscopate.required = false;
        dateEpiscopate.value = '';
    }
    // Priesthood requires Diaconate
    if (priesthoodBox.checked && !diaconateBox.checked) {
        diaconateBox.checked = true;
    }
    else if (!priesthoodBox.checked) {
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
export function toggleProfessionInputs() {
    const temporalBox = getElement('temporal_prof');
    const permanentBox = getElement('permanent_prof');
    const dateTemporalGroup = getElement('date_temporal_prof_group');
    const datePermanentGroup = getElement('date_permanent_prof_group');
    const dateTemporalInput = getElement('date_temporal_prof');
    const datePermanentInput = getElement('date_permanent_prof');
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
    }
    else {
        dateTemporalGroup.style.display = 'none';
        dateTemporalInput.required = false;
        dateTemporalInput.value = '';
        permanentBox.checked = false; // Ensure permanent is off if temporal is cleared
    }
    // Permanent Profession
    if (permanentBox.checked) {
        datePermanentGroup.style.display = 'block';
        datePermanentInput.required = true;
    }
    else {
        datePermanentGroup.style.display = 'none';
        datePermanentInput.required = false;
        datePermanentInput.value = '';
    }
}
/**
 * Shows/hides relocation details based on checkbox state
 */
export function toggleRelocationDetails() {
    const relocatedBox = getElement('relocated_status');
    const parishGroup = getElement('new_parish_group');
    const parishInput = getElement('new_parish_name');
    if (!relocatedBox || !parishGroup || !parishInput) {
        console.error("Missing elements for toggleRelocationDetails");
        return;
    }
    const isRelocated = relocatedBox.checked;
    // Toggle visibility and requirements
    if (isRelocated) {
        parishGroup.style.display = 'block';
        parishInput.required = true;
    }
    else {
        parishGroup.style.display = 'none';
        parishInput.required = false;
        parishInput.value = '';
    }
}
/**
 * Shows/hides death date input based on deceased status
 */
export function toggleDeathDate() {
    const deceasedBox = getElement('faithful_deceased');
    const dateGroup = getElement('death_date_group');
    const dateInput = getElement('date_of_death');
    if (!deceasedBox || !dateGroup || !dateInput) {
        console.error("Missing elements for toggleDeathDate");
        return;
    }
    const isDeceased = deceasedBox.checked;
    // Toggle visibility and requirements
    if (isDeceased) {
        dateGroup.style.display = 'block';
        dateInput.required = true;
    }
    else {
        dateGroup.style.display = 'none';
        dateInput.required = false;
        dateInput.value = '';
    }
}
// Global counter for generating unique IDs for dynamic lapse entries
let lapseCounter = 0;
/**
 * Adds a new lapse entry to the form using the template
 */
export function addLapseEntry() {
    const template = getElement('lapse_template');
    const container = getElement('lapses_container');
    if (!template || !container || !template.firstElementChild) {
        console.error("Missing lapse template or container elements");
        return;
    }
    // Clone the template and generate unique IDs
    const newEntry = template.firstElementChild.cloneNode(true);
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
export function removeLapseEntry(buttonElement) {
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
async function fetchFromAPI(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    }
    catch (error) {
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
async function fetchWithParams(endpoint, params) {
    try {
        const queryString = new URLSearchParams(params).toString();
        const url = `${API_BASE_URL}${endpoint}?${queryString}`;
        const response = await fetch(url);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        return data;
    }
    catch (error) {
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
async function postToAPI(endpoint, data) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.json();
        return result;
    }
    catch (error) {
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
async function deleteFromAPI(endpoint) {
    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, {
            method: 'DELETE'
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const result = await response.json();
        return result;
    }
    catch (error) {
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
async function createFaithful(faithfulData) {
    try {
        const response = await postToAPI('/api/faithful', faithfulData);
        if (response.status === 'success' && response.data) {
            showFormSuccess('Faithful created successfully!');
            document.getElementById('faithful-form').reset();
            loadAllFaithful();
        }
        else {
            showFormError(response.message || 'Failed to create faithful');
        }
    }
    catch (error) {
        showFormError('Error creating faithful: ' + (error instanceof Error ? error.message : 'Unknown error'));
    }
}
/**
 * Loads all faithful records and displays them
 */
async function loadAllFaithful() {
    // Only run on faithful management page
    if (!isFaithfulPage())
        return;
    try {
        const response = await fetchFromAPI('/api/faithful');
        if (response.status === 'success' && response.data) {
            displayFaithfulList(response.data);
        }
        else {
            const container = document.getElementById('faithful-list');
            if (container) {
                container.innerHTML = '<p class="text-gray-500">Error loading faithful records</p>';
            }
        }
    }
    catch (error) {
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
async function searchFaithful() {
    // Only run on faithful management page
    if (!isFaithfulPage())
        return;
    const searchInput = document.getElementById('search-input');
    const searchTerm = searchInput.value.trim();
    if (!searchTerm) {
        alert('Please enter a name to search');
        return;
    }
    try {
        const response = await fetchWithParams('/api/faithful/search/name', { name: searchTerm });
        if (response.status === 'success' && response.data) {
            displayFaithfulList(response.data);
        }
        else {
            const container = document.getElementById('faithful-list');
            if (container) {
                container.innerHTML = '<p class="text-gray-500">No faithful found with that name</p>';
            }
        }
    }
    catch (error) {
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
function displayFaithfulList(faithfulArray) {
    const container = document.getElementById('faithful-list');
    if (!container)
        return;
    if (!faithfulArray || faithfulArray.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-center py-4">No faithful records found</p>';
        return;
    }
    const html = faithfulArray.map(faithful => `
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
function viewFaithful(id) {
    alert('View faithful details for ID: ' + id);
    // TODO: Implement detailed view modal or page navigation
}
/**
 * Deletes a faithful record after confirmation
 */
async function deleteFaithful(id) {
    if (!confirm('Are you sure you want to delete this faithful record?')) {
        return;
    }
    try {
        const response = await deleteFromAPI(`/api/faithful/${id}`);
        if (response.status === 'success') {
            alert('Faithful record deleted successfully');
            loadAllFaithful();
        }
        else {
            alert('Error deleting faithful: ' + response.message);
        }
    }
    catch (error) {
        alert('Error deleting faithful: ' + (error instanceof Error ? error.message : 'Unknown error'));
    }
}
/**
 * Shows success message in the form
 */
function showFormSuccess(message) {
    const successElement = document.getElementById('form-success');
    const successText = document.getElementById('form-success-text');
    const errorElement = document.getElementById('form-error');
    if (successElement && successText && errorElement) {
        successText.textContent = message;
        successElement.style.display = 'block';
        errorElement.style.display = 'none';
    }
}
/**
 * Shows error message in the form
 */
function showFormError(message) {
    const errorElement = document.getElementById('form-error');
    const errorText = document.getElementById('form-error-text');
    const successElement = document.getElementById('form-success');
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
async function loadWelcomeMessage() {
    const welcomeElement = document.getElementById('welcome-message');
    if (!welcomeElement)
        return;
    const response = await fetchFromAPI('/api/hello');
    if (response.status === 'success' && response.data) {
        welcomeElement.textContent = response.data.welcomeMessage;
    }
    else {
        welcomeElement.textContent = 'Failed to load welcome message';
    }
}
/**
 * Handles greeting form submission
 */
async function handleGreetingSubmit(event) {
    event.preventDefault();
    const nameInput = document.getElementById('name-input');
    const name = nameInput.value.trim();
    if (!name) {
        showError('Please enter your name');
        return;
    }
    hideGreeting();
    hideError();
    const response = await fetchWithParams('/api/hello/greet', { name: name });
    if (response.status === 'success' && response.data) {
        showGreeting(response.data.greeting);
    }
    else {
        showError(response.message || 'Failed to get greeting');
    }
}
/**
 * Displays personalized greeting
 */
function showGreeting(message) {
    const greetingResponse = document.getElementById('greeting-response');
    const greetingText = document.getElementById('greeting-text');
    if (!greetingResponse || !greetingText)
        return;
    greetingText.textContent = message;
    greetingResponse.style.display = 'block';
}
/**
 * Hides the greeting display
 */
function hideGreeting() {
    const greetingResponse = document.getElementById('greeting-response');
    if (greetingResponse) {
        greetingResponse.style.display = 'none';
    }
}
/**
 * Shows error message on home page
 */
function showError(message) {
    const errorMessage = document.getElementById('error-message');
    const errorText = document.getElementById('error-text');
    if (!errorMessage || !errorText)
        return;
    errorText.textContent = message;
    errorMessage.style.display = 'block';
}
/**
 * Hides error message on home page
 */
function hideError() {
    const errorMessage = document.getElementById('error-message');
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
function collectLapseHistory(formData) {
    // Collect all arrays for the dynamic fields using .getAll()
    const lapseDates = formData.getAll('lapseDate[]');
    const lapseTypes = formData.getAll('lapseType[]');
    const lapseReasons = formData.getAll('lapseReason[]');
    const returnDates = formData.getAll('returnDate[]');
    const count = lapseTypes.length; // All arrays should have the same length
    const lapseEvents = [];
    for (let i = 0; i < count; i++) {
        // Only collect entries that have a selected type (non-empty)
        if (lapseTypes[i]) {
            lapseEvents.push({
                lapseType: lapseTypes[i],
                lapseDate: lapseDates[i] || undefined,
                lapseReason: lapseReasons[i] || undefined,
                returnDate: returnDates[i] || undefined,
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
function initializeHomePage() {
    console.log('Initializing Home Page...');
    loadWelcomeMessage();
    const greetingForm = document.getElementById('greeting-form');
    if (greetingForm) {
        greetingForm.addEventListener('submit', handleGreetingSubmit);
    }
}
/**
 * Initializes faithful management page with all event listeners
 */
function initializeFaithfulPage() {
    console.log('Initializing Faithful Management Page...');
    // Form submission handler
    const faithfulForm = getElement('faithful-form');
    if (faithfulForm) {
        faithfulForm.addEventListener('submit', (event) => {
            event.preventDefault();
            const formData = new FormData(faithfulForm);
            // Construct the data object from form inputs
            const faithfulData = {
                // BASIC INFO
                firstname: formData.get('firstname'),
                name: formData.get('name'),
                fatherName: formData.get('fatherName'),
                motherName: formData.get('motherName'),
                godparentName: formData.get('godparentName') || undefined,
                baptismMinister: formData.get('baptismMinister') || undefined,
                // DATES & SACRAMENTS
                dateOfBirth: formData.get('dateOfBirth') || undefined,
                dateOfBaptism: formData.get('dateOfBaptism') || undefined,
                baptismId: formData.get('baptismId') || undefined,
                dateOfFirstCommunion: formData.get('dateOfFirstCommunion') || undefined,
                dateOfConfirmation: formData.get('dateOfConfirmation') || undefined,
                confirmationId: formData.get('confirmationId') || undefined,
                dateOfMatrimony: formData.get('dateOfMatrimony') || undefined,
                matrimonyId: formData.get('matrimonyId') || undefined,
                spouseName: formData.get('spouseName') || undefined,
                spouseBaptismId: formData.get('spouseBaptismId') || undefined,
                // ORDINATION
                level_diaconate: formData.get('level_diaconate') || undefined,
                date_diaconate: formData.get('date_diaconate') || undefined,
                level_priesthood: formData.get('level_priesthood') || undefined,
                date_priesthood: formData.get('date_priesthood') || undefined,
                level_episcopate: formData.get('level_episcopate') || undefined,
                date_episcopate: formData.get('date_episcopate') || undefined,
                // RELIGIOUS PROFESSION
                congregationName: formData.get('congregationName') || undefined,
                hasTemporalProfession: formData.get('hasTemporalProfession') || undefined,
                dateTemporalProfession: formData.get('dateTemporalProfession') || undefined,
                hasPermanentProfession: formData.get('hasPermanentProfession') || undefined,
                datePermanentProfession: formData.get('datePermanentProfession') || undefined,
                // RELOCATION & DEATH
                hasRelocated: formData.get('hasRelocated') || undefined,
                newParishName: formData.get('newParishName') || undefined,
                isDeceased: formData.get('isDeceased') || undefined,
                dateOfDeath: formData.get('dateOfDeath') || undefined,
                // CHURCH TERRITORY ADDRESS
                diocese: formData.get('diocese') || undefined,
                parish: formData.get('parish') || undefined,
                subparish: formData.get('subparish') || undefined,
                basicEcclesialCommunity: formData.get('basicEcclesialCommunity') || undefined,
                // ARRAY FIELDS
                ministry: formData.getAll('ministry').map(val => val),
                otherMinistryDetails: formData.get('otherMinistryDetails') || undefined,
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
    const searchButton = document.getElementById('search-button');
    if (searchButton) {
        searchButton.addEventListener('click', searchFaithful);
    }
    const loadAllButton = document.getElementById('load-all-button');
    if (loadAllButton) {
        loadAllButton.addEventListener('click', loadAllFaithful);
    }
    // Form field event listeners
    // Ordinations
    getElement('diaconate')?.addEventListener('change', toggleDateInputs);
    getElement('priesthood')?.addEventListener('change', toggleDateInputs);
    getElement('episcopate')?.addEventListener('change', toggleDateInputs);
    // Professions
    getElement('temporal_prof')?.addEventListener('change', toggleProfessionInputs);
    getElement('permanent_prof')?.addEventListener('change', toggleProfessionInputs);
    // Relocation
    getElement('relocated_status')?.addEventListener('change', toggleRelocationDetails);
    // Deceased Status
    getElement('faithful_deceased')?.addEventListener('change', toggleDeathDate);
    // Lapse management
    getElement('add_lapse_button')?.addEventListener('click', addLapseEntry);
    // Initialize all conditional fields to be hidden on page load
    initializeConditionalFields();
    // Load initial data
    loadAllFaithful();
}
/**
 * Initializes all conditional form fields to be hidden by default
 */
function initializeConditionalFields() {
    console.log('Initializing conditional fields...');
    // Hide all ordination date groups by default
    const dateDiaconateGroup = getElement('date_diaconate_group');
    const datePriesthoodGroup = getElement('date_priesthood_group');
    const dateEpiscopateGroup = getElement('date_episcopate_group');
    if (dateDiaconateGroup)
        dateDiaconateGroup.style.display = 'none';
    if (datePriesthoodGroup)
        datePriesthoodGroup.style.display = 'none';
    if (dateEpiscopateGroup)
        dateEpiscopateGroup.style.display = 'none';
    // Hide all religious profession date groups by default
    const dateTemporalProfGroup = getElement('date_temporal_prof_group');
    const datePermanentProfGroup = getElement('date_permanent_prof_group');
    if (dateTemporalProfGroup)
        dateTemporalProfGroup.style.display = 'none';
    if (datePermanentProfGroup)
        datePermanentProfGroup.style.display = 'none';
    // Hide relocation parish group by default
    const newParishGroup = getElement('new_parish_group');
    if (newParishGroup)
        newParishGroup.style.display = 'none';
    // Hide death date group by default
    const deathDateGroup = getElement('death_date_group');
    if (deathDateGroup)
        deathDateGroup.style.display = 'none';
    // Remove required attributes from hidden fields initially
    const dateDiaconate = getElement('date_diaconate');
    const datePriesthood = getElement('date_priesthood');
    const dateEpiscopate = getElement('date_episcopate');
    const dateTemporalProf = getElement('date_temporal_prof');
    const datePermanentProf = getElement('date_permanent_prof');
    const newParishName = getElement('new_parish_name');
    const dateOfDeath = getElement('date_of_death');
    if (dateDiaconate)
        dateDiaconate.required = false;
    if (datePriesthood)
        datePriesthood.required = false;
    if (dateEpiscopate)
        dateEpiscopate.required = false;
    if (dateTemporalProf)
        dateTemporalProf.required = false;
    if (datePermanentProf)
        datePermanentProf.required = false;
    if (newParishName)
        newParishName.required = false;
    if (dateOfDeath)
        dateOfDeath.required = false;
    console.log('Conditional fields initialized and hidden');
}
// ==========================================
// MAIN INITIALIZATION
// ==========================================
/**
 * Main application initialization function
 * Routes to appropriate page-specific initialization
 */
function initializeApp() {
    console.log('Initializing Parish Management System...');
    if (isHomePage()) {
        initializeHomePage();
    }
    else if (isFaithfulPage()) {
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
window.searchFaithful = searchFaithful;
window.loadAllFaithful = loadAllFaithful;
window.viewFaithful = viewFaithful;
window.deleteFaithful = deleteFaithful;
window.addLapseEntry = addLapseEntry;
window.removeLapseEntry = removeLapseEntry;
// ==========================================
// APPLICATION START
// ==========================================
/**
 * Start the application when DOM is fully loaded
 */
document.addEventListener('DOMContentLoaded', initializeApp);
