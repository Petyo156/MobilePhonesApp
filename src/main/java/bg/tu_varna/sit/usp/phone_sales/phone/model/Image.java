package bg.tu_varna.sit.usp.phone_sales.phone.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(name = "image_index", nullable = false)
    private Integer imageIndex;

    @ManyToOne
    @JoinColumn(name = "phone_id")
    private Phone phone;
}
