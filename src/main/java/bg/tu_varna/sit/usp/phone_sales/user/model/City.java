package bg.tu_varna.sit.usp.phone_sales.user.model;

import bg.tu_varna.sit.usp.phone_sales.orderdetails.model.SaleDetails;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "city")
    private List<User> users;

    @OneToMany(mappedBy = "city")
    private List<SaleDetails> saleDetails;
}
