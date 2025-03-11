package org.example.phone_sales.dimension.model;

import jakarta.persistence.*;
import lombok.*;
import org.example.phone_sales.phone.model.Phone;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Dimension {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "dimension")
    private Phone phone;

}
