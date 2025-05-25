// Finance Calculator Module
function initFinanceCalculator() {
    const discountedPriceElement = document.querySelector('.discounted-price');
    const regularPriceElement = document.querySelector('.price');
    // const priceElement = discountedPriceElement || regularPriceElement;

    if (!regularPriceElement) return;

    const priceText = regularPriceElement.textContent.trim();
    const mainPrice = priceText.match(/\d+/)[0];
    const decimalPart = priceText.match(/\.(\d+)/)?.[1] || '00';
    const price = parseFloat(`${mainPrice}.${decimalPart}`);

    const financePeriod = document.getElementById('financePeriod');
    const monthlyPaymentMain = document.getElementById('monthlyPaymentMain');
    const monthlyPaymentDecimal = document.getElementById('monthlyPaymentDecimal');
    const totalMonths = document.getElementById('totalMonths');

    if (!financePeriod || !monthlyPaymentMain || !monthlyPaymentDecimal || !totalMonths) return;

    const periods = [
        {months: 6, apr: 7.9},
        {months: 12, apr: 8.9},
        {months: 24, apr: 9.9},
        {months: 36, apr: 11.9}
    ];

    function calculateMonthlyPayment() {
        const months = parseInt(financePeriod.value);
        const period = periods.find(p => p.months === months);
        const interestRate = period.apr / 100;

        const totalAmount = price * (1 + interestRate);
        const monthlyAmount = totalAmount / months;

        const [main, decimal] = monthlyAmount.toFixed(2).split('.');
        monthlyPaymentMain.textContent = main;
        monthlyPaymentDecimal.textContent = decimal;
        totalMonths.textContent = `за ${months} месеца`;
    }

    financePeriod.addEventListener('change', calculateMonthlyPayment);
    calculateMonthlyPayment();

    const mainCarousel = document.getElementById('carouselExampleIndicators');
    const modalCarousel = document.getElementById('modalCarousel');
    const imageModal = document.getElementById('imageModal');

    if (mainCarousel && modalCarousel && imageModal) {
        mainCarousel.addEventListener('slide.bs.carousel', function (e) {
            if (imageModal.classList.contains('show')) {
                const modalCarouselInstance = bootstrap.Carousel.getInstance(modalCarousel);
                if (modalCarouselInstance) modalCarouselInstance.to(e.to);
            }
        });

        modalCarousel.addEventListener('slide.bs.carousel', function (e) {
            const mainCarouselInstance = bootstrap.Carousel.getInstance(mainCarousel);
            if (mainCarouselInstance) mainCarouselInstance.to(e.to);
        });

        imageModal.addEventListener('show.bs.modal', function () {
            const mainCarouselInstance = bootstrap.Carousel.getInstance(mainCarousel);
            if (!mainCarouselInstance) return;
            
            const activeIndex = [...mainCarousel.querySelectorAll('.carousel-item')].findIndex(item => item.classList.contains('active'));
            const modalCarouselInstance = bootstrap.Carousel.getInstance(modalCarousel);
            if (modalCarouselInstance) modalCarouselInstance.to(activeIndex);
        });
    }

    const downPaymentInput = document.getElementById('modal_down_payment');
    const paymentRange = document.getElementById('paymentRange');
    const paymentMonths = document.getElementById('paymentMonths');
    const aprValue = document.getElementById('aprValue');
    const monthlyPaymentValue = document.getElementById('monthlyPaymentValue');
    const totalCreditValue = document.getElementById('totalCreditValue');
    const finalMonthlyPayment = document.getElementById('finalMonthlyPayment');
    const finalAprValue = document.getElementById('finalAprValue');

    if (!downPaymentInput || !paymentRange) return;

    function validateDownPayment() {
        const value = parseFloat(downPaymentInput.value) || 0;
        if (value > price) {
            downPaymentInput.classList.add('is-invalid');
            return false;
        }
        downPaymentInput.classList.remove('is-invalid');
        return true;
    }

    function updateCreditInfo() {
        if (!validateDownPayment()) {
            if (monthlyPaymentValue) monthlyPaymentValue.textContent = '0.00 лв.';
            if (totalCreditValue) totalCreditValue.textContent = '0.00 лв.';
            if (finalMonthlyPayment) finalMonthlyPayment.textContent = '0.00 лв.';
            return;
        }

        const months = parseInt(paymentRange.value);
        const period = periods.find(p => p.months >= months) || periods[periods.length - 1];
        const currentAPR = period.apr;
        const downPayment = parseFloat(downPaymentInput.value) || 0;
        const creditAmount = price - downPayment;

        const totalAmount = creditAmount * (1 + (currentAPR / 100));
        const monthlyPayment = totalAmount / months;

        if (paymentMonths) paymentMonths.textContent = `${months} вноски`;
        if (aprValue) aprValue.textContent = `${currentAPR}%`;
        if (monthlyPaymentValue) monthlyPaymentValue.textContent = `${monthlyPayment.toFixed(2)} лв.`;
        if (totalCreditValue) totalCreditValue.textContent = `${creditAmount.toFixed(2)} лв.`;
        if (finalMonthlyPayment) finalMonthlyPayment.textContent = `${monthlyPayment.toFixed(2)} лв.`;
        if (finalAprValue) finalAprValue.textContent = `${currentAPR}%`;
    }

    downPaymentInput.addEventListener('input', updateCreditInfo);
    paymentRange.addEventListener('input', updateCreditInfo);

    updateCreditInfo();
}

export default { init: initFinanceCalculator };

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initFinanceCalculator);
} else {
    initFinanceCalculator();
}