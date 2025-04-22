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
    private final ImageRepository imageRepository;
    private final Cloudinary cloudinary;

    @Autowired
    public ImageService(ImageRepository imageRepository, Cloudinary cloudinary) {
        this.imageRepository = imageRepository;
        this.cloudinary = cloudinary;
    }

    public List<Image> saveImages(List<MultipartFile> imageFiles, int thumbnailIndex, Phone phone) {
        List<Image> images = new ArrayList<>();
        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            if (!file.isEmpty()) {
                Boolean isThumbnail = i == thumbnailIndex;
                images.add(saveImage(file, isThumbnail, phone));
            }
        }
        return images;
    }

    private Image saveImage(MultipartFile imageFile, Boolean isThumbnail, Phone phone) {
        try {
            // Generate a unique public ID for the image
            String publicId = UUID.randomUUID().toString();
            
            // Upload the image to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(
                imageFile.getBytes(),
                ObjectUtils.asMap(
                    "public_id", publicId,
                    "resource_type", "auto"
                )
            );

            // Get the secure URL from the upload result
            String secureUrl = (String) uploadResult.get("secure_url");

            Image image = initializeImage(isThumbnail, phone, secureUrl);

            log.info("Uploaded image to Cloudinary: {}", secureUrl);
            return image;
        } catch (IOException e) {
            throw new DomainException("Failed to upload image to Cloudinary: " + e.getMessage());
        }
    }

    private Image initializeImage(Boolean isThumbnail, Phone phone, String imageUrl) {
        return Image.builder()
                .phone(phone)
                .isThumbnail(isThumbnail)
                .imageUrl(imageUrl)
                .build();
    }
}
