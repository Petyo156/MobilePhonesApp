import './finance-calculator.js';
import './carousel.js';
import './review.js';
import './options.js';

function initPhonePage() {
    console.log('Phone page initialized');
    initSimilarPhonesScroll();
}

function initSimilarPhonesScroll() {
    const scrollContainer = document.querySelector('.scrollable-products');
    const leftArrow = document.querySelector('.left-arrow');
    const rightArrow = document.querySelector('.right-arrow');
    
    if (!scrollContainer || !leftArrow || !rightArrow) return;
    
    const scrollAmount = () => {
        const productWidth = scrollContainer.querySelector('.product-wrapper')?.offsetWidth || 280;
        return productWidth + 24;
    };
    
    leftArrow.addEventListener('click', () => {
        scrollContainer.scrollBy({
            left: -scrollAmount() * 2,
            behavior: 'smooth'
        });
    });
    
    rightArrow.addEventListener('click', () => {
        scrollContainer.scrollBy({
            left: scrollAmount() * 2,
            behavior: 'smooth'
        });
    });
    
    function updateArrowVisibility() {
        if (scrollContainer.scrollLeft <= 10) {
            leftArrow.style.opacity = '0.5';
            leftArrow.style.pointerEvents = 'none';
        } else {
            leftArrow.style.opacity = '1';
            leftArrow.style.pointerEvents = 'auto';
        }
        
        if (scrollContainer.scrollLeft >= (scrollContainer.scrollWidth - scrollContainer.clientWidth - 10)) {
            rightArrow.style.opacity = '0.5';
            rightArrow.style.pointerEvents = 'none';
        } else {
            rightArrow.style.opacity = '1';
            rightArrow.style.pointerEvents = 'auto';
        }
    }
    
    updateArrowVisibility();
    scrollContainer.addEventListener('scroll', updateArrowVisibility);
    window.addEventListener('resize', updateArrowVisibility);
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPhonePage);
} else {
    initPhonePage();
}

export default { initPhonePage };