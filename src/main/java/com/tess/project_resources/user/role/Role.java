package com.tess.project_resources.user.role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
@Table(name = "roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name") // Уникальное название роли
})
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Название роли обязательно")
    @Size(min = 6, max = 50, message = "Название роли должно быть от 6 до 50 символов")
    @Pattern(regexp = "^ROLE_[A-Z]+$", message = "Название роли должно начинаться с 'ROLE_' и содержать только заглавные буквы")
    private String name;

}