package com.dini.mindmatrix.peripherals;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Leaderboard extends JFrame {

    private JTable leaderboardTable;
    private JLabel currentPlayerLabel;
    private JButton backButton;
    private Font customFontAgency;
    private Font customFontMai;
    private Font customFontRog;
    private int loggedInUserId;
    private Image backgroundImage;

    public Leaderboard() {
        setTitle("Leaderboard");
        setSize(300, 440);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream bgStream = getClass().getResourceAsStream("/resources/bg.png");
            backgroundImage = ImageIO.read(bgStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            InputStream isAgency = getClass().getResourceAsStream("/resources/agency.ttf");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.BOLD, 19f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFontAgency);

            InputStream isMai = getClass().getResourceAsStream("/resources/eras.ttf");
            customFontMai = Font.createFont(Font.TRUETYPE_FONT, isMai).deriveFont(Font.PLAIN, 17f);
            ge.registerFont(customFontMai);

            InputStream isRog = getClass().getResourceAsStream("/resources/rog.otf");
            customFontRog = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.PLAIN, 14f);
            ge.registerFont(customFontRog);
        } catch (Exception e) {
            e.printStackTrace();
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        String[] columnNames = {"Rank", "Player Name", "Best Score"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        leaderboardTable = new JTable(tableModel);
        leaderboardTable.setEnabled(false);

        leaderboardTable.setRowHeight(30);
        leaderboardTable.getTableHeader().setFont(customFontAgency);
        leaderboardTable.setFont(customFontAgency);
        leaderboardTable.setForeground(Color.BLACK);
        leaderboardTable.setShowGrid(false);

        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        scrollPane.setBounds(35, 30, 220, 240);

        currentPlayerLabel = new JLabel("", SwingConstants.CENTER);
        currentPlayerLabel.setBounds(35, 280, 220, 40);
        currentPlayerLabel.setFont(customFontAgency);
        currentPlayerLabel.setForeground(Color.BLACK);

        ImageIcon buttonBackgroundIcon = new ImageIcon(getClass().getResource("/resources/button.png"));
        backButton = createCustomButton("Back", buttonBackgroundIcon);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);

        backButton.addActionListener(e -> {
            this.dispose();
        });

        backgroundPanel.add(scrollPane);
        backgroundPanel.add(currentPlayerLabel);
        backgroundPanel.add(backButton);
        populateLeaderboard(tableModel);
        setVisible(true);
    }

    private JButton createCustomButton(String text, ImageIcon backgroundIcon) {
        JButton button = new JButton(text);
        button.setBounds(80, 330, 130, 50);
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
                if (!button.getModel().isPressed()) {
                    button.setForeground(originalForeground);
                }
            }
        });
    }


    /**Cell renderer method and table layout from chatgpt**/

    private class CustomTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String usernameInRow = table.getValueAt(row, 1).toString();
            String loggedInUsername = Login.loggedInUsername;

            if (row == 0) {
                c.setBackground(new Color(255, 226, 124));
            } else if (usernameInRow.equals(loggedInUsername)) {
                c.setBackground(new Color(190, 244, 255));
            } else if (row % 2 == 0) {
                c.setBackground(new Color(236, 236, 251));
            } else {
                c.setBackground(new Color(245, 245, 251));
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }



    private void populateLeaderboard(DefaultTableModel tableModel) {
        try {
            tableModel.setRowCount(0);
            Connection connection = DatabaseConnection.getConnection();

            String leaderboardQuery = "SELECT username, highest_score FROM users ORDER BY highest_score DESC";
            PreparedStatement leaderboardPS = connection.prepareStatement(leaderboardQuery);
            ResultSet leaderboardRS = leaderboardPS.executeQuery();

            int rank = 1;
            int loggedInUserRank = -1;
            int loggedInUserScore = -1;

            String loggedInUsername = Login.loggedInUsername;

            while (leaderboardRS.next()) {
                String username = leaderboardRS.getString("username");
                int highestScore = leaderboardRS.getInt("highest_score");

                Vector<Object> row = new Vector<>();
                row.add(rank);
                row.add(username);
                row.add(highestScore);
                tableModel.addRow(row);

                if (username.equals(loggedInUsername)) {
                    loggedInUserRank = rank;
                    loggedInUserScore = highestScore;
                }
                rank++;
            }
            highlightLoggedInUserRow();

            updateCurrentPlayerLabel(loggedInUserRank, loggedInUserScore);

            leaderboardTable.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
            leaderboardTable.getColumnModel().getColumn(0).setPreferredWidth(30);
            leaderboardTable.setShowVerticalLines(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateCurrentPlayerLabel(int rank, int score) {
        if (rank == -1 || score == -1) {
            currentPlayerLabel.setText("You are not ranked yet.");
        } else {
            currentPlayerLabel.setText("<html>Your Rank: " + rank + " | Best Score: " + score + "</html>");
        }
    }

    private void highlightLoggedInUserRow() {
        String loggedInUsername = Login.loggedInUsername;
        for (int row = 0; row < leaderboardTable.getRowCount(); row++) {
            String usernameInRow = leaderboardTable.getValueAt(row, 1).toString();
            if (usernameInRow.equals(loggedInUsername)) {
                leaderboardTable.setRowSelectionAllowed(true);
                leaderboardTable.setSelectionBackground(Color.GREEN);
                leaderboardTable.addRowSelectionInterval(row, row);
                break;
            }
        }
    }

    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try {
                backgroundImage = new ImageIcon(getClass().getResource("/resources/bg.png")).getImage();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

        }
    }
}

