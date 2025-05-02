package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.EnsureValidCartItems;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartSessionService;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartViewModelService;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.CheckoutResponse;
import jakarta.servlet.http.HttpSession;
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
    private final CartSessionService cartSessionService;
    private final CartViewModelService cartViewModelService;

    @Autowired
    public CartController(UserService userService, CartService cartService, DiscountCodeService discountCodeService, OrderService orderService, CartSessionService cartSessionService, CartViewModelService cartViewModelService) {
        this.userService = userService;
        this.cartService = cartService;
        this.discountCodeService = discountCodeService;
        this.orderService = orderService;
        this.cartSessionService = cartSessionService;
        this.cartViewModelService = cartViewModelService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    @EnsureValidCartItems
    public ModelAndView getCartPage(
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
            HttpSession session) {

        ModelAndView modelAndView = new ModelAndView("user/cart");
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        CartResponse cart = cartService.getCartResponseForUser(user);
        modelAndView.addObject("cart", cart);

        cartViewModelService.attachSessionAttributes(modelAndView, session, user);
        cartViewModelService.attachAndClearError(modelAndView, session);

        return modelAndView;
    }

    @PostMapping("/apply-discount")
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public String applyDiscount(
            @RequestParam("discountCode") String discountCode,
            @AuthenticationPrincipal AuthenticationMetadata auth,
            HttpSession session) {
        cartSessionService.clearDiscountInfo(session);
        User user = userService.getAuthenticatedUser(auth);
        if (discountCodeService.isValidCode(discountCode)) {
            CheckoutResponse checkoutResponse = orderService.applyDiscount(user, discountCode);
            cartSessionService.storeDiscountInfo(session, checkoutResponse, discountCode);
        } else {
            session.setAttribute("error", "Invalid discount code.");
        }

        return "redirect:/cart";
    }

    @PostMapping("/clear-discount")
    @RequireNotEmptyCart
    @RequireAuthenticatedUser
    public String clearDiscountSession(HttpSession session) {
        cartSessionService.clearDiscountInfo(session);
        return "redirect:/cart";
    }

    @PostMapping("/up/{slug}")
    @RequireAuthenticatedUser
    public String upPhoneQuantity(@PathVariable String slug,
                                  @AuthenticationPrincipal AuthenticationMetadata authMeta,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = userService.getAuthenticatedUser(authMeta);
        cartSessionService.clearDiscountInfo(session);
        boolean enoughStock = cartService.incrementPhoneQuantityInCart(user, slug);
        if (!enoughStock) {
            redirectAttributes.addFlashAttribute("cartError", "Not enough stock! Chill bro.");
        }
        return "redirect:/cart";
    }

    @PostMapping("/down/{slug}")
    @RequireAuthenticatedUser
    public String downPhoneQuantity(@PathVariable String slug,
                                    @AuthenticationPrincipal AuthenticationMetadata authMeta,
                                    HttpSession session) {
        User user = userService.getAuthenticatedUser(authMeta);
        cartSessionService.clearDiscountInfo(session);
        cartService.decrementPhoneQuantityInCart(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/{slug}/delete")
    @RequireAuthenticatedUser
    public String removePhoneFromCart(@PathVariable String slug,
                                      @AuthenticationPrincipal AuthenticationMetadata authMeta,
                                      HttpSession session) {
        User user = userService.getAuthenticatedUser(authMeta);
        cartSessionService.clearDiscountInfo(session);
        cartService.removePhoneFromCart(user, slug);
        return "redirect:/cart";
    }

    @PostMapping("/{slug}")
    @RequireAuthenticatedUser
    public String addPhoneToCart(@AuthenticationPrincipal AuthenticationMetadata auth,
                                 @PathVariable String slug,
                                 HttpSession session) {
        User user = userService.getAuthenticatedUser(auth);
        cartSessionService.clearDiscountInfo(session);
        cartService.addPhoneToCart(slug, user);
        return "redirect:/cart";
    }
}
