package bg.tu_varna.sit.usp.phone_sales.phone.model;

import jakarta.persistence.*;
import lombok.*;
import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import bg.tu_varna.sit.usp.phone_sales.model.model.PhoneModel;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
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
    private Integer releaseYear;

    @ManyToOne
    @JoinColumn(name = "model_id", nullable = false)
    private PhoneModel phoneModel;

    @OneToOne(cascade = CascadeType.ALL)
    private Hardware hardware;

    @ManyToOne
    private OperatingSystem operatingSystem;

    @OneToOne(cascade = CascadeType.ALL)
    private Dimension dimension;

    @OneToMany(mappedBy = "phone")
    private List<Image> images = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime addedDate;

    @Column(nullable = false)
    private Boolean isVisible;

    @Column(nullable = false, unique = true)
    private String slug;
}
