package com.bank.cardmanagement.repository;

import com.bank.cardmanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// 继承JpaRepository，自动获得CRUD方法（无需手动实现）
public interface UserRepository extends JpaRepository<User, Long> {
    // 自定义查询：根据用户名查询用户（用于登录/注册校验）
    Optional<User> findByUsername(String username);

    // 新增：判断用户名是否存在的方法
    boolean existsByUsername(String username);
}