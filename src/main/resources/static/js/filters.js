document.addEventListener('DOMContentLoaded', function () {
    // Get the filter form
    const filterForm = document.getElementById('filterForm');
    if (!filterForm) {
        console.error('Filter form not found');
        return;
    }

    // Get the max price from the data attribute
    const maxPhonePrice = parseInt(filterForm.dataset.maxPrice) || 3000; // Fallback to 3000 if not available
    
    // Price range slider functionality
    const slider = document.querySelector(".range-slider");
    if (slider) {
        const progress = slider.querySelector(".progress");
        const minPriceInput = slider.querySelector(".min-price");
        const maxPriceInput = slider.querySelector(".max-price");
        const minInput = slider.querySelector(".min-input");
        const maxInput = slider.querySelector(".max-input");
        const priceGap = 100;

        const updateProgress = () => {
            const minValue = parseInt(minInput.value);
            const maxValue = parseInt(maxInput.value);

            const range = maxInput.max - minInput.min;
            const valueRange = maxValue - minValue;
            const width = (valueRange / range) * 100;
            const minOffset = ((minValue - minInput.min) / range) * 100;

            progress.style.width = width + "%";
            progress.style.left = minOffset + "%";

            minPriceInput.value = minValue;
            maxPriceInput.value = maxValue;
        };

        const updateRange = (event) => {
            const input = event.target;

            let min = parseInt(minPriceInput.value);
            let max = parseInt(maxPriceInput.value);

            if (min < 0) min = 0;
            if (max > maxPhonePrice) max = maxPhonePrice;

            if (max - min < priceGap) {
                if (input === minPriceInput) {
                    min = max - priceGap;
                    minPriceInput.value = min;
                } else {
                    max = min + priceGap;
                    maxPriceInput.value = max;
                }
            }

            minInput.value = min;
            maxInput.value = max;

            updateProgress();
        };

        minPriceInput.addEventListener("input", updateRange);
        maxPriceInput.addEventListener("input", updateRange);

        minInput.addEventListener("input", () => {
            const minVal = parseInt(minInput.value);
            const maxVal = parseInt(maxInput.value);

            if (maxVal - minVal < priceGap) {
                minInput.value = maxVal - priceGap;
            }
            updateProgress();
        });

        maxInput.addEventListener("input", () => {
            const minVal = parseInt(minInput.value);
            const maxVal = parseInt(maxInput.value);

            if (maxVal - minVal < priceGap) {
                maxInput.value = minVal + priceGap;
            }
            updateProgress();
        });

        let isDragging = false;
        let startOffsetX;

        progress.addEventListener("mousedown", (e) => {
            e.preventDefault();

            isDragging = true;
            startOffsetX = e.clientX - progress.getBoundingClientRect().left;
            slider.classList.toggle("dragging", isDragging);
        });

        document.addEventListener("mousemove", (e) => {
            if (isDragging) {
                const sliderRect = slider.getBoundingClientRect();
                const progressWidth = parseFloat(progress.style.width || 0);

                let newLeft = ((e.clientX - sliderRect.left - startOffsetX) / sliderRect.width) * 100;

                newLeft = Math.min(Math.max(newLeft, 0), 100 - progressWidth);

                progress.style.left = newLeft + "%";

                const range = maxInput.max - minInput.min;
                const newMin = Math.round((newLeft / 100) * range) + parseInt(minInput.min);
                const newMax = newMin + parseInt(maxInput.value) - parseInt(minInput.value);

                if (newMax <= maxPhonePrice) {
                    minInput.value = newMin;
                    maxInput.value = newMax;
                    updateProgress();
                }
            }
            slider.classList.toggle("dragging", isDragging);
        });

        document.addEventListener("mouseup", () => {
            if (isDragging) {
                isDragging = false;
            }
            slider.classList.toggle("dragging", isDragging);
        });

        updateProgress();
    }

    // Form submission
    if (filterForm) {
        filterForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const brands = Array.from(document.querySelectorAll('input[name="brands"]:checked')).map(cb => cb.value);
            const storages = Array.from(document.querySelectorAll('input[name="storages"]:checked')).map(cb => parseInt(cb.value));
            const ram = Array.from(document.querySelectorAll('input[name="ram"]:checked')).map(cb => parseInt(cb.value));
            const colors = Array.from(document.querySelectorAll('input[name="colors"]:checked')).map(cb => cb.value);
            const cameraResolutions = Array.from(document.querySelectorAll('input[name="cameraResolutions"]:checked')).map(cb => cb.value);
            const screenSizes = Array.from(document.querySelectorAll('input[name="screenSizes"]:checked')).map(cb => cb.value);
            const waterResistant = document.getElementById('waterResistant')?.checked ? 'true' : null;
            const batteryCapacities = Array.from(document.querySelectorAll('input[name="batteryCapacities"]:checked')).map(cb => parseInt(cb.value));
            const discountedOnly = document.getElementById('discountedOnly')?.checked ? 'true' : null;
            const minPrice = document.querySelector('.min-price')?.value || 0;
            const maxPrice = document.querySelector('.max-price')?.value || maxPhonePrice;
            
            const params = new URLSearchParams();
            if (brands.length > 0) brands.forEach(b => params.append('brands', b));
            if (storages.length > 0) storages.forEach(s => params.append('storages', s));
            if (ram.length > 0) ram.forEach(r => params.append('ram', r));
            if (colors.length > 0) colors.forEach(c => params.append('colors', c));
            if (cameraResolutions.length > 0) cameraResolutions.forEach(r => params.append('cameraResolutions', r));
            if (screenSizes.length > 0) screenSizes.forEach(s => params.append('screenSizes', s));
            if (waterResistant) params.append('waterResistant', waterResistant);
            if (batteryCapacities.length > 0) batteryCapacities.forEach(b => params.append('batteryCapacities', b));
            if (discountedOnly) params.append('discountedOnly', discountedOnly);
            if (minPrice > 0) params.append('minPrice', minPrice);
            if (maxPrice < maxPhonePrice) params.append('maxPrice', maxPrice);
            window.location.href = '/phones?' + params.toString();
        });
    }

    // Filter removal links
    document.querySelectorAll('.filter-remove-link').forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            let url = new URL(this.href);
            let params = new URLSearchParams(url.search);
            
            // Remove any empty parameters
            for (let key of [...params.keys()]) {
                if (params.get(key) === '' || params.get(key) === null) {
                    params.delete(key);
                }
            }
            
            // Redirect to the cleaned URL
            window.location.href = url.pathname + (params.toString() ? '?' + params.toString() : '');
        });
    });

    // Show more/less functionality for filter sections
    initFilterShowMoreLess();
});

