package com.tess.project_resources.forum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.tess.project_resources.user.User;
import com.tess.project_resources.user.UserService;

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
            @ModelAttribute ForumMessageDTO messageDTO,
            Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        forumMessageService.createMessage(messageDTO, user.getId());
        return "redirect:/forum/" + messageDTO.getThreadId();
    }
}