package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ChangePasswordRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class UserController {
//    private final UserService userService;
//    private final InventoryService inventoryService;
//
//    @Autowired
//    public UserController(UserService userService, InventoryService inventoryService) {
//        this.userService = userService;
//        this.inventoryService = inventoryService;
//    }
//
//    @GetMapping()
//    @RequireAuthenticatedUser
//    public ModelAndView getProfilePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//        ModelAndView modelAndView = new ModelAndView("home/profile");
//
//        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        List<OrderResponse> orders = inventoryService.getAllOrdersForUser(user);
//
//        modelAndView.addObject("changePasswordRequest", new ChangePasswordRequest());
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("orders", orders);
//
//        return modelAndView;
//    }
//
//    @PostMapping("/password")
//    @RequireAuthenticatedUser
//    public String changePassword(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
//                                 @Valid @ModelAttribute ChangePasswordRequest changePasswordRequest,
//                                 BindingResult bindingResult,
//                                 RedirectAttributes redirectAttributes) {
//        if (bindingResult.hasErrors()) {
//            return "home/profile";
//        }
//
//        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        userService.changePassword(changePasswordRequest, user);
//
//        redirectAttributes.addFlashAttribute("successMessage", "Password changed successfully");
//        return "redirect:/profile";
//    }

}
