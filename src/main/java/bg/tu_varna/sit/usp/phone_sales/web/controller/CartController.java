package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
public class CartController {
    private final UserService userService;
    private final CartService cartService;
    private final DiscountCodeService discountCodeService;
    private final OrderService orderService;

    @Autowired
    public CartController(UserService userService, CartService cartService, DiscountCodeService discountCodeService, OrderService orderService) {
        this.userService = userService;
        this.cartService = cartService;
        this.discountCodeService = discountCodeService;
        this.orderService = orderService;
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

    @PostMapping("/discount")
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView applyDiscount(
            @RequestParam("discountCode") String discountCode,
            @AuthenticationPrincipal AuthenticationMetadata auth,
            RedirectAttributes redirectAttributes) {

        User user = userService.getAuthenticatedUser(auth);
        boolean codeIsValid = discountCodeService.isValidCode(discountCode);
        if (codeIsValid) {
            CheckoutResponse checkoutResponse = orderService.applyDiscount(user, discountCode);
            redirectAttributes.addFlashAttribute("checkoutResponse", checkoutResponse);
            redirectAttributes.addFlashAttribute("discountApplied", true);
            redirectAttributes.addFlashAttribute("discountCode", discountCode);
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid discount code.");
        }

        return new ModelAndView("redirect:/checkout");
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
