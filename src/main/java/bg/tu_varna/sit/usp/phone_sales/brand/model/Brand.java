package bg.tu_varna.sit.usp.phone_sales.brand.model;

import bg.tu_varna.sit.usp.phone_sales.model.model.PhoneModel;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    private List<PhoneModel> phoneModels = new ArrayList<>();
}
