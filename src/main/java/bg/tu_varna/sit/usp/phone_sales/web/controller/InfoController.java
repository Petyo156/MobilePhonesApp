package bg.tu_varna.sit.usp.phone_sales.web.controller;


import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class InfoController {

    private final UserService userService;

    @Autowired
    public InfoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/about")
    public ModelAndView about(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        ModelAndView modelAndView = new ModelAndView("info/about");
        modelAndView.addObject("user", user);

        return modelAndView;
    }


    @GetMapping("/contact")
    public ModelAndView contact(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        ModelAndView modelAndView = new ModelAndView("info/contact");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/faq")
    public ModelAndView faq(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        ModelAndView modelAndView = new ModelAndView("info/faq");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/privacy")
    public ModelAndView privacy(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        ModelAndView modelAndView = new ModelAndView("info/privacy");
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/contact/submit")
    public String submitContactForm() {
        return "redirect:/contact?success";
    }

} 