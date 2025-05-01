document.addEventListener('DOMContentLoaded', function() {
    const table = document.querySelector('.phone-table');
    const headers = table.querySelectorAll('th[data-sort]');
    let currentSort = {
        column: 'date',
        direction: 'desc'
    };

    headers.forEach(header => {
        header.addEventListener('click', () => {
            const column = header.getAttribute('data-sort');
            if (currentSort.column === column) {
                currentSort.direction = currentSort.direction === 'asc' ? 'desc' : 'asc';
            } else {
                currentSort.column = column;
                currentSort.direction = 'asc';
            }
            sortTable(column, currentSort.direction);
            updateSortIcons(header);
        });
    });

    const visibilityButtons = document.querySelectorAll('.action-button');
    visibilityButtons.forEach(button => {
        button.addEventListener('click', function() {
            const slug = this.getAttribute('data-slug');
            updateVisibility(slug);
        });
    });

    // Initial sort by date
    sortTable('date', 'desc');
    updateSortIcons(document.querySelector('th[data-sort="date"]'));
});

function sortTable(column, direction) {
    const table = document.querySelector('.phone-table');
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));

    const columnIndex = getColumnIndex(column);
    rows.sort((a, b) => {
        const aValue = a.cells[columnIndex].textContent.trim();
        const bValue = b.cells[columnIndex].textContent.trim();

        if (column === 'price' || column === 'stock' || column === 'discount') {
            const aNum = parseFloat(aValue.replace(/[^0-9.-]+/g, ''));
            const bNum = parseFloat(bValue.replace(/[^0-9.-]+/g, ''));
            return direction === 'asc' ? aNum - bNum : bNum - aNum;
        } else if (column === 'date') {
            const aDate = new Date(aValue.split('/').reverse().join('-'));
            const bDate = new Date(bValue.split('/').reverse().join('-'));
            return direction === 'asc' ? aDate - bDate : bDate - aDate;
        } else {
            return direction === 'asc' ? 
                aValue.localeCompare(bValue) : 
                bValue.localeCompare(aValue);
        }
    });

    rows.forEach(row => tbody.appendChild(row));
}

function getColumnIndex(column) {
    const columnMap = {
        'name': 0,
        'model': 1,
        'storage': 2,
        'ram': 3,
        'color': 4,
        'price': 5,
        'stock': 6,
        'discount': 7,
        'date': 8
    };
    return columnMap[column];
}

function updateSortIcons(clickedHeader) {
    const headers = document.querySelectorAll('th[data-sort]');
    headers.forEach(header => {
        const icon = header.querySelector('.sort-icon');
        if (header === clickedHeader) {
            icon.textContent = currentSort.direction === 'asc' ? '↑' : '↓';
            header.classList.toggle('asc', currentSort.direction === 'asc');
        } else {
            icon.textContent = '↕';
            header.classList.remove('asc');
        }
    });
}

function updateVisibility(slug) {
    fetch(`/admin/phone/${slug}/visibility`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        window.location.reload();
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Failed to update visibility. Please try again.');
    });
} 