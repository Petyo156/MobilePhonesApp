package bg.tu_varna.sit.usp.phone_sales.web.dto;

import bg.tu_varna.sit.usp.phone_sales.review.model.Rating;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"name", "comment", "rating"})
public class ReviewResponse {
    private String comment;

    private Rating rating;

    private String name;
}
