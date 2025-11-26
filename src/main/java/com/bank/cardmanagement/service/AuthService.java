package com.bank.cardmanagement.service;

import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.repository.UserRepository;
import com.bank.cardmanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder; // 新增：注入密码编码器（需在SecurityConfig中配置）

    // 用户登录，返回JWT令牌（保持不变，但依赖正确的加密配置）
    public String login(String username, String password) {
        // 验证用户名和密码（Spring Security会自动用PasswordEncoder比对）
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        return jwtUtil.generateToken(username);
    }

    // 注册用户（修改：用PasswordEncoder加密密码）
    public User register(User user) {
        // 1. 检查用户名是否已存在
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        // 2. 用Spring Security的编码器加密密码（关键修改）
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 3. 保存用户
        return userRepository.save(user);
    }

    // 根据用户名查询用户（保持不变）
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }
}