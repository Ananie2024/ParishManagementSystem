// ==========================================
// SACRAMENT INFO - TYPESCRIPT
// ==========================================

const API_BASE_URL = 'http://localhost:8080/api/faithful';

// --- INTERFACES ---
interface FaithfulSearchResult {
    id: number;
    firstname?: string;
    name: string;
    fatherName?: string;
    motherName?: string;
    godparentName?: string;
    dateOfBirth?: string;
    diocese?: string;
    parish?: string;
    subparish?: string;
    basicEcclesialCommunity?: string;
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
}

// FaithfulSacramentInfo uses the same structure as FaithfulSearchResult
type FaithfulSacramentInfo = FaithfulSearchResult;

// --- DOM ELEMENTS ---
const searchForm = document.getElementById('search-form') as HTMLFormElement;
const searchInput = document.getElementById('search-input') as HTMLInputElement;
const loadingEl = document.getElementById('loading') as HTMLDivElement;
const errorMessageEl = document.getElementById('error-message') as HTMLDivElement;
const errorTextEl = document.getElementById('error-text') as HTMLParagraphElement;
const searchResultsSection = document.getElementById('search-results-section') as HTMLElement;
const searchResultsEl = document.getElementById('search-results') as HTMLDivElement;
const sacramentInfoSection = document.getElementById('sacrament-info-section') as HTMLElement;

// --- UTILITY FUNCTIONS ---
function showLoading(): void {
    loadingEl.classList.remove('hidden');
    errorMessageEl.classList.add('hidden');
}

function hideLoading(): void {
    loadingEl.classList.add('hidden');
}

function showError(message: string): void {
    errorTextEl.textContent = message;
    errorMessageEl.classList.remove('hidden');
}

function hideError(): void {
    errorMessageEl.classList.add('hidden');
}

function formatDate(dateString?: string): string {
    if (!dateString) return '--';
    const date = new Date(dateString);
    return date.toLocaleDateString('rw-RW', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}

// --- API FUNCTIONS ---
async function searchFaithfulByName(name: string): Promise<FaithfulSearchResult[]> {
    const response = await fetch(`${API_BASE_URL}/search?name=${encodeURIComponent(name)}`);
    if (!response.ok) {
        throw new Error('Ishakiro ryanze. Ongera ugerageze.');
    }
    return response.json();
}

async function getFaithfulSacramentInfo(id: number): Promise<FaithfulSacramentInfo> {
    const response = await fetch(`${API_BASE_URL}/${id}/sacrament-info`);
    if (!response.ok) {
        throw new Error('Ntibyashobotse kubona amakuru. Ongera ugerageze.');
    }
    return response.json();
}

// --- RENDER FUNCTIONS ---
function renderSearchResults(results: FaithfulSearchResult[]): void {
    searchResultsEl.innerHTML = '';

    if (results.length === 0) {
        searchResultsEl.innerHTML = `
            <div class="response-box warning-box">
                <p class="response-text">Nta mukristu ubonetse. Ongera ugerageze izina ritandukanye.</p>
            </div>
        `;
        searchResultsSection.classList.remove('hidden');
        return;
    }

    results.forEach(faithful => {
        const card = document.createElement('div');
        card.className = 'faithful-card';
        card.style.cursor = 'pointer';
        card.innerHTML = `
            <div class="faithful-header">
                <h3 class="faithful-name">${faithful.firstname || ''} ${faithful.name}</h3>
            </div>
            <div class="faithful-details">
                <div class="faithful-detail">
                    <span class="detail-label">Izina rya Se</span>
                    <span class="detail-value">${faithful.fatherName || '--'}</span>
                </div>
                <div class="faithful-detail">
                    <span class="detail-label">Itariki y'amavuko</span>
                    <span class="detail-value">${formatDate(faithful.dateOfBirth)}</span>
                </div>
                <div class="faithful-detail">
                    <span class="detail-label">Nomero y'ubatisimu</span>
                    <span class="detail-value">${faithful.baptismId || '--'}</span>
                </div>
            </div>
        `;

        // Click event to fetch full sacrament info
        card.addEventListener('click', () => handleFaithfulSelect(faithful.id));

        searchResultsEl.appendChild(card);
    });

    searchResultsSection.classList.remove('hidden');
    sacramentInfoSection.classList.add('hidden');
}

function renderSacramentInfo(info: FaithfulSacramentInfo): void {
    // Personal Information
    setText('firstname', info.firstname);
    setText('name', info.name);
    setText('father-name', info.fatherName);
    setText('mother-name', info.motherName);
    setText('godparent-name', info.godparentName);
    setText('date-of-birth', formatDate(info.dateOfBirth));

    // Address Information
    setText('diocese', info.diocese);
    setText('parish', info.parish);
    setText('subparish', info.subparish);
    setText('bec', info.basicEcclesialCommunity);

    // Baptism
    setText('date-baptism', formatDate(info.dateOfBaptism));
    setText('baptism-id', info.baptismId);
    setText('baptism-minister', info.baptismMinister);

    // First Communion
    setText('date-first-communion', formatDate(info.dateOfFirstCommunion));

    // Confirmation
    setText('date-confirmation', formatDate(info.dateOfConfirmation));
    setText('confirmation-id', info.confirmationId);

    // Matrimony
    setText('date-matrimony', formatDate(info.dateOfMatrimony));
    setText('matrimony-id', info.matrimonyId);
    setText('spouse-name', info.spouseName);
    setText('spouse-baptism-id', info.spouseBaptismId);

    // Show sacrament info section, hide search results
    sacramentInfoSection.classList.remove('hidden');
    searchResultsSection.classList.add('hidden');
}

function setText(elementId: string, value?: string): void {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = value || '--';
    }
}

// --- EVENT HANDLERS ---
async function handleSearch(event: Event): Promise<void> {
    event.preventDefault();
    const name = searchInput.value.trim();

    if (name.length < 2) {
        showError('Andika nibura inyuguti 2.');
        return;
    }

    showLoading();
    hideError();

    try {
        const results = await searchFaithfulByName(name);
        renderSearchResults(results);
    } catch (error) {
        showError(error instanceof Error ? error.message : 'Habaye ikosa. Ongera ugerageze.');
    } finally {
        hideLoading();
    }
}

async function handleFaithfulSelect(id: number): Promise<void> {
    showLoading();
    hideError();

    try {
        const sacramentInfo = await getFaithfulSacramentInfo(id);
        renderSacramentInfo(sacramentInfo);
    } catch (error) {
        showError(error instanceof Error ? error.message : 'Habaye ikosa. Ongera ugerageze.');
    } finally {
        hideLoading();
    }
}

// --- INITIALIZE ---
document.addEventListener('DOMContentLoaded', () => {
    searchForm.addEventListener('submit', handleSearch);
    searchInput.focus();
});