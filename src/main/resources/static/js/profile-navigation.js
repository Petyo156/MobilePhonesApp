document.addEventListener("DOMContentLoaded", function () {
    const tabContents = document.querySelectorAll(".tab-content");
    const tabLinks = document.querySelectorAll(".tab-link");

    function activateTab(tabId) {
        tabContents.forEach(content => {
            content.classList.remove("active");
        });
        
        tabLinks.forEach(link => {
            link.classList.remove("active");
        });
        
        document.getElementById(tabId).classList.add("active");
        document.querySelector(`.tab-link[href="#${tabId}"]`).classList.add("active");
        
        window.scrollTo(0, 0);
    }

    const initialHash = window.location.hash ? window.location.hash.substring(1) : "profile";
    activateTab(initialHash);

    tabLinks.forEach(link => {
        link.addEventListener("click", function (e) {
            e.preventDefault();
            const targetId = this.getAttribute("href").substring(1);
            activateTab(targetId);
            history.pushState(null, "", `#${targetId}`);
        });
    });
    
    window.addEventListener("popstate", function() {
        const currentHash = window.location.hash ? window.location.hash.substring(1) : "profile";
        activateTab(currentHash);
    });
    
    const itemsPreviews = document.querySelectorAll(".items-preview");
    itemsPreviews.forEach(preview => {
        preview.addEventListener("mouseenter", function() {
            const tooltip = this.querySelector(".items-tooltip");
            tooltip.style.display = "block";
        });
        
        preview.addEventListener("mouseleave", function() {
            const tooltip = this.querySelector(".items-tooltip");
            tooltip.style.display = "none";
        });
    });
});
