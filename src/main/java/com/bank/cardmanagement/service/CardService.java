package com.bank.cardmanagement.service;

import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.repository.CardRepository;
import com.bank.cardmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    // 在CardService中添加两个方法（重载）
// 1. 支持Swing调用（传userId和初始余额）
    public Card createCard(Long userId, double initialBalance) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        String cardNumber = generateCardNumber();
        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setUser(user);
        card.setBalance(BigDecimal.valueOf(initialBalance)); // 使用传入的初始余额
        card.setCreateTime(LocalDateTime.now()); // 设置开卡时间
        return cardRepository.save(card);
    }

    // 2. 支持CardController调用（只传userId，默认初始余额为0）
    public Card createCard(Long userId) {
        return createCard(userId, 0.0); // 调用上面的方法，默认初始余额为0
    }

    // 查询所有卡片
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    // 根据卡号查询卡片
    public Optional<Card> getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    // 查询用户名下的所有卡片
    public List<Card> getCardsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return cardRepository.findByUser(user);
    }

    // 私有方法：生成16位卡号
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}