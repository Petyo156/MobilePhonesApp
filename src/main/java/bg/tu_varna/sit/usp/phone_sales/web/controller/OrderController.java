package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
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
    private final OrderService orderService;
    private final CartService cartService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, CartService cartService) {
        this.userService = userService;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView getCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("user/checkout");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        CartResponse cart = cartService.getCartResponseForUser(user);

        modelAndView.addObject("user", user);
        modelAndView.addObject("orderRequest", new OrderRequest());
        modelAndView.addObject("cart", cart);

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
