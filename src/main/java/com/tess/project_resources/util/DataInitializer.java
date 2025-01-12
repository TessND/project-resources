package com.tess.project_resources.util;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;
import com.tess.project_resources.user.role.Role;
import com.tess.project_resources.user.role.RoleService;
import com.tess.project_resources.project.ProjectType;
import com.tess.project_resources.project.ProjectTypeRepository;

@Component
public class DataInitializer {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectTypeRepository projectTypeRepository; // Репозиторий для типов проектов

    /**
     * Метод запускается после старта приложения.
     * Проверяет, есть ли в базе данных необходимые роли, пользователи и типы
     * проектов,
     * и создает их, если их нет.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        // Создание ролей
        createRoleIfNotFound("ROLE_USER");
        createRoleIfNotFound("ROLE_ADMIN");

        // Создание пользователей
        createUserIfNotFound("admin12345", "admin@gmail.com", "admin12345", roleService.getAllRoles());
        createUserIfNotFound("user12345", "user12345@gmail.com", "user12345",
                roleService.getRoleByName("ROLE_USER").stream().toList());

        // Создание типов проектов
        createProjectTypeIfNotFound("Веб-сайт", "Проекты, связанные с разработкой веб-сайтов.");
        createProjectTypeIfNotFound("Мобильное приложение", "Проекты, связанные с разработкой мобильных приложений.");
        createProjectTypeIfNotFound("Десктопное приложение", "Проекты, связанные с разработкой десктопных приложений.");
    }

    /**
     * Создает роль, если она не найдена в базе данных.
     *
     * @param roleName Название роли.
     */
    private void createRoleIfNotFound(String roleName) {
        if (!roleService.getRoleByName(roleName).isPresent()) {
            Role role = new Role();
            role.setName(roleName);
            roleService.createRole(role);
            System.out.println("Роль " + roleName + " создана.");
        } else {
            System.out.println("Роль " + roleName + " уже существует.");
        }
    }

    /**
     * Создает пользователя, если он не найден в базе данных.
     *
     * @param username Имя пользователя.
     * @param email    Электронная почта пользователя.
     * @param password Пароль пользователя.
     * @param roles    Список ролей пользователя.
     */
    private void createUserIfNotFound(String username, String email, String password, List<Role> roles) {
        if (!userService.findByUsername(username).isPresent()) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setRoles(roles);
            userService.registerUser(user);
            System.out.println("Пользователь " + username + " создан.");
        } else {
            System.out.println("Пользователь " + username + " уже существует.");
        }
    }

    /**
     * Создает тип проекта, если он не найден в базе данных.
     *
     * @param name        Название типа проекта.
     * @param description Описание типа проекта.
     */
    private void createProjectTypeIfNotFound(String name, String description) {
        if (!projectTypeRepository.findByName(name).isPresent()) {
            ProjectType projectType = new ProjectType();
            projectType.setName(name);
            projectType.setDescription(description);
            projectTypeRepository.save(projectType);
            System.out.println("Тип проекта " + name + " создан.");
        } else {
            System.out.println("Тип проекта " + name + " уже существует.");
        }
    }
}