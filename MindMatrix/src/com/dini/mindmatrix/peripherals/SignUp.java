package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class SignUp extends JFrame {

    private JTextField usernameField, emailField, ageField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> genderComboBox;
    private JButton signupButton, backButton;
    private Font customFontAgency, customFontRog;
    private JLabel backgroundLabel;

    public SignUp(JFrame parent) {
        setTitle("Sign Up");
        setSize(690, 520);
        setResizable(false);

        Point parentLocation = parent.getLocation();
        int relativeX = parentLocation.x - 175;
        int relativeY = parentLocation.y - 30;
        setLocation(relativeX, relativeY);

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream isAgency = getClass().getResourceAsStream("/resources/agency.ttf");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.BOLD, 20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFontAgency);

            InputStream isRog = getClass().getResourceAsStream("/resources/rog.otf");
            customFontRog = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.PLAIN, 14f);
            ge.registerFont(customFontRog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/bg.png"));
        backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false);

        usernameField = new JTextField(15);
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        ageField = new JTextField(5);
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});

        JLabel usernameLabel = createLabel("Username:");
        JLabel emailLabel = createLabel("Email:");
        JLabel passwordLabel = createLabel("Password:");
        JLabel confirmPasswordLabel = createLabel("Confirm Password:");
        JLabel ageLabel = createLabel("Age:");
        JLabel genderLabel = createLabel("Gender:");

        signupButton = createCustomButton("Sign Up", new ImageIcon(getClass().getResource("/resources/button4.png")));
        backButton = createCustomButton("Back", new ImageIcon(getClass().getResource("/resources/button.png")));


        /**Grid layout from chatGPT**/

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        addLabelAndField(mainPanel, gbc, usernameLabel, usernameField, 0, 0);
        addLabelAndField(mainPanel, gbc, emailLabel, emailField, 0, 1);

        addLabelAndField(mainPanel, gbc, passwordLabel, passwordField, 1, 0);
        addLabelAndField(mainPanel, gbc, confirmPasswordLabel, confirmPasswordField, 1, 1);

        addLabelAndField(mainPanel, gbc, ageLabel, ageField, 2, 0);
        addLabelAndField(mainPanel, gbc, genderLabel, genderComboBox, 2, 1);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);

        GridBagConstraints buttonGbc = new GridBagConstraints();
        buttonGbc.insets = new Insets(10, 0, 0, 0);
        buttonGbc.gridx = 0;
        buttonGbc.gridy = 0;
        buttonGbc.anchor = GridBagConstraints.CENTER;

        buttonPanel.add(signupButton, buttonGbc);
        buttonGbc.gridy = 1;
        buttonPanel.add(backButton, buttonGbc);

        gbc.gridy = 6;
        mainPanel.add(buttonPanel, gbc);


        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String age = ageField.getText();
                String gender = (String) genderComboBox.getSelectedItem();

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(SignUp.this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String hashedPassword = PasswordUtils.hashPassword(password);

                if (insertUserIntoDatabase(username, email, hashedPassword, age, gender)) {
                    JOptionPane.showMessageDialog(SignUp.this, "Signup successful! Please Login.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(SignUp.this, "Username or Email already exists. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        backgroundLabel.add(mainPanel, BorderLayout.CENTER);

        setContentPane(backgroundLabel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public class PasswordUtils {

        public static String hashPassword(String password) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                byte[] hashedBytes = md.digest(password.getBytes());

                StringBuilder sb = new StringBuilder();
                for (byte b : hashedBytes) {
                    sb.append(String.format("%02x", b));
                }
                return sb.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("Error hashing password", e);
            }
        }

    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(customFontAgency);
        label.setForeground(Color.BLACK);
        return label;
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


    /**Grid layout from chatGPT**/

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, JLabel label, JComponent field, int row, int col) {
        gbc.gridx = col * 2;
        gbc.gridy = row * 2;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.insets = new Insets(10, 50, 10, 10);
        panel.add(label, gbc);

        gbc.insets = new Insets(10, 50, 10, 50);
        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
    }



    private boolean insertUserIntoDatabase(String username, String email, String password, String age, String gender) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean success = false;

        try {
            String url = "jdbc:mysql://localhost:3306/mindmatrix";
            String user = "root";
            String pass = "";

            conn = DriverManager.getConnection(url, user, pass);

            String sql = "INSERT INTO users (username, email, password, age, gender) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setInt(4, Integer.parseInt(age));
            pstmt.setString(5, gender);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return success;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp(null));
    }
}

