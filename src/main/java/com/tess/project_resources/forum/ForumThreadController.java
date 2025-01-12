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
import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumThreadController {

    @Autowired
    private ForumThreadService forumThreadService;

    @Autowired
    private UserService userService;

    @Autowired
    private ForumMessageService forumMessageService;

    /**
     * Отображает список всех тредов.
     */
    @GetMapping
    public String getAllThreads(Model model) {
        model.addAttribute("threads", forumThreadService.getAllThreads());
        return "forum/thread-list";
    }

    /**
     * Отображает форму для создания нового треда.
     */
    @GetMapping("/create")
    public String showCreateThreadForm(Model model) {
        model.addAttribute("threadDTO", new ForumThreadDTO());
        return "forum/create-thread";
    }

    /**
     * Обрабатывает создание нового треда.
     */
    @PostMapping("/create")
    public String createThread(
            @Valid @ModelAttribute("threadDTO") ForumThreadDTO threadDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "forum/create-thread";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        forumThreadService.createThread(threadDTO, user.getId());
        return "redirect:/forum";
    }

    /**
     * Отображает детали треда, включая сообщения.
     */
    @GetMapping("/{threadId}")
    public String getThreadDetails(
            @PathVariable Long threadId,
            Model model) {
        if (threadId == null) {
            throw new IllegalArgumentException("ID треда не может быть null");
        }

        ForumThread thread = forumThreadService.getThreadById(threadId);
        List<ForumMessage> messages = forumMessageService.getMessagesByThreadId(threadId);

        model.addAttribute("thread", thread);
        model.addAttribute("messages", messages);
        model.addAttribute("messageDTO", new ForumMessageDTO());

        return "forum/thread-details";
    }

    /**
     * Отображает форму редактирования треда.
     */
    @GetMapping("/edit/{threadId}")
    public String showEditThreadForm(
            @PathVariable Long threadId,
            Principal principal,
            Model model) {
        ForumThread thread = forumThreadService.getThreadById(threadId);

        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!thread.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Вы не можете редактировать этот тред");
        }

        model.addAttribute("thread", thread);
        model.addAttribute("threadDTO", new ForumThreadDTO());
        return "forum/edit-thread";
    }

    /**
     * Обрабатывает редактирование треда.
     */
    @PostMapping("/edit/{threadId}")
    public String editThread(
            @PathVariable Long threadId,
            @Valid @ModelAttribute("threadDTO") ForumThreadDTO threadDTO,
            BindingResult bindingResult,
            Principal principal,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "forum/edit-thread";
        }

        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ForumThread thread = forumThreadService.getThreadById(threadId);
        if (!thread.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Вы не можете редактировать этот тред");
        }

        forumThreadService.updateThread(threadId, threadDTO);
        return "redirect:/forum/" + threadId;
    }

    /**
     * Удаляет тред.
     */
    @PostMapping("/delete/{threadId}")
    public String deleteThread(
            @PathVariable Long threadId,
            Principal principal) {
        String username = principal.getName();
        User currentUser = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ForumThread thread = forumThreadService.getThreadById(threadId);
        if (!thread.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Вы не можете удалить этот тред");
        }

        forumThreadService.deleteThread(threadId);
        return "redirect:/forum";
    }
}