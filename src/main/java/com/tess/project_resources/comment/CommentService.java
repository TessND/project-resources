package com.tess.project_resources.comment;

import com.tess.project_resources.project.Project;
import com.tess.project_resources.project.ProjectService;
import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    /**
     * Создает новый комментарий для проекта.
     */
    public Comment createComment(CommentDTO commentDTO, Long userId) {
        User user = userService.findById(userId);
        Project project = projectService.findById(commentDTO.getProjectId());

        Comment comment = new Comment();
        comment.setText(commentDTO.getText());
        comment.setUser(user);
        comment.setProject(project);

        return commentRepository.save(comment);
    }

    /**
     * Получает все комментарии для конкретного проекта.
     */
    public List<Comment> getCommentsByProjectId(Long projectId) {
        return commentRepository.findByProjectId(projectId);
    }

    /**
     * Получает все комментарии, оставленные конкретным пользователем.
     */
    public List<Comment> getCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId);
    }

    /**
     * Получает комментарий по его ID.
     */
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий с ID " + commentId + " не найден"));
    }

    /**
     * Обновляет существующий комментарий.
     */
    public Comment updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        comment.setText(commentDTO.getText());
        return commentRepository.save(comment);
    }

    /**
     * Удаляет комментарий по его ID.
     */
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }
}