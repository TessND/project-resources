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

@Component
public class DataInitializer {

    @Autowired
    private RoleService roleService;


    @Autowired
    private UserService userService;
    
        /**
         * Метод запускается после старта приложения.
         * Проверяет, есть ли в базе данных необходимые роли, и создает их, если их нет.
         */
        @EventListener(ApplicationReadyEvent.class)
        public void initData() {
            createRoleIfNotFound("ROLE_USER");
            createRoleIfNotFound("ROLE_ADMIN");
            createUserIfNotFound("admin", "admin@gmail.com", "admin12345", roleService.getAllRoles());
            createUserIfNotFound("user12345", "user12345@gmail.com", "user12345", roleService.getRoleByName("ROLE_USER").stream().toList());
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
    
        private void createUserIfNotFound(String username, String email, String password, List<Role> roles) {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setRoles(roles);
            userService.registerUser(user);
    }

}