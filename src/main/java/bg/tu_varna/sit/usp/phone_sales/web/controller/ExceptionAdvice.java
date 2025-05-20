package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(UserHasAlreadyLeftAReviewException.class)
    public String handleReviewConflict(RedirectAttributes redirectAttributes, UserHasAlreadyLeftAReviewException e) {
        redirectAttributes.addFlashAttribute("reviewError", e.getMessage());
        return "redirect:/phone/" + e.getProductSlug();
    }

    @ExceptionHandler(InvalidChangePasswordRequestException.class)
    public String handleInvalidChangePassword(RedirectAttributes redirectAttributes, InvalidChangePasswordRequestException e) {
        redirectAttributes.addFlashAttribute("changePasswordError", e.getMessage());
        return "redirect:/profile";
    }

    @ExceptionHandler({
            InvalidDiscountCodePercentInputException.class
    })
    public String handleInvalidDiscounts(RedirectAttributes redirectAttributes, Exception e) {
        redirectAttributes.addFlashAttribute("discountError", e.getMessage());
        return "redirect:/admin/discounts";
    }

    @ExceptionHandler({
            PhoneWithThisSlugAlreadyExistsException.class
    })
    public String handlePhoneWithSlugAlreadyExists(RedirectAttributes redirectAttributes, PhoneWithThisSlugAlreadyExistsException e) {
        String phoneWithSlugAlreadyExists = e.getMessage();
        redirectAttributes.addFlashAttribute("phoneWithSlugAlreadyExists", phoneWithSlugAlreadyExists);

        return "redirect:/admin/phone";
    }

    @ExceptionHandler({
            SetAtLeastOnePhonePictureException.class
    })
    public String handleSetAtLeastOnePhonePicture(RedirectAttributes redirectAttributes, SetAtLeastOnePhonePictureException e) {
        String atLeastOnePhonePicture = e.getMessage();
        redirectAttributes.addFlashAttribute("atLeastOnePhonePicture", atLeastOnePhonePicture);

        return "redirect:/admin/phone";
    }

    @ExceptionHandler({
            ExpectedAuthenticationMetadataPrincipalException.class,
            AddStuffToYourCartBeforeCheckingOutException.class
    })
    public String handleCheckingOutOnEmptyCart(RedirectAttributes redirectAttributes, Exception e) {
        String addStuffToYourCart = e.getMessage();
        redirectAttributes.addFlashAttribute("addStuffToYourCart", addStuffToYourCart);

        return "redirect:/cart";
    }

    @ExceptionHandler({
            UserWithEmailAlreadyExistsException.class
    })
    public String handleEmailAlreadyExists(RedirectAttributes redirectAttributes, UserWithEmailAlreadyExistsException e) {
        String userWithEmailExists = e.getMessage();
        redirectAttributes.addFlashAttribute("userWithEmailExists", userWithEmailExists);

        return "redirect:/register";
    }

    @ExceptionHandler({
            PasswordsDoNotMatchException.class
    })
    public String passwordsDoNotMatch(RedirectAttributes redirectAttributes, PasswordsDoNotMatchException e) {
        String notMatchingPasswords = e.getMessage();
        redirectAttributes.addFlashAttribute("notMatchingPasswords", notMatchingPasswords);

        return "redirect:/register";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({
            AuthorizationDeniedException.class,
            AccessDeniedException.class,
            NoResourceFoundException.class,
            MethodArgumentTypeMismatchException.class,
            MissingRequestValueException.class,

            DetailsForOrderDoNotExistException.class,
            OrderNumberDoesNotExistException.class,
            PhoneWithThisSlugDoesntExistInUsersCartException.class,
            PhoneWithThisSlugDoesntExistException.class,
            SaleItemDoesNotExistException.class,
            UserCannotAccessOtherUsersOrdersException.class,
            UserWithEmailDoesntExistException.class,

            InvalidDiscountPercentException.class,
            InvalidDiscountCodeException.class,
    })
    public ModelAndView handleNotFoundExceptions() {

        return new ModelAndView("exception/not-found");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleAnyException(Exception exception) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("exception/internal-server-error");
        modelAndView.addObject("errorMessage", exception.getClass().getSimpleName());

        return modelAndView;
    }
}
