import './finance-calculator.js';
import './carousel.js';
import './review.js';
import './options.js';

function initPhonePage() {
    console.log('Phone page initialized');
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initPhonePage);
} else {
    initPhonePage();
}

export default { initPhonePage };