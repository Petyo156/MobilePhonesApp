package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.security.AuthenticationMetadata;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.DifferentColorPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.DifferentStoragePhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitPhoneRequest;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitBrandAndModel;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitCamera;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitHardware;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitOperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitPhoneDimensions;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/phone")
public class PhoneController {
    private final PhoneService phoneService;
    private final UserService userService;

    public PhoneController(PhoneService phoneService, UserService userService) {
        this.phoneService = phoneService;
        this.userService = userService;
    }

    @GetMapping("/{slug}")
    public ModelAndView getPhonePage(@PathVariable String slug, @AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {
        ModelAndView modelAndView = new ModelAndView("home/phone");

        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
        User user = userService.getAuthenticatedUser(authenticationMetadata);

        List<DifferentColorPhoneResponse> differentColorPhones = phoneService.getPhonesWithDifferentColor(slug);
        List<DifferentStoragePhoneResponse> differentStoragePhones = phoneService.getPhonesWithDifferentStorage(slug);

        modelAndView.addObject("phoneResponse", phoneResponse);
        modelAndView.addObject("modelUrl", phoneResponse.getModelUrl());
        modelAndView.addObject("differentColorPhones", differentColorPhones);
        modelAndView.addObject("differentStoragePhones", differentStoragePhones);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    @GetMapping("/{slug}/add-similar")
    public ModelAndView getAddSimilarPhonePage(@PathVariable String slug) {
        ModelAndView modelAndView = new ModelAndView("admin/add-similar-phone");
        GetPhoneResponse phoneResponse = phoneService.getPhoneResponseForVisiblePhoneBySlug(slug);
        
        SubmitPhoneRequest submitPhoneRequest = new SubmitPhoneRequest();
        
        SubmitBrandAndModel brandAndModel = new SubmitBrandAndModel();
        brandAndModel.setBrand(phoneResponse.getBrandAndModelResponse().getBrand());
        brandAndModel.setModel(phoneResponse.getBrandAndModelResponse().getModel());
        submitPhoneRequest.setBrandAndModel(brandAndModel);
        
        submitPhoneRequest.setPrice(Double.parseDouble(phoneResponse.getPrice().replaceAll("[^\\d.]", "")));
        submitPhoneRequest.setQuantity(phoneResponse.getQuantity());
        submitPhoneRequest.setReleaseYear(phoneResponse.getReleaseYear());
        
        SubmitPhoneDimensions dimensions = new SubmitPhoneDimensions();
        dimensions.setColor(phoneResponse.getDimensions().getColor());
        dimensions.setWaterResistance(phoneResponse.getDimensions().getWaterResistance());
        dimensions.setHeight(phoneResponse.getDimensions().getHeight());
        dimensions.setWeight(phoneResponse.getDimensions().getWeight());
        dimensions.setThickness(phoneResponse.getDimensions().getThickness());
        dimensions.setWidth(phoneResponse.getDimensions().getWidth());
        submitPhoneRequest.setDimensions(dimensions);
        
        SubmitOperatingSystem operatingSystem = new SubmitOperatingSystem();
        operatingSystem.setOperatingSystemType(phoneResponse.getOperatingSystemResponse().getOperatingSystemType());
        operatingSystem.setVersion(phoneResponse.getOperatingSystemResponse().getVersion());
        submitPhoneRequest.setOperatingSystem(operatingSystem);
        
        SubmitHardware hardware = new SubmitHardware();
        hardware.setRam(phoneResponse.getHardwareResponse().getRam());
        hardware.setStorage(phoneResponse.getHardwareResponse().getStorage());
        hardware.setBatteryCapacity(phoneResponse.getHardwareResponse().getBatteryCapacity());
        hardware.setScreenSize(phoneResponse.getHardwareResponse().getScreenSize());
        hardware.setSimType(phoneResponse.getHardwareResponse().getSimType());
        hardware.setRefreshRate(phoneResponse.getHardwareResponse().getRefreshRate());
        hardware.setCoreCount(phoneResponse.getHardwareResponse().getCoreCount());
        hardware.setScreenResolution(phoneResponse.getHardwareResponse().getScreenResolution());
        submitPhoneRequest.setHardware(hardware);
        
        SubmitCamera camera = new SubmitCamera();
        camera.setResolution(phoneResponse.getCameraResponse().getResolution());
        camera.setCount(phoneResponse.getCameraResponse().getCount());
        camera.setVideoResolution(phoneResponse.getCameraResponse().getVideoResolution());
        submitPhoneRequest.setCamera(camera);
        submitPhoneRequest.setModelUrl(phoneResponse.getModelUrl());
        
        modelAndView.addObject("submitPhoneRequest", submitPhoneRequest);
        modelAndView.addObject("originalPhone", phoneResponse);
        return modelAndView;
    }
}
