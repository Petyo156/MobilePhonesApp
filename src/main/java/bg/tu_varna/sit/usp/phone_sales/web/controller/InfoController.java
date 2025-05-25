package bg.tu_varna.sit.usp.phone_sales.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/contact/submit")
    public String submitContact() {
        // TODO: Implement contact form submission logic
        return "redirect:/contact?success";
    }
} 