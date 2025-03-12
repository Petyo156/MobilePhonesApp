package org.example.phone_sales.hardware.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.camera.model.Camera;
import org.example.phone_sales.phone.model.Phone;
import org.example.phone_sales.processor.model.Processor;

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
}
