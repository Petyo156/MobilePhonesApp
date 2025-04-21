package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/checkout")
public class OrderController {
    private final UserService userService;
    private final OrderService orderService;
    private final DiscountService discountService;

    @Autowired
    public OrderController(UserService userService, OrderService orderService, DiscountService discountService) {
        this.userService = userService;
        this.orderService = orderService;
        this.discountService = discountService;
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

    //tova trqbva da e v kolichkata
    @PostMapping("/discount")
    @RequireAuthenticatedUser
    @RequireNotEmptyCart
    public ModelAndView applyDiscount(
            @RequestParam("discountCode") String discountCode,
            @AuthenticationPrincipal AuthenticationMetadata auth,
            RedirectAttributes redirectAttributes) {

        User user = userService.getAuthenticatedUser(auth);
        boolean codeIsValid = discountService.isValidCode(discountCode);
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
