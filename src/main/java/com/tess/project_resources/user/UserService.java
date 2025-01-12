package com.tess.project_resources.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tess.project_resources.user.role.Role;
import com.tess.project_resources.user.role.RoleService;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Регистрирует нового пользователя.
     *
     * @param user Пользователь для регистрации.
     * @return Зарегистрированный пользователь.
     * @throws UserAlreadyExistsException Если пользователь с таким именем или email
     *                                    уже существует.
     */
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UserAlreadyExistsException("Пользователь с именем " + user.getUsername() + " уже существует");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с email " + user.getEmail() + " уже существует");
        }

        // Хешируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Назначаем роль ROLE_USER по умолчанию
        user.setRoles(Collections.singletonList(roleService.getRoleByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Роль ROLE_USER не найдена"))));

        return userRepository.save(user);
    }

    /**
     * Добавляет роль пользователю.
     *
     * @param userId   ID пользователя.
     * @param roleName Название роли.
     */
    public void addRoleToUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Role role = roleService.getRoleByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        if (!user.getRoles().contains(role)) {
            user.addRole(role);
            userRepository.save(user);
        }
    }

    /**
     * Удаляет роль у пользователя.
     *
     * @param userId   ID пользователя.
     * @param roleName Название роли.
     */
    public void removeRoleFromUser(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Role role = roleService.getRoleByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль не найдена"));

        if (user.getRoles().contains(role)) {
            user.removeRole(role);
            userRepository.save(user);
        }
    }

    /**
     * Находит пользователя по имени.
     *
     * @param username Имя пользователя.
     * @return Найденный пользователь.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Находит пользователя по ID.
     *
     * @param id ID пользователя.
     * @return Найденный пользователь.
     * @throws RuntimeException Если пользователь не найден.
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + id + " не найден"));
    }


    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    /**
     * Проверяет, существует ли пользователь с таким email.
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getName()))
                                .collect(Collectors.toList())))
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
