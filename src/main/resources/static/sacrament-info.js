// ==========================================
// SACRAMENT INFO - TYPESCRIPT
// ==========================================
const API_BASE_URL = 'http://localhost:8080/api/faithful';
// --- DOM ELEMENTS ---
const searchForm = document.getElementById('search-form');
const searchInput = document.getElementById('search-input');
const loadingEl = document.getElementById('loading');
const errorMessageEl = document.getElementById('error-message');
const errorTextEl = document.getElementById('error-text');
const searchResultsSection = document.getElementById('search-results-section');
const searchResultsEl = document.getElementById('search-results');
const sacramentInfoSection = document.getElementById('sacrament-info-section');
// --- UTILITY FUNCTIONS ---
function showLoading() {
    loadingEl.classList.remove('hidden');
    errorMessageEl.classList.add('hidden');
}
function hideLoading() {
    loadingEl.classList.add('hidden');
}
function showError(message) {
    errorTextEl.textContent = message;
    errorMessageEl.classList.remove('hidden');
}
function hideError() {
    errorMessageEl.classList.add('hidden');
}
function formatDate(dateString) {
    if (!dateString)
        return '--';
    const date = new Date(dateString);
    return date.toLocaleDateString('rw-RW', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
    });
}
// --- API FUNCTIONS ---
async function searchFaithfulByName(name) {
    const response = await fetch(`${API_BASE_URL}/search?name=${encodeURIComponent(name)}`);
    if (!response.ok) {
        throw new Error('Ishakiro ryanze. Ongera ugerageze.');
    }
    return response.json();
}
async function getFaithfulSacramentInfo(id) {
    const response = await fetch(`${API_BASE_URL}/${id}/sacrament-info`);
    if (!response.ok) {
        throw new Error('Ntibyashobotse kubona amakuru. Ongera ugerageze.');
    }
    return response.json();
}
// --- RENDER FUNCTIONS ---
function renderSearchResults(results) {
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
function renderSacramentInfo(info) {
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
function setText(elementId, value) {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = value || '--';
    }
}
// --- EVENT HANDLERS ---
async function handleSearch(event) {
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
    }
    catch (error) {
        showError(error instanceof Error ? error.message : 'Habaye ikosa. Ongera ugerageze.');
    }
    finally {
        hideLoading();
    }
}
async function handleFaithfulSelect(id) {
    showLoading();
    hideError();
    try {
        const sacramentInfo = await getFaithfulSacramentInfo(id);
        renderSacramentInfo(sacramentInfo);
    }
    catch (error) {
        showError(error instanceof Error ? error.message : 'Habaye ikosa. Ongera ugerageze.');
    }
    finally {
        hideLoading();
    }
}
// --- INITIALIZE ---
document.addEventListener('DOMContentLoaded', () => {
    searchForm.addEventListener('submit', handleSearch);
    searchInput.focus();
});
