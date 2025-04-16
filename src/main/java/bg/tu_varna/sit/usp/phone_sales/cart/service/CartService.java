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

        List<GetPhoneResponse> phoneResponses = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            GetPhoneResponse phoneResponse = phoneService.getPhoneResponseByPhone(item.getPhone());
            phoneResponse.setQuantity(item.getQuantity());
            phoneResponses.add(phoneResponse);

            BigDecimal itemPrice = item.getPhone().getPrice();
            BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(item.getQuantity()));

            totalPrice = totalPrice.add(itemTotal);
        }
        log.info("Initializing cart response");
        return initializeCartResponse(phoneResponses, totalPrice);
    }

    private CartResponse initializeCartResponse(List<GetPhoneResponse> phoneResponses, BigDecimal totalPrice) {
        return CartResponse.builder()
                .phones(phoneResponses)
                .totalPrice(totalPrice.toString())
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
