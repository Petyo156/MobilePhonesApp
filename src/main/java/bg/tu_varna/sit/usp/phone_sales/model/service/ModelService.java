package bg.tu_varna.sit.usp.phone_sales.model.service;

import bg.tu_varna.sit.usp.phone_sales.brand.model.Brand;
import bg.tu_varna.sit.usp.phone_sales.brand.service.BrandService;
import bg.tu_varna.sit.usp.phone_sales.model.model.PhoneModel;
import bg.tu_varna.sit.usp.phone_sales.model.repository.ModelRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitBrandAndModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class ModelService {
    private final ModelRepository modelRepository;
    private final BrandService brandService;

    @Autowired
    public ModelService(ModelRepository modelRepository, BrandService brandService) {
        this.modelRepository = modelRepository;
        this.brandService = brandService;
    }

    public PhoneModel submitBrandAndModel(SubmitBrandAndModel brandAndModel) {
        String brandName = brandAndModel.getBrand().trim();
        String modelName = brandAndModel.getModel().trim();

        Brand brand = brandService.findOrCreateBrand(brandName);

        Optional<PhoneModel> existingModel = modelRepository.findByNameAndBrand(modelName, brand);

        if (existingModel.isPresent()) {
            log.info("Model {} already exists", modelName);
            return existingModel.get();
        }

        PhoneModel phoneModel = PhoneModel.builder()
                .name(modelName)
                .brand(brand)
                .build();

        log.info("Model {} does not exist. Creating new model.", modelName);
        return modelRepository.save(phoneModel);
    }
}
