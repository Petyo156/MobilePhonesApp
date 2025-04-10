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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "camera_id", referencedColumnName = "id")
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
    private Integer resolution;

    @Column(nullable = false)
    private Integer refreshRate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SIMType simType;

    @Column(nullable = false)
    private Integer coreCount;
}
