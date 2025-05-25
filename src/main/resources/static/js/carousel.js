function initCarousel() {
    const viewToggleBtns = document.querySelectorAll('.view-toggle-btn');
    const carouselContainer = document.querySelector('.carousel-container');
    const modelContainer = document.querySelector('.model-container');

    if (!viewToggleBtns.length || !carouselContainer || !modelContainer) return;

    viewToggleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            viewToggleBtns.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');

            if (btn.dataset.view === 'carousel') {
                carouselContainer.classList.add('active');
                modelContainer.classList.remove('active');
            } else {
                carouselContainer.classList.remove('active');
                modelContainer.classList.add('active');
            }
        });
    });
}

export default { init: initCarousel };

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initCarousel);
} else {
    initCarousel();
}
