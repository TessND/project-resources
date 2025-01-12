package com.tess.project_resources.forum;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ForumMessageService {

    @Autowired
    private ForumMessageRepository forumMessageRepository;

    @Autowired
    private ForumThreadService forumThreadService;

    @Autowired
    private UserService userService;

    /**
     * Создает новое сообщение в треде.
     */
    @Transactional
    public ForumMessage createMessage(ForumMessageDTO messageDTO, Long userId) {
        User user = userService.findById(userId);
        ForumThread thread = forumThreadService.getThreadById(messageDTO.getThreadId());

        ForumMessage message = new ForumMessage();
        message.setText(messageDTO.getText());
        message.setUser(user);
        message.setThread(thread);

        return forumMessageRepository.save(message);
    }

    /**
     * Получает все сообщения для конкретного треда.
     */
    public List<ForumMessage> getMessagesByThreadId(Long threadId) {
        return forumMessageRepository.findByThreadId(threadId);
    }

    /**
     * Получает сообщение по его ID.
     */
    public ForumMessage getMessageById(Long messageId) {
        return forumMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Сообщение не найдено"));
    }

    /**
     * Редактирует сообщение.
     */
    @Transactional
    public ForumMessage updateMessage(Long messageId, ForumMessageDTO messageDTO) {
        ForumMessage message = forumMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Сообщение не найдено"));

        message.setText(messageDTO.getText());

        return forumMessageRepository.save(message);
    }

    /**
     * Удаляет сообщение по его ID.
     */
    @Transactional
    public void deleteMessage(Long messageId) {
        forumMessageRepository.deleteById(messageId);
    }
}