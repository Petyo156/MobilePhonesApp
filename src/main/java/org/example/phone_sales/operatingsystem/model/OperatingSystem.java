package org.example.phone_sales.operatingsystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.software.model.Software;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OperatingSystem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "operatingSystem")
    private Software software;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperatingSystemTypeEnum type;

    @Column(nullable = false)
    private String version;
}
