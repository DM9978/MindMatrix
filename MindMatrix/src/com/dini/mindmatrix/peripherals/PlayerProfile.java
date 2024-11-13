package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerProfile extends JFrame {
    private JLabel playerNameLabel;
    private JLabel playerIdLabel;
    private JLabel highestScoreLabel;
    private JButton logoutButton;
    private JButton leaderboardButton;
    private Font customFontAgency, customFontMai, customFontRog;
    private JButton backButton;

    public PlayerProfile(JFrame parent) {
        String loggedInUserId = String.valueOf(Login.loggedInUserId);
        setTitle("Player Profile");
        setSize(300, 440);
        setResizable(false);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        loadCustomFont();
        try {
            InputStream isAgency = getClass().getResourceAsStream("/resources/agency.ttf");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.BOLD, 19f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFontAgency);

            InputStream isMai = getClass().getResourceAsStream("/resources/eras.ttf");
            customFontMai = Font.createFont(Font.TRUETYPE_FONT, isMai).deriveFont(Font.PLAIN, 17f);
            ge.registerFont(customFontMai);

            InputStream isRog = getClass().getResourceAsStream("/resources/rog.otf");
            customFontRog = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.PLAIN, 13f);
            ge.registerFont(customFontRog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

        playerNameLabel = createStyledLabel("PLAYER NAME: ");
        playerIdLabel = createStyledLabel("PLAYER ID: ");
        highestScoreLabel = createStyledLabel("HIGHEST SCORE: ");

        JLabel playerNameValue = new JLabel();
        playerNameValue.setFont(customFontMai);
        playerNameValue.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel playerIdValue = new JLabel();
        playerIdValue.setFont(customFontMai);
        playerIdValue.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel highestScoreValue = new JLabel();
        highestScoreValue.setFont(customFontMai);
        highestScoreValue.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel namePanel = createPanelWithLabelAndValue(playerNameLabel, playerNameValue);
        JPanel idPanel = createPanelWithLabelAndValue(playerIdLabel, playerIdValue);
        JPanel scorePanel = createPanelWithLabelAndValue(highestScoreLabel, highestScoreValue);

        backgroundPanel.add(Box.createVerticalStrut(30));
        backgroundPanel.add(namePanel);
        backgroundPanel.add(Box.createVerticalStrut(-100));
        backgroundPanel.add(idPanel);
        backgroundPanel.add(Box.createVerticalStrut(-100));
        backgroundPanel.add(scorePanel);

        backgroundPanel.add(Box.createVerticalStrut(-45));
        logoutButton = createStyledButton("Logout", "/resources/button5.png");
        leaderboardButton = createStyledButton("Rank", "/resources/button4.png");
        backButton = createStyledButton("Back", "/resources/button.png");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(logoutButton);
        buttonPanel.add(leaderboardButton);
        buttonPanel.add(backButton);

        backgroundPanel.add(buttonPanel);
        setContentPane(backgroundPanel);

        fetchPlayerData(loggedInUserId, playerNameValue, playerIdValue, highestScoreValue);

        setVisible(true);

        logoutButton.addActionListener(e -> {
            Login.isLoggedIn = false;
            JOptionPane.showMessageDialog(this, "You have been logged out.");
            dispose();
            Point location = parent.getLocation();
            parent.dispose();
            GameMenu newGameMenu = new GameMenu();
            newGameMenu.setLocation(location);
            newGameMenu.setSize(parent.getSize());
            newGameMenu.setVisible(true);
        });

        leaderboardButton.addActionListener(e -> {
            Point location = this.getLocation();
        });

        backButton.addActionListener(e -> {
            dispose();
        });
    }

    private JPanel createPanelWithLabelAndValue(JLabel label, JLabel value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setOpaque(false);
        label.setFont(customFontAgency);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        value.setFont(customFontMai);
        value.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
        panel.add(Box.createHorizontalStrut(18));
        panel.add(value);
        return panel;
    }

    private void fetchPlayerData(String loggedInUserId, JLabel playerNameValue, JLabel playerIdValue, JLabel highestScoreValue) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = "SELECT username, player_id, highest_score FROM users WHERE player_id = ?";
                    PreparedStatement ps = conn.prepareStatement(query);
                    ps.setString(1, loggedInUserId);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        String playerName = rs.getString("username");
                        String playerId = rs.getString("player_id");
                        int highestScore = rs.getInt("highest_score");

                        SwingUtilities.invokeLater(() -> {
                            playerNameValue.setText(playerName);
                            playerIdValue.setText("00" + playerId);
                            highestScoreValue.setText(String.valueOf(highestScore));
                        });
                    } else {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(PlayerProfile.this, "No player data found.", "Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(PlayerProfile.this, "Error fetching player data.", "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                return null;
            }
        };
        worker.execute();
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(customFontAgency);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JButton createStyledButton(String text, String iconPath) {
        JButton button = new JButton(text);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            if (icon.getIconWidth() > 0) {
                button.setIcon(icon);
            } else {
                System.err.println("Icon not found at: " + iconPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void loadCustomFont() {
        try {
            InputStream is = getClass().getResourceAsStream("/resources/rog.otf");
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;
        private Image prImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/resources/bg.png")).getImage();
                prImage = new ImageIcon(getClass().getResource("/resources/pr1.png")).getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**Image centering method from chatGPT**/

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                int imageWidth = backgroundImage.getWidth(null);
                int imageHeight = backgroundImage.getHeight(null);
                int x = (getWidth() - imageWidth) / 2;
                int y = (getHeight() - imageHeight) / 2;
                g.drawImage(backgroundImage, x, y, this);
            }

            if (prImage != null) {
                int imageWidth = prImage.getWidth(null);
                int x = (getWidth() - imageWidth) / 2;
                g.drawImage(prImage, x, 25, this);
            }
        }
    }
}

