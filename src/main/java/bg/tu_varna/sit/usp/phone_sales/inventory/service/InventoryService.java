package bg.tu_varna.sit.usp.phone_sales.inventory.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import bg.tu_varna.sit.usp.phone_sales.inventory.repository.InventoryRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import bg.tu_varna.sit.usp.phone_sales.web.dto.CartResponse;
import bg.tu_varna.sit.usp.phone_sales.web.dto.getphoneresponse.GetPhoneResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final PhoneService phoneService;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, PhoneService phoneService) {
        this.inventoryRepository = inventoryRepository;
        this.phoneService = phoneService;
    }

    public CartResponse getCartForUser(User user) {
        return getCartResponse(user);
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

    private CartResponse getCartResponse(User user) {
        List<Inventory> inCartItemsList = inventoryRepository.getAllByUserAndInInventoryFalse(user);
        if (inCartItemsList.isEmpty()) {
            throw new DomainException(ExceptionMessages.ADD_STUFF_TO_YOUR_CART);
        }
        log.info("Initializing cart response");
        return initializeCartResponse(inCartItemsList);
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
                .build();
    }

    public void setItemAsSold(Inventory inventory) {
        inventory.setInInventory(true);
        inventoryRepository.save(inventory);
    }
}
