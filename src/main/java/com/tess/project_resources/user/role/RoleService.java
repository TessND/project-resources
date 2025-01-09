package com.tess.project_resources.user.role;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Создает новую роль.
     *
     * @param role Роль для создания.
     * @return Созданная роль.
     * @throws RoleAlreadyExistsException Если роль с таким именем уже существует.
     */
    public Role createRole(@Valid Role role) {
        // Проверяем, существует ли роль с таким именем
        if (roleRepository.existsByName(role.getName())) {
            throw new RoleAlreadyExistsException("Роль с именем " + role.getName() + " уже существует");
        }

        // Сохраняем роль в базу данных
        return roleRepository.save(role);
    }

    /**
     * Возвращает список всех ролей.
     *
     * @return Список всех ролей.
     */
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    /**
     * Находит роль по ID.
     *
     * @param id ID роли.
     * @return Найденная роль (если существует).
     */
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    /**
     * Находит роль по названию.
     *
     * @param name Название роли.
     * @return Найденная роль (если существует).
     */
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    /**
     * Удаляет роль по ID.
     *
     * @param id ID роли.
     * @throws RoleNotFoundException Если роль с таким ID не найдена.
     */
    public void deleteRole(Long id) {
        // Проверяем, существует ли роль с таким ID
        if (!roleRepository.existsById(id)) {
            throw new RoleNotFoundException("Роль с ID " + id + " не найдена");
        }

        // Удаляем роль
        roleRepository.deleteById(id);
    }
}