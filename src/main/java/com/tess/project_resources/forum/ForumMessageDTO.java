package com.tess.project_resources.forum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForumMessageDTO {

    @NotBlank(message = "Текст сообщения не может быть пустым")
    @Size(max = 1000, message = "Сообщение не может быть длиннее 1000 символов")
    private String text;

    private Long threadId; // ID треда, к которому относится сообщение
}