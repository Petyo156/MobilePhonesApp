package org.example.phone_sales.camera.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.hardware.model.Hardware;

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

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private int count;

    @Column(nullable = false)
    private boolean autofocus;

    @Column(nullable = false)
    private int resolution;

    @Column(nullable = false)
    private int videoResolution;

    @Column(nullable = false)
    private int framerate;
}
