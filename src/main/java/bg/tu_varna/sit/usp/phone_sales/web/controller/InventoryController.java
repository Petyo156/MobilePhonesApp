package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class InventoryController {
//    private final InventoryService inventoryService;
//    private final UserService userService;
//    private final DeliveryOptionService deliveryOptionService;
//
//    @Autowired
//    public InventoryController(InventoryService inventoryService, UserService userService, DeliveryOptionService deliveryOptionService) {
//        this.inventoryService = inventoryService;
//        this.userService = userService;
//        this.deliveryOptionService = deliveryOptionService;
//    }
//
//    @GetMapping("/checkout")
//    @RequireAuthenticatedUser
//    public ModelAndView getCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//        ModelAndView modelAndView = new ModelAndView("user/checkout");
//
//        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        List<Inventory> cart = inventoryService.getAllItemsInCartForUser(user);
//        String price = inventoryService.getTotalPriceForItemsInCart(cart);
//
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("cart", cart);
//        modelAndView.addObject("price", price);
//        modelAndView.addObject("checkoutRequest", new CheckoutRequest());
//
//        return modelAndView;
//    }
//
//    @PostMapping("/checkout")
//    @RequireAuthenticatedUser
//    public ModelAndView checkout(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
//                                 BindingResult bindingResult,
//                                 @Valid @ModelAttribute CheckoutRequest checkoutRequest) {
//
//        ModelAndView modelAndView = new ModelAndView("user/checkout");
//        if(bindingResult.hasErrors()) {
//            modelAndView.addObject("checkoutRequest", new CheckoutRequest());
//            return modelAndView;
//        }
//
//        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        deliveryOptionService.makeOrder(user, checkoutRequest);
//        return new ModelAndView("redirect:/checkout/success");
//    }
//
//    @GetMapping("/cart")
//    @RequireAuthenticatedUser
//    public ModelAndView getCartPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//        ModelAndView modelAndView = new ModelAndView("user/cart");
//        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        CartResponse cart = inventoryService.getCartForUser(user);
//
//        modelAndView.addObject("user", user);
//        modelAndView.addObject("cart", cart);
//
//        return modelAndView;
//    }
//
//    @PostMapping("/cart/up/{slug}")
//    @RequireAuthenticatedUser
//    public String upProductQuantity(@PathVariable String slug,
//                                    @AuthenticationPrincipal AuthenticationMetadata authMeta) {
//        User user = userService.getAuthenticatedUser(authMeta);
//        inventoryService.incrementProductQuantity(user, slug);
//        return "redirect:/cart";
//    }
//
//    @PostMapping("/cart/down/{slug}")
//    @RequireAuthenticatedUser
//    public String downProductQuantity(@PathVariable String slug,
//                                      @AuthenticationPrincipal AuthenticationMetadata authMeta) {
//        User user = userService.getAuthenticatedUser(authMeta);
//
//        inventoryService.decrementProductQuantity(user, slug);
//        return "redirect:/cart";
//    }
//
//    @PostMapping("/cart/{slug}/delete")
//    @RequireAuthenticatedUser
//    public String removeProductFromCart(@PathVariable String slug,
//                                      @AuthenticationPrincipal AuthenticationMetadata authMeta) {
//        User user = userService.getAuthenticatedUser(authMeta);
//
//        inventoryService.removeProductFromCart(user, slug);
//        return "redirect:/cart";
//    }
//
//    @GetMapping("/checkout/success")
//    @RequireAuthenticatedUser
//    public ModelAndView getSuccessfulCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
//        ModelAndView modelAndView = new ModelAndView("user/successful-checkout");
//
//        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        modelAndView.addObject("user", user);
//
//        return modelAndView;
//    }
}
