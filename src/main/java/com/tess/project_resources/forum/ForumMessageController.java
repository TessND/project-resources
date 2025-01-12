package com.tess.project_resources.forum;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/forum/messages")
public class ForumMessageController {

    @Autowired
    private ForumMessageService forumMessageService;

    @Autowired
    private UserService userService;

    /**
     * Добавляет новое сообщение в тред.
     */
    @PostMapping("/create")
    public String createMessage(
            @Valid @ModelAttribute("messageDTO") ForumMessageDTO messageDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/forum/" + messageDTO.getThreadId();
        }

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        forumMessageService.createMessage(messageDTO, user.getId());
        return "redirect:/forum/" + messageDTO.getThreadId();
    }

    /**
     * Отображает форму редактирования сообщения.
     */
    @GetMapping("/edit/{messageId}")
    public String showEditMessageForm(
            @PathVariable Long messageId,
            Model model) {
        ForumMessage message = forumMessageService.getMessageById(messageId);
        model.addAttribute("message", message);
        model.addAttribute("messageDTO", new ForumMessageDTO());
        return "forum/edit-message";
    }

    /**
     * Редактирует сообщение.
     */
    @PostMapping("/edit/{messageId}")
    public String editMessage(
            @PathVariable Long messageId,
            @Valid @ModelAttribute("messageDTO") ForumMessageDTO messageDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/forum/" + messageDTO.getThreadId();
        }

        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ForumMessage message = forumMessageService.getMessageById(messageId);
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Вы не можете редактировать это сообщение");
        }

        forumMessageService.updateMessage(messageId, messageDTO);
        return "redirect:/forum/" + message.getThread().getId();
    }

    /**
     * Удаляет сообщение.
     */
    @PostMapping("/delete/{messageId}")
    public String deleteMessage(
            @PathVariable Long messageId,
            Principal principal) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ForumMessage message = forumMessageService.getMessageById(messageId);
        if (!message.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Вы не можете удалить это сообщение");
        }

        forumMessageService.deleteMessage(messageId);
        return "redirect:/forum/" + message.getThread().getId();
    }
}