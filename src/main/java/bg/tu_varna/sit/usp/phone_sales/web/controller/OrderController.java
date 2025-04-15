package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.security.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    private final UserService userService;

    @Autowired
    public OrderController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    public ModelAndView getCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/checkout");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        List<Inventory> cart = inventoryService.getAllItemsInCartForUser(user);
//        String price = inventoryService.getTotalPriceForItemsInCart(cart);

        modelAndView.addObject("user", user);
//        modelAndView.addObject("cart", cart);
//        modelAndView.addObject("price", price);
//        modelAndView.addObject("checkoutRequest", new CheckoutRequest());

        return modelAndView;
    }

    @PostMapping
    @RequireAuthenticatedUser
    public ModelAndView checkout(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 BindingResult bindingResult){
//                                 @Valid @ModelAttribute CheckoutRequest checkoutRequest) {

        ModelAndView modelAndView = new ModelAndView("user/checkout");
        if(bindingResult.hasErrors()) {
//            modelAndView.addObject("checkoutRequest", new CheckoutRequest());
            return modelAndView;
        }

        User user = userService.getAuthenticatedUser(authenticationMetadata);
//        deliveryOptionService.makeOrder(user, checkoutRequest);
        return new ModelAndView("redirect:/checkout/success");
    }

    @GetMapping("/success")
    @RequireAuthenticatedUser
    public ModelAndView getSuccessfulCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/successful-checkout");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
