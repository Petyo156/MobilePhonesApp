package bg.tu_varna.sit.usp.phone_sales.phone.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Image;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.ImageRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    private final Cloudinary cloudinary;

    @Autowired
    public ImageService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public List<Image> saveImages(List<MultipartFile> imageFiles, int thumbnailIndex, Phone phone) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            if (!file.isEmpty()) {
                images.add(saveImage(file, i, phone));
            }
        }
        return images;
    }

    private Image saveImage(MultipartFile imageFile, int index, Phone phone) {
        try {
            String publicId = UUID.randomUUID().toString();
            
            Map uploadResult = cloudinary.uploader().upload(
                imageFile.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "auto"
                )
            );

            String secureUrl = (String) uploadResult.get("secure_url");

            Image image = initializeImage(index, phone, secureUrl);

            log.info("Uploaded image to Cloudinary: {}", secureUrl);
            return image;
        } catch (IOException e) {
            throw new DomainException("Failed to upload image to Cloudinary: " + e.getMessage());
        }
    }

    private Image initializeImage(int index, Phone phone, String imageUrl) {
        return Image.builder()
                .phone(phone)
                .imageIndex(index)
                .imageUrl(imageUrl)
                .build();
    }
}
