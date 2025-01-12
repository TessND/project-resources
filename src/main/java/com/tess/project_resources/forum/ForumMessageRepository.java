package com.tess.project_resources.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumMessageRepository extends JpaRepository<ForumMessage, Long> {
    List<ForumMessage> findByThreadId(Long threadId); // Поиск сообщений по ID треда
    void deleteByThreadId(Long threadId); // Удаление сообщений по ID треда
}