package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitPhoneRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PhoneService phoneService;

    @Autowired
    public AdminController(UserService userService, PhoneService phoneService) {
        this.userService = userService;
        this.phoneService = phoneService;
    }

    @GetMapping("/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPhonePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("admin/add-phone");

        User user = userService.getAuthenticatedUser(authenticationMetadata);

        modelAndView.addObject("submitPhoneRequest", new SubmitPhoneRequest());
        modelAndView.addObject("user", user);

        return modelAndView;
    }


    @PostMapping("/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPhone(@Valid @ModelAttribute SubmitPhoneRequest submitPhoneRequest,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("submitPhoneRequest", submitPhoneRequest);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submitPhoneRequest", bindingResult);
            return new ModelAndView("redirect:/admin/phone");
        }

        Phone phone = phoneService.submitPhone(submitPhoneRequest);

        return new ModelAndView("redirect:/");
    }


}
