document.addEventListener('DOMContentLoaded', function() {
    const tables = document.querySelectorAll('.phone-table');
    const headers = tables[0].querySelectorAll('th[data-sort]');
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

    sortTable('date', 'desc');
    updateSortIcons(document.querySelector('th[data-sort="date"]'));

    const modal = document.getElementById('editPhoneModal');
    const closeBtn = document.querySelector('.close');
    const editForm = document.getElementById('editPhoneForm');
    let currentPhoneSlug = '';

    const editButtons = document.querySelectorAll('.action-button.edit');
    alert('Found ' + editButtons.length + ' edit buttons');

    editButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            alert('Edit button clicked');
            e.preventDefault();
            e.stopPropagation();
            
            const row = this.closest('tr');
            const cells = row.cells;
            currentPhoneSlug = this.getAttribute('data-slug');

            document.getElementById('brand').value = cells[0].textContent.split(' ')[0];
            document.getElementById('model').value = cells[1].textContent;
            document.getElementById('storage').value = cells[2].textContent.replace('GB', '');
            document.getElementById('ram').value = cells[3].textContent.replace('GB', '');
            document.getElementById('color').value = cells[4].textContent;
            document.getElementById('price').value = cells[5].textContent.replace(/[^0-9.]/g, '');
            document.getElementById('quantity').value = cells[6].textContent;
            document.getElementById('discount').value = cells[7].textContent.replace('%', '');

            modal.style.display = 'block';
        });
    });

    closeBtn.addEventListener('click', function() {
        modal.style.display = 'none';
    });

    window.addEventListener('click', function(e) {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });

    editForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = new FormData(this);
        formData.append('slug', currentPhoneSlug);

        fetch(`/admin/products/${currentPhoneSlug}/edit`, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                window.location.reload();
            } else {
                alert('Error updating phone');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error updating phone');
        });
    });
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
