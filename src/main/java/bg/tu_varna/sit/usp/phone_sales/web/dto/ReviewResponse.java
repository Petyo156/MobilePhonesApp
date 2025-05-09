package bg.tu_varna.sit.usp.phone_sales.web.dto;

import bg.tu_varna.sit.usp.phone_sales.review.model.Rating;
import lombok.*;
import java.util.UUID;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id", "name", "comment", "rating"})
public class ReviewResponse {
    private UUID id;
    private String comment;
    private Rating rating;
    private String name;
    private LocalDateTime createdAt;
}