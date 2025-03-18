package bg.tu_varna.sit.usp.phone_sales.phone.model;

import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.model.model.Model;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import jakarta.persistence.*;
import lombok.*;

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

//    @ManyToOne
//    @JoinColumn(name = "brand_id", nullable = false)
//    private Brand brand; // phone -> model -> brand

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private Model model;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "hardware_id", referencedColumnName = "id")
    private Hardware hardware;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "operating_system_id", referencedColumnName = "id")
    private OperatingSystem operatingSystem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dimension_id", referencedColumnName = "id")
    private Dimension dimension;
}
