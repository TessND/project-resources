package com.tess.project_resources.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public void createProject(ProjectDTO projectDTO, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setUser(user);

        // Обработка изображений (если они загружены)
        if (projectDTO.getImages() != null && !projectDTO.getImages().isEmpty()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : projectDTO.getImages()) {
                if (!image.isEmpty()) { // Сохраняем только непустые файлы
                    String imageUrl = saveFile(image);
                    imageUrls.add(imageUrl);
                }
            }
            project.setImageUrls(imageUrls);
        } else {
            project.setImageUrls(new ArrayList<>()); // Инициализируем пустой список, если изображений нет
        }

        // Обработка файлов (если они загружены)
        if (projectDTO.getFiles() != null && !projectDTO.getFiles().isEmpty()) {
            List<String> fileUrls = new ArrayList<>();
            for (MultipartFile file : projectDTO.getFiles()) {
                if (!file.isEmpty()) { // Сохраняем только непустые файлы
                    String fileUrl = saveFile(file);
                    fileUrls.add(fileUrl);
                }
            }
            project.setFileUrls(fileUrls);
        } else {
            project.setFileUrls(new ArrayList<>()); // Инициализируем пустой список, если файлов нет
        }

        projectRepository.save(project);
    }


    /**
     * Находит проект по ID.
     *
     * @param projectId ID проекта.
     * @return Найденный проект.
     */
    public Project findById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Проект не найден"));
    }

    /**
     * Проверяет, является ли пользователь владельцем проекта.
     *
     * @param projectId ID проекта.
     * @param userId    ID пользователя.
     * @return true, если пользователь является владельцем, иначе false.
     */
    public boolean isProjectOwner(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Проект не найден"));
        return project.getUser().getId().equals(userId);
    }

    
    public String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Генерируем уникальное имя файла (с дефисами)
        String uniqueFileName = UUID.randomUUID().toString();

        // Получаем расширение оригинального файла
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        
        // Убираем тире из уникального имени файла
        uniqueFileName = uniqueFileName.replace("-", "_");

        // Собираем имя файла с расширением
        String fileName = uniqueFileName + fileExtension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        return "/uploads/" + fileName;
    }
    /**
     * Находит проекты по названию (частичное совпадение).
     */
    public Page<Project> findByNameContaining(String name, Pageable pageable) {
        return projectRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Получает все проекты с пагинацией.
     */
    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }
}