package bg.tu_varna.sit.usp.phone_sales.discount;

import bg.tu_varna.sit.usp.phone_sales.order.model.Sale;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal discount;

    @OneToMany(mappedBy = "discountCode")
    private List<Sale> sales = new ArrayList<>();
}
