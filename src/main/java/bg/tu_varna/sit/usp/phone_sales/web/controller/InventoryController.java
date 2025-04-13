package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.deliveryoption.service.DeliveryOptionService;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import bg.tu_varna.sit.usp.phone_sales.inventory.service.InventoryService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutRequest;
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
import java.util.UUID;

@Controller
public class InventoryController {
    private final InventoryService inventoryService;
    private final UserService userService;
    private final DeliveryOptionService deliveryOptionService;

    @Autowired
    public InventoryController(InventoryService inventoryService, UserService userService, DeliveryOptionService deliveryOptionService) {
        this.inventoryService = inventoryService;
        this.userService = userService;
        this.deliveryOptionService = deliveryOptionService;
    }

    @GetMapping("/checkout")
    @RequireAuthenticatedUser
    public ModelAndView getCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/checkout");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        List<Inventory> cart = inventoryService.getAllItemsInCartForUser(user);
        String price = inventoryService.getTotalPriceForItemsInCart(cart);

        modelAndView.addObject("user", user);
        modelAndView.addObject("cart", cart);
        modelAndView.addObject("price", price);
        modelAndView.addObject("checkoutRequest", new CheckoutRequest());

        return modelAndView;
    }

    @PostMapping("/checkout")
    @RequireAuthenticatedUser
    public ModelAndView checkout(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 BindingResult bindingResult,
                                 @Valid @ModelAttribute CheckoutRequest checkoutRequest) {

        ModelAndView modelAndView = new ModelAndView("user/checkout");
        if(bindingResult.hasErrors()) {
            modelAndView.addObject("checkoutRequest", new CheckoutRequest());
            return modelAndView;
        }

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        deliveryOptionService.makeOrder(user, checkoutRequest);
        return new ModelAndView("redirect:/checkout/success");
    }

    @GetMapping("/cart")
    @RequireAuthenticatedUser
    public ModelAndView getCartPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/cart");
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        CartResponse cart = inventoryService.getCartForUser(user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("cart", cart);

        return modelAndView;
    }

    @PostMapping("/cart/up/{id}")
    @RequireAuthenticatedUser
    public String upProductQuantity(@PathVariable UUID id,
                                    @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);
        inventoryService.incrementProductQuantity(user, id);
        return "redirect:/user/cart";
    }

    @PostMapping("/cart/down/{id}")
    @RequireAuthenticatedUser
    public String downProductQuantity(@PathVariable UUID id,
                                      @AuthenticationPrincipal AuthenticationMetadata authMeta) {
        User user = userService.getAuthenticatedUser(authMeta);

        inventoryService.decrementProductQuantity(user, id);
        return "redirect:/user/cart";
    }

    @GetMapping("/checkout/success")
    @RequireAuthenticatedUser
    public String getSuccessfulCheckoutPage() {

        return "user/successful-checkout";
    }
}
