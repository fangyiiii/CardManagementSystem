package com.bank.cardmanagement.service;

import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.repository.UserRepository;
import com.bank.cardmanagement.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    // 新增：日志记录器，用于记录关键操作
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // 用户登录，返回JWT令牌（保持不变）
    public String login(String username, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        logger.info("用户{}登录成功", username); // 新增：记录登录日志
        return jwtUtil.generateToken(username);
    }

    // 注册用户（保持原有逻辑，新增日志）
    public User register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // 若User类有createTime/updateTime字段，此处无需手动设置（实体类默认值已处理）
        User savedUser = userRepository.save(user);
        logger.info("用户{}注册成功", user.getUsername()); // 新增：记录注册日志
        return savedUser;
    }

    // 根据用户名查询用户（保持不变）
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 新增：修改密码功能
    public void changePassword(String username, String oldPassword, String newPassword) {
        // 1. 查询用户
        User user = getUserByUsername(username);

        // 2. 校验旧密码是否正确
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("用户{}修改密码失败：旧密码错误", username);
            throw new RuntimeException("旧密码错误");
        }

        // 3. 校验新密码复杂度（至少6位，包含字母+数字）
        if (newPassword.length() < 6 || !newPassword.matches("^(?=.*[a-zA-Z])(?=.*\\d).+$")) {
            logger.warn("用户{}修改密码失败：新密码不符合复杂度要求", username);
            throw new RuntimeException("新密码需至少6位，且包含字母和数字");
        }

        // 4. 避免新密码与旧密码相同
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            logger.warn("用户{}修改密码失败：新密码与旧密码相同", username);
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        // 5. 更新密码和最后修改时间
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now()); // 需确保User类有updateTime字段
        userRepository.save(user);

        logger.info("用户{}修改密码成功", username);
    }

    // AuthService.java新增：
    public void updateUserInfo(String username, String realName, String phone, String idCard) {
        // 1. 查询用户
        User user = getUserByUsername(username);
        // 2. 校验手机号/身份证号格式（示例）
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new RuntimeException("手机号格式不正确");
        }
        if (!idCard.matches("^\\d{17}[0-9Xx]$")) {
            throw new RuntimeException("身份证号格式不正确");
        }
        // 3. 更新信息
        user.setRealName(realName);
        user.setPhone(phone);
        user.setIdCard(idCard);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.save(user);
        logger.info("用户{}更新个人信息成功", username);
    }
}