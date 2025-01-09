package com.tess.project_resources.user;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String showHome(Model model) {
        return "home";
    }

    // Показать форму регистрации
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User()); // Добавляем пустой объект User в модель
        return "register";
    }

    // Обработка формы регистрации
    @PostMapping("/register")
    public String registerUser(@Valid User user, BindingResult bindingResult, Model model) {
        // Проверка на ошибки валидации
        if (bindingResult.hasErrors()) {
            return "register"; // Возвращаем форму с сообщениями об ошибках
        }

        // Проверка, существует ли пользователь с таким именем или email
        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.user", "Имя пользователя уже занято");
            return "register";
        }
        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email уже зарегистрирован");
            return "register";
        }

        // Регистрация пользователя
        userService.registerUser(user);
        return "redirect:/login"; // Перенаправляем на страницу входа после успешной регистрации
    }

    // Показать форму входа
    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль.");
        }
        return "login";
    }
}