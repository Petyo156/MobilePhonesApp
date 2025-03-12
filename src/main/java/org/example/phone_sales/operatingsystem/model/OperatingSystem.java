package org.example.phone_sales.operatingsystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.phone.model.Phone;

import java.util.List;
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

    @OneToMany(mappedBy = "operatingSystem")
    private List<Phone> phones;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OperatingSystemTypeEnum type;

    @Column(nullable = false)
    private String version;
}
