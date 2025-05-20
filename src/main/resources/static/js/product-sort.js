document.addEventListener('DOMContentLoaded', function() {
    const productContainer = document.querySelector('.product-container');
    // const sortDropdown = document.querySelector('#sortDropdown');
    const sortOptions = document.querySelectorAll('.dropdown-menu .dropdown-item');
    const currentSortText = document.querySelector('#currentSort');
    
    if (!productContainer || !sortOptions.length) {
        console.error('Required elements not found: productContainer or sortOptions');
        return;
    }
    
    console.log('Sort functionality initialized with', sortOptions.length, 'options');
    
    sortOptions.forEach(option => {
        option.addEventListener('click', handleSort);
    });
    
    function handleSort(event) {
        event.preventDefault();
        
        const selectedOption = event.target.closest('.dropdown-item');
        const sortType = selectedOption.getAttribute('data-sort');
        const sortText = selectedOption.textContent.trim();
        
        sortOptions.forEach(opt => opt.classList.remove('active'));
        selectedOption.classList.add('active');
        
        currentSortText.textContent = sortText.replace(/^[\s\uFEFF\xA0\u200B\u2003]+|[\s\uFEFF\xA0\u200B\u2003]+$/g, '');
        
        const products = Array.from(document.querySelectorAll('.product-wrapper'));
        
        if (products.length > 0) {
            products.sort((a, b) => {
                const priceElementA = a.querySelector('.product-price');
                const priceElementB = b.querySelector('.product-price');
                
                if (!priceElementA || !priceElementB) {
                    console.warn('Price elements not found');
                    return 0;
                }
                
                const priceA = parseFloat(priceElementA.getAttribute('data-price'));
                const priceB = parseFloat(priceElementB.getAttribute('data-price'));
                
                const ratingElementA = a.querySelector('.product-rating');
                const ratingElementB = b.querySelector('.product-rating');
                
                const ratingA = ratingElementA ? parseFloat(ratingElementA.getAttribute('data-rating') || 0) : 0;
                const ratingB = ratingElementB ? parseFloat(ratingElementB.getAttribute('data-rating') || 0) : 0;
                
                switch(sortType) {
                    case 'price-asc':
                        return priceA - priceB;
                    case 'price-desc':
                        return priceB - priceA;
                    case 'rating-desc':
                        return ratingB === ratingA ? priceA - priceB : ratingB - ratingA;
                    case 'relevance':
                    default:
                        const indexA = parseInt(a.getAttribute('data-index')) || 0;
                        const indexB = parseInt(b.getAttribute('data-index')) || 0;
                        return indexA - indexB;
                }
            });
            
            productContainer.style.opacity = '0.5';
            productContainer.style.transition = 'opacity 0.3s ease';
            
            setTimeout(() => {
                while (productContainer.firstChild) {
                    productContainer.removeChild(productContainer.firstChild);
                }
                
                products.forEach(product => {
                    productContainer.appendChild(product);
                });
                
                productContainer.style.opacity = '1';
            }, 200);
        }

        sessionStorage.setItem('sortPreference', sortType);
    }

    const products = document.querySelectorAll('.product-wrapper');
    products.forEach((product, index) => {
        if (!product.hasAttribute('data-index')) {
            product.setAttribute('data-index', index);
        }
    });

    const savedSort = sessionStorage.getItem('sortPreference');
    if (savedSort) {
        const matchingOption = Array.from(sortOptions).find(opt => opt.getAttribute('data-sort') === savedSort);
        if (matchingOption) {
            matchingOption.click();
        }
    }
}); 