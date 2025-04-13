package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.LoginRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.RegisterRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/")
public class IndexController {
    private final UserService userService;
    private final PhoneService phoneService;

    @Autowired
    public IndexController(UserService userService, PhoneService phoneService) {
        this.userService = userService;
        this.phoneService = phoneService;
    }

    @GetMapping()
    public ModelAndView getHomePage(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("home/index");

        List<GetPhoneResponse> mostRecentPhones = phoneService.getMostRecentPhones();
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        modelAndView.addObject("user", user);
        modelAndView.addObject("mostRecentPhones", mostRecentPhones);

        return modelAndView;
    }

    @GetMapping("/search")
    public ModelAndView getSearchPage(@RequestParam("result") String info,
                                      @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("home/search");

        List<GetPhoneResponse> searchResult = phoneService.getSearchResult(info);
        User user = userService.getAuthenticatedUser(authenticationMetadata);
        modelAndView.addObject("searchResult", searchResult);
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @GetMapping("/register")
    public ModelAndView register(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        modelAndView.setViewName("index/register");
        modelAndView.addObject("registerRequest", new RegisterRequest());
        modelAndView.addObject("user", user);

        return modelAndView;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ModelAndView("index/register");
        }

        userService.register(registerRequest);
        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/login")
    public ModelAndView login(@RequestParam(value = "error", required = false) String errorParam, @Valid LoginRequest loginRequest, BindingResult bindingResult, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index/login");

        User user = userService.getAuthenticatedUser(authenticationMetadata);
        modelAndView.addObject("error", errorParam);
        modelAndView.addObject("loginRequest", loginRequest);
        modelAndView.addObject("user", user);

        if (errorParam != null || bindingResult.hasErrors()) {
            modelAndView.addObject("errorMessage", "Incorrect username or password!");
        }

        return modelAndView;
    }
}
