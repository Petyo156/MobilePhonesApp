package bg.tu_varna.sit.usp.phone_sales.cartitem.service;

import bg.tu_varna.sit.usp.phone_sales.cart.model.Cart;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.cartitem.repository.CartItemRepository;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    public void addItemToCart(Phone phone, Cart cart) {
        Boolean itemExistsInCart = checkItemExistsInCart(phone, cart);
        if (itemExistsInCart) {
            incrementItemQuantity(cart.getUser(), phone.getSlug());
            return;
        }
        CartItem cartItem = initializeCartItem(phone, cart);
        cartItemRepository.save(cartItem);
        log.info("Added new cart item: {} {}", phone.getPhoneModel().getBrand().getName(), phone.getPhoneModel().getName());
    }

    public void incrementItemQuantity(User user, String slug) {
        CartItem item = getCartItem(user, slug);
        if (item.getQuantity() < 10) {
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
            log.info("Incremented item quantity successfully");
            return;
        }
        log.info("Cannot increment item quantity - it's already 10");
    }

    public void decrementItemQuantity(User user, String slug) {
        CartItem item = getCartItem(user, slug);
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
            cartItemRepository.save(item);
            log.info("Decremented item quality successfully");
        } else {
            deleteItem(item);
        }
    }

    public void removeFromCart(User user, String slug) {
        CartItem item = getCartItem(user, slug);
        deleteItem(item);
    }

    private CartItem getCartItem(User user, String slug) {
        return user.getCart().getCartItems().stream()
                .filter(cartItem -> cartItem.getPhone().getSlug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new DomainException(ExceptionMessages.PHONE_WITH_THIS_SLUG_DOESNT_EXIST_IN_USERS_CART));
    }

    private void deleteItem(CartItem item) {
        cartItemRepository.delete(item);
        log.info("Deleted cart item");
    }

    private CartItem initializeCartItem(Phone phone, Cart cart) {
        return CartItem.builder()
                .cart(cart)
                .phone(phone)
                .quantity(1)
                .build();
    }

    private Boolean checkItemExistsInCart(Phone phone, Cart cart) {
        for(CartItem cartItem : cart.getCartItems()) {
            if(cartItem.getPhone().getSlug().equals(phone.getSlug())) {
                return true;
            }
        }
        return false;
    }
}
