package org.example.phone_sales.camera.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
}
