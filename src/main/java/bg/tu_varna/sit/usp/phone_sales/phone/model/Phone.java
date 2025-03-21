package bg.tu_varna.sit.usp.phone_sales.phone.model;

import bg.tu_varna.sit.usp.phone_sales.brand.model.Brand;
import jakarta.persistence.*;
import lombok.*;
import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.model.model.Model;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime releaseDate;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @OneToOne(cascade = CascadeType.ALL)
    private Hardware hardware;

    @ManyToOne(cascade = CascadeType.ALL)
    private OperatingSystem operatingSystem;

    @OneToOne(cascade = CascadeType.ALL)
    private Dimension dimension;
}
