package bg.tu_varna.sit.usp.phone_sales.phone.model;

import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class PhoneSpecification {
    public static Specification<Phone> getFilteredPhones(
            List<String> brands,
            List<Integer> storages,
            List<Integer> ram,
            Double minPrice,
            Double maxPrice,
            List<String> colors,
            List<String> cameraResolutions,
            List<String> screenSizes,
            Boolean waterResistant,
            List<Integer> batteryCapacities,
            Boolean discountedOnly
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.isTrue(root.get("isVisible")));

            if (brands != null && !brands.isEmpty()) {
                predicates.add(root.get("phoneModel").get("brand").get("name").in(brands));
            }

            if (storages != null && !storages.isEmpty()) {
                predicates.add(root.get("hardware").get("storage").in(storages));
            }

            if (ram != null && !ram.isEmpty()) {
                predicates.add(root.get("hardware").get("ram").in(ram));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), BigDecimal.valueOf(minPrice)));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), BigDecimal.valueOf(maxPrice)));
            }

            if (colors != null && !colors.isEmpty()) {
                predicates.add(root.get("dimension").get("color").in(colors));
            }

            if (cameraResolutions != null && !cameraResolutions.isEmpty()) {
                Expression<String> cameraResStr = cb.function("CAST", String.class, root.get("hardware").get("camera").get("resolution"));
                predicates.add(cameraResStr.in(cameraResolutions));
            }

            if (screenSizes != null && !screenSizes.isEmpty()) {
                Expression<String> screenSizeStr = cb.function("FORMAT", String.class, root.get("hardware").get("screenSize"), cb.literal("0.0"));
                predicates.add(screenSizeStr.in(screenSizes));
            }

            if (waterResistant != null && waterResistant) {
                predicates.add(cb.isTrue(root.get("dimension").get("isWaterResistant")));
            }

            if (batteryCapacities != null && !batteryCapacities.isEmpty()) {
                predicates.add(root.get("hardware").get("batteryCapacity").in(batteryCapacities));
            }

            if (discountedOnly != null && discountedOnly) {
                predicates.add(cb.greaterThan(root.get("discountPercent"), BigDecimal.ZERO));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
