package org.example.phone_sales.software.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.operatingsystem.model.OperatingSystem;
import org.example.phone_sales.phone.model.Phone;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Software {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "software")
    private Phone phone;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SIMTypeEnum simType;

    @OneToOne
    @JoinColumn(name = "operatingsystem_id")
    private OperatingSystem operatingSystem;
}
