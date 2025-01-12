package com.tess.project_resources.project;

import com.tess.project_resources.file.FileService;
import com.tess.project_resources.file.ProjectFileInfo;
import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    /**
     * Получает список всех проектов с пагинацией.
     */
    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }
    
    /**
     * Ищет проекты по названию с пагинацией.
     */
    public Page<Project> findByNameContaining(String search, Pageable pageable) {
        return projectRepository.findByNameContaining(search, pageable);
    }

    /**
     * Находит проект по ID.
     */
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Проект не найден"));
    }

    /**
     * Создаёт новый проект.
     *
     * @param projectDTO Данные проекта.
     * @param userId     ID пользователя, создающего проект.
     * @return Созданный проект.
     */
    @Transactional
    public Project createProject(ProjectDTO projectDTO, Long userId) throws IOException {
        User user = userService.findById(userId);
        ProjectType projectType = projectTypeRepository.findById(projectDTO.getProjectTypeId())
                .orElseThrow(() -> new RuntimeException("Тип проекта не найден"));

        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setUser(user);
        project.setProjectType(projectType);


        // Инициализация списка изображений
        project.setImages(new ArrayList<>());

        // Сохранение изображений (если они есть)
        if (projectDTO.getImages() != null && !projectDTO.getImages().isEmpty()) {
            for (MultipartFile image : projectDTO.getImages()) {
                if (!image.isEmpty()) { // Проверяем, что файл не пустой
                    ProjectFileInfo imageInfo = fileService.storeImage(image);

                    ProjectImage projectImage = new ProjectImage();
                    projectImage.setOriginalFileName(imageInfo.getOriginalFileName());
                    projectImage.setUniqueFileName(imageInfo.getUniqueFileName());
                    projectImage.setProject(project);

                    project.getImages().add(projectImage);
                }
            }
        }

        // Сохранение файлов (если они есть)
        if (projectDTO.getFiles() != null && !projectDTO.getFiles().isEmpty()) {
            for (MultipartFile file : projectDTO.getFiles()) {
                if (!file.isEmpty()) { // Проверяем, что файл не пустой
                    ProjectFileInfo fileInfo = fileService.storeFile(file);

                    ProjectFile projectFile = new ProjectFile();
                    projectFile.setOriginalFileName(fileInfo.getOriginalFileName());
                    projectFile.setUniqueFileName(fileInfo.getUniqueFileName());
                    projectFile.setProject(project);

                    project.getFiles().add(projectFile);
                }
            }
        }

        return projectRepository.save(project);
    }

    /**
     * Удаляет проект по ID.
     */
    @Transactional
    public void deleteProject(Long id) throws IOException {
        // Находим проект по ID
        Project project = findById(id);

        // Удаляем изображения проекта
        for (ProjectImage image : project.getImages()) {
            fileService.delete(image.getUniqueFileName());
        }

        // Удаляем файлы проекта
        for (ProjectFile file : project.getFiles()) {
            fileService.delete(file.getUniqueFileName());
        }

        // Удаляем проект из базы данных
        projectRepository.delete(project);

        logger.info("Проект удалён. Project ID: {}", id);
    }

    @Transactional
    public Project editProject(Long id, ProjectDTO projectDTO) throws IOException {
        Project project = findById(id);
        ProjectType projectType = projectTypeRepository.findById(projectDTO.getProjectTypeId())
                .orElseThrow(() -> new RuntimeException("Тип проекта не найден"));

        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setProjectType(projectType);

        // Удаляем старые изображения
        for (ProjectImage image : project.getImages()) {
            fileService.delete(image.getUniqueFileName());
        }
        project.getImages().clear();

        // Сохраняем новые изображения (если они есть)
        if (projectDTO.getImages() != null) {
            for (MultipartFile image : projectDTO.getImages()) {
                if (image != null && !image.isEmpty()) { // Проверка на пустой файл
                    ProjectFileInfo imageInfo = fileService.storeImage(image);

                    ProjectImage projectImage = new ProjectImage();
                    projectImage.setOriginalFileName(imageInfo.getOriginalFileName());
                    projectImage.setUniqueFileName(imageInfo.getUniqueFileName());
                    projectImage.setProject(project);

                    project.getImages().add(projectImage);
                }
            }
        }

        // Удаляем старые файлы
        for (ProjectFile file : project.getFiles()) {
            fileService.delete(file.getUniqueFileName());
        }
        project.getFiles().clear();

        // Сохраняем новые файлы (если они есть)
        if (projectDTO.getFiles() != null) {
            for (MultipartFile file : projectDTO.getFiles()) {
                if (file != null && !file.isEmpty()) { // Проверка на пустой файл
                    ProjectFileInfo fileInfo = fileService.storeFile(file);

                    ProjectFile projectFile = new ProjectFile();
                    projectFile.setOriginalFileName(fileInfo.getOriginalFileName());
                    projectFile.setUniqueFileName(fileInfo.getUniqueFileName());
                    projectFile.setProject(project);

                    project.getFiles().add(projectFile);
                }
            }
        }

        // Сохраняем изменения в базе данных
        return projectRepository.save(project);
    }

    /**
     * Проверяет, является ли пользователь владельцем проекта.
     */
    public boolean isProjectOwner(Long projectId, String username) {
        Project project = findById(projectId);
        return project.getUser().getUsername().equals(username);
    }
}