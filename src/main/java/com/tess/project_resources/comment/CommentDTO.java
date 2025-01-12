package com.tess.project_resources.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentDTO {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 1000, message = "Комментарий не может быть длиннее 1000 символов")
    private String text; // Текст комментария

    private Long projectId; // ID проекта, к которому относится комментарий
}