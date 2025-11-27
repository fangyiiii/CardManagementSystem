package com.bank.cardmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; // 存储加密后的密码

    private String realName;
    private String phone;
    private String idCard;

    // 新增：用户创建时间（默认当前时间）
    @Column(nullable = false, updatable = false) // 不可更新
    private LocalDateTime createTime = LocalDateTime.now();

    // 新增：用户信息更新时间（默认当前时间，修改密码时更新）
    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();

    // 优化：复用PasswordEncoder实例（避免每次new，提升性能）
    private static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // 验证密码（登录时调用）
    public boolean matchesPassword(String rawPassword) {
        return PASSWORD_ENCODER.matches(rawPassword, this.password);
    }
}