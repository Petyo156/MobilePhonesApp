package bg.tu_varna.sit.usp.phone_sales.order.service;

import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.exception.InvalidCounterIdException;
import bg.tu_varna.sit.usp.phone_sales.order.model.SaleCounter;
import bg.tu_varna.sit.usp.phone_sales.order.repository.SaleCounterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SaleCounterService {
    private final SaleCounterRepository saleCounterRepository;

    @Autowired
    public SaleCounterService(SaleCounterRepository saleCounterRepository) {
        this.saleCounterRepository = saleCounterRepository;
    }

    public String getFormattedOrderNumber() {
        SaleCounter counter = saleCounterRepository.findById(1L)
                .orElseThrow(() -> new InvalidCounterIdException(ExceptionMessages.INVALID_COUNTER_ID));

        long nextNumber = counter.getLastOrderNumber() + 1;
        counter.setLastOrderNumber(nextNumber);
        saleCounterRepository.save(counter);

        log.info("Initialized order number: " + counter.getLastOrderNumber());
        return String.format("%05d", nextNumber);
    }
}
