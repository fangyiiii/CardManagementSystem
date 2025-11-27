package com.bank.cardmanagement.gui;

import com.bank.cardmanagement.entity.User;
import com.bank.cardmanagement.entity.Card;
import com.bank.cardmanagement.service.AuthService;
import com.bank.cardmanagement.service.CardService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private final AuthService authService;
    private final CardService cardService;
    private User currentUser; // 当前登录用户

    // 初始化窗口
    public void init() {
        setTitle("银行卡管理系统（V4.0）");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        showLoginPanel();
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

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            try {
                authService.login(username, password);
                currentUser = authService.getUserByUsername(username);
                showHomePanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "登录失败: " + ex.getMessage());
                logger.error("登录异常", ex);
            }
        });

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
                showLoginPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "注册失败: " + ex.getMessage());
                logger.error("注册异常", ex);
            }
        });

        backBtn.addActionListener(e -> showLoginPanel());

        setContentPane(panel);
        revalidate();
    }

    // 首页界面（修复布局）
    private void showHomePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("欢迎, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));

        // 功能按钮面板（2x2布局，适配4个按钮）
        JPanel btnPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JButton cardBtn = new JButton("我的银行卡");
        JButton transferBtn = new JButton("银行卡转账");
        JButton changePwdBtn = new JButton("修改密码");
        JButton updateInfoBtn = new JButton("修改个人信息");
        JButton logoutBtn = new JButton("退出登录");

        btnPanel.add(cardBtn);
        btnPanel.add(transferBtn);
        btnPanel.add(changePwdBtn);
        btnPanel.add(updateInfoBtn);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(logoutBtn);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(welcomeLabel, BorderLayout.NORTH);
        centerPanel.add(btnPanel, BorderLayout.CENTER);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        cardBtn.addActionListener(e -> showCardPanel());
        transferBtn.addActionListener(e -> showTransferPanel());
        changePwdBtn.addActionListener(e -> showChangePwdPanel());
        updateInfoBtn.addActionListener(e -> showUpdateUserInfoPanel());
        logoutBtn.addActionListener(e -> {
            currentUser = null;
            showLoginPanel();
        });

        setContentPane(panel);
        revalidate();
    }

    // 银行卡管理界面（新增账户管理按钮）
    private void showCardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setName("cardPanel");

        JButton createBtn = new JButton("创建新银行卡");
        JButton transferBtn = new JButton("转账");
        JButton depositBtn = new JButton("存款");
        JButton withdrawBtn = new JButton("取款");
        JButton accountManageBtn = new JButton("账户管理");
        JButton backBtn = new JButton("返回首页");
        JTextArea cardArea = new JTextArea();
        cardArea.setEditable(false);
        cardArea.setFont(new Font("等线", Font.PLAIN, 14));

        loadCards(cardArea);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(backBtn);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.add(createBtn);
        bottomPanel.add(transferBtn);
        bottomPanel.add(depositBtn);
        bottomPanel.add(withdrawBtn);
        bottomPanel.add(accountManageBtn);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(cardArea), BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        createBtn.addActionListener(e -> {
            try {
                Card newCard = cardService.createCard(currentUser.getId(), 0.0);
                JOptionPane.showMessageDialog(this, "银行卡创建成功!卡号: " + newCard.getCardNumber());
                loadCards(cardArea);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "创建失败: " + ex.getMessage());
                logger.error("创建银行卡异常", ex);
            }
        });

        transferBtn.addActionListener(e -> showTransferPanel());
        depositBtn.addActionListener(e -> showDepositWithdrawPanel(true));
        withdrawBtn.addActionListener(e -> showDepositWithdrawPanel(false));
        accountManageBtn.addActionListener(e -> showAccountManagePanel());
        backBtn.addActionListener(e -> showHomePanel());

        setContentPane(panel);
        revalidate();
    }

    // 转账面板（补全userId参数）
    private void showTransferPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField fromCardField = new JTextField();
        JTextField toCardField = new JTextField();
        JTextField amountField = new JTextField();
        JButton transferBtn = new JButton("确认转账");
        JButton backBtn = new JButton("返回");

        panel.add(new JLabel("转出银行卡号:"));
        panel.add(fromCardField);
        panel.add(new JLabel("转入银行卡号:"));
        panel.add(toCardField);
        panel.add(new JLabel("转账金额(元):"));
        panel.add(amountField);
        panel.add(transferBtn);
        panel.add(backBtn);

        transferBtn.addActionListener(e -> {
            try {
                String fromCard = fromCardField.getText().trim();
                String toCard = toCardField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());

                Card fromCardObj = cardService.getCardByNumber(fromCard);
                if (!fromCardObj.getUser().getId().equals(currentUser.getId())) {
                    JOptionPane.showMessageDialog(this, "转出银行卡不属于当前用户!");
                    return;
                }

                // 补全userId参数
                cardService.transfer(fromCard, toCard, amount, currentUser.getId());
                JOptionPane.showMessageDialog(this, "转账成功!");

                fromCardField.setText("");
                toCardField.setText("");
                amountField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "转账金额必须是数字!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "转账失败: " + ex.getMessage());
                logger.error("转账异常", ex);
            }
        });

        backBtn.addActionListener(e -> {
            if (getContentPane().getName() != null && getContentPane().getName().equals("cardPanel")) {
                showCardPanel();
            } else {
                showHomePanel();
            }
        });

        setContentPane(panel);
        revalidate();
    }

    // 修改密码面板
    private void showChangePwdPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPasswordField oldPwdField = new JPasswordField();
        JPasswordField newPwdField = new JPasswordField();
        JPasswordField confirmPwdField = new JPasswordField();
        JButton submitBtn = new JButton("确认修改");
        JButton backBtn = new JButton("返回首页");

        panel.add(new JLabel("旧密码:"));
        panel.add(oldPwdField);
        panel.add(new JLabel("新密码:"));
        panel.add(newPwdField);
        panel.add(new JLabel("确认新密码:"));
        panel.add(confirmPwdField);
        panel.add(submitBtn);
        panel.add(backBtn);

        submitBtn.addActionListener(e -> {
            try {
                String oldPwd = new String(oldPwdField.getPassword());
                String newPwd = new String(newPwdField.getPassword());
                String confirmPwd = new String(confirmPwdField.getPassword());

                if (!newPwd.equals(confirmPwd)) {
                    JOptionPane.showMessageDialog(this, "两次输入的新密码不一致!");
                    return;
                }

                authService.changePassword(currentUser.getUsername(), oldPwd, newPwd);
                JOptionPane.showMessageDialog(this, "密码修改成功，请重新登录!");
                currentUser = null;
                showLoginPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "修改失败: " + ex.getMessage());
                logger.error("修改密码异常", ex);
            }
        });

        backBtn.addActionListener(e -> showHomePanel());
        setContentPane(panel);
        revalidate();
    }

    // 存款/取款面板（补全userId参数）
    private void showDepositWithdrawPanel(boolean isDeposit) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        String title = isDeposit ? "存款" : "取款";
        setTitle("银行卡管理系统（V4.0）-" + title);

        JTextField cardNumberField = new JTextField();
        JTextField amountField = new JTextField();
        JButton confirmBtn = new JButton("确认" + title);
        JButton backBtn = new JButton("返回");

        panel.add(new JLabel("银行卡号:"));
        panel.add(cardNumberField);
        panel.add(new JLabel(title + "金额(元):"));
        panel.add(amountField);
        panel.add(confirmBtn);
        panel.add(backBtn);

        confirmBtn.addActionListener(e -> {
            try {
                String cardNumber = cardNumberField.getText().trim();
                BigDecimal amount = new BigDecimal(amountField.getText().trim());

                Card card = cardService.getCardByNumber(cardNumber);
                if (!card.getUser().getId().equals(currentUser.getId())) {
                    JOptionPane.showMessageDialog(this, "该银行卡不属于当前用户!");
                    return;
                }

                // 补全userId参数
                if (isDeposit) {
                    cardService.deposit(cardNumber, amount, currentUser.getId());
                } else {
                    cardService.withdraw(cardNumber, amount, currentUser.getId());
                }
                JOptionPane.showMessageDialog(this, title + "成功!");
                showCardPanel();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, title + "金额必须是数字!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, title + "失败: " + ex.getMessage());
                logger.error(title + "异常", ex);
            }
        });

        backBtn.addActionListener(e -> showCardPanel());
        setContentPane(panel);
        revalidate();
    }

    // 修改个人信息面板
    private void showUpdateUserInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField realNameField = new JTextField(currentUser.getRealName());
        JTextField phoneField = new JTextField(currentUser.getPhone());
        JTextField idCardField = new JTextField(currentUser.getIdCard());
        JButton submitBtn = new JButton("确认修改");
        JButton backBtn = new JButton("返回首页");

        panel.add(new JLabel("真实姓名:"));
        panel.add(realNameField);
        panel.add(new JLabel("手机号:"));
        panel.add(phoneField);
        panel.add(new JLabel("身份证号:"));
        panel.add(idCardField);
        panel.add(submitBtn);
        panel.add(backBtn);

        submitBtn.addActionListener(e -> {
            try {
                String realName = realNameField.getText().trim();
                String phone = phoneField.getText().trim();
                String idCard = idCardField.getText().trim();
                authService.updateUserInfo(currentUser.getUsername(), realName, phone, idCard);
                JOptionPane.showMessageDialog(this, "个人信息修改成功!");
                currentUser = authService.getUserByUsername(currentUser.getUsername());
                showHomePanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "修改失败: " + ex.getMessage());
                logger.error("修改个人信息异常", ex);
            }
        });

        backBtn.addActionListener(e -> showHomePanel());
        setContentPane(panel);
        revalidate();
    }

    // 账户管理面板（新增）
    private void showAccountManagePanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField cardNumberField = new JTextField();
        JButton lostBtn = new JButton("挂失账户");
        JButton closeBtn = new JButton("办理销户");
        JButton freezeBtn = new JButton("冻结账户");
        JButton unfreezeBtn = new JButton("解冻账户");
        JButton backBtn = new JButton("返回");

        panel.add(new JLabel("银行卡号:"));
        panel.add(cardNumberField);
        panel.add(lostBtn);
        panel.add(closeBtn);
        panel.add(freezeBtn);
        panel.add(unfreezeBtn);
        panel.add(backBtn);

        lostBtn.addActionListener(e -> {
            try {
                String cardNumber = cardNumberField.getText().trim();
                cardService.reportLost(cardNumber, currentUser.getId());
                JOptionPane.showMessageDialog(this, "挂失成功!");
                showCardPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "挂失失败: " + ex.getMessage());
            }
        });

        closeBtn.addActionListener(e -> {
            try {
                String cardNumber = cardNumberField.getText().trim();
                cardService.closeAccount(cardNumber, currentUser.getId());
                JOptionPane.showMessageDialog(this, "销户成功!");
                showCardPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "销户失败: " + ex.getMessage());
            }
        });

        freezeBtn.addActionListener(e -> {
            try {
                String cardNumber = cardNumberField.getText().trim();
                cardService.freezeAccount(cardNumber, currentUser.getId());
                JOptionPane.showMessageDialog(this, "冻结成功!");
                showCardPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "冻结失败: " + ex.getMessage());
            }
        });

        unfreezeBtn.addActionListener(e -> {
            try {
                String cardNumber = cardNumberField.getText().trim();
                cardService.unfreezeAccount(cardNumber, currentUser.getId());
                JOptionPane.showMessageDialog(this, "解冻成功!");
                showCardPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "解冻失败: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> showCardPanel());
        setContentPane(panel);
        revalidate();
    }

    // 加载银行卡列表
    private void loadCards(JTextArea area) {
        List<Card> cards = cardService.getCardsByUserId(currentUser.getId());
        StringBuilder sb = new StringBuilder("=== 我的银行卡列表 ===\n");
        if (cards.isEmpty()) {
            sb.append("暂无银行卡，请先创建!\n");
        } else {
            for (Card card : cards) {
                sb.append("卡号: ").append(card.getCardNumber())
                        .append("\n余额: ").append(card.getBalance()).append("元")
                        .append("\n状态: ").append(card.getStatus())
                        .append("\n开卡时间: ").append(card.getCreateTime())
                        .append("\n最后操作: ").append(card.getLastUpdateTime())
                        .append("\n------------------------\n");
            }
        }
        area.setText(sb.toString());
    }
}