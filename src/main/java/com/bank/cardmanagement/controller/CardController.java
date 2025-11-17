package com.bank.cardmanagement.controller;

import com.bank.cardmanagement.entity.Card;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards") // 所有卡片接口的统一前缀
public class CardController {

    // 办卡（示例：为用户创建银行卡）
    @PostMapping("/create")
    public String createCard(@RequestBody Card card) {
        // 第一次迭代：仅返回占位信息
        return "银行卡创建成功，卡号：" + card.getCardNumber();
    }

    // 查询余额（示例：根据卡号查询）
    @GetMapping("/balance/{cardNumber}")
    public String getBalance(@PathVariable String cardNumber) {
        // 第一次迭代：返回模拟余额
        return "卡号 " + cardNumber + " 的余额为：1000.00 元";
    }
}
