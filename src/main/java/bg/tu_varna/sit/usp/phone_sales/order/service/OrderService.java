package bg.tu_varna.sit.usp.phone_sales.order.service;

import bg.tu_varna.sit.usp.phone_sales.cart.service.CartService;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.cartitem.service.CartItemService;
import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.order.model.Sale;
import bg.tu_varna.sit.usp.phone_sales.order.model.SaleStatus;
import bg.tu_varna.sit.usp.phone_sales.order.repository.OrderRepository;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.DeliveryMethod;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.PaymentMethod;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.SaleDetails;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.service.SaleDetailsService;
import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.orderitem.service.SaleItemService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.CheckoutResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.DeliveryMethodResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.PaymentMethodResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.ExtendedOrderResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.OrderItemResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.OrderResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class OrderService {
    private final CartService cartService;
    private final DiscountCodeService discountCodeService;
    private final DecimalFormat decimalFormat;
    private final SaleDetailsService saleDetailsService;
    private final SaleItemService saleItemService;
    private final OrderRepository orderRepository;
    private final SaleCounterService saleCounterService;
    private final CartItemService cartItemService;
    private final PhoneService phoneService;

    @Autowired
    public OrderService(CartService cartService, DiscountCodeService discountCodeService, DecimalFormat decimalFormat, SaleDetailsService saleDetailsService, SaleItemService saleItemService, OrderRepository orderRepository, SaleCounterService saleCounterService, CartItemService cartItemService, PhoneService phoneService) {
        this.cartService = cartService;
        this.discountCodeService = discountCodeService;
        this.decimalFormat = decimalFormat;
        this.saleDetailsService = saleDetailsService;
        this.saleItemService = saleItemService;
        this.orderRepository = orderRepository;
        this.saleCounterService = saleCounterService;
        this.cartItemService = cartItemService;
        this.phoneService = phoneService;
    }

    @Transactional
    public String makeOrder(User user, OrderRequest orderRequest, CheckoutResponse checkoutResponse) {
        SaleDetails saleDetails = saleDetailsService.initializeSaleDetailsForUser(orderRequest, user);

        String priceToUse = checkoutResponse.getDiscountPrice() != null ?
                checkoutResponse.getDiscountPrice() :
                checkoutResponse.getTotalPrice();

        String formattedOrderNumber = saleCounterService.getFormattedOrderNumber();
        BigDecimal totalPrice = getTotalPriceForOrder(priceToUse, orderRequest.getDeliveryMethod());
        Sale sale = initializeSale(user, checkoutResponse, saleDetails, totalPrice, formattedOrderNumber);
        orderRepository.save(sale);

        saleItemService.createSaleItemsForNewSale(sale, user);
        cartItemService.clearCart(user);
        return formattedOrderNumber;
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

    public List<DeliveryMethodResponse> getDeliveryMethodValues() {
        List<DeliveryMethodResponse> responses = new ArrayList<>();
        for (DeliveryMethod deliveryMethod : DeliveryMethod.values()) {
            DeliveryMethodResponse response = initializeDeliveryMethodResponse(deliveryMethod);
            responses.add(response);
        }
        log.info("Initialized delivery method responses");
        return responses;
    }

    public List<PaymentMethodResponse> getPaymentMethodValues() {
        List<PaymentMethodResponse> responses = new ArrayList<>();
        for (PaymentMethod paymentMethod : PaymentMethod.values()) {
            PaymentMethodResponse response = initializePaymentMethodResponse(paymentMethod);
            responses.add(response);
        }
        log.info("Initialized payment method responses");
        return responses;
    }

    public boolean userHasBoughtItem(String slug, User user) {
        if (null == user) {
            log.info("User is not logged in");
            return false;
        }
        for (Sale sale : user.getSales()) {
            for (SaleItem saleItem : sale.getSaleItems()) {
                if (saleItem.getPhone().getSlug().equals(slug)) {
                    log.info("User has bought this item");
                    return true;
                }
            }
        }
        log.info("User has not bought this item");
        return false;
    }

    public List<OrderResponse> fetchAllOrdersForAdmin() {
        List<Sale> sales = orderRepository.findAllByOrderByOrderDateDesc();
        List<OrderResponse> orderResponses = new ArrayList<>();
        for (Sale sale : sales) {
            OrderResponse orderResponse = initializeOrderResponseAdmin(sale);
            orderResponse.setUserEmail(sale.getUser().getEmail());
            orderResponse.setUserPhoneNumber(sale.getUser().getPhoneNumber());
            orderResponses.add(orderResponse);
        }
        log.info("Initialized all order responses for admin visualization");
        return orderResponses;
    }

    public User getUserByOrderNumber(String orderNumber) {
        return getSaleByOrderNumber(orderNumber).getUser();
    }

    public List<OrderResponse> getAllOrders(User user) {
        List<OrderResponse> orderResponses = new ArrayList<>();
        List<Sale> sales = orderRepository.findAllByUserIdOrderByOrderDateDesc(user.getId());

        for (Sale sale : sales) {
            List<SaleItem> saleItems = sale.getSaleItems();
            List<OrderItemResponse> orderItemResponses = new ArrayList<>();

            for (SaleItem saleItem : saleItems) {
                Phone phone = saleItem.getPhone();
                OrderItemResponse orderItemResponse = initializeOrderItemResponse(saleItem, phone);
                orderItemResponses.add(orderItemResponse);
            }

            OrderResponse orderResponse = initializeOrderResponse(sale, orderItemResponses);
            orderResponses.add(orderResponse);
        }
        log.info("Initialized all order responses for user");
        return orderResponses;
    }

    public ExtendedOrderResponse getExtendedInformationForOrder(String orderNumber, User user) {
        SaleDetails saleDetails = saleDetailsService.getSaleDetailsForOrderNumber(orderNumber);
        if (saleDetails.getSale().getUser() != user) {
            throw new DomainException(ExceptionMessages.USER_CANNOT_ACCESS_OTHER_USERS_ORDERS);
        }

        log.info("Initialized information for order with number {}", orderNumber);
        return initializeExtendedOrderResponse(saleDetails);
    }

    public OrderResponse getInformationForOrder(String orderNumber) {
        Sale sale = getSaleByOrderNumber(orderNumber);
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (SaleItem saleItem : sale.getSaleItems()) {
            Phone phone = saleItem.getPhone();

            OrderItemResponse orderItemResponse = initializeOrderItemResponse(saleItem, phone);
            orderItemResponses.add(orderItemResponse);
        }
        return initializeOrderResponse(sale, orderItemResponses);
    }

    public Sale getSaleByOrderNumber(String orderNumber) {
        Optional<Sale> saleByOrderNumberOptional = orderRepository.getSaleByOrderNumber(orderNumber);
        if (saleByOrderNumberOptional.isEmpty()) {
            throw new DomainException(ExceptionMessages.ORDER_NUMBER_DOES_NOT_EXIST);
        }
        return saleByOrderNumberOptional.get();
    }

    public void updateOrderStatus(String orderNumber, SaleStatus saleStatus) {
        Sale sale = getSaleByOrderNumber(orderNumber);
        sale.setSaleStatus(saleStatus);
        orderRepository.save(sale);
        log.info("Updated order status for order {}", orderNumber);
    }

    private ExtendedOrderResponse initializeExtendedOrderResponse(SaleDetails saleDetails) {
        return ExtendedOrderResponse.builder()
                .city(saleDetails.getCity())
                .address(saleDetails.getAddress())
                .zipCode(saleDetails.getZipCode())
                .paymentMethod(saleDetails.getPaymentMethod())
                .deliveryMethod(saleDetails.getDeliveryMethod())
                .build();
    }

    private OrderResponse initializeOrderResponseAdmin(Sale sale) {
        return OrderResponse.builder()
                .orderNumber(sale.getOrderNumber())
                .orderDate(sale.getOrderDate())
                .status(sale.getSaleStatus())
                .price(decimalFormat.format(sale.getTotalPrice()))
                .build();
    }

    private OrderResponse initializeOrderResponse(Sale sale, List<OrderItemResponse> orderItemResponses) {
        return OrderResponse.builder()
                .orderNumber(sale.getOrderNumber())
                .orderDate(sale.getOrderDate())
                .status(sale.getSaleStatus())
                .price(decimalFormat.format(sale.getTotalPrice()))
                .orderItemResponseList(orderItemResponses)
                .build();
    }

    private OrderItemResponse initializeOrderItemResponse(SaleItem saleItem, Phone phone) {
        return OrderItemResponse.builder()
                .itemName(phoneService.getExtendedPhoneNameBySlug(phone.getSlug()))
                .media(phone.getImages().get(0).getImageUrl())
                .quantity(saleItem.getQuantity())
                .slug(phone.getSlug())
                .build();
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

    private BigDecimal getTotalPriceForOrder(String totalPrice, DeliveryMethod deliveryMethod) {
        String normalizedPrice = totalPrice.replace(",", ".");
        BigDecimal basePrice = new BigDecimal(normalizedPrice);
        return basePrice.add(deliveryMethod.getPrice());
    }

    private Sale initializeSale(User user, CheckoutResponse checkoutResponse, SaleDetails saleDetails, BigDecimal totalPrice, String formattedOrderNumber) {
        return Sale.builder()
                .orderDate(LocalDateTime.now())
                .totalPrice(totalPrice)
                .saleStatus(SaleStatus.PENDING)
                .discountCode(discountCodeService.getDiscountCodeForSaleCreation(checkoutResponse.getDiscountCode()))
                .user(user)
                .saleDetails(saleDetails)
                .orderNumber(formattedOrderNumber)
                .build();
    }
}
