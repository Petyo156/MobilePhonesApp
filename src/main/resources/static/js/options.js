function initOptions() {
    const colorOptions = document.querySelectorAll('.color-option');
    const storageOptions = document.querySelectorAll('.storage-option');

    if (colorOptions.length > 0) {
        colorOptions.forEach(button => {
            button.addEventListener('click', function () {
                const slug = this.dataset.slug;
                if (slug) {
                    window.location.href = `/phone/${slug}`;
                }
            });
        });
    }

    if (storageOptions.length > 0) {
        storageOptions.forEach(button => {
            button.addEventListener('click', function () {
                const slug = this.dataset.slug;
                if (slug) {
                    window.location.href = `/phone/${slug}`;
                }
            });
        });
    }
}

export default { init: initOptions };

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initOptions);
} else {
    initOptions();
}