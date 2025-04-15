package bg.tu_varna.sit.usp.phone_sales.orderdetails.model;

import bg.tu_varna.sit.usp.phone_sales.order.model.Sale;
import bg.tu_varna.sit.usp.phone_sales.user.model.City;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SaleDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String address;

    @ManyToOne(optional = false)
    private City city;

    @Column(nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryMethod deliveryMethod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @OneToOne(mappedBy = "saleDetails")
    private Sale sale;
}
