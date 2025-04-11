package bg.tu_varna.sit.usp.phone_sales.operatingsystem.service;

import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystem;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.model.OperatingSystemType;
import bg.tu_varna.sit.usp.phone_sales.operatingsystem.repository.OperatingSystemRepository;
import bg.tu_varna.sit.usp.phone_sales.web.dto.submitphonerequest.SubmitOperatingSystem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OperatingSystemService {
    private final OperatingSystemRepository operatingSystemRepository;

    @Autowired
    public OperatingSystemService(OperatingSystemRepository operatingSystemRepository) {
        this.operatingSystemRepository = operatingSystemRepository;
    }

    public OperatingSystem submitOperatingSystem(SubmitOperatingSystem operatingSystemInfo) {
        String version = operatingSystemInfo.getVersion();
        OperatingSystemType type = operatingSystemInfo.getOperatingSystemType();

        log.info("Check if OS already exists");
        return operatingSystemRepository.findByTypeAndVersion(type, version)
                .orElseGet(() -> {
                    OperatingSystem newOs = OperatingSystem.builder()
                            .type(type)
                            .version(version)
                            .build();
                    log.info("Saving new OS");
                    return operatingSystemRepository.save(newOs);
                });
    }

}
