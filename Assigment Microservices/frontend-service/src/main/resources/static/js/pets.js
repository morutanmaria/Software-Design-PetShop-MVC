
let currentSearch = '';
let currentSortField = 'name';
let currentSortDir = 'asc';

async function loadPets() {
  const body = document.getElementById('petsBody');
  body.innerHTML = `
    <tr>
      <td colspan="7" class="text-center py-4">
        <div class="spinner-border spinner-border-sm me-2" role="status"></div>
        Loading...
      </td>
    </tr>`;

  try {
    const url = `/api/pets`
      + `?search=${encodeURIComponent(currentSearch)}`
      + `&sortField=${currentSortField}`
      + `&sortDir=${currentSortDir}`;

    const response = await fetch(url);

    if (!response.ok) {
      let errorMsg = window._i18n.loadError;
      try {
        const errData = await response.json();
        errorMsg = errData.message || errorMsg;
        showApiError(`[${errData.errorCode || response.status}] ${errorMsg}`);
      } catch (_) {
        showApiError(`HTTP ${response.status}: ${errorMsg}`);
      }

      body.innerHTML = `
        <tr>
          <td colspan="7" class="text-center text-danger py-4">
            <i class="fa fa-circle-exclamation me-2"></i>${window._i18n.loadError}
          </td>
        </tr>`;
      return;
    }

    const pets = await response.json();
    renderPets(pets);

  } catch (err) {
    console.error('Network error loading pets:', err);
    showApiError(window._i18n.loadError);
    body.innerHTML = `
      <tr>
        <td colspan="7" class="text-center text-danger py-4">
          <i class="fa fa-wifi-slash me-2"></i>${window._i18n.loadError}
        </td>
      </tr>`;
  }
}

function renderPets(pets) {
  const body = document.getElementById('petsBody');

  if (!pets || pets.length === 0) {
    body.innerHTML = `
      <tr>
        <td colspan="7" class="text-center text-muted py-4">
          <i class="fa fa-magnifying-glass me-2"></i>${window._i18n.noResults}
        </td>
      </tr>`;
    return;
  }

  body.innerHTML = pets.map(pet => {
    const image = pet.imagePath
      ? `<img src="${escHtml(pet.imagePath)}" style="width:60px;height:60px;object-fit:cover;border-radius:8px;" alt="pet"/>`
      : `<div style="width:60px;height:60px;background:#e9ecef;border-radius:8px;display:flex;align-items:center;justify-content:center;"><i class="fa fa-paw text-muted"></i></div>`;

    const actions = window._isAdmin
      ? `<a href="/pets/edit/${pet.id}" class="btn btn-sm btn-warning me-1">
           <i class="fa fa-edit me-1"></i>${escHtml(window._i18n.edit)}
         </a>
         <button onclick="deletePet(${pet.id})" class="btn btn-sm btn-danger">
           <i class="fa fa-trash me-1"></i>${escHtml(window._i18n.deleteBtn)}
         </button>`
      : '';

    return `
      <tr>
        <td class="text-center">${image}</td>
        <td class="fw-semibold">${escHtml(pet.name ?? '')}</td>
        <td>${escHtml(pet.species ?? '')}</td>
        <td>${escHtml(pet.breed ?? '')}</td>
        <td>${pet.age ?? ''}</td>
        <td>${pet.price != null ? '$' + Number(pet.price).toFixed(2) : ''}</td>
        <td>${actions}</td>
      </tr>`;
  }).join('');
}

async function deletePet(id) {
  if (!confirm(window._i18n.confirmDelete)) return;

  try {
    const response = await fetch(`/api/pets/${id}`, { method: 'DELETE' });
    if (!response.ok) {
      let errorMsg = 'Failed to delete pet.';
      try {
        const errData = await response.json();
        errorMsg = errData.message || errorMsg;
      } catch (_) {}
      showApiError(errorMsg);
      return;
    }
    loadPets();
  } catch (err) {
    showApiError('Network error. Could not delete pet.');
  }
}

function changeSort(field, dir) {
  currentSortField = field;
  currentSortDir = dir;
  loadPets();
}

function escHtml(str) {
  if (!str) return '';
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

document.getElementById('searchForm').addEventListener('submit', function(e) {
  e.preventDefault();
  currentSearch = document.getElementById('searchInput').value.trim();
  loadPets();
});

loadPets();
