package bg.tu_varna.sit.usp.phone_sales.web.dto;

import bg.tu_varna.sit.usp.phone_sales.review.model.Rating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {
    @Size(max = 200)
    private String comment;

    @NotNull
    private Rating rating;

    @NotNull
    private String name;
}
