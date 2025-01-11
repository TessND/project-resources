package com.tess.project_resources.project;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;

import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    /**
     * Отображает форму для создания проекта.
     * Используем Principal для получения текущего пользователя.
     */
    @GetMapping("/create")
    public String showCreateProjectForm(Principal principal, Model model) {
        // Получаем имя пользователя (например, email или username)
        String username = principal.getName();

        // Находим пользователя по имени
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Передаем userId в модель
        model.addAttribute("projectDTO", new ProjectDTO());
        model.addAttribute("userId", user.getId()); // Передаем userId в модель
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
            Model model
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "create-project";
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
            @RequestParam(defaultValue = "0") int page,   // Номер страницы (для пагинации)
            @RequestParam(defaultValue = "10") int size,  // Количество элементов на странице
            Model model
    ) {
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
            Model model
    ) {
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

        // Передаем данные в модель
        model.addAttribute("project", project);
        model.addAttribute("isOwner", isOwner);
        return "project-details";
    }
}