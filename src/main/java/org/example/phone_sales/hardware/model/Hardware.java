package org.example.phone_sales.hardware.model;

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
public class Hardware {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "hardware")
    private Phone phone;
}
