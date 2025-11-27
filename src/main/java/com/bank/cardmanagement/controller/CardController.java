package com.bank.cardmanagement.controller;

import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")  // 银行卡接口的统一路径前缀
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;  // 注入银行卡服务

    // 为用户创建银行卡（需要用户ID）
    @PostMapping("/user/{userId}")
    public ResponseEntity<Card> createCard(@PathVariable Long userId) {
        return ResponseEntity.ok(cardService.createCard(userId));
    }

    // 查询所有银行卡
    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.getAllCards());
    }

    // 根据卡号查询银行卡
    // CardController.java 中修改 getCardByNumber 方法
    @GetMapping("/number/{cardNumber}")
    public ResponseEntity<Card> getCardByNumber(@PathVariable String cardNumber) {
        // 直接调用service方法（找不到会抛异常，Spring会自动处理为500错误）
        Card card = cardService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(card);
    }

    // 查询指定用户名下的所有银行卡
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Card>> getCardsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(cardService.getCardsByUserId(userId));
    }
}