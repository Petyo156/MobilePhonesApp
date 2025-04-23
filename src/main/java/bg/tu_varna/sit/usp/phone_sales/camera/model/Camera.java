package bg.tu_varna.sit.usp.phone_sales.camera.model;

import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "camera")
    private Hardware hardware;

    @Column
    private Integer count;

    @Column(nullable = false)
    private Integer resolution;

    @Column
    private Integer videoResolution;
}
