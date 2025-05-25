function initReview() {
    const stars = document.querySelectorAll('#starRating .star');
    const ratingInput = document.getElementById('ratingInput');
    let selected = 0;
    
    if (stars.length > 0 && ratingInput) {
        stars.forEach(star => {
            star.addEventListener('mouseenter', function () {
                const val = parseInt(this.getAttribute('data-value'));
                stars.forEach((s, i) => {
                    s.classList.toggle('hovered', i < val);
                });
            });
            star.addEventListener('mouseleave', function () {
                stars.forEach((s, i) => {
                    s.classList.toggle('hovered', false);
                });
            });
            star.addEventListener('click', function () {
                selected = parseInt(this.getAttribute('data-value'));
                stars.forEach((s, i) => {
                    s.classList.toggle('selected', i < selected);
                });
                const enumValues = ['ONE', 'TWO', 'THREE', 'FOUR', 'FIVE'];
                ratingInput.value = enumValues[selected - 1];
            });
        });
    }

    const anonymousCheckbox = document.getElementById('anonymousCheckbox');
    const nameInput = document.getElementById('name');

    if (anonymousCheckbox && nameInput) {
        anonymousCheckbox.addEventListener('change', function () {
            if (this.checked) {
                nameInput.value = 'Анонимен';
                nameInput.style.display = 'none';
            } else {
                nameInput.value = '';
                nameInput.style.display = 'block';
            }
        });
    }

    const reviewForm = document.querySelector('.review-form');
    if (reviewForm) {
        reviewForm.addEventListener('submit', function (e) {
            let valid = true;
            const ratingInput = document.getElementById('ratingInput');
            const ratingError = document.getElementById('ratingError');
            const nameInput = document.getElementById('name');
            const nameError = document.getElementById('nameError');
            const commentInput = document.getElementById('comment');
            const commentError = document.getElementById('commentError');

            if (ratingInput && ratingError && !ratingInput.value) {
                ratingError.style.display = 'block';
                valid = false;
            } else if (ratingError) {
                ratingError.style.display = 'none';
            }

            if (nameInput && nameError && nameInput.style.display !== 'none' && 
                (!nameInput.value || nameInput.value.trim().length < 2)) {
                nameError.style.display = 'block';
                valid = false;
            } else if (nameError) {
                nameError.style.display = 'none';
            }

            if (commentInput && commentError && commentInput.value && 
                commentInput.value.trim().length > 0 && commentInput.value.trim().length < 3) {
                commentError.style.display = 'block';
                valid = false;
            } else if (commentError) {
                commentError.style.display = 'none';
            }

            if (!valid) {
                e.preventDefault();
            }
        });
    }
}

export default { init: initReview };

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initReview);
} else {
    initReview();
}