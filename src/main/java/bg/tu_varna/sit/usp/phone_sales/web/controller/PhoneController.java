package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.order.service.OrderService;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.review.service.ReviewService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.ReviewRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.DifferentColorPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.DifferentStoragePhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/phone")
public class PhoneController {
    private final PhoneService phoneService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final OrderService orderService;

    public PhoneController(PhoneService phoneService, UserService userService, ReviewService reviewService, OrderService orderService) {
        this.phoneService = phoneService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.orderService = orderService;
    }

    @GetMapping("/{slug}")
    public ModelAndView getPhonePage(@PathVariable String slug, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("home/phone");

        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        List<DifferentColorPhoneResponse> differentColorPhones = phoneService.getPhonesWithDifferentColor(slug);
        List<DifferentStoragePhoneResponse> differentStoragePhones = phoneService.getPhonesWithDifferentStorage(slug);

        if(orderService.userHasBoughtItem(slug, user) && !reviewService.userHasAlreadyLeftAReview(slug, user)){
            modelAndView.addObject("reviewRequest", new ReviewRequest());
            modelAndView.addObject("hasBoughtItem", true);
        } else {
            modelAndView.addObject("hasBoughtItem", false);
        }
        modelAndView.addObject("reviews", reviewService.getAllReviewsForProduct(slug));
        modelAndView.addObject("phoneResponse", phoneResponse);
        modelAndView.addObject("modelUrl", phoneResponse.getModelUrl());
        modelAndView.addObject("differentColorPhones", differentColorPhones);
        modelAndView.addObject("differentStoragePhones", differentStoragePhones);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/review/{slug}")
    public String postReview(@PathVariable String slug,
                             @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                             @Valid ReviewRequest reviewRequest) {
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        reviewService.postReview(reviewRequest, user, slug);

        return "redirect:/phone/" + slug;
    }

}
