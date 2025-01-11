package com.tess.project_resources.project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProjectDTO {

    @NotBlank(message = "Название проекта обязательно")
    @Size(max = 100, message = "Название проекта должно быть не более 100 символов")
    private String name;

    @Size(max = 1000, message = "Описание проекта должно быть не более 1000 символов")
    private String description;

    private List<MultipartFile> images; // Список загружаемых изображений
    private List<MultipartFile> files; // Список загружаемых файлов
}