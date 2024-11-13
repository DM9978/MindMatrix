package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUp extends JFrame {

    private JTextField usernameField, emailField, ageField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> genderComboBox;
    private JButton signupButton, backButton;

    public SignUp(JFrame parent) {
        setTitle("Sign Up");
        setSize(400, 300);
        setResizable(false);

        Point parentLocation = parent.getLocation();
        int relativeX = parentLocation.x + 50;
        int relativeY = parentLocation.y + 50;
        setLocation(relativeX, relativeY);

        JPanel mainPanel = new JPanel(new GridBagLayout());

        usernameField = new JTextField(15);
        emailField = new JTextField(15);
        passwordField = new JPasswordField(15);
        confirmPasswordField = new JPasswordField(15);
        ageField = new JTextField(5);
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female"});

        JLabel usernameLabel = new JLabel("Username:");
        JLabel emailLabel = new JLabel("Email:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JLabel ageLabel = new JLabel("Age:");
        JLabel genderLabel = new JLabel("Gender:");

        signupButton = new JButton("Sign Up");
        backButton = new JButton("Back");

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

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

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
                } else {
                    JOptionPane.showMessageDialog(SignUp.this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, JLabel label, JComponent field, int row, int col) {
        gbc.gridx = col * 2;
        gbc.gridy = row * 2;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(label, gbc);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridy = row * 2 + 1;
        panel.add(field, gbc);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp(null));
    }
}
