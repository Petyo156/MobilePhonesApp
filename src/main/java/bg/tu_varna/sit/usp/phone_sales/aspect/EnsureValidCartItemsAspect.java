package bg.tu_varna.sit.usp.phone_sales.aspect;

import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.cartitem.service.CartItemService;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Aspect
@Component
public class EnsureValidCartItemsAspect {
    private final UserService userService;
    private final CartItemService cartItemService;

    public EnsureValidCartItemsAspect(UserService userService, CartItemService cartItemService) {
        this.userService = userService;
        this.cartItemService = cartItemService;
    }

    @Before("@annotation(bg.tu_varna.sit.usp.phone_sales.aspect.annotation.EnsureValidCartItems)")
    public void ensureValidCartItems() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof AuthenticationMetadata metadata)) {
            throw new DomainException(ExceptionMessages.EXPECTED_AUTHENTICATION_METADATA_PRINCIPLE);
        }

        String email = metadata.getEmail();
        User user = userService.getByEmail(email);

        List<CartItem> cartItems = user.getCart().getCartItems();
        for (CartItem cartItem : cartItems) {
            if (cartItem.getPhone().getQuantity() >= cartItem.getQuantity() && cartItem.getPhone().getIsVisible()) {
                continue;
            }
            cartItemService.removeFromCart(user, cartItem.getPhone().getSlug());
            log.info("Removed item from cart because quantity in database has changed or phone is not visible");
        }
    }
}
