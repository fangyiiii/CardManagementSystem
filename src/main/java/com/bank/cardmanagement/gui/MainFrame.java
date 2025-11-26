package com.bank.cardmanagement.gui;

import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.service.AuthService;
import com.bank.cardmanagement.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MainFrame extends JFrame {
    private final AuthService authService;
    private final CardService cardService;
    private User currentUser; // 当前登录用户

    // 初始化窗口
    public void init() {
        setTitle("银行卡管理系统");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 窗口居中
        showLoginPanel(); // 默认显示登录界面
    }

    // 登录界面
    private void showLoginPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginBtn = new JButton("登录");
        JButton registerBtn = new JButton("注册");

        panel.add(new JLabel("用户名:"));
        panel.add(usernameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(loginBtn);
        panel.add(registerBtn);

        // 登录按钮事件
        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                authService.login(username, password); // 调用登录服务
                currentUser = authService.getUserByUsername(username); // 获取当前用户
                showHomePanel(); // 登录成功跳转到首页
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "登录失败: " + ex.getMessage());
            }
        });

        // 注册按钮事件
        registerBtn.addActionListener(e -> showRegisterPanel());

        setContentPane(panel);
        revalidate();
    }

    // 注册界面
    private void showRegisterPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField realNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField idCardField = new JTextField();
        JButton submitBtn = new JButton("注册");
        JButton backBtn = new JButton("返回登录");

        panel.add(new JLabel("用户名:"));
        panel.add(usernameField);
        panel.add(new JLabel("密码:"));
        panel.add(passwordField);
        panel.add(new JLabel("真实姓名:"));
        panel.add(realNameField);
        panel.add(new JLabel("手机号:"));
        panel.add(phoneField);
        panel.add(new JLabel("身份证号:"));
        panel.add(idCardField);
        panel.add(submitBtn);
        panel.add(backBtn);

        // 注册按钮事件
        submitBtn.addActionListener(e -> {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setPassword(new String(passwordField.getPassword()));
            user.setRealName(realNameField.getText());
            user.setPhone(phoneField.getText());
            user.setIdCard(idCardField.getText());
            try {
                authService.register(user);
                JOptionPane.showMessageDialog(this, "注册成功!");
                showLoginPanel(); // 注册成功返回登录页
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "注册失败: " + ex.getMessage());
            }
        });

        // 返回登录按钮事件
        backBtn.addActionListener(e -> showLoginPanel());

        setContentPane(panel);
        revalidate();
    }

    // 首页界面
    private void showHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("欢迎, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        JButton cardBtn = new JButton("我的银行卡");
        JButton logoutBtn = new JButton("退出登录");

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(logoutBtn);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 50));
        centerPanel.add(welcomeLabel);
        centerPanel.add(cardBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        // 我的银行卡按钮事件
        cardBtn.addActionListener(e -> showCardPanel());

        // 退出登录按钮事件
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            showLoginPanel();
        });

        setContentPane(panel);
        revalidate();
    }

    // 银行卡管理界面
    private void showCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton createBtn = new JButton("创建新银行卡");
        JButton backBtn = new JButton("返回首页");
        JTextArea cardArea = new JTextArea();
        cardArea.setEditable(false);
        cardArea.setFont(new Font("等线", Font.PLAIN, 14));

        // 加载已有银行卡
        loadCards(cardArea);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(backBtn);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(createBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(cardArea), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        // 创建银行卡按钮事件
        createBtn.addActionListener(e -> {
            try {
                Card newCard = cardService.createCard(currentUser.getId(), 0.0);
                JOptionPane.showMessageDialog(this, "银行卡创建成功!卡号: " + newCard.getCardNumber());
                loadCards(cardArea); // 刷新列表
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "创建失败: " + ex.getMessage());
            }
        });

        // 返回首页按钮事件
        backBtn.addActionListener(e -> showHomePanel());

        setContentPane(panel);
        revalidate();
    }

    // 加载用户的银行卡列表
    private void loadCards(JTextArea area) {
        List<Card> cards = cardService.getCardsByUserId(currentUser.getId());
        StringBuilder sb = new StringBuilder("我的银行卡列表:\n");
        for (Card card : cards) {
            sb.append("卡号: ").append(card.getCardNumber())
                    .append(" | 余额: ").append(card.getBalance())
                    .append("元 | 开卡时间: ").append(card.getCreateTime())
                    .append("\n");
        }
        area.setText(sb.toString());
    }
}
