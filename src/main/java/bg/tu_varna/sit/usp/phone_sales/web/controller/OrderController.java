package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartSessionService;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartViewModelService;
import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.CheckoutResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.DeliveryMethodResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.PaymentMethodResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final CartViewModelService cartViewModelService;
    private final CartSessionService cartSessionService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, CartViewModelService cartViewModelService, CartSessionService cartSessionService) {
        this.userService = userService;
        this.orderService = orderService;
        this.cartViewModelService = cartViewModelService;
        this.cartSessionService = cartSessionService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView getCheckoutPage(
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
            HttpSession session) {

        ModelAndView modelAndView = new ModelAndView("user/checkout");
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        cartViewModelService.attachSessionAttributes(modelAndView, session, user);
        cartViewModelService.attachAndClearError(modelAndView, session);

        List<DeliveryMethodResponse> deliveryMethodValues = orderService.getDeliveryMethodValues();
        List<PaymentMethodResponse> paymentMethodValues = orderService.getPaymentMethodValues();

        modelAndView.addObject("orderRequest", new OrderRequest());
        modelAndView.addObject("deliveryMethodValues", deliveryMethodValues);
        modelAndView.addObject("paymentMethodValues", paymentMethodValues);
        return modelAndView;
    }

    @PostMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView checkout(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 @Valid @ModelAttribute OrderRequest orderRequest,
                                 BindingResult bindingResult,
                                 HttpSession session) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        CheckoutResponse checkoutResponse = cartViewModelService.getCheckoutResponse(session, user);
        ModelAndView modelAndView = new ModelAndView("user/checkout");
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("orderRequest", orderRequest);
            modelAndView.addObject("checkoutResponse", checkoutResponse);
            return modelAndView;
        }
        orderService.makeOrder(user, orderRequest, checkoutResponse);
        cartSessionService.clearDiscountInfo(session);
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
