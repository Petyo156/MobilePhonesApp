package org.example.phone_sales.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.deliveryoption.model.DeliveryOption;
import org.example.phone_sales.phone.model.Phone;
import org.example.phone_sales.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
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
    private int quantity;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Column
    private boolean inInventory;
}
