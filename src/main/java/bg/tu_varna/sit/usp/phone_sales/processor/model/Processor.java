package bg.tu_varna.sit.usp.phone_sales.processor.model;

import bg.tu_varna.sit.usp.phone_sales.hardware.model.Hardware;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Processor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "processor")
    private Hardware hardware;

    @Column(nullable = false)
    private int coreCount;

    @Column
    private String gpu;
}
