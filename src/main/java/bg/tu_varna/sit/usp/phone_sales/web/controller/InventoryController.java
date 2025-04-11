package bg.tu_varna.sit.usp.phone_sales.web.controller;

import bg.tu_varna.sit.usp.phone_sales.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller("/cart")
public class InventoryController {
    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    //TODO
    //-getCartPage
    //-getCheckoutPage - saving DeliveryOption object
}
