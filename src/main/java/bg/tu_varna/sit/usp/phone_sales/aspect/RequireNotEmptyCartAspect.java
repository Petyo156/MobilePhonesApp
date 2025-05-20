package bg.tu_varna.sit.usp.phone_sales.aspect;

import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.exception.ExpectedAuthenticationMetadataPrincipalException;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequireNotEmptyCartAspect {

    private final UserService userService;

    public RequireNotEmptyCartAspect(UserService userService) {
        this.userService = userService;
    }

    @Around("@annotation(bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireNotEmptyCart)")
    public Object checkCartNotEmpty(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication.getPrincipal() instanceof AuthenticationMetadata metadata)) {
            throw new ExpectedAuthenticationMetadataPrincipalException(ExceptionMessages.EXPECTED_AUTHENTICATION_METADATA_PRINCIPLE);
        }

        String email = metadata.getEmail();
        User user = userService.getByEmail(email);

        if (user.getCart().getCartItems().isEmpty()) {
            throw new ExpectedAuthenticationMetadataPrincipalException(ExceptionMessages.ADD_STUFF_TO_YOUR_CART_BEFORE_CHECKING_OUT);
        }

        return joinPoint.proceed();
    }
}

