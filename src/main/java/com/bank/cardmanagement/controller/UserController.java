package com.bank.cardmanagement.controller;

import com.bank.cardmanagement.entity.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // 所有用户接口的统一前缀
public class UserController {

    // 用户注册（示例：接收用户信息，返回成功提示）
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        // 第一次迭代：仅返回占位信息，后续迭代实现数据库逻辑
        return "用户注册成功：" + user.getUsername();
    }

    // 用户登录（示例：接收账号密码，返回登录状态）
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // 第一次迭代：仅返回占位信息
        return "登录成功，用户名：" + username;
    }

    // 查询用户信息（示例：根据ID查询）
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // 第一次迭代：返回模拟数据
        User user = new User();
        user.setId(id);
        user.setUsername("testUser");
        user.setRealName("测试用户");
        return user;
    }
}
