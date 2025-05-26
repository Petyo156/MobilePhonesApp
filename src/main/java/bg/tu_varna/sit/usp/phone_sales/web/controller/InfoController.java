package bg.tu_varna.sit.usp.phone_sales.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InfoController {

    @GetMapping("/about")
    public String about() {
        return "info/about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "info/contact";
    }

    @GetMapping("/faq")
    public String faq() {
        return "info/faq";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "info/privacy";
    }

    @PostMapping("/contact/submit")
    public String submitContactForm() {
        return "redirect:/contact?success";
    }
} 