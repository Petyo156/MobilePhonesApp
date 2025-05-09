package bg.tu_varna.sit.usp.phone_sales.review.model;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum Rating {
    ONE(BigDecimal.valueOf(1)),
    TWO(BigDecimal.valueOf(2)),
    THREE(BigDecimal.valueOf(3)),
    FOUR(BigDecimal.valueOf(4)),
    FIVE(BigDecimal.valueOf(5));

    private final BigDecimal value;

    Rating(BigDecimal value) {
        this.value = value;
    }
}