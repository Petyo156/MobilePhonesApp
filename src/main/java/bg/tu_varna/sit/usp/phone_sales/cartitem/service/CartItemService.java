package bg.tu_varna.sit.usp.phone_sales.cartitem.service;

import bg.tu_varna.sit.usp.phone_sales.cart.model.Cart;
import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.cartitem.repository.CartItemRepository;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import jakarta.transaction.Transactional;
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
        if(!phone.getIsVisible()){
            log.info("Cannot be added to cart, item is not visible");
            return;
        }
        Boolean itemExistsInCart = checkItemExistsInCart(phone, cart);
        if (itemExistsInCart) {
            incrementItemQuantity(cart.getUser(), phone.getSlug());
            return;
        }
        CartItem cartItem = initializeCartItem(phone, cart);
        cartItemRepository.save(cartItem);
        log.info("Added new cart item: {} {}", phone.getPhoneModel().getBrand().getName(), phone.getPhoneModel().getName());
    }

    public boolean incrementItemQuantity(User user, String slug) {
        CartItem item = getCartItem(user, slug);
        int cartQuantity = item.getQuantity();
        int stockQuantity = item.getPhone().getQuantity();

        if (cartQuantity + 1 > stockQuantity) {
            log.info("Cannot increment item quantity - not enough stock");
            return false;
        }

        if (cartQuantity < 10) {
            item.setQuantity(cartQuantity + 1);
            cartItemRepository.save(item);
            log.info("Incremented item quantity successfully");
            return true;
        }
        log.info("Cannot increment item quantity - it's already 10");
        return true;
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

    public void clearCart(User user) {
        for (CartItem cartItem : user.getCart().getCartItems()) {
            deleteItem(cartItem);
        }
        log.info("Cleared cart successfully");
    }

    @Transactional
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
        cartItemRepository.flush();
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
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getPhone().getSlug().equals(phone.getSlug())) {
                return true;
            }
        }
        return false;
    }
}
