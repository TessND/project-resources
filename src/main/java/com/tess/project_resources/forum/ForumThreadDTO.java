package com.tess.project_resources.forum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ForumThreadDTO {

    @NotBlank(message = "Заголовок треда не может быть пустым")
    @Size(max = 255, message = "Заголовок треда не может быть длиннее 255 символов")
    private String title;

    @Size(max = 1000, message = "Описание треда не может быть длиннее 1000 символов")
    private String description;
}