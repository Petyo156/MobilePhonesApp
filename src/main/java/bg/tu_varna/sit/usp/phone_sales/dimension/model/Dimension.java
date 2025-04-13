package bg.tu_varna.sit.usp.phone_sales.dimension.model;

import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Dimension {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "dimension")
    private Phone phone;

    @Column
    private Double height;

    @Column
    private Double width;

    @Column
    private Double thickness;

    @Column
    private Double weight;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private Boolean isWaterResistant;

}
