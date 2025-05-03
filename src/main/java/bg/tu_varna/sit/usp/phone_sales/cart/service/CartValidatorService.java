package bg.tu_varna.sit.usp.phone_sales.cart.service;

import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.cartitem.service.CartItemService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import org.springframework.stereotype.Service;

@Service
public class CartValidatorService {
    private final CartItemService cartItemService;

    public CartValidatorService(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    public boolean validateAndCleanCart(User user) {
        boolean modified = false;
        for (CartItem item : user.getCart().getCartItems()) {
            if (item.getPhone().getQuantity() < item.getQuantity()) {
                cartItemService.removeFromCart(user, item.getPhone().getSlug());
                modified = true;
            }
        }
        return modified;
    }
}
