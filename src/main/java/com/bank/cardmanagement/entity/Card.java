package com.bank.cardmanagement.entity;

import lombok.Data;

@Data
public class Card {
    private Long id; // 卡片ID
    private String cardNumber; // 卡号（16-19位）
    private Long userId; // 关联的用户ID（外键）
    private String cardType; // 卡类型（储蓄卡/信用卡）
    private Double balance; // 余额
    private String status; // 状态（正常/冻结/注销）
}
