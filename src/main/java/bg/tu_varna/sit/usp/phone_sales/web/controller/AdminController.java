package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.service.ImageService;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.review.service.ReviewService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.DiscountCodeResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;
import java.util.Arrays;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PhoneService phoneService;
    private final DiscountCodeService discountCodeService;
    private final ReviewService reviewService;

    @Autowired
    public AdminController(UserService userService, PhoneService phoneService, ImageService imageService, DiscountCodeService discountCodeService, ReviewService reviewService) {
        this.userService = userService;
        this.phoneService = phoneService;
        this.discountCodeService = discountCodeService;
        this.reviewService = reviewService;
    }

    @GetMapping("/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPhonePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("admin/add-product");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        modelAndView.addObject("submitPhoneRequest", new SubmitPhoneRequest());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPhone(@Valid @ModelAttribute SubmitPhoneRequest submitPhoneRequest,
                                    BindingResult bindingResult,
                                    @RequestParam("imageFile") List<MultipartFile> files,
                                    @RequestParam("thumbnailIndex") int thumbnailIndex) {

        ModelAndView modelAndView = new ModelAndView("admin/add-product");
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("submitPhoneRequest", submitPhoneRequest);
            return modelAndView;
        }

        Phone phone = phoneService.submitPhone(submitPhoneRequest, files, thumbnailIndex);

        return new ModelAndView("redirect:/phone/" + phone.getSlug());
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getProductsPage() {
        ModelAndView modelAndView = new ModelAndView("admin/manage-products");
        List<GetPhoneResponse> allVisiblePhones = phoneService.getAllVisiblePhones();
        List<GetPhoneResponse> allHiddenPhones = phoneService.getAllHiddenPhones();

        modelAndView.addObject("allVisiblePhones", allVisiblePhones);
        modelAndView.addObject("allHiddenPhones", allHiddenPhones);

        return modelAndView;
    }

    @GetMapping("/discounts")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getDiscountsPage() {
        ModelAndView modelAndView = new ModelAndView("admin/add-promotion");
        List<GetPhoneResponse> allVisiblePhones = phoneService.getAllVisiblePhones();
        List<GetPhoneResponse> allHiddenPhones = phoneService.getAllHiddenPhones();

        modelAndView.addObject("allVisiblePhones", allVisiblePhones);
        modelAndView.addObject("allHiddenPhones", allHiddenPhones);

        return modelAndView;
    }

    @PostMapping("/discounts/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public String setBulkDiscount(@RequestParam List<String> slugs,
                                @RequestParam String discountPercent) {
        phoneService.setBulkDiscountPercentForPhones(slugs, discountPercent);
        return "redirect:/admin/products";
    }

    @GetMapping("/codes")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getCodesPage() {
        ModelAndView modelAndView = new ModelAndView("admin/add-code");
        List<DiscountCodeResponse> activeDiscountCodes = discountCodeService.getDiscountCodeResponses(true);
        List<DiscountCodeResponse> inactiveDiscountCodes = discountCodeService.getDiscountCodeResponses(false);

        modelAndView.addObject("activeDiscountCodes", activeDiscountCodes);
        modelAndView.addObject("inactiveDiscountCodes", inactiveDiscountCodes);

        return modelAndView;
    }

    @PostMapping("/codes")
    @PreAuthorize("hasRole('ADMIN')")
    public String addCode(@RequestParam String name, @RequestParam String discountPercent) {
        discountCodeService.addNewCode(name, discountPercent);

        return "redirect:/admin/codes";
    }

    @PostMapping("/codes/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCodeStatus(@PathVariable String name) {
        discountCodeService.updateStatusByName(name);

        return "redirect:/admin/codes";
    }

    @PostMapping("/discounts/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public String setDiscount(@PathVariable String slug,
                                  @RequestParam String discountPercent) {
        phoneService.setDiscountPercentForPhone(slug, discountPercent);

        return "redirect:/admin/discounts";
    }

    @PostMapping("/products/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateVisibility(@PathVariable String slug) {
        phoneService.updateVisibility(slug);

        return "redirect:/admin/products";
    }

    @PostMapping("/products/bulk/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public String bulkHidePhones(@RequestParam String slugs) {
        List<String> phoneSlugs = Arrays.asList(slugs.split(","));
        phoneService.bulkUpdateVisibility(phoneSlugs, false);

        return "redirect:/admin/products";
    }

    @PostMapping("/products/bulk/show")
    @PreAuthorize("hasRole('ADMIN')")
    public String bulkShowPhones(@RequestParam String slugs) {
        List<String> phoneSlugs = Arrays.asList(slugs.split(","));
        phoneService.bulkUpdateVisibility(phoneSlugs, true);

        return "redirect:/admin/products";
    }

    @PostMapping("/phone/{slug}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    public void updatePhoneVisibility(@PathVariable String slug) {
        phoneService.updateVisibility(slug);
    }

    @GetMapping("/{slug}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView updatePhone(@PathVariable String slug, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("admin/edit-product");
        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        SubmitPhoneRequest submitPhoneRequest = phoneService.convertToSubmitPhoneRequest(phoneResponse);

        modelAndView.addObject("submitPhoneRequest", submitPhoneRequest);
        modelAndView.addObject("phoneToEdit", phoneResponse);
        modelAndView.addObject("user", user);
        
        return modelAndView;
    }

    @PostMapping("/{slug}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView editPhone(@PathVariable String slug,
                             @Valid @ModelAttribute SubmitPhoneRequest submitPhoneRequest,
                             BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("admin/edit-product");
        if (bindingResult.hasErrors()) {
            GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
            modelAndView.addObject("submitPhoneRequest", submitPhoneRequest);
            modelAndView.addObject("phoneToEdit", phoneResponse);
            return modelAndView;
        }

        Phone updatedPhone = phoneService.updatePhone(slug, submitPhoneRequest);

        return new ModelAndView("redirect:/phone/" + updatedPhone.getSlug());
    }

    @GetMapping("/{slug}/add-similar")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAddSimilarPhonePage(@PathVariable String slug) {
        ModelAndView modelAndView = new ModelAndView("admin/add-similar-product");
        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
        SubmitPhoneRequest submitPhoneRequest = phoneService.convertToSubmitPhoneRequest(phoneResponse);

        modelAndView.addObject("submitPhoneRequest", submitPhoneRequest);
        modelAndView.addObject("originalPhone", phoneResponse);

        return modelAndView;
    }

    @PostMapping("/review/delete/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteReview(@PathVariable UUID reviewId) {
        String phoneSlug = phoneService.getPhoneResponseByReviewId(reviewId).getSlug();
        reviewService.deleteReview(reviewId);
        return "redirect:/phone/" + phoneSlug;
    }
}
