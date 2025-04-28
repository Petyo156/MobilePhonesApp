package bg.tu_varna.sit.usp.phone_sales.cart.service;

import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

@Service
@Slf4j
public class CartViewModelService {

    private final CartSessionService cartSessionService;
    private final OrderService orderService;

    @Autowired
    public CartViewModelService(CartSessionService cartSessionService, OrderService orderService) {
        this.cartSessionService = cartSessionService;
        this.orderService = orderService;
    }

    public void attachSessionAttributes(ModelAndView modelAndView, HttpSession session, User user) {
        boolean discountApplied = cartSessionService.isDiscountApplied(session);
        modelAndView.addObject("user", user);
        CheckoutResponse checkoutResponse = getCheckoutResponse(session, user);
        modelAndView.addObject("checkoutResponse", checkoutResponse);
        if (discountApplied) {
            log.info("Discount applied");
            modelAndView.addObject("discountApplied", true);
        }
    }

    public CheckoutResponse getCheckoutResponse(HttpSession session, User user) {
        CheckoutResponse checkoutResponse = (CheckoutResponse) cartSessionService.getCheckoutResponse(session);
        if (checkoutResponse == null) {
            checkoutResponse = orderService.getCheckoutResponse(user);
            cartSessionService.storeCheckoutResponse(session, checkoutResponse);
        }
        return checkoutResponse;
    }

    public void attachAndClearError(ModelAndView modelAndView, HttpSession session) {
        String error = (String) session.getAttribute("error");
        if (error != null) {
            log.info("Adding invalid code message");
            modelAndView.addObject("error", error);
            session.removeAttribute("error");
        }
    }
}
