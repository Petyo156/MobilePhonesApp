package bg.tu_varna.sit.usp.phone_sales.orderitem.service;

import bg.tu_varna.sit.usp.phone_sales.cartitem.model.CartItem;
import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.order.model.Sale;
import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.orderitem.repository.SaleItemRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.service.PhoneService;
import bg.tu_varna.sit.usp.phone_sales.review.model.Review;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SaleItemService {
    private final SaleItemRepository saleItemRepository;
    private final PhoneService phoneService;

    @Autowired
    public SaleItemService(SaleItemRepository saleItemRepository, PhoneService phoneService) {
        this.saleItemRepository = saleItemRepository;
        this.phoneService = phoneService;
    }

    public void createSaleItemsForNewSale(Sale sale, User user) {
        List<CartItem> cartItems = user.getCart().getCartItems();
        for (CartItem cartItem : cartItems) {
            SaleItem saleItem = initializeSaleItem(sale, cartItem);
            phoneService.reducePhoneQuantityAfterPurchase(cartItem.getPhone(), cartItem.getQuantity());
            saleItemRepository.save(saleItem);
        }
        log.info("Initialized sale items based on user's cart");
    }

    private SaleItem initializeSaleItem(Sale sale, CartItem cartItem) {
        return SaleItem.builder()
                .phone(cartItem.getPhone())
                .quantity(cartItem.getQuantity())
                .priceAtTime(cartItem.getPhone().getPrice())
                .sale(sale)
                .build();
    }

    public SaleItem getSaleItemReviewForUser(User user, String slug) {
        Optional<SaleItem> saleItemOptional = saleItemRepository.findFirstSaleItemByPhone_SlugAndSale_User(slug, user);
        if(saleItemOptional.isEmpty()) {
            throw new DomainException(ExceptionMessages.SALE_ITEM_DOESNT_EXIST);
        }
        return saleItemOptional.get();
    }

    public void setSaleItemReview(SaleItem saleItem, Review review) {
        saleItem.setReview(review);
        saleItemRepository.save(saleItem);
    }
}
