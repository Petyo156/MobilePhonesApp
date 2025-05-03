package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartSessionService;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartValidatorService;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartViewModelService;
import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.PersonalInformationResponse;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final CartViewModelService cartViewModelService;
    private final CartSessionService cartSessionService;
    private final CartValidatorService cartValidatorService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, CartViewModelService cartViewModelService, CartSessionService cartSessionService, CartValidatorService cartValidatorService) {
        this.userService = userService;
        this.orderService = orderService;
        this.cartViewModelService = cartViewModelService;
        this.cartSessionService = cartSessionService;
        this.cartValidatorService = cartValidatorService;
    }

    @GetMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView getCheckoutPage(
            @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        ModelAndView modelAndView = new ModelAndView("user/checkout");
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        boolean cartModified = cartValidatorService.validateAndCleanCart(user);
        if (cartModified) {
            redirectAttributes.addFlashAttribute("cartWarning", "Some items were removed due to stock changes.");
            return new ModelAndView("redirect:/cart");
        }

        cartViewModelService.attachSessionAttributes(modelAndView, session, user);
        cartViewModelService.attachAndClearError(modelAndView, session);
        PersonalInformationResponse personalInformationResponse = userService.getPersonalInformationResponse(user);
        List<DeliveryMethodResponse> deliveryMethodValues = orderService.getDeliveryMethodValues();
        List<PaymentMethodResponse> paymentMethodValues = orderService.getPaymentMethodValues();

        modelAndView.addObject("orderRequest", new OrderRequest());
        modelAndView.addObject("deliveryMethodValues", deliveryMethodValues);
        modelAndView.addObject("paymentMethodValues", paymentMethodValues);
        modelAndView.addObject("personalInformationResponse", personalInformationResponse);

        return modelAndView;
    }

    @PostMapping
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView checkout(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                 @Valid @ModelAttribute OrderRequest orderRequest,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        boolean cartModified = cartValidatorService.validateAndCleanCart(user);
        if (cartModified) {
            redirectAttributes.addFlashAttribute("cartWarning", "Some items were removed due to stock changes.");
            return new ModelAndView("redirect:/cart");
        }

        CheckoutResponse checkoutResponse = cartViewModelService.getCheckoutResponse(session, user);
        ModelAndView modelAndView = new ModelAndView("user/checkout");
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("orderRequest", orderRequest);
            modelAndView.addObject("checkoutResponse", checkoutResponse);
            return modelAndView;
        }
        String orderNumber = orderService.makeOrder(user, orderRequest, checkoutResponse);
        redirectAttributes.addAttribute("orderNumber", orderNumber);
        cartSessionService.clearDiscountInfo(session);
        return new ModelAndView("redirect:/checkout/success");
    }

    @GetMapping("/success")
    @RequireAuthenticatedUser
    public ModelAndView getSuccessfulCheckoutPage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                                  @RequestParam("orderNumber") String orderNumber) {//will throw Bad Request in case order number is not present
        ModelAndView modelAndView = new ModelAndView("user/successful-checkout");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        modelAndView.addObject("orderNumber", orderNumber);
        modelAndView.addObject("user", user);

        return modelAndView;
    }
}
