package com.tess.project_resources.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Генерация статистики и создание файла.
     */
    @PostMapping("/generate-statistics")
    public String generateStatistics(RedirectAttributes redirectAttributes) {
        String fileName = statisticsService.generateStatistics();
        redirectAttributes.addFlashAttribute("message", "Файл со статистикой успешно создан: " + fileName);
        return "redirect:/"; // Перенаправление на главную страницу
    }
}