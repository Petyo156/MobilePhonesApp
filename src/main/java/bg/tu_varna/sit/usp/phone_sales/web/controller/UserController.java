package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ChangePasswordRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.ExtendedOrderResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserController {
    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping()
    @RequireAuthenticatedUser
    public ModelAndView getProfilePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/profile");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        List<OrderResponse> orders = orderService.getAllOrders(user);

        modelAndView.addObject("changePasswordRequest", new ChangePasswordRequest());
        modelAndView.addObject("user", user);
        modelAndView.addObject("orders", orders);

        return modelAndView;
    }

    @GetMapping("/order/{orderNumber}")
    @RequireAuthenticatedUser
    public ModelAndView getOrderPage(@PathVariable String orderNumber,
                                     @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/order");
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        ExtendedOrderResponse extendedOrderResponse = orderService.getExtendedInformationForOrder(orderNumber, user);
        OrderResponse orderResponse = orderService.getInformationForOrder(orderNumber);

        modelAndView.addObject("orderResponse", orderResponse);
        modelAndView.addObject("extendedOrderResponse", extendedOrderResponse);

        return modelAndView;
    }

    @PostMapping("/password")
    @RequireAuthenticatedUser
    public String changePassword(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 @Valid @ModelAttribute ChangePasswordRequest changePasswordRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("changePasswordRequest", changePasswordRequest);
            redirectAttributes.addFlashAttribute("errorMessage", "Password must be at least 3 symbols.");
            return "redirect:/profile";
        }

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        userService.changePassword(changePasswordRequest, user);

        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
        return "redirect:/profile";
    }

}
