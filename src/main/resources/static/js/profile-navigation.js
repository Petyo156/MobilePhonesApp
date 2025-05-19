document.addEventListener("DOMContentLoaded", function () {
    const sections = document.querySelectorAll("main > section");
    const navLinks = document.querySelectorAll(".profile-nav-strip .nav-link");

    function showSection(id) {
        sections.forEach(section => {
            section.style.display = section.id === id ? "block" : "none";
        });
        window.scrollTo(0, 0);
    }

    const initialHash = window.location.hash.substring(1);
    if (initialHash) {
        showSection(initialHash);
    } else {
        showSection("profile");
    }

    navLinks.forEach(link => {
        link.addEventListener("click", function (e) {
            e.preventDefault(); // Prevent default anchor scroll
            const targetId = this.getAttribute("href").substring(1);
            showSection(targetId);
            history.pushState(null, "", `#${targetId}`);
        });
    });
});
