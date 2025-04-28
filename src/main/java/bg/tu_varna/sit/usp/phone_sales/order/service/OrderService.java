package bg.tu_varna.sit.usp.phone_sales.order.service;

import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.DeliveryMethod;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.PaymentMethod;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.CheckoutResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.DeliveryMethodResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.PaymentMethodResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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
        System.out.println();
    }

    public CheckoutResponse getCheckoutResponse(User user) {
        List<CartItem> cartItems = user.getCart().getCartItems();
        BigDecimal totalPrice = cartService.getTotalPrice(cartItems);
        Integer summary = cartService.getSummary(cartItems);

        return initializeCheckoutResponse(summary, totalPrice, totalPrice, BigDecimal.ZERO, null);
    }

    public CheckoutResponse applyDiscount(User user, String discountCode) {
        List<CartItem> cartItems = user.getCart().getCartItems();

        Integer summary = cartService.getSummary(cartItems);
        BigDecimal totalPrice = cartService.getTotalPrice(cartItems);
        BigDecimal discountPercent = discountCodeService.getDiscountCodePercent(discountCode);
        BigDecimal discountPrice = cartService.getTotalPriceAfterDiscountCode(cartItems, discountPercent);

        return initializeCheckoutResponse(summary, totalPrice, discountPrice, discountPercent, discountCode);
    }

    private CheckoutResponse initializeCheckoutResponse(Integer summary, BigDecimal totalPrice, BigDecimal discountPrice, BigDecimal discountPercent, String discountCode) {
        return CheckoutResponse.builder()
                .summary(summary)
                .totalPrice(decimalFormat.format(totalPrice))
                .discountPercent(decimalFormat.format(discountPercent))
                .discountPrice(decimalFormat.format(discountPrice))
                .priceDifference(decimalFormat.format(totalPrice.subtract(discountPrice)))
                .discountCode(discountCode)
                .build();
    }

    public List<DeliveryMethodResponse> getDeliveryMethodValues() {
        List<DeliveryMethodResponse> responses = new ArrayList<>();
        for(DeliveryMethod deliveryMethod : DeliveryMethod.values()){
            DeliveryMethodResponse response = initializeDeliveryMethodResponse(deliveryMethod);
            responses.add(response);
        }
        log.info("Initialized delivery method responses");
        return responses;
    }

    public List<PaymentMethodResponse> getPaymentMethodValues() {
        List<PaymentMethodResponse> responses = new ArrayList<>();
        for(PaymentMethod paymentMethod : PaymentMethod.values()){
            PaymentMethodResponse response = initializePaymentMethodResponse(paymentMethod);
            responses.add(response);
        }
        log.info("Initialized payment method responses");
        return responses;
    }

    private PaymentMethodResponse initializePaymentMethodResponse(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
                .description(paymentMethod.getDescription())
                .build();
    }

    private DeliveryMethodResponse initializeDeliveryMethodResponse(DeliveryMethod deliveryMethod) {
        return DeliveryMethodResponse.builder()
                .price(decimalFormat.format(deliveryMethod.getPrice()))
                .description(deliveryMethod.getDescription())
                .build();
    }


}
