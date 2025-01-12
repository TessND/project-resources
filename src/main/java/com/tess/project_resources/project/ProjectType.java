package com.tess.project_resources.project;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "project_types")
public class ProjectType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Название типа проекта (например, "Веб-сайт", "Мобильное приложение")

    // Дополнительные поля, если необходимо
    @Column(columnDefinition = "TEXT")
    private String description; // Описание типа проекта
}