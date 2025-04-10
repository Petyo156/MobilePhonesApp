package bg.tu_varna.sit.usp.phone_sales.operatingsystem.service;

import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystemType;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.repository.OperatingSystemRepository;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphone.SubmitOperatingSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperatingSystemService {
    private final OperatingSystemRepository operatingSystemRepository;

    @Autowired
    public OperatingSystemService(OperatingSystemRepository operatingSystemRepository) {
        this.operatingSystemRepository = operatingSystemRepository;
    }

    public OperatingSystem submitOperatingSystem(SubmitOperatingSystem operatingSystemInfo, Phone phone) {
        String operatingSystemVersion = operatingSystemInfo.getVersion();
        OperatingSystemType operatingSystemType = operatingSystemInfo.getOperatingSystemType();

        Optional<OperatingSystem> operatingSystemOptional =
                operatingSystemRepository.findByTypeAndVersion(operatingSystemType, operatingSystemVersion);
        if(operatingSystemOptional.isEmpty()) {
            OperatingSystem operatingSystem = OperatingSystem.builder()
                    .type(operatingSystemInfo.getOperatingSystemType())
                    .version(operatingSystemInfo.getVersion())
                    .phones(List.of(phone))
                    .build();
            return operatingSystemRepository.save(operatingSystem);
        }

        OperatingSystem operatingSystem = operatingSystemOptional.get();
        operatingSystem.getPhones().add(phone);
        return operatingSystemRepository.save(operatingSystem);
    }
}
