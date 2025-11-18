package com.bank.cardmanagement.service;

import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.repository.CardRepository;
import com.bank.cardmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    // 为用户创建银行卡（自动生成卡号）
    public Card createCard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 生成16位随机卡号
        String cardNumber = generateCardNumber();

        Card card = new Card();
        card.setCardNumber(cardNumber);
        card.setUser(user);  // 关联用户
        return cardRepository.save(card);  // 保存到数据库
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