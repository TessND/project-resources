package com.tess.project_resources.forum;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumThreadService {

    @Autowired
    private ForumThreadRepository forumThreadRepository;

    @Autowired
    private ForumMessageRepository forumMessageRepository;

    @Autowired
    private UserService userService;

    /**
     * Создает новый тред.
     */
    @Transactional
    public ForumThread createThread(ForumThreadDTO threadDTO, Long userId) {
        User user = userService.findById(userId);

        ForumThread thread = new ForumThread();
        thread.setTitle(threadDTO.getTitle());
        thread.setDescription(threadDTO.getDescription());
        thread.setUser(user);

        return forumThreadRepository.save(thread);
    }

    /**
     * Получает все треды.
     */
    public List<ForumThread> getAllThreads() {
        return forumThreadRepository.findAll();
    }

    /**
     * Получает тред по его ID.
     */
    public ForumThread getThreadById(Long threadId) {
        return forumThreadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Тред не найден"));
    }

    /**
     * Редактирует тред.
     */
    @Transactional
    public ForumThread updateThread(Long threadId, ForumThreadDTO threadDTO) {
        ForumThread thread = forumThreadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Тред не найден"));

        thread.setTitle(threadDTO.getTitle());
        thread.setDescription(threadDTO.getDescription());

        return forumThreadRepository.save(thread);
    }

    /**
     * Удаляет тред по его ID.
     */
    @Transactional
    public void deleteThread(Long threadId) {
        ForumThread thread = forumThreadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Тред не найден"));

        // Удаляем все сообщения в треде
        forumMessageRepository.deleteByThreadId(threadId);

        // Удаляем сам тред
        forumThreadRepository.delete(thread);
    }
}