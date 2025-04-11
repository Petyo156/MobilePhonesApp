package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.inventory.service.InventoryService;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

@Controller("/profile")
public class UserController {
    private final UserService userService;
    private final PhoneService phoneService;
    private final InventoryService inventoryService;

    @Autowired
    public UserController(UserService userService, PhoneService phoneService, InventoryService inventoryService) {
        this.userService = userService;
        this.phoneService = phoneService;
        this.inventoryService = inventoryService;
    }

    @GetMapping("/phone/{slug}")
    public ModelAndView getPhonePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                     @PathVariable String slug) {
        ModelAndView modelAndView = new ModelAndView("home/phone");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseBySlug(slug);

        modelAndView.addObject("user", user);
        modelAndView.addObject("phoneResponse", phoneResponse);
        return modelAndView;
    }

    //TODO
    //-getProfilePage method with past orders
}
