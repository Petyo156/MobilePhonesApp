package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CartController {
    private final UserService userService;

    @Autowired
    public CartController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/cart")
    @RequireAuthenticatedUser
    public ModelAndView getCartPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/cart");
        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        CartResponse cart = inventoryService.getCartForUser(user);

        modelAndView.addObject("user", user);
//        modelAndView.addObject("cart", cart);

        return modelAndView;
    }

    @PostMapping("/cart/up/{slug}")
    @RequireAuthenticatedUser
    public String upProductQuantity(@PathVariable String slug,
                                    @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);
//        inventoryService.incrementProductQuantity(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/cart/down/{slug}")
    @RequireAuthenticatedUser
    public String downProductQuantity(@PathVariable String slug,
                                      @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);

//        inventoryService.decrementProductQuantity(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/cart/{slug}/delete")
    @RequireAuthenticatedUser
    public String removeProductFromCart(@PathVariable String slug,
                                        @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);

//        inventoryService.removeProductFromCart(user, slug);
        return "redirect:/cart";
    }
}
