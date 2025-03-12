package org.example.phone_sales.deliveryoption.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.inventory.model.Inventory;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DeliveryOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryMethodEnum deliveryMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum paymentMethod;

    @OneToOne(mappedBy = "deliveryOption")
    private Inventory inventory;
}
