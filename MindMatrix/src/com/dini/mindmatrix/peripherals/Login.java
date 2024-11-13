package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.sql.*;

public class Login extends JFrame {

    public static boolean isLoggedIn = false;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox showPasswordCheckBox;
    private JButton loginButton;
    private JButton signupButton;
    private JButton backButton;
    private Font customFontAgency, customFontRog;
    private JLabel backgroundLabel;
    public static int loggedInUserId;
    public static String loggedInUsername;

    public Login(JFrame parent) {
        setTitle("Login");
        setSize(340, 460);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream isRog = getClass().getResourceAsStream("/resources/agency.ttf");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.BOLD, 20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFontAgency);

            InputStream isAgency = getClass().getResourceAsStream("/resources/rog.otf");
            customFontRog = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.PLAIN, 14f);
            ge.registerFont(customFontRog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/bg.png"));
        backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(customFontAgency);
        usernameLabel.setForeground(Color.BLACK);
        usernameField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(customFontAgency);
        passwordLabel.setForeground(Color.BLACK);
        passwordField = new JPasswordField(15);

        showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.setForeground(Color.BLACK);
        showPasswordCheckBox.setOpaque(false);
        showPasswordCheckBox.setFocusPainted(false);
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());

        loginButton = createCustomButton("Login", new ImageIcon(getClass().getResource("/resources/button4.png")));
        signupButton = createCustomButton("Signup", new ImageIcon(getClass().getResource("/resources/button4.png")));
        backButton = createCustomButton("Back", new ImageIcon(getClass().getResource("/resources/button.png")));

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SignUp(Login.this);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String passwordStr = new String(password);

                if (authenticateUser(username, passwordStr)) {
                    isLoggedIn = true;
                    JOptionPane.showMessageDialog(Login.this, "Login Successful!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(Login.this, "Invalid username or password.");
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                parent.setVisible(true);
            }
        });


        /**Grid layout is from chatGPT**/

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(usernameLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passwordLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(showPasswordCheckBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(signupButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(backButton, gbc);

        backgroundLabel.add(mainPanel, BorderLayout.CENTER);
        setContentPane(backgroundLabel);
        setVisible(true);
    }


    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
    }


    private boolean authenticateUser(String username, String password) {
        try {
            String url = "jdbc:mysql://localhost:3306/mindmatrix";
            String dbUsername = "root";
            String dbPassword = "";

            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String query = "SELECT password, player_id FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                loggedInUserId = rs.getInt("player_id");

                String hashedInputPassword = SignUp.PasswordUtils.hashPassword(password);
                if (hashedInputPassword.equals(storedHashedPassword)) {
                    loggedInUsername = username;
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private JButton createCustomButton(String text, ImageIcon backgroundIcon) {
        JButton button = new JButton(text);
        button.setIcon(backgroundIcon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(customFontRog);
        addHoverEffect(button);
        return button;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalForeground = button.getForeground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setForeground(originalForeground);
            }
        });
    }

    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    public void setLoggedInUserId(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public void setloggedInUsername(String username) {
        this.usernameField.setText(username);
    }
}


