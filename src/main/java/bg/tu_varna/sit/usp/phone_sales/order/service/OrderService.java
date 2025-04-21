package bg.tu_varna.sit.usp.phone_sales.order.service;

import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final CartService cartService;

    @Autowired
    public OrderService(CartService cartService) {
        this.cartService = cartService;
    }


    public void makeOrder(User user, OrderRequest orderRequest) {
//        cartService.

    }

    public CheckoutResponse getCheckoutResponse(User user) {
        List<CartItem> cartItems = user.getCart().getCartItems();
        BigDecimal totalPrice = cartService.getTotalPrice(cartItems);
        Integer summary = cartService.getSummary(cartItems);

        return initializeCheckoutResponse(summary, totalPrice);
    }

    private CheckoutResponse initializeCheckoutResponse(Integer summary, BigDecimal totalPrice) {
        return CheckoutResponse.builder()
                .summary(summary)
                .totalPrice(totalPrice.toString())
                .build();
    }

    //hardcoded for now
    public CheckoutResponse applyDiscount(User user, String discountCode) {
        return initializeCheckoutResponse(1, BigDecimal.ONE);
    }
}
