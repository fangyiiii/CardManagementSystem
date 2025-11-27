package com.bank.cardmanagement.service;

import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.repository.CardRepository;
import com.bank.cardmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {
    private static final Logger logger = LoggerFactory.getLogger(CardService.class); // 日志
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
        card.setLastUpdateTime(LocalDateTime.now()); // 新增：设置最后更新时间（创建时与开卡时间一致）
        card.setStatus(Card.CardStatus.NORMAL); // 初始化卡片状态为“正常”
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

    // 根据卡号查询卡片（非Optional版，便于上层调用）
    public Card getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("银行卡不存在"));
    }

    // 根据卡号查询卡片（保留Optional版，兼容原有逻辑）
    public Optional<Card> findCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber);
    }

    // 查询用户名下的所有卡片
    public List<Card> getCardsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return cardRepository.findByUser(user);
    }

    // 存款功能（新增：卡片归属+状态校验）
    @Transactional
    public void deposit(String cardNumber, BigDecimal amount, Long userId) {
        // 1. 金额校验
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("存款金额必须大于0");
        }
        // 2. 查询卡片
        Card card = getCardByNumber(cardNumber);
        // 3. 校验卡片归属
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("该银行卡不属于当前用户");
        }
        // 4. 校验卡片状态（仅正常状态可操作）
        if (!card.getStatus().equals(Card.CardStatus.NORMAL)) {
            throw new RuntimeException("卡片状态异常（当前状态：" + card.getStatus() + "），无法操作");
        }
        // 5. 执行存款
        card.setBalance(card.getBalance().add(amount));
        card.setLastUpdateTime(LocalDateTime.now());
        cardRepository.save(card);
        logger.info("用户{}的卡号{}存款成功，金额{}元", userId, cardNumber, amount);
    }

    // 取款功能（新增：卡片归属+状态校验）
    @Transactional
    public void withdraw(String cardNumber, BigDecimal amount, Long userId) {
        // 1. 金额校验
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("取款金额必须大于0");
        }
        // 2. 查询卡片
        Card card = getCardByNumber(cardNumber);
        // 3. 校验卡片归属
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("该银行卡不属于当前用户");
        }
        // 4. 校验卡片状态（仅正常状态可操作）
        if (!card.getStatus().equals(Card.CardStatus.NORMAL)) {
            throw new RuntimeException("卡片状态异常（当前状态：" + card.getStatus() + "），无法操作");
        }
        // 5. 余额校验
        if (card.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，无法取款");
        }
        // 6. 执行取款
        card.setBalance(card.getBalance().subtract(amount));
        card.setLastUpdateTime(LocalDateTime.now());
        cardRepository.save(card);
        logger.info("用户{}的卡号{}取款成功，金额{}元", userId, cardNumber, amount);
    }

    // 转账功能（新增：卡片归属+状态校验）
    @Transactional
    public void transfer(String fromCardNumber, String toCardNumber, BigDecimal amount, Long userId) {
        // 1. 参数校验
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("转账金额必须大于0");
        }
        if (fromCardNumber.equals(toCardNumber)) {
            throw new RuntimeException("不能向自身银行卡转账");
        }

        // 2. 查询转出/转入卡片
        Card fromCard = getCardByNumber(fromCardNumber);
        Card toCard = getCardByNumber(toCardNumber);

        // 3. 校验转出卡归属
        if (!fromCard.getUser().getId().equals(userId)) {
            throw new RuntimeException("转出银行卡不属于当前用户");
        }
        // 4. 校验卡片状态（仅正常状态可操作）
        if (!fromCard.getStatus().equals(Card.CardStatus.NORMAL)) {
            throw new RuntimeException("转出卡片状态异常（当前状态：" + fromCard.getStatus() + "），无法操作");
        }
        if (!toCard.getStatus().equals(Card.CardStatus.NORMAL)) {
            throw new RuntimeException("转入卡片状态异常（当前状态：" + toCard.getStatus() + "），无法操作");
        }

        // 5. 余额校验
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("余额不足，无法转账");
        }

        // 6. 执行转账
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        fromCard.setLastUpdateTime(LocalDateTime.now());
        toCard.setBalance(toCard.getBalance().add(amount));
        toCard.setLastUpdateTime(LocalDateTime.now());

        cardRepository.save(fromCard);
        cardRepository.save(toCard);
        logger.info("用户{}转账成功：转出卡号{} -> 转入卡号{}，金额{}元", userId, fromCardNumber, toCardNumber, amount);
    }

    // 1. 挂失账户（已完善：卡片归属校验）
    @Transactional
    public void reportLost(String cardNumber, Long userId) {
        Card card = getCardByNumber(cardNumber);
        // 校验卡片归属
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("该银行卡不属于当前用户");
        }
        // 更新卡片状态为“挂失”
        card.setStatus(Card.CardStatus.LOST);
        card.setLastUpdateTime(LocalDateTime.now());
        cardRepository.save(card);
        logger.info("用户{}的卡号{}挂失成功", userId, cardNumber);
    }

    // 2. 办理账户销户（已完善：卡片归属+余额校验）
    @Transactional
    public void closeAccount(String cardNumber, Long userId) {
        Card card = getCardByNumber(cardNumber);
        // 校验卡片归属
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("该银行卡不属于当前用户");
        }
        // 校验余额（需为0才能销户）
        if (card.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("账户余额不为0，无法销户");
        }
        cardRepository.delete(card);
        logger.info("用户{}的卡号{}销户成功", userId, cardNumber);
    }

    // 3. 冻结账户（已完善：卡片归属校验）
    @Transactional
    public void freezeAccount(String cardNumber, Long userId) {
        Card card = getCardByNumber(cardNumber);
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("该银行卡不属于当前用户");
        }
        card.setStatus(Card.CardStatus.FROZEN);
        card.setLastUpdateTime(LocalDateTime.now());
        cardRepository.save(card);
        logger.info("用户{}的卡号{}冻结成功", userId, cardNumber);
    }

    // 4. 解冻账户（已完善：卡片归属校验）
    @Transactional
    public void unfreezeAccount(String cardNumber, Long userId) {
        Card card = getCardByNumber(cardNumber);
        if (!card.getUser().getId().equals(userId)) {
            throw new RuntimeException("该银行卡不属于当前用户");
        }
        card.setStatus(Card.CardStatus.NORMAL);
        card.setLastUpdateTime(LocalDateTime.now());
        cardRepository.save(card);
        logger.info("用户{}的卡号{}解冻成功", userId, cardNumber);
    }

    // 私有方法：生成16位卡号（避免重复）
    private String generateCardNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(random.nextInt(10));
        }
        // 校验卡号唯一性（避免重复）
        while (cardRepository.existsByCardNumber(sb.toString())) {
            sb.setLength(0);
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }
}