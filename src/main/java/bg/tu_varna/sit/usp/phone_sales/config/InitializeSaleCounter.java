package bg.tu_varna.sit.usp.phone_sales.config;

import bg.tu_varna.sit.usp.phone_sales.order.model.SaleCounter;
import bg.tu_varna.sit.usp.phone_sales.order.repository.SaleCounterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
@Slf4j
public class InitializeSaleCounter implements CommandLineRunner {
    private final SaleCounterRepository saleCounterRepository;

    @Autowired
    public InitializeSaleCounter(SaleCounterRepository saleCounterRepository) {
        this.saleCounterRepository = saleCounterRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (saleCounterRepository.findById(1L).isEmpty()) {
            SaleCounter counter = SaleCounter.builder()
                    .lastOrderNumber(0L)
                    .build();

            saleCounterRepository.save(counter);
            log.info("Sale counter initialized.");
        }
    }
}
