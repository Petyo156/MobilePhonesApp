package bg.tu_varna.sit.usp.phone_sales.aspect;

import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.exception.UserMustBeLoggedInException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class RequireAuthenticatedUserAspect {

    @Before("@annotation(bg.tu_varna.sit.usp.phone_sales.aspect.annotation.RequireAuthenticatedUser)")
    public void checkAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new UserMustBeLoggedInException(ExceptionMessages.USER_MUST_BE_LOGGED_IN);
        }
    }
}

