package bg.tu_varna.sit.usp.phone_sales.brand.service;

import bg.tu_varna.sit.usp.phone_sales.brand.model.Brand;
import bg.tu_varna.sit.usp.phone_sales.brand.repository.BrandRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class BrandService {
    private final BrandRepository brandRepository;

    @Autowired
    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public Brand findOrCreateBrand(String brandName) {
        Optional<Brand> existingBrand = brandRepository.findByName(brandName);
        if (existingBrand.isPresent()) {
            log.info("Brand found with name: {}", brandName);
            return existingBrand.get();
        }
        log.info("No existing brand found with name: {}. Creating a new brand.", brandName);
        return brandRepository.save(Brand.builder().name(brandName).build());
    }


}
