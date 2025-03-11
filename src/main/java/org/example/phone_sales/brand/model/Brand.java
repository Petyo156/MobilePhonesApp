package org.example.phone_sales.brand.model;

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
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "brand")
    private List<Phone> phones;
}
