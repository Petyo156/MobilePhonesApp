package bg.tu_varna.sit.usp.phone_sales.order.model;

import bg.tu_varna.sit.usp.phone_sales.discount.model.DiscountCode;
import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.SaleDetails;
import bg.tu_varna.sit.usp.phone_sales.orderitem.model.SaleItem;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime orderDate;

    @Column(nullable = false)
    private BigDecimal totalPrice;

    @Column(nullable = false)
    private SaleStatus saleStatus;

    @OneToOne(optional = false)
    private SaleDetails saleDetails;

    @ManyToOne
    private DiscountCode discountCode;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    private List<SaleItem> saleItems = new ArrayList<>();
}
