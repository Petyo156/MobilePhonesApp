package bg.tu_varna.sit.usp.phone_sales.cart.service;

import bg.tu_varna.sit.usp.phone_sales.cart.model.Cart;
import bg.tu_varna.sit.usp.phone_sales.cart.repository.CartRepository;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.cartitem.service.CartItemService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CartService {
    private final CartRepository cartRepository;
    private final PhoneService phoneService;
    private final CartItemService cartItemService;

    @Autowired
    public CartService(CartRepository cartRepository, PhoneService phoneService, CartItemService cartItemService) {
        this.cartRepository = cartRepository;
        this.phoneService = phoneService;
        this.cartItemService = cartItemService;
    }

    public Cart initializeCartForUser(User user) {
        log.info("Initializing cart for user");
        Cart cart = Cart.builder()
                .user(user)
                .build();
        return cartRepository.save(cart);
    }

    public void addPhoneToCart(String slug, User user) {
        Phone phone = phoneService.getPhoneBySlug(slug);
        Cart cart = user.getCart();
        cartItemService.addItemToCart(phone, cart);
    }

    public CartResponse getCartResponseForUser(User user) {
        Cart cart = user.getCart();
        List<CartItem> cartItems = cart.getCartItems();
        Integer summary = 0;

        List<GetPhoneResponse> phoneResponses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            summary += item.getQuantity();

            GetPhoneResponse phoneResponse = phoneService.getPhoneResponseByPhone(item.getPhone());
            phoneResponse.setQuantity(item.getQuantity());
            phoneResponses.add(phoneResponse);

            String priceToUse = phoneResponse.getDiscountPrice() != null && !phoneResponse.getDiscountPrice().isEmpty() 
                ? phoneResponse.getDiscountPrice() 
                : phoneResponse.getPrice();
            
            BigDecimal itemPrice = convertPriceToBigDecimal(priceToUse);
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemTotal);
        }
        log.info("Initializing cart response");
        return initializeCartResponse(phoneResponses, totalPrice, summary);
    }

    private BigDecimal convertPriceToBigDecimal(String priceStr) {
        priceStr = priceStr.replace(" лв.", "").replace(",", ".");
        priceStr = priceStr.replace(".", "");
        if (priceStr.length() > 2) {
            priceStr = priceStr.substring(0, priceStr.length() - 2) + "." + priceStr.substring(priceStr.length() - 2);
        }
        return new BigDecimal(priceStr);
    }

    private CartResponse initializeCartResponse(List<GetPhoneResponse> phoneResponses, BigDecimal totalPrice, Integer summary) {
        return CartResponse.builder()
                .phones(phoneResponses)
                .totalPrice(totalPrice.toString())
                .summary(summary)
                .build();
    }

    public void incrementPhoneQuantityInCart(User user, String slug) {
        cartItemService.incrementItemQuantity(user, slug);
    }

    public void decrementPhoneQuantityInCart(User user, String slug) {
        cartItemService.decrementItemQuantity(user, slug);
    }

    public void removePhoneFromCart(User user, String slug) {
        cartItemService.removeFromCart(user, slug);
    }
}
