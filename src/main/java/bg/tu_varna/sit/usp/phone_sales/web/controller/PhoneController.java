package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.inventory.service.InventoryService;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/phone")
public class PhoneController {
    private final PhoneService phoneService;
    private final InventoryService inventoryService;
    private final UserService userService;

    public PhoneController(PhoneService phoneService, InventoryService inventoryService, UserService userService) {
        this.phoneService = phoneService;
        this.inventoryService = inventoryService;
        this.userService = userService;
    }

    @GetMapping("/{slug}")
    public ModelAndView getPhonePage(@PathVariable String slug, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("home/phone");

        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        modelAndView.addObject("phoneResponse", phoneResponse);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @PostMapping("/{slug}")
    @RequireAuthenticatedUser
    public String addPhoneToCart(@AuthenticationPrincipal AuthenticationMetadata auth,
                                 @PathVariable String slug) {
        User user = userService.getAuthenticatedUser(auth);
        inventoryService.addPhoneToCartForUser(user, slug);

        return "redirect:/phone/" + slug;
    }
}
