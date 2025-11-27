package com.bank.cardmanagement.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;  // 导入时间类
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "t_card")
public class Card {
    // 卡片状态：设为public
    public enum CardStatus {
        NORMAL, LOST, FROZEN
    }

    @Column(nullable = false)
    private CardStatus status = CardStatus.NORMAL; // 默认正常

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)  // 卡号唯一、非空
    private String cardNumber;

    @Column(nullable = false)  // 余额非空，默认0
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();  // 默认为当前时间

    private LocalDateTime lastUpdateTime = LocalDateTime.now(); // 新增字段

    // 关联用户（多对一：一张卡属于一个用户，一个用户可有多张卡）
    @ManyToOne
    @JoinColumn(name = "user_id")  // 数据库中关联字段名（t_card表中的user_id）
    private User user;


}