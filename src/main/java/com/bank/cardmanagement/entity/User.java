package com.bank.cardmanagement.entity;

import lombok.Data;

// Lombok的@Data自动生成getter/setter/toString等方法
@Data
public class User {
    private Long id; // 用户ID
    private String username; // 用户名（登录账号）
    private String password; // 密码
    private String realName; // 真实姓名
    private String phone; // 手机号
    private String idCard; // 身份证号（用于实名）
}