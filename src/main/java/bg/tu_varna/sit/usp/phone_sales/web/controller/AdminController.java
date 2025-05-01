package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.discount.service.DiscountCodeService;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.service.ImageService;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.DiscountCodeResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitPhoneRequest;
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

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final PhoneService phoneService;
    private final DiscountCodeService discountCodeService;

    @Autowired
    public AdminController(UserService userService, PhoneService phoneService, ImageService imageService, DiscountCodeService discountCodeService) {
        this.userService = userService;
        this.phoneService = phoneService;
        this.discountCodeService = discountCodeService;
    }

    @GetMapping("/phone")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView submitPhonePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("admin/add-phone");

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

        ModelAndView modelAndView = new ModelAndView("admin/add-phone");
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

    @GetMapping("/phones")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAdminPhoneList() {
        ModelAndView modelAndView = new ModelAndView("admin/phone-list");
        List<GetPhoneResponse> phones = phoneService.getAllPhones();
        modelAndView.addObject("phones", phones);
        return modelAndView;
    }

    @PostMapping("/phone/{slug}/visibility")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public void updatePhoneVisibility(@PathVariable String slug) {
        phoneService.updateVisibility(slug);
    }

}
