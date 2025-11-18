package com.bank.cardmanagement.service;

import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor  // Lombok注解，自动注入依赖
public class UserService {
    private final UserRepository userRepository;  // 注入Repository

    // 新增用户（注册）
    public User createUser(User user) {
        // 校验用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        return userRepository.save(user);  // 保存到数据库
    }

    // 根据ID查询用户
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);  // 从数据库查询
    }

    // 查询所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();  // 从数据库查询
    }

    // 更新用户信息
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRealName(updatedUser.getRealName());
                    user.setPhone(updatedUser.getPhone());
                    user.setIdCard(updatedUser.getIdCard());
                    return userRepository.save(user);  // 更新数据库
                })
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    // 删除用户
    public void deleteUser(Long id) {
        userRepository.deleteById(id);  // 从数据库删除
    }
}
