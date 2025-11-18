package com.bank.cardmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data  // Lombok注解，自动生成getter/setter
@Entity  // 标记为JPA实体（对应数据库表）
@Table(name = "t_user")  // 数据库表名
public class User {
    @Id  // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 自增主键（MySQL常用）
    private Long id;

    @Column(unique = true, nullable = false)  // 用户名唯一、非空
    private String username;

    @Column(nullable = false)  // 密码非空
    private String password;

    private String realName;  // 真实姓名
    private String phone;     // 手机号
    private String idCard;    // 身份证号
}