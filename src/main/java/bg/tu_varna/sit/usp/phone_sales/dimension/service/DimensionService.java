package bg.tu_varna.sit.usp.phone_sales.dimension.service;

import bg.tu_varna.sit.usp.phone_sales.dimension.model.Dimension;
import bg.tu_varna.sit.usp.phone_sales.dimension.repository.DimensionRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitPhoneDimensions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DimensionService {
    private final DimensionRepository dimensionRepository;

    @Autowired
    public DimensionService(DimensionRepository dimensionRepository) {
        this.dimensionRepository = dimensionRepository;
    }

    public Dimension submitDimension(SubmitPhoneDimensions dimensions, Phone phone) {
        Dimension dimension = Dimension.builder()
                .color(dimensions.getColor())
                .phone(phone)
                .height(dimensions.getHeight())
                .width(dimensions.getWidth())
                .isWaterResistant(dimensions.getWaterResistance())
                .thickness(dimensions.getThickness())
                .build();

        return dimensionRepository.save(dimension);
    }
}
