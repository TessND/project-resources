package com.tess.project_resources.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    private final Path fileStorageLocation; // Директория для хранения файлов
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    public FileService() {
        // Указываем директорию для хранения файлов (например, "uploads")
        this.fileStorageLocation = Paths.get("src/main/resources/static/uploads").toAbsolutePath().normalize();

        try {
            // Создаём директорию, если она не существует
            Files.createDirectories(this.fileStorageLocation);
            logger.info("Директория для хранения файлов создана: {}", this.fileStorageLocation);
        } catch (IOException ex) {
            logger.error("Не удалось создать директорию для хранения файлов.", ex);
            throw new RuntimeException("Не удалось создать директорию для хранения файлов.", ex);
        }
    }

    /**
     * Сохраняет файл на сервере и возвращает информацию о файле.
     *
     * @param file Файл для сохранения.
     * @return Информация о сохранённом файле.
     * @throws IOException Если произошла ошибка при сохранении файла.
     */
    public ProjectFileInfo storeFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Файл пуст и не может быть сохранён.");
        }

        // Генерируем уникальное имя файла
        String originalFileName = file.getOriginalFilename();
        String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // Сохраняем файл на сервере
        Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        logger.info("Файл сохранён: {}", targetLocation);
        return new ProjectFileInfo(originalFileName, uniqueFileName);
    }

    /**
     * Сохраняет изображение на сервере и возвращает информацию о файле.
     *
     * @param file Изображение для сохранения.
     * @return Информация о сохранённом изображении.
     * @throws IOException Если произошла ошибка при сохранении изображения.
     */
    public ProjectFileInfo storeImage(MultipartFile file) throws IOException {
        // Можно добавить дополнительную логику для обработки изображений (например, проверку формата)
        return storeFile(file);
    }

    /**
     * Загружает файл с сервера по его уникальному имени.
     *
     * @param uniqueFileName Уникальное имя файла.
     * @return Ресурс (файл) для скачивания.
     * @throws IOException Если файл не найден или недоступен.
     */
    public Resource load(String uniqueFileName) throws IOException {
        try {
            Path filePath = this.fileStorageLocation.resolve(uniqueFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                logger.info("Файл загружен: {}", filePath);
                return resource;
            } else {
                logger.error("Файл не найден или недоступен для чтения: {}", uniqueFileName);
                throw new IOException("Файл не найден или недоступен для чтения: " + uniqueFileName);
            }
        } catch (MalformedURLException ex) {
            logger.error("Ошибка при загрузке файла: {}", uniqueFileName, ex);
            throw new IOException("Ошибка при загрузке файла: " + uniqueFileName, ex);
        }
    }

    /**
     * Удаляет файл с сервера по его уникальному имени.
     *
     * @param uniqueFileName Уникальное имя файла.
     * @throws IOException Если файл не найден или произошла ошибка при удалении.
     */
    public void delete(String uniqueFileName) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(uniqueFileName).normalize();
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            logger.info("Файл удалён: {}", filePath);
        } else {
            logger.warn("Файл не найден: {}", filePath);
            throw new IOException("Файл не найден: " + uniqueFileName);
        }
    }
}