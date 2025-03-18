package bg.tu_varna.sit.usp.phone_sales.user.model;

import bg.tu_varna.sit.usp.phone_sales.inventory.model.Inventory;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private String city;

    @Column
    private String address;

    @Size(max = 10)
    @Column
    private String phoneNumber;

    @Column(nullable = false)
    private boolean isAdmin;

    @OneToMany(mappedBy = "user")
    private List<Inventory> inventories;
}
