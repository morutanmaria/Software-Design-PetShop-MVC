
let currentSearch = '';
let currentSortField = 'name';
let currentSortDir = 'asc';

async function loadItems() {
    const body = document.getElementById('itemsBody');
    if (!body) return;

    body.innerHTML = `
    <tr>
      <td colspan="7" class="text-center py-4">
        <div class="spinner-border spinner-border-sm me-2" role="status"></div>
        Loading...
      </td>
    </tr>`;

    try {
        const url = `/api/items`
            + `?search=${encodeURIComponent(currentSearch)}`
            + `&sortField=${currentSortField}`
            + `&sortDir=${currentSortDir}`;

        const response = await fetch(url);

        if (!response.ok) {
            let errorMsg = window._i18n?.loadError || 'Failed to load items.';
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
            <i class="fa fa-circle-exclamation me-2"></i>${window._i18n?.loadError || errorMsg}
          </td>
        </tr>`;
            return;
        }

        const items = await response.json();
        renderItems(items);

    } catch (err) {
        console.error('Network error loading items:', err);
        const fallbackMsg = window._i18n?.loadError || 'Network error loading items.';
        showApiError(fallbackMsg);
        body.innerHTML = `
      <tr>
        <td colspan="7" class="text-center text-danger py-4">
          <i class="fa fa-wifi-slash me-2"></i>${fallbackMsg}
        </td>
      </tr>`;
    }
}

function renderItems(items) {
    const body = document.getElementById('itemsBody');

    if (!items || items.length === 0) {
        body.innerHTML = `
      <tr>
        <td colspan="7" class="text-center text-muted py-4">
          <i class="fa fa-magnifying-glass me-2"></i>${window._i18n?.noResults || 'No items found.'}
        </td>
      </tr>`;
        return;
    }

    body.innerHTML = items.map(item => {
        const image = item.imagePath
            ? `<img src="${escHtml(item.imagePath)}" style="width:60px;height:60px;object-fit:cover;border-radius:8px;" alt="item"/>`
            : `<div style="width:60px;height:60px;background:#e9ecef;border-radius:8px;display:flex;align-items:center;justify-content:center;"><i class="fa fa-box text-muted"></i></div>`;

        const associatedPet = item.petName ? escHtml(item.petName) : (item.petId ? `Pet #${item.petId}` : '-');

        const actions = window._isAdmin
            ? `<a href="/items/edit/${item.id}" class="btn btn-sm btn-warning me-1">
           <i class="fa fa-edit me-1"></i>${escHtml(window._i18n?.edit || 'Edit')}
         </a>
         <button onclick="deleteItem(${item.id})" class="btn btn-sm btn-danger">
           <i class="fa fa-trash me-1"></i>${escHtml(window._i18n?.deleteBtn || 'Delete')}
         </button>`
            : '';

        return `
      <tr>
        <td class="text-center">${image}</td>
        <td class="fw-semibold">${escHtml(item.name ?? '')}</td>
        <td>${escHtml(item.description ?? '')}</td>
        <td><span class="badge bg-secondary">${escHtml(item.type ?? '')}</span></td>
        <td>${associatedPet}</td>
        <td>${item.price != null ? '$' + Number(item.price).toFixed(2) : ''}</td>
        <td>${actions}</td>
      </tr>`;
    }).join('');
}

async function deleteItem(id) {
    const confirmMsg = window._i18n?.confirmDelete || 'Are you sure you want to delete this item?';
    if (!confirm(confirmMsg)) return;

    try {
        const response = await fetch(`/api/items/${id}`, { method: 'DELETE' });
        if (!response.ok) {
            let errorMsg = 'Failed to delete item.';
            try {
                const errData = await response.json();
                errorMsg = errData.message || errorMsg;
            } catch (_) {}
            showApiError(errorMsg);
            return;
        }
        loadItems();
    } catch (err) {
        showApiError('Network error. Could not delete item.');
    }
}

function changeSort(field, dir) {
    currentSortField = field;
    currentSortDir = dir;
    loadItems();
}

function escHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

const searchForm = document.getElementById('searchForm');
if (searchForm) {
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        const searchInput = document.getElementById('searchInput');
        currentSearch = searchInput ? searchInput.value.trim() : '';
        loadItems();
    });
}

window.addEventListener('DOMContentLoaded', () => {
    loadItems();
});