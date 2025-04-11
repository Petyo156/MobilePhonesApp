package bg.tu_varna.sit.usp.phone_sales.dimension.service;

import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.dimension.repository.DimensionRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitPhoneDimensions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DimensionService {
    private final DimensionRepository dimensionRepository;

    @Autowired
    public DimensionService(DimensionRepository dimensionRepository) {
        this.dimensionRepository = dimensionRepository;
    }

    public Dimension submitDimension(SubmitPhoneDimensions dimensions) {
        Dimension dimension = Dimension.builder()
                .color(dimensions.getColor())
                .height(dimensions.getHeight())
                .width(dimensions.getWidth())
                .weight(dimensions.getWeight())
                .isWaterResistant(dimensions.getWaterResistance())
                .thickness(dimensions.getThickness())
                .build();
        log.info("Submitting new dimensions");
        return dimensionRepository.save(dimension);
    }
}
