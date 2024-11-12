package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameMenu extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    JButton startButton, settingsButton, aboutButton, loginButton;
    JLabel backgroundLabel;
    public GameMenu() {
        setSize(690, 500);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Welcome to MindMatrix");

        backgroundLabel = new JLabel();
        backgroundLabel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        startButton = new JButton("Start");
        settingsButton = new JButton("Settings");
        aboutButton = new JButton("About");
        loginButton = new JButton("Login");

        gbc.gridx = 0;

        gbc.gridy = 0;
        backgroundLabel.add(startButton, gbc);

        gbc.gridy = 1;
        backgroundLabel.add(loginButton, gbc);

        gbc.gridy = 2;
        backgroundLabel.add(settingsButton, gbc);

        gbc.gridy = 3;
        backgroundLabel.add(aboutButton, gbc);

        startButton.addActionListener(this);
        loginButton.addActionListener(this);
        settingsButton.addActionListener(this);
        aboutButton.addActionListener(this);

        setContentPane(backgroundLabel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            GameGUI game = new GameGUI();
            game.setVisible(true);
            this.dispose();
        } else if (e.getSource() == loginButton) {
            this.dispose();
        } else if (e.getSource() == settingsButton) {
            JOptionPane.showMessageDialog(this, "Settings menu (Coming soon!)");
        } else if (e.getSource() == aboutButton) {
            JOptionPane.showMessageDialog(this, "MindMatrix\nVersion 1.0");
        }
    }

    public static void main(String[] args) {
        new GameMenu();
    }
}
