package bg.tu_varna.sit.usp.phone_sales.inventory.service;

import bg.tu_varna.sit.usp.phone_sales.deliveryoption.model.DeliveryOption;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.OrderStatus;
import bg.tu_varna.sit.usp.phone_sales.inventory.repository.InventoryRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.DeliveryOptionResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final PhoneService phoneService;
    private final DecimalFormat decimalFormat;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, PhoneService phoneService, DecimalFormat decimalFormat) {
        this.inventoryRepository = inventoryRepository;
        this.phoneService = phoneService;
        this.decimalFormat = decimalFormat;
    }

    public CartResponse getCartForUser(User user) {
        List<Inventory> inCartItemsList = inventoryRepository.getAllByUserAndInInventoryFalse(user);
        if (inCartItemsList.isEmpty()) {
            throw new DomainException(ExceptionMessages.ADD_STUFF_TO_YOUR_CART);
        }
        log.info("Initializing cart response");
        return initializeCartResponse(inCartItemsList);
    }

    public void addPhoneToCartForUser(User user, String slug) {
        Inventory inventory = initializeInCartInventory(slug, user);
        inventoryRepository.save(inventory);
    }

    public List<Inventory> getAllItemsInCartForUser(User user) {
        log.info("Get all items in logged user's cart");
        return inventoryRepository.getAllByUserAndInInventoryFalse(user);
    }

    public List<OrderResponse> getAllOrdersForUser(User user) {
        List<Inventory> orders = inventoryRepository.getAllByUserAndInInventoryTrueOrderByDateTimeDesc(user);
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Inventory inventory : orders) {
            DeliveryOptionResponse deliveryOptionResponse = getDeliveryOptionResponse(inventory.getDeliveryOption());
            GetPhoneResponse phoneResponse = phoneService.getPhoneResponseByPhone(inventory.getPhone());
            orderResponses.add(initializeOrderResponse(inventory, phoneResponse, deliveryOptionResponse));
        }

        log.info("Returning all order responses for logged user");
        return orderResponses;
    }

    public void setItemAsSold(Inventory inventory) {
        inventory.setInInventory(true);
        inventoryRepository.save(inventory);
    }

    public void incrementProductQuantity(User user, UUID id) {
        Inventory inventory = getInventoryByIdAndUser(user, id);
        if(inventory.getQuantity() >= 10){
            log.info("Item quantity in cart is maximum 10");
            return;
        }

        log.info("Incrementing product quantity");
        inventory.setQuantity(inventory.getQuantity() + 1);
        inventoryRepository.save(inventory);
    }

    public void decrementProductQuantity(User user, UUID id) {
        Inventory inventory = getInventoryByIdAndUser(user, id);

        log.info("Decrementing product quantity");
        if(inventory.getQuantity() <= 0) {
            return;
        }
        inventory.setQuantity(inventory.getQuantity() - 1);
        inventoryRepository.save(inventory);
    }

    private DeliveryOptionResponse getDeliveryOptionResponse(DeliveryOption deliveryOption) {
        return DeliveryOptionResponse.builder()
                .deliveryMethod(deliveryOption.getDeliveryMethod())
                .paymentMethod(deliveryOption.getPaymentMethod())
                .address(deliveryOption.getAddress())
                .city(deliveryOption.getCity())
                .zipCode(deliveryOption.getZipCode())
                .build();
    }

    private OrderResponse initializeOrderResponse(Inventory inventory, GetPhoneResponse phoneResponse, DeliveryOptionResponse deliveryOptionResponse) {
        return OrderResponse.builder()
                .orderId(inventory.getId().toString())
                .orderDate(inventory.getDateTime())
                .status(inventory.getStatus())
                .quantity(inventory.getQuantity())
                .phone(phoneResponse)
                .deliveryOption(deliveryOptionResponse)
                .build();
    }

    private CartResponse initializeCartResponse(List<Inventory> inCartItemsList) {
        String totalPrice = getTotalPriceForItemsInCart(inCartItemsList);
        List<GetPhoneResponse> phoneResponses = getPhoneResponseByCartItemsList(inCartItemsList);

        return initializeCartResponseFromPhoneResponses(phoneResponses, totalPrice);
    }

    private String getTotalPriceForItemsInCart(List<Inventory> inCartItemsList) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Inventory inventory : inCartItemsList) {
            BigDecimal price = inventory.getPhone().getPrice();
            totalPrice = totalPrice.add(price);
        }
        return decimalFormat.format(totalPrice);
    }

    private List<GetPhoneResponse> getPhoneResponseByCartItemsList(List<Inventory> inCartItemsList) {
        List<GetPhoneResponse> responses = new ArrayList<>();
        for (Inventory inventory : inCartItemsList) {
            GetPhoneResponse phoneResponse = phoneService.getPhoneResponseByPhone(inventory.getPhone());
            phoneResponse.setQuantity(inventory.getQuantity());

            responses.add(phoneResponse);
        }
        return responses;
    }

    private CartResponse initializeCartResponseFromPhoneResponses(List<GetPhoneResponse> phoneResponses, String totalPrice) {
        return CartResponse.builder()
                .phones(phoneResponses)
                .totalPrice(totalPrice)
                .build();
    }

    private Inventory initializeInCartInventory(String slug, User user) {
        Phone phone = phoneService.getPhoneBySlug(slug);
        Optional<Inventory> inventoryOptional = inventoryRepository.getInventoryByUserAndPhone(user, phone);
        if(inventoryOptional.isPresent()){
            log.info("Product exists in cart");
            Inventory inventory = inventoryOptional.get();

            incrementProductQuantity(user, inventory.getId());
            return inventoryRepository.save(inventory);
        }
        log.info("New inventory added");
        return Inventory.builder()
                .user(user)
                .phone(phone)
                .inInventory(false)
                .quantity(1)
                .status(OrderStatus.PENDING)
                .dateTime(LocalDateTime.now())
                .build();
    }

    private Inventory getInventoryByIdAndUser(User user, UUID id) {
        Optional<Inventory> inventoryOptional = inventoryRepository.getInventoryByUserAndInInventoryFalseAndId(user, id);
        if(inventoryOptional.isEmpty()) {
            throw new DomainException(ExceptionMessages.INVENTORY_DOES_NOT_EXIST);
        }
        return inventoryOptional.get();
    }
}
