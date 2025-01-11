package com.tess.project_resources.project;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "project_images")
public class ProjectImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName; // Оригинальное имя файла

    @Column(nullable = false)
    private String uniqueFileName; // Уникальное имя файла

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // Связь с проектом
}