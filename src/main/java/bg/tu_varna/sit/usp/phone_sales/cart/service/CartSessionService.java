package bg.tu_varna.sit.usp.phone_sales.cart.service;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartSessionService {

    private static final String CHECKOUT_RESPONSE = "checkoutResponse";
    private static final String DISCOUNT_CODE = "discountCode";
    private static final String DISCOUNT_APPLIED = "discountApplied";
    private static final String ERROR = "error";

    public void storeDiscountInfo(HttpSession session, Object checkoutResponse, String discountCode) {
        session.setAttribute(CHECKOUT_RESPONSE, checkoutResponse);
        session.setAttribute(DISCOUNT_CODE, discountCode);
        session.setAttribute(DISCOUNT_APPLIED, true);
    }

    public void storeCheckoutResponse(HttpSession session, Object checkoutResponse) {
        session.setAttribute(CHECKOUT_RESPONSE, checkoutResponse);
        log.info("Storing new checkout response in session");
    }

    public void clearDiscountInfo(HttpSession session) {
        session.removeAttribute(CHECKOUT_RESPONSE);
        session.removeAttribute(DISCOUNT_CODE);
        session.removeAttribute(DISCOUNT_APPLIED);
        session.removeAttribute(ERROR);
        log.info("Clearing discount info");
    }

    public Object getCheckoutResponse(HttpSession session) {
        return session.getAttribute(CHECKOUT_RESPONSE);
    }

    public Boolean isDiscountApplied(HttpSession session) {
        Boolean applied = (Boolean) session.getAttribute(DISCOUNT_APPLIED);
        return applied != null && applied;
    }
}