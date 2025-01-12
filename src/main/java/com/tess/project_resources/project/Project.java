package com.tess.project_resources.project;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.tess.project_resources.comment.Comment;
import com.tess.project_resources.user.User;

@Data
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectImage> images = new ArrayList<>(); // Список изображений

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectFile> files = new ArrayList<>(); // Список файлов

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Связь с типом проекта
    @ManyToOne
    @JoinColumn(name = "project_type_id", nullable = false)
    private ProjectType projectType; // Тип проекта

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>(); // Список комментариев
}