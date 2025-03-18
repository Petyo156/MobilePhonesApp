package bg.tu_varna.sit.usp.phone_sales.hardware.model;

import bg.tu_varna.sit.usp.phone_sales.camera.model.Camera;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.processor.model.Processor;
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "processor_id", referencedColumnName = "id")
    private Processor processor;

    @Column(nullable = false)
    private int ram;

    @Column(nullable = false)
    private int storage;

    @Column(nullable = false)
    private int batteryCapacity;

    @Column(nullable = false)
    private int screenSize;

    @Column(nullable = false)
    private int resolution;

    @Column(nullable = false)
    private int refreshRate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SIMTypeEnum simType;
}
