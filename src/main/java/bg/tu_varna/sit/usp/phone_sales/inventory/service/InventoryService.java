package bg.tu_varna.sit.usp.phone_sales.inventory.service;

import bg.tu_varna.sit.usp.phone_sales.deliveryoption.service.DeliveryOptionService;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.OrderStatus;
import bg.tu_varna.sit.usp.phone_sales.inventory.repository.InventoryRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.user.service.UserService;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.DeliveryOptionResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.orderresponse.OrderResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final PhoneService phoneService;
    private final UserService userService;
    private final DeliveryOptionService deliveryOptionService;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, PhoneService phoneService, UserService userService, DeliveryOptionService deliveryOptionService) {
        this.inventoryRepository = inventoryRepository;
        this.phoneService = phoneService;
        this.userService = userService;
        this.deliveryOptionService = deliveryOptionService;
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
        log.info("New inventory added");
    }

    public String getPriceForAllItemsInCart(CartResponse cart) {
        BigDecimal price = BigDecimal.ZERO;
        List<GetPhoneResponse> phones = cart.getPhones();
        for (GetPhoneResponse phone : phones) {
            price = price.add(phone.getPrice());
        }
        log.info("Price for all items is {}", price);
        return price.setScale(2, RoundingMode.HALF_UP).toString();
    }

    public List<Inventory> getAllItemsInCartForUser(User user) {
        log.info("Get all items in logged user's cart");
        return inventoryRepository.getAllByUserAndInInventoryFalse(user);
    }

    public List<OrderResponse> getAllOrdersForUser(User user) {
        List<Inventory> orders = inventoryRepository.getAllByUserAndInInventoryTrueOrderByDateTimeDesc(user);
        List<OrderResponse> orderResponses = new ArrayList<>();

        for (Inventory inventory : orders) {
            DeliveryOptionResponse deliveryOptionResponse = deliveryOptionService.getDeliveryOptionResponse(inventory.getDeliveryOption());
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
        List<GetPhoneResponse> phoneResponses = getPhoneResponseByCartItemsList(inCartItemsList);
        return initializeCartResponseFromPhoneResponses(phoneResponses);
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

    private CartResponse initializeCartResponseFromPhoneResponses(List<GetPhoneResponse> phoneResponses) {
        return CartResponse.builder()
                .phones(phoneResponses)
                .build();
    }

    private Inventory initializeInCartInventory(String slug, User user) {
        return Inventory.builder()
                .user(user)
                .phone(phoneService.getPhoneBySlug(slug))
                .inInventory(false)
                .quantity(1)
                .status(OrderStatus.PENDING)
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
