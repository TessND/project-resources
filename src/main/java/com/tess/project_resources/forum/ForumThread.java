package com.tess.project_resources.forum;

import com.tess.project_resources.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "forum_threads")
public class ForumThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title; // Заголовок треда

    @Column(columnDefinition = "TEXT")
    private String description; // Описание треда

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Автор треда

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ForumMessage> messages; // Сообщения в треде

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Дата создания треда

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // Дата последнего обновления треда
}