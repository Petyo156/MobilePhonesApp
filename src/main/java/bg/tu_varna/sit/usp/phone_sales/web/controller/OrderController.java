package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final DiscountCodeService discountCodeService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, DiscountCodeService discountCodeService) {
        this.userService = userService;
        this.orderService = orderService;
        this.discountCodeService = discountCodeService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView getCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                        @ModelAttribute("checkoutResponse") CheckoutResponse checkoutResponse,
                                        @ModelAttribute("error") String error) {

        ModelAndView modelAndView = new ModelAndView("user/checkout");

        User user = userService.getAuthenticatedUser(authenticationMetadata);

        if (checkoutResponse == null) {
            checkoutResponse = orderService.getCheckoutResponse(user);
        }

        modelAndView.addObject("user", user);
        modelAndView.addObject("orderRequest", new OrderRequest());
        modelAndView.addObject("checkoutResponse", checkoutResponse);
        modelAndView.addObject("error", error);

        return modelAndView;
    }

    @PostMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView checkout(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 BindingResult bindingResult,
                                 @Valid @ModelAttribute OrderRequest orderRequest) {

        ModelAndView modelAndView = new ModelAndView("user/checkout");
        if(bindingResult.hasErrors()) {
            modelAndView.addObject("orderRequest", orderRequest);
            return modelAndView;
        }

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        orderService.makeOrder(user, orderRequest);
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
