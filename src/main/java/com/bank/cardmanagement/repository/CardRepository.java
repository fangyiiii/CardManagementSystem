package com.bank.cardmanagement.repository;

import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    // 自定义查询：根据卡号查询卡片
    Optional<Card> findByCardNumber(String cardNumber);

    // 自定义查询：查询用户名下的所有卡片
    List<Card> findByUser(User user);
}
