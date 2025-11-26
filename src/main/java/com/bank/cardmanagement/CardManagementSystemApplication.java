// CardManagementSystemApplication.java
package com.bank.cardmanagement;

import com.bank.cardmanagement.gui.MainFrame;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class CardManagementSystemApplication {

    public static void main(String[] args) {
        // 关键修复：禁用Headless模式，支持Swing GUI
        System.setProperty("java.awt.headless", "false");

        // 启动Spring容器
        ConfigurableApplicationContext context = SpringApplication.run(CardManagementSystemApplication.class, args);

        // 在Swing事件调度线程中启动窗口
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = context.getBean(MainFrame.class);
            mainFrame.init();
            mainFrame.setVisible(true);
        });
    }
}