package bg.tu_varna.sit.usp.phone_sales.inventory.model;

import bg.tu_varna.sit.usp.phone_sales.deliveryoption.model.DeliveryOption;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "phone_id", nullable = false)
    private Phone phone;

    @OneToOne
    @JoinColumn(name = "delivery_option_id", nullable = false)
    private DeliveryOption deliveryOption;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column
    private Boolean inInventory;
}
