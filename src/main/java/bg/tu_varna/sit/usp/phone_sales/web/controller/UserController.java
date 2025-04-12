package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.inventory.service.InventoryService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.OrderResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserController {
    private final UserService userService;
    private final InventoryService inventoryService;

    @Autowired
    public UserController(UserService userService, InventoryService inventoryService) {
        this.userService = userService;
        this.inventoryService = inventoryService;
    }

    @GetMapping()
    @RequireAuthenticatedUser
    public ModelAndView getProfilePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("home/profile");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        List<OrderResponse> orders = inventoryService.getAllOrdersForUser(user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }
}
