package com.tess.project_resources.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    private final Path rootLocation = Paths.get("src/main/resources/static/uploads");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию для загрузки файлов", e);
        }
    }

    public ProjectFileInfo storeFile(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

            // Сохраняем файл на сервере
            Path destinationFile = rootLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // Возвращаем информацию о файле
            return new ProjectFileInfo(originalFileName, uniqueFileName);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл", e);
        }
    }

    public ProjectFileInfo storeImage(MultipartFile image) throws IOException {
        return storeFile(image); // Используем тот же метод, что и для файлов
    }   

    public void delete(String fileName) throws IOException {
        Path filePath = rootLocation.resolve(fileName);
        Files.deleteIfExists(filePath);
    }
}