// Separated the filter show/hide functionality for better organization
function initFilterShowMoreLess() {
    const toggleButtons = document.querySelectorAll('.show-more-toggle');
    
    // Function to make checked items visible even when collapsed
    function showCheckedItems() {
        document.querySelectorAll('.filter-section').forEach(section => {
            // First, ensure only first 5 items are visible
            const allItems = section.querySelectorAll('.filter-item');
            allItems.forEach((item, index) => {
                if (index >= 5) {
                    item.classList.add('filter-hidden');
                } else {
                    item.classList.remove('filter-hidden');
                }
            });
            
            // Then, make any checked items visible
            const checkedItems = section.querySelectorAll('.filter-item input:checked');
            if (checkedItems.length > 0) {
                checkedItems.forEach(item => {
                    item.closest('.filter-item').classList.remove('filter-hidden');
                });
            }
            
            // Update the toggle button text if we've made any hidden items visible
            const toggle = section.querySelector('.show-more-toggle');
            if (toggle) {
                const hiddenItems = section.querySelectorAll('.filter-hidden');
                const totalItems = section.querySelectorAll('.filter-item').length;
                
                if (hiddenItems.length === 0 && totalItems > 5) {
                    toggle.textContent = 'Show less';
                    toggle.classList.add('expanded');
                }
            }
        });
    }

    // Run on page load to make sure checked items are visible and only 5 items are shown by default
    showCheckedItems();

    // Add event listeners to toggle buttons
    toggleButtons.forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const section = this.closest('.filter-section');
            const allItems = section.querySelectorAll('.filter-item');
            
            if (this.textContent === 'Show more') {
                // Show all items
                allItems.forEach(item => {
                    item.classList.remove('filter-hidden');
                });
                this.textContent = 'Show less';
                this.classList.add('expanded');
            } else {
                // Hide items after the 5th one, but keep checked items visible
                allItems.forEach((item, index) => {
                    if (index >= 5) {
                        item.classList.add('filter-hidden');
                    }
                });
                
                // Make sure checked items stay visible
                const checkedItems = section.querySelectorAll('.filter-item input:checked');
                checkedItems.forEach(input => {
                    input.closest('.filter-item').classList.remove('filter-hidden');
                });
                
                this.textContent = 'Show more';
                this.classList.remove('expanded');
            }
        });
    });
}