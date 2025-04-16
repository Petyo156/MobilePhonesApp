package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final UserService userService;
    private final CartService cartService;

    @Autowired
    public CartController(UserService userService, CartService cartService) {
        this.userService = userService;
        this.cartService = cartService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    public ModelAndView getCartPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/cart");
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        CartResponse cart = cartService.getCartResponseForUser(user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("cart", cart);

        return modelAndView;
    }

    @PostMapping("/up/{slug}")
    @RequireAuthenticatedUser
    public String upPhoneQuantity(@PathVariable String slug,
                                  @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);
        cartService.incrementPhoneQuantityInCart(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/down/{slug}")
    @RequireAuthenticatedUser
    public String downPhoneQuantity(@PathVariable String slug,
                                    @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);

        cartService.decrementPhoneQuantityInCart(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/{slug}/delete")
    @RequireAuthenticatedUser
    public String removePhoneFromCart(@PathVariable String slug,
                                      @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);

        cartService.removePhoneFromCart(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/{slug}")
    @RequireAuthenticatedUser
    public String addPhoneToCart(@AuthenticationPrincipal AuthenticationMetadata auth,
                                 @PathVariable String slug) {
        User user = userService.getAuthenticatedUser(auth);
        cartService.addPhoneToCart(slug, user);

        return "redirect:/cart";
    }
}
