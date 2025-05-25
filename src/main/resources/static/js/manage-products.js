document.addEventListener('DOMContentLoaded', function() {
    const selectAllVisible = document.getElementById('selectAllVisible');
    const selectAllHidden = document.getElementById('selectAllHidden');
    const visibleCheckboxes = document.querySelectorAll('.visible-checkbox');
    const hiddenCheckboxes = document.querySelectorAll('.hidden-checkbox');

    selectAllVisible.addEventListener('change', function() {
        document.querySelectorAll('.phone-table tbody tr').forEach(row => {
            if (row.closest('.table-container') && row.closest('.table-container').classList.contains('hidden-phones')) return;
            if (row.style.display !== 'none') {
                const checkbox = row.querySelector('.visible-checkbox');
                if (checkbox) checkbox.checked = this.checked;
            }
        });
    });

    selectAllHidden.addEventListener('change', function() {
        document.querySelectorAll('.hidden-phones .phone-table tbody tr').forEach(row => {
            if (row.style.display !== 'none') {
                const checkbox = row.querySelector('.hidden-checkbox');
                if (checkbox) checkbox.checked = this.checked;
            }
        });
    });

    const searchBar = document.getElementById('search-bar');
    if (searchBar) {
        searchBar.addEventListener('input', filterTableRows);
    }
});

function validateForm() {
    const selectedCheckboxes = document.querySelectorAll('input[name="slugs"]:checked');
    if (selectedCheckboxes.length === 0) {
        alert('Please select at least one phone to apply the discount');
        return false;
    }
    return true;
}

function getSelectedSlugs() {
    const selectedCheckboxes = document.querySelectorAll('input[name="slugs"]:checked');
    return Array.from(selectedCheckboxes).map(checkbox => checkbox.value);
}

function handleBulkHide() {
    const selectedSlugs = getSelectedSlugs();
    if (selectedSlugs.length === 0) {
        alert('Please select at least one phone to hide');
        return;
    }

    const form = document.getElementById('bulk-hide-form');
    const input = document.getElementById('hide-slugs');
    input.value = selectedSlugs.join(',');
    form.submit();
}

function handleBulkShow() {
    const selectedSlugs = getSelectedSlugs();
    if (selectedSlugs.length === 0) {
        alert('Please select at least one phone to show');
        return;
    }

    const form = document.getElementById('bulk-show-form');
    const input = document.getElementById('show-slugs');
    input.value = selectedSlugs.join(',');
    form.submit();
}

function filterTableRows() {
    const query = document.getElementById('search-bar').value.toLowerCase();
    document.querySelectorAll('.phone-table tbody tr').forEach(row => {
        const rowText = row.textContent.toLowerCase();
        row.style.display = rowText.includes(query) ? '' : 'none';
    });
}
