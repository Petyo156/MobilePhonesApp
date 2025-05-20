document.addEventListener('DOMContentLoaded', function() {
    const productContainer = document.querySelector('.product-container');
    const sortButtons = document.querySelectorAll('.btn-group[role="group"][aria-label="Sort options"] .btn');
    
    if (!productContainer || !sortButtons.length) {
        console.error('Required elements not found: productContainer or sortButtons');
        return;
    }
    
    console.log('Sort functionality initialized with', sortButtons.length, 'buttons');
    
    sortButtons.forEach(button => {
        button.disabled = false;
        button.addEventListener('click', handleSort);
        console.log('Enabled button:', button.textContent.trim());
    });
    
    function handleSort(event) {
        console.log('Sort clicked:', event.target.textContent.trim());
        
        sortButtons.forEach(btn => btn.classList.remove('active', 'btn-secondary'));
        event.target.classList.add('active', 'btn-secondary');
        
        const sortType = event.target.textContent.trim();
        const products = Array.from(document.querySelectorAll('.product-wrapper'));
        
        console.log('Sorting', products.length, 'products by', sortType);
        
        products.sort((a, b) => {
            const priceElementA = a.querySelector('.product-price');
            const priceElementB = b.querySelector('.product-price');
            
            if (!priceElementA || !priceElementB) {
                console.warn('Price elements not found');
                return 0;
            }
            
            const priceA = parseFloat(priceElementA.getAttribute('data-price'));
            const priceB = parseFloat(priceElementB.getAttribute('data-price'));

            
            switch(sortType) {
                case 'Price: Low to High':
                    return priceA - priceB;
                case 'Price: High to Low':
                    return priceB - priceA;
                default:
                    return 0;
            }
        });
        
        while (productContainer.firstChild) {
            productContainer.removeChild(productContainer.firstChild);
        }
        
        products.forEach(product => {
            productContainer.appendChild(product);
        });

        sessionStorage.setItem('sortPreference', sortType);
        console.log('Sorting complete and preference saved:', sortType);
    }

    const savedSort = sessionStorage.getItem('sortPreference');
    if (savedSort) {
        console.log('Applying saved sort preference:', savedSort);
        const matchingButton = Array.from(sortButtons).find(btn => btn.textContent.trim() === savedSort);
        if (matchingButton) {
            matchingButton.click();
        }
    }
}); 