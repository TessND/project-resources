package com.tess.project_resources.forum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    List<ForumThread> findByUserId(Long userId); // Поиск тредов по ID пользователя
}