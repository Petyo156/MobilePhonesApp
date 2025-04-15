package bg.tu_varna.sit.usp.phone_sales.hardware.model;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
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
public class Hardware {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "hardware")
    private Phone phone;

    @OneToOne
    private Camera camera;

    @Column(nullable = false)
    private Integer ram;

    @Column(nullable = false)
    private Integer storage;

    @Column(nullable = false)
    private Integer batteryCapacity;

    @Column(nullable = false)
    private Double screenSize;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SIMType simType;

    @Column
    private Integer coreCount;

    @Column
    private Integer refreshRate;

    @Column
    private String screenResolution;
}
