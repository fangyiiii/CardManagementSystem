package com.bank.cardmanagement.controller;

import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    // 跳转到登录页
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 对应templates/login.html
    }

    // 跳转到注册页
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User()); // 用于表单绑定
        return "register"; // 对应templates/register.html
    }

    // 处理注册请求
    @PostMapping("/api/auth/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            authService.register(user);
            return "redirect:/login?success=注册成功，请登录";
        } catch (Exception e) {
            model.addAttribute("error", "注册失败：用户名已存在");
            return "register";
        }
    }

    // 处理登录请求（返回令牌并跳转首页）
    @PostMapping("/api/auth/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpServletResponse response,
            Model model
    ) {
        try {
            String token = authService.login(username, password);
            // 将令牌存入Cookie（前端页面可读取）
            response.addHeader("Authorization", "Bearer " + token);
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "登录失败：用户名或密码错误");
            return "login";
        }
    }
}