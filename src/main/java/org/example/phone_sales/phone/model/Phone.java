package org.example.phone_sales.phone.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.brand.model.Brand;
import org.example.phone_sales.dimension.model.Dimension;
import org.example.phone_sales.hardware.model.Hardware;
import org.example.phone_sales.model.model.Model;
import org.example.phone_sales.operatingsystem.model.OperatingSystem;

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
    @JoinColumn(name = "hardware_id", referencedColumnName = "id")
    private Hardware hardware;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "operating_system_id", referencedColumnName = "id")
    private OperatingSystem operatingSystem;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dimension_id", referencedColumnName = "id")
    private Dimension dimension;
}
