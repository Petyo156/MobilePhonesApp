package bg.tu_varna.sit.usp.phone_sales.order.service;

import bg.tu_varna.sit.usp.phone_sales.cart.model.Cart;
import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Service
public class OrderService {
    private final CartService cartService;
    private final DiscountCodeService discountCodeService;
    private final DecimalFormat decimalFormat;

    @Autowired
    public OrderService(CartService cartService, DiscountCodeService discountCodeService, DecimalFormat decimalFormat) {
        this.cartService = cartService;
        this.discountCodeService = discountCodeService;
        this.decimalFormat = decimalFormat;
    }


    public void makeOrder(User user, OrderRequest orderRequest, CheckoutResponse checkoutResponse) {

    }

    public CheckoutResponse getCheckoutResponse(User user) {
        List<CartItem> cartItems = user.getCart().getCartItems();
        BigDecimal totalPrice = cartService.getTotalPrice(cartItems);
        Integer summary = cartService.getSummary(cartItems);

        return initializeCheckoutResponse(summary, totalPrice, totalPrice, BigDecimal.ZERO);
    }

    public CheckoutResponse applyDiscount(User user, String discountCode) {
        List<CartItem> cartItems = user.getCart().getCartItems();

        Integer summary = cartService.getSummary(cartItems);
        BigDecimal totalPrice = cartService.getTotalPrice(cartItems);
        BigDecimal discountPercent = discountCodeService.getDiscountCodePercent(discountCode);
        BigDecimal discountPrice = cartService.getTotalPriceAfterDiscountCode(cartItems, discountPercent);

        return initializeCheckoutResponse(summary, totalPrice, discountPrice, discountPercent);
    }

    private CheckoutResponse initializeCheckoutResponse(Integer summary, BigDecimal totalPrice, BigDecimal discountPrice, BigDecimal discountPercent) {
        return CheckoutResponse.builder()
                .summary(summary)
                .totalPrice(decimalFormat.format(totalPrice))
                .discountPercent(decimalFormat.format(discountPercent))
                .discountPrice(decimalFormat.format(discountPrice))
                .priceDifference(decimalFormat.format(totalPrice.subtract(discountPrice)))
                .build();
    }
}
