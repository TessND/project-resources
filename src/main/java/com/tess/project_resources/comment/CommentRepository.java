package com.tess.project_resources.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Example of a custom query method: find comments by project ID
    List<Comment> findByProjectId(Long projectId);

    // Example of another custom query method: find comments by user ID
    List<Comment> findByUserId(Long userId);
}