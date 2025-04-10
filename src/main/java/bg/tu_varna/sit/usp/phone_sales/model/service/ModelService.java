package bg.tu_varna.sit.usp.phone_sales.model.service;

import bg.tu_varna.sit.usp.phone_sales.brand.service.BrandService;
import bg.tu_varna.sit.usp.phone_sales.model.model.Model;
import bg.tu_varna.sit.usp.phone_sales.model.repository.ModelRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitBrandAndModel;
import org.springframework.stereotype.Service;

@Service
public class ModelService {
    private ModelRepository modelRepository;
    private BrandService brandService;

    public Model submitBrandAndModel(SubmitBrandAndModel brandAndModel, Phone phone) {


        return null;
    }
}
