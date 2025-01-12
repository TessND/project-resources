package com.tess.project_resources.comment;

import com.tess.project_resources.project.Project;
import com.tess.project_resources.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text; // Текст комментария

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Дата и время создания комментария

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt; // Дата и время последнего обновления комментария

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Пользователь, оставивший комментарий

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project; // Проект, к которому относится комментарий
}