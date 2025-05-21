document.addEventListener('DOMContentLoaded', function() {
    const stripePublicKey = document.querySelector('meta[name="stripe-public-key"]').getAttribute('content');
    
    if (stripePublicKey) {
        const stripe = Stripe(stripePublicKey);
        const elements = stripe.elements();
        const card = elements.create('card', {
            style: {
                base: {
                    fontSize: '16px',
                    color: '#32325d',
                    '::placeholder': {
                        color: '#aab7c4'
                    }
                },
                invalid: {
                    color: '#fa755a',
                    iconColor: '#fa755a'
                }
            },
            hidePostalCode: true
        });
        
        const cardElement = document.getElementById('card-element');
        if (cardElement) {
            card.mount('#card-element');
            
            card.addEventListener('change', function(event) {
                const displayError = document.getElementById('card-errors');
                if (displayError) {
                    if (event.error) {
                        displayError.textContent = event.error.message;
                    } else {
                        displayError.textContent = '';
                    }
                }
            });
        }
    }

    const totalPriceElement = document.getElementById('total-price');
    const shippingPriceDisplay = document.getElementById('shipping-price-display');
    
    const basePrice = totalPriceElement ? parseFloat(totalPriceElement.textContent.replace(',', '.')) : 0;

    const shippingOptions = document.querySelectorAll('.shipping-option');
    shippingOptions.forEach(option => {
        const radio = option.querySelector('input[type="radio"]');
        const priceElement = option.querySelector('.price');

        if (!radio || !priceElement) return;

        radio.addEventListener('change', function() {
            shippingOptions.forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');

            let shippingPrice = 0;
            if (priceElement.textContent !== 'Free') {
                shippingPrice = parseFloat(priceElement.textContent.replace(',', '.'));
            }

            if (shippingPrice === 0) {
                shippingPriceDisplay.innerHTML = '<span>Free</span>';
            } else {
                shippingPriceDisplay.innerHTML = `<span>${shippingPrice.toFixed(2)} лв.</span>`;
            }

            const newTotal = basePrice + shippingPrice;
            if (totalPriceElement) {
                totalPriceElement.textContent = newTotal.toFixed(2);
            }
        });

        if (radio.checked) {
            option.classList.add('selected');
            let shippingPrice = 0;
            if (priceElement.textContent !== 'Free') {
                shippingPrice = parseFloat(priceElement.textContent.replace(',', '.'));
            }

            if (shippingPrice === 0) {
                shippingPriceDisplay.innerHTML = '<span>Free</span>';
            } else {
                shippingPriceDisplay.innerHTML = `<span>${shippingPrice.toFixed(2)} лв.</span>`;
            }

            const initialTotal = basePrice + shippingPrice;
            if (totalPriceElement) {
                totalPriceElement.textContent = initialTotal.toFixed(2);
            }
        } else {
            shippingPriceDisplay.innerHTML = '<span>-</span>';
        }
    });

    const paymentOptions = document.querySelectorAll('.payment-option');
    const cardInfo = document.getElementById('card-info');
    const cashInfo = document.getElementById('cash-info');
    const installmentInfo = document.getElementById('installment-info');

    paymentOptions.forEach(option => {
        const radio = option.querySelector('input[type="radio"]');
        if (!radio) return;

        radio.addEventListener('change', function() {
            paymentOptions.forEach(opt => opt.classList.remove('selected'));
            option.classList.add('selected');

            if (cardInfo) cardInfo.classList.remove('active');
            if (cashInfo) cashInfo.classList.remove('active');
            if (installmentInfo) installmentInfo.classList.remove('active');

            if (radio.id === 'card-payment' && cardInfo) {
                cardInfo.classList.add('active');
            } else if (radio.id === 'cash-payment' && cashInfo) {
                cashInfo.classList.add('active');
            } else if (radio.id === 'installment-payment' && installmentInfo) {
                installmentInfo.classList.add('active');
            }
        });

        if (radio.checked) {
            option.classList.add('selected');
            if (radio.id === 'card-payment' && cardInfo) {
                cardInfo.classList.add('active');
            } else if (radio.id === 'cash-payment' && cashInfo) {
                cashInfo.classList.add('active');
            } else if (radio.id === 'installment-payment' && installmentInfo) {
                installmentInfo.classList.add('active');
            }
        }
    });

    initAutocomplete();

    const downPaymentInput = document.getElementById('downPayment');
    const installmentPeriod = document.getElementById('installmentPeriod');
    const monthlyInstallment = document.getElementById('monthlyInstallment');
    const totalAmount = document.getElementById('totalAmount');
    const paymentMonths = document.getElementById('paymentMonths');
    const interestValue = document.getElementById('interestValue');
    const aprValue = document.getElementById('aprValue');

    const summaryTotal = document.querySelector('.summary-total span:last-child');
    const totalPrice = summaryTotal ? parseFloat(summaryTotal.textContent.replace(/[^0-9.]/g, '')) : 0;

    const periods = [
        { months: 6, interest: 6.9, apr: 7.9 },
        { months: 12, interest: 7.9, apr: 8.9 },
        { months: 24, interest: 8.9, apr: 9.9 },
        { months: 36, interest: 10.9, apr: 11.9 }
    ];

    function calculateInstallment() {
        if (!downPaymentInput || !installmentPeriod || !monthlyInstallment || !totalAmount) return;
        
        const downPayment = parseFloat(downPaymentInput.value) || 0;
        const months = parseInt(installmentPeriod.value);
        const period = periods.find(p => p.months === months);

        if (!period) return;

        if (downPayment > totalPrice) {
            downPaymentInput.classList.add('is-invalid');
            monthlyInstallment.textContent = '0.00 лв.';
            totalAmount.textContent = '0.00 лв.';
            return;
        }

        downPaymentInput.classList.remove('is-invalid');
        const remainingAmount = totalPrice - downPayment;
        const interestRate = period.interest / 100;
        const totalWithInterest = remainingAmount * (1 + interestRate);
        const monthlyPayment = totalWithInterest / months;

        if (paymentMonths) paymentMonths.textContent = `${months} вноски`;
        if (interestValue) interestValue.textContent = `${period.interest}%`;
        if (aprValue) aprValue.textContent = `${period.apr}%`;

        monthlyInstallment.textContent = monthlyPayment.toFixed(2) + ' лв.';
        totalAmount.textContent = totalWithInterest.toFixed(2) + ' лв.';
    }

    if (downPaymentInput) {
        downPaymentInput.addEventListener('input', calculateInstallment);
    }
    
    if (installmentPeriod) {
        installmentPeriod.addEventListener('input', calculateInstallment);
    }

    calculateInstallment();
    
    const form = document.querySelector('.checkout-form');
    if (form) {
        form.addEventListener('submit', async function(event) {
            event.preventDefault();

            const selectedShipping = document.querySelector('input[name="deliveryMethod"]:checked');
            const selectedPayment = document.querySelector('input[name="paymentMethod"]:checked');
            const shippingValidation = document.getElementById('shipping-validation');
            const paymentValidation = document.getElementById('payment-validation');

            if (shippingValidation) shippingValidation.style.display = 'none';
            if (paymentValidation) paymentValidation.style.display = 'none';

            let isValid = true;
            if (!selectedShipping) {
                if (shippingValidation) shippingValidation.style.display = 'block';
                isValid = false;
            }
            if (!selectedPayment) {
                if (paymentValidation) paymentValidation.style.display = 'block';
                isValid = false;
            }
            if (!isValid) {
                return;
            }

            if (selectedPayment.value === 'CARD' && typeof stripe !== 'undefined') {
                // Implement Stripe payment processing here
                // For now, just submit the form
            }

            form.submit();
        });
    }
});

