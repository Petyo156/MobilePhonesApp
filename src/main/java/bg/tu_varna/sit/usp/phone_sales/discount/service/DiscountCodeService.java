package bg.tu_varna.sit.usp.phone_sales.discount.service;

import bg.tu_varna.sit.usp.phone_sales.discount.model.DiscountCode;
import bg.tu_varna.sit.usp.phone_sales.discount.repository.DiscountCodeRepository;
import bg.tu_varna.sit.usp.phone_sales.exception.ExceptionMessages;
import bg.tu_varna.sit.usp.phone_sales.exception.InvalidDiscountCodeException;
import bg.tu_varna.sit.usp.phone_sales.exception.InvalidDiscountCodePercentInputException;
import bg.tu_varna.sit.usp.phone_sales.web.dto.order.DiscountCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DiscountCodeService {
    private final DiscountCodeRepository discountCodeRepository;
    private final DecimalFormat decimalFormat;

    @Autowired
    public DiscountCodeService(DiscountCodeRepository discountCodeRepository, DecimalFormat decimalFormat) {
        this.discountCodeRepository = discountCodeRepository;
        this.decimalFormat = decimalFormat;
    }

    public Boolean isValidCode(String discountCode) {
        Map<String, BigDecimal> allDiscountCodes = getDiscountCodesByStatus(true);
        String lowerCaseDiscountCode = discountCode.toLowerCase();
        for (String key : allDiscountCodes.keySet()) {
            if (lowerCaseDiscountCode.equals(key.toLowerCase())) {
                log.info("Discount code {} is valid", discountCode);
                return true;
            }
        }
        log.info("Invalid discount code {}", discountCode);
        return false;
    }

    public List<DiscountCodeResponse> getDiscountCodeResponses(boolean isActive) {
        log.info("Retrieving discount code responses by status {}", isActive);
        return getDiscountCodesByStatus(isActive).entrySet().stream()
                .map(entry -> DiscountCodeResponse.builder()
                        .discountCode(entry.getKey())
                        .discountPercent(decimalFormat.format(entry.getValue()))
                        .build())
                .collect(Collectors.toList());
    }

    public Map<String, BigDecimal> getDiscountCodesByStatus(boolean isActive) {
        List<DiscountCode> discountCodes = isActive
                ? discountCodeRepository.findAllByIsActiveTrue()
                : discountCodeRepository.findAllByIsActiveFalse();

        return discountCodes.stream()
                .collect(Collectors.toMap(DiscountCode::getName, DiscountCode::getDiscount));
    }

    public BigDecimal getDiscountCodePercent(String discountCode) {
        Map<String, BigDecimal> all = getDiscountCodesByStatus(true);
        String lowerCaseDiscountCode = discountCode.toLowerCase();
        for (String key : all.keySet()) {
            if (lowerCaseDiscountCode.equals(key.toLowerCase())) {
                return all.get(key);
            }
        }
        throw new InvalidDiscountCodeException(ExceptionMessages.INVALID_DISCOUNT_CODE);
    }

    public BigDecimal calculateDiscountPrice(BigDecimal totalPrice, BigDecimal discountCodePercent) {
        BigDecimal discountFraction = discountCodePercent.divide(BigDecimal.valueOf(100));
        BigDecimal discountAmount = totalPrice.multiply(discountFraction);
        return totalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public void updateStatusByName(String name) {
        DiscountCode discountCode = findByName(name);
        discountCode.setIsActive(!discountCode.getIsActive());
        discountCodeRepository.save(discountCode);
        log.info("Code status updated successfully");
    }

    public DiscountCode findByName(String name) {
        return discountCodeRepository.findByName(name)
                .orElseThrow(() -> new InvalidDiscountCodeException(ExceptionMessages.INVALID_DISCOUNT_CODE));
    }

    public DiscountCode getDiscountCodeForSaleCreation(String discountCode) {
        Optional<DiscountCode> discountCodeOptional = discountCodeRepository.findByName(discountCode);
        if (discountCodeOptional.isEmpty()) {
            log.info("No discount code usage");
            return null;
        }
        return discountCodeOptional.get();
    }

    public void addNewCode(String name, String discountPercent) {
        try {
            BigDecimal percent = new BigDecimal(discountPercent);
            if (percent.compareTo(BigDecimal.ZERO) <= 0 || percent.compareTo(BigDecimal.valueOf(100)) >= 0) {
                throw new InvalidDiscountCodePercentInputException(ExceptionMessages.INVALID_DISCOUNT_CODE_PERCENT_INPUT);
            }
            DiscountCode discountCode = initializeDiscountCode(name, percent);
            discountCodeRepository.save(discountCode);
            log.info("Code added successfully");
        } catch (NumberFormatException e) {
            throw new InvalidDiscountCodePercentInputException(ExceptionMessages.INVALID_DISCOUNT_CODE_PERCENT_INPUT);
        }
    }

    private DiscountCode initializeDiscountCode(String name, BigDecimal percent) {
        return DiscountCode.builder()
                .name(name)
                .discount(percent)
                .isActive(true)
                .build();
    }
}
