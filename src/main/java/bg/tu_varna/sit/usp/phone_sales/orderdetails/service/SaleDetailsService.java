package bg.tu_varna.sit.usp.phone_sales.orderdetails.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.SaleDetails;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.repository.SaleDetailsRepository;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SaleDetailsService {
    private final UserService userService;
    private final SaleDetailsRepository saleDetailsRepository;

    @Autowired
    public SaleDetailsService(UserService userService, SaleDetailsRepository saleDetailsRepository) {
        this.userService = userService;
        this.saleDetailsRepository = saleDetailsRepository;
    }

    public SaleDetails initializeSaleDetailsForUser(OrderRequest orderRequest, User user) {
        userService.updateUserPersonalInformationPreference(orderRequest.getZipCode(), orderRequest.getAddress(), orderRequest.getCity(), orderRequest.getPhoneNumber(), user);
        userService.updateUserFirstAndLastNamePreference(orderRequest.getFirstName(), orderRequest.getLastName(), user);

        SaleDetails saleDetails = initializeSaleDetails(orderRequest);
        return saleDetailsRepository.save(saleDetails);
    }

    public SaleDetails getSaleDetailsForOrderNumber(String orderNumber) {
        Optional<SaleDetails> saleDetailsOptional = saleDetailsRepository.getSaleDetailsBySale_OrderNumber(orderNumber);
        if(saleDetailsOptional.isEmpty()) {
            throw new DomainException(ExceptionMessages.DETAILS_FOR_ORDER_DO_NOT_EXIST);
        }
        return saleDetailsOptional.get();
    }

    private SaleDetails initializeSaleDetails(OrderRequest orderRequest) {
        return SaleDetails.builder()
                .address(orderRequest.getAddress())
                .city(orderRequest.getCity())
                .zipCode(orderRequest.getZipCode())
                .firstName(orderRequest.getFirstName())
                .lastName(orderRequest.getLastName())
                .deliveryMethod(orderRequest.getDeliveryMethod())
                .paymentMethod(orderRequest.getPaymentMethod())
                .phoneNumber(orderRequest.getPhoneNumber())
                .build();
    }
}
