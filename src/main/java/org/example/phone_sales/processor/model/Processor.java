package org.example.phone_sales.processor.model;

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
