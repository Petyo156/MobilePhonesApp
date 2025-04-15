package bg.tu_varna.sit.usp.phone_sales.phone.service;

import bg.tu_varna.sit.usp.phone_sales.exception.DomainException;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Image;
import bg.tu_varna.sit.usp.phone_sales.phone.model.Phone;
import bg.tu_varna.sit.usp.phone_sales.phone.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void saveImages(List<MultipartFile> imageFiles, int thumbnailIndex, Phone phone) {
        for (int i = 0; i < imageFiles.size(); i++) {
            MultipartFile file = imageFiles.get(i);
            if (!file.isEmpty()) {
                Boolean isThumbnail = i == thumbnailIndex;
                saveImage(file, isThumbnail, phone);
            }
        }
    }

    private void saveImage(MultipartFile imageFile, Boolean isThumbnail, Phone phone) {
        String folder = "C:\\Codes\\Spring\\PhoneSales\\phone_sales\\src\\main\\resources\\static\\images\\phoneimages";
        try {
            Files.createDirectories(Paths.get(folder));

            String uniqueName = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
            Path path = Paths.get(folder, uniqueName);
            Files.write(path, imageFile.getBytes());

            String relativePath ="static/images/phoneimages/" + uniqueName;

            Image image = initializeImage(isThumbnail, phone, relativePath);

            imageRepository.save(image);
            log.info("Saved image to {}", relativePath);
        } catch (IOException e) {
            throw new DomainException("Failed to save image: " + e.getMessage());
        }
    }

    private Image initializeImage(Boolean isThumbnail, Phone phone, String relativePath) {
        return Image.builder()
                .phone(phone)
                .isThumbnail(isThumbnail)
                .imageUrl(relativePath)
                .build();
    }

}
