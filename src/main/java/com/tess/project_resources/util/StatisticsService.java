package com.tess.project_resources.util;

import com.tess.project_resources.user.UserRepository;
import com.tess.project_resources.project.ProjectRepository;
import com.tess.project_resources.forum.ForumThreadRepository;
import com.tess.project_resources.forum.ForumMessageRepository;
import com.tess.project_resources.comment.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ForumThreadRepository forumThreadRepository;

    @Autowired
    private ForumMessageRepository forumMessageRepository;

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Собирает статистику по всему приложению и сохраняет её в текстовый файл.
     *
     * @return Имя созданного файла.
     */
    public String generateStatistics() {
        // Сбор данных
        long totalUsers = userRepository.count();
        long totalProjects = projectRepository.count();
        long totalThreads = forumThreadRepository.count();
        long totalMessages = forumMessageRepository.count();
        long totalComments = commentRepository.count();

        // Формирование содержимого файла
        StringBuilder content = new StringBuilder();
        content.append("Статистика приложения\n");
        content.append("Дата генерации: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n\n");
        content.append("=== Пользователи ===\n");
        content.append("Общее количество пользователей: ").append(totalUsers).append("\n\n");
        content.append("=== Проекты ===\n");
        content.append("Общее количество проектов: ").append(totalProjects).append("\n\n");
        content.append("=== Форум ===\n");
        content.append("Общее количество тредов: ").append(totalThreads).append("\n");
        content.append("Общее количество сообщений: ").append(totalMessages).append("\n\n");
        content.append("=== Комментарии ===\n");
        content.append("Общее количество комментариев: ").append(totalComments).append("\n");

        // Сохранение в файл
        String fileName = "statistics_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(content.toString());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при создании файла статистики", e);
        }

        return fileName;
    }
}