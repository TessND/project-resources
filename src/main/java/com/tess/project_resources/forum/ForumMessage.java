package com.tess.project_resources.forum;

import com.tess.project_resources.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "forum_messages")
public class ForumMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String text; // Текст сообщения

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Автор сообщения

    @ManyToOne
    @JoinColumn(name = "thread_id", nullable = false)
    private ForumThread thread; // Тред, к которому относится сообщение

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // Дата создания сообщения
}