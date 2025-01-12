package com.tess.project_resources.project;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tess.project_resources.comment.Comment;
import com.tess.project_resources.comment.CommentDTO;
import com.tess.project_resources.comment.CommentService;
import com.tess.project_resources.file.FileService;
import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProjectTypeRepository projectTypeRepository;

    @Autowired
    private CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    /**
     * Отображает форму для создания проекта.
     * Используем Principal для получения текущего пользователя.
     */
    @GetMapping("/create")
    public String showCreateProjectForm(Principal principal, Model model) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем список типов проектов
        List<ProjectType> projectTypes = projectTypeRepository.findAll();

        model.addAttribute("projectDTO", new ProjectDTO());
        model.addAttribute("userId", user.getId());
        model.addAttribute("projectTypes", projectTypes); // Передаем список типов проектов
        return "create-project";
    }

    /**
     * Обрабатывает создание проекта.
     */
    @PostMapping("/create")
    public String createProject(
            @Valid @ModelAttribute ProjectDTO projectDTO,
            BindingResult bindingResult,
            @RequestParam Long userId,
            Model model) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "create-project";
        }

        // Убедитесь, что списки изображений и файлов не null
        if (projectDTO.getImages() == null) {
            projectDTO.setImages(new ArrayList<>());
        }
        if (projectDTO.getFiles() == null) {
            projectDTO.setFiles(new ArrayList<>());
        }

        projectService.createProject(projectDTO, userId);
        return "redirect:/projects?userId=" + userId;
    }

    /**
     * Отображает список проектов с возможностью поиска по названию.
     */
    @GetMapping
    public String getProjects(
            @RequestParam(required = false) String search, // Параметр поиска
            @RequestParam(defaultValue = "0") int page, // Номер страницы (для пагинации)
            @RequestParam(defaultValue = "10") int size, // Количество элементов на странице
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Project> projects;

        if (search != null && !search.isEmpty()) {
            // Поиск проектов по названию
            projects = projectService.findByNameContaining(search, pageable);
        } else {
            // Получение всех проектов
            projects = projectService.findAll(pageable);
        }

        model.addAttribute("projects", projects);
        model.addAttribute("search", search); // Передаем поисковый запрос в модель
        return "project-list"; // Имя HTML-шаблона
    }

    /**
     * Отображает детали проекта.
     */
    @GetMapping("/{projectId}")
    public String getProjectDetails(
            @PathVariable Long projectId,
            Principal principal, // Текущий пользователь
            Model model) {
        // Находим проект по ID
        Project project = projectService.findById(projectId);

        // Проверяем, является ли текущий пользователь владельцем проекта
        boolean isOwner = false;
        if (principal != null) {
            String username = principal.getName();
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            isOwner = project.getUser().getId().equals(currentUser.getId());
        }
        // Получаем комментарии для проекта
        List<Comment> comments = commentService.getCommentsByProjectId(projectId);

        // Передаем данные в модель
        model.addAttribute("project", project);
        model.addAttribute("comments", comments);
        model.addAttribute("commentDTO", new CommentDTO());
        return "project-details";
    }

    @GetMapping("/{projectId}/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long projectId,
            @PathVariable Long fileId,
            Model model) throws IOException {

        logger.info("Запрос на скачивание файла. Project ID: {}, File ID: {}", projectId, fileId);

        // Находим проект
        Project project = projectService.findById(projectId);

        // Находим файл в проекте
        ProjectFile projectFile = project.getFiles().stream()
                .filter(file -> {
                    logger.debug("Проверка файла: File ID: {}, Original Name: {}", file.getId(),
                            file.getOriginalFileName());
                    return file.getId().equals(fileId);
                })
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Файл не найден. Project ID: {}, File ID: {}", projectId, fileId);
                    return new RuntimeException("Файл не найден");
                });

        // Загружаем файл с сервера
        Resource fileResource = fileService.load(projectFile.getUniqueFileName());

        // Устанавливаем заголовок Content-Disposition
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + projectFile.getOriginalFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(fileResource);
    }

    @GetMapping("/edit/{projectId}")
    public String showEditProjectForm(
            @PathVariable Long projectId,
            Principal principal,
            Model model) {
        Project project = projectService.findById(projectId);
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!project.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете редактировать этот проект");
        }

        // Получаем список типов проектов
        List<ProjectType> projectTypes = projectTypeRepository.findAll();

        model.addAttribute("project", project);
        model.addAttribute("projectTypes", projectTypes); // Передаем список типов проектов
        return "edit-project";
    }

    @PostMapping("/edit/{projectId}")
    public String editProject(
            @PathVariable Long projectId,
            @ModelAttribute ProjectDTO projectDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) throws IOException {
        // Проверяем ошибки валидации
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "edit-project";
        }

        // Проверяем, является ли текущий пользователь владельцем проекта
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Project project = projectService.findById(projectId);
        if (!project.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете редактировать этот проект");
        }

        // Редактируем проект
        projectService.editProject(projectId, projectDTO);

        return "redirect:/projects/" + projectId;
    }

    @PostMapping("/delete/{projectId}")
    public String deleteProject(
            @PathVariable Long projectId,
            Principal principal) throws IOException {
        logger.info("Запрос на удаление проекта. Project ID: {}", projectId);

        // Проверяем, является ли текущий пользователь владельцем проекта
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        Project project = projectService.findById(projectId);
        if (!project.getUser().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете удалить этот проект");
        }

        // Удаляем проект
        projectService.deleteProject(projectId);

        return "redirect:/projects";
    }

    /**
     * Adds a comment to a project.
     */
    @PostMapping("/{projectId}/comments")
    public String addComment(
            @PathVariable Long projectId,
            @ModelAttribute CommentDTO commentDTO,
            Principal principal,
            Model model) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        commentDTO.setProjectId(projectId);
        commentService.createComment(commentDTO, user.getId());

        return "redirect:/projects/" + projectId; // Redirect back to the project details page
    }

    @PostMapping("/comments/{commentId}/delete")
    public String deleteComment(
            @PathVariable Long commentId,
            Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Используем метод getCommentById для поиска комментария
        Comment comment = commentService.getCommentById(commentId);

        // Проверяем, является ли текущий пользователь автором комментария
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Вы не можете удалить этот комментарий");
        }

        // Удаляем комментарий
        commentService.deleteComment(commentId);
        return "redirect:/projects/" + comment.getProject().getId(); // Перенаправляем обратно на страницу проекта
    }

}