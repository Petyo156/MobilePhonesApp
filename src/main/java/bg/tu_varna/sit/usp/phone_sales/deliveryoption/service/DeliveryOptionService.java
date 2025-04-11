package bg.tu_varna.sit.usp.phone_sales.deliveryoption.service;

import bg.tu_varna.sit.usp.phone_sales.deliveryoption.model.DeliveryOption;
import bg.tu_varna.sit.usp.phone_sales.deliveryoption.repository.DeliveryOptionRepository;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import bg.tu_varna.sit.usp.phone_sales.inventory.service.InventoryService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CheckoutRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryOptionService {
    private final DeliveryOptionRepository deliveryOptionRepository;
    private final InventoryService inventoryService;
    private final UserService userService;

    @Autowired
    public DeliveryOptionService(DeliveryOptionRepository deliveryOptionRepository, InventoryService inventoryService, UserService userService, UserService userService1) {
        this.deliveryOptionRepository = deliveryOptionRepository;
        this.inventoryService = inventoryService;
        this.userService = userService1;
    }

    @Transactional
    public void makeOrder(User user, CheckoutRequest checkoutRequest) {
        List<Inventory> cartItems = inventoryService.getAllItemsInCartForUser(user);

        userService.updateUserAddressPreference(checkoutRequest.getAddress(), checkoutRequest.getCity(), user);

        for (Inventory inventory : cartItems) {
            createDeliveryForItem(checkoutRequest, inventory);
            inventoryService.setItemAsSold(inventory);
        }
    }

    private void createDeliveryForItem(CheckoutRequest checkoutRequest, Inventory inventory) {
        DeliveryOption deliveryOption = DeliveryOption.builder()
                .deliveryMethod(checkoutRequest.getDeliveryMethod())
                .paymentMethod(checkoutRequest.getPaymentMethod())
                .inventory(inventory)
                .address(checkoutRequest.getAddress())
                .city(checkoutRequest.getCity())
                .zipCode(checkoutRequest.getZipCode())
                .build();
        deliveryOptionRepository.save(deliveryOption);
    }
}