function initAutocomplete() {
    const addressInput = document.querySelector('.address-input');
    const cityInput = document.getElementById('city-input');
    const zipInput = document.getElementById('zip-input');
    const validationMessage = document.getElementById('address-validation');

    if (!addressInput || !cityInput || !zipInput) {
        console.error('Required elements not found');
        return;
    }

    if (typeof google === 'undefined' || typeof google.maps === 'undefined') {
        console.error('Google Maps API not loaded');
        return;
    }

    addressInput.setAttribute('lang', 'bg');
    addressInput.setAttribute('autocomplete', 'off');
    addressInput.setAttribute('spellcheck', 'false');

    const autocomplete = new google.maps.places.Autocomplete(addressInput, {
        types: ['address'],
        componentRestrictions: {country: "bg"},
        fields: ["address_components", "geometry", "formatted_address", "name"],
        language: 'bg',
        region: 'bg'
    });

    autocomplete.addListener('place_changed', () => {
        const place = autocomplete.getPlace();
        if (!place.address_components) {
            if (validationMessage) {
                validationMessage.style.display = 'block';
                validationMessage.textContent = 'Моля, изберете валиден български адрес';
            }
            return;
        }

        let country = '';
        let city = '';
        let postalCode = '';
        let streetNumber = '';
        let streetName = '';

        for (const component of place.address_components) {
            const types = component.types;

            if (types.includes('country')) {
                country = component.short_name;
            }
            if (types.includes('locality') || types.includes('administrative_area_level_1')) {
                city = component.long_name;
            }
            if (types.includes('postal_code')) {
                postalCode = component.long_name;
            }
            if (types.includes('street_number')) {
                streetNumber = component.long_name;
            }
            if (types.includes('route')) {
                streetName = component.long_name;
            }
        }

        if (country !== 'BG') {
            if (validationMessage) {
                validationMessage.style.display = 'block';
                validationMessage.textContent = 'Моля, изберете адрес в България';
            }
            addressInput.value = '';
            cityInput.value = '';
            zipInput.value = '';
            return;
        }

        if (!streetName || !streetNumber) {
            if (validationMessage) {
                validationMessage.style.display = 'block';
                validationMessage.textContent = 'Моля, изберете пълен адрес с номер на улица';
            }
            addressInput.value = '';
            cityInput.value = '';
            zipInput.value = '';
            return;
        }

        if (validationMessage) {
            validationMessage.style.display = 'none';
        }

        const formattedAddress = `${streetName} ${streetNumber}`;
        addressInput.value = formattedAddress;

        if (city) {
            cityInput.value = city;
        }
        if (postalCode) {
            zipInput.value = postalCode;
        }
    });

    addressInput.addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            e.preventDefault();
        }
    });

    addressInput.addEventListener('input', () => {
        if (validationMessage) {
            validationMessage.style.display = 'none';
        }
    });
}