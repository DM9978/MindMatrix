package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

public class HelpMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JLabel backgroundLabel;
    private JButton backButton;
    private JTextPane helpTextPane;
    private Font customFontRog, customFontAgency;
    private GameMenu gameMenu;
    private JButton[] helpButtons;
    private int currentIndex = -1;
    private boolean navigationStarted = false;

    public HelpMenu(GameMenu menu) {
        this.gameMenu = menu;
        setSize(690, 520);
        setLocation(menu.getX(), menu.getY());
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Help");

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream isRog = getClass().getResourceAsStream("/resources/rog.otf");
            customFontRog = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.BOLD, 14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFontRog);

            InputStream isAgency = getClass().getResourceAsStream("/resources/mai.TTF");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.BOLD,18f);
            ge.registerFont(customFontAgency);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/bg1.png"));
        backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(null);

        helpTextPane = new JTextPane();
        helpTextPane.setBounds(50, 30, 590, 355);
        helpTextPane.setOpaque(false);
        helpTextPane.setEditable(false);
        helpTextPane.setFocusable(false);
        helpTextPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        StyledDocument doc = helpTextPane.getStyledDocument();
        Style headingStyle = helpTextPane.addStyle("heading", null);
        StyleConstants.setFontFamily(headingStyle, customFontRog.getFamily());
        StyleConstants.setFontSize(headingStyle, customFontRog.getSize());

        Style paragraphStyle = helpTextPane.addStyle("paragraph", null);
        StyleConstants.setFontFamily(paragraphStyle, customFontAgency.getFamily());
        StyleConstants.setFontSize(paragraphStyle, customFontAgency.getSize());

        try {
            doc.insertString(doc.getLength(), "What is this game?\n", headingStyle);
            doc.insertString(doc.getLength(), "Welcome to Mind Matrix, a mathematical puzzle game that challenges your problem-solving skills!\n\n", paragraphStyle);

            doc.insertString(doc.getLength(), "How to Play?\n", headingStyle);
            doc.insertString(doc.getLength(), "The game consists of six equations arranged vertically and horizontally. Each equation may involve addition, subtraction, multiplication, or division. Your goal is to find the missing digit, represented by a banana icon (\uD83C\uDF4C).\n\n", paragraphStyle);

            doc.insertString(doc.getLength(), "Rules:\n", headingStyle);
            doc.insertString(doc.getLength(), "You have 5 lives. Each incorrect answer will cost you one life. The time limit for each game decreases as you advance to harder levels.\n\n", paragraphStyle);

            doc.insertString(doc.getLength(), "Objective:\n", headingStyle);
            doc.insertString(doc.getLength(), "Solve the equations, find the missing values, and beat the clock! Keep your lives intact as you progress through increasingly difficult puzzles.\n", paragraphStyle);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        ImageIcon buttonBackgroundIcon = new ImageIcon(getClass().getResource("/resources/button.png"));
        backButton = createCustomButton("Back", buttonBackgroundIcon);
        int buttonWidth = 130;
        int buttonHeight = 50;
        int frameWidth = getWidth();
        backButton.setBounds((frameWidth - buttonWidth) / 2, 395, buttonWidth, buttonHeight);


        /**Keyboard navigation method from chatGPT**/

        helpButtons = new JButton[]{backButton};

        backButton.addActionListener(e -> {
            gameMenu.setVisible(true);
            this.dispose();
        });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_DOWN) {
                    if (!navigationStarted) {
                        navigationStarted = true;
                        currentIndex = 0;
                    } else {
                        currentIndex = (currentIndex + 1) % helpButtons.length;
                    }
                    updateButtonFocus(currentIndex);
                } else if (keyCode == KeyEvent.VK_UP) {
                    if (!navigationStarted) {
                        navigationStarted = true;
                        currentIndex = 0;
                    } else {
                        currentIndex = (currentIndex - 1 + helpButtons.length) % helpButtons.length;
                    }
                    updateButtonFocus(currentIndex);
                } else if (keyCode == KeyEvent.VK_ENTER && navigationStarted) {
                    helpButtons[currentIndex].doClick();
                }
            }
        });
        setFocusable(true);
        setContentPane(backgroundLabel);
        backgroundLabel.add(helpTextPane);
        backgroundLabel.add(backButton);
        setVisible(true);
    }


    private void updateButtonFocus(int index) {
        for (int i = 0; i < helpButtons.length; i++) {
            JButton button = helpButtons[i];
            if (i == index) {
                button.setForeground(Color.RED);
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                button.setForeground(Color.BLACK);
                button.setBorder(null);
            }
        }
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


        button.setFont(button.getFont().deriveFont(14f));

        return button;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalForeground = button.getForeground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(Color.RED);
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!button.getModel().isPressed()) {
                    button.setForeground(originalForeground);
                    button.setBorder(null);
                }
            }
        });
    }

    public static void main(String[] args) {
        new HelpMenu(new GameMenu()).setVisible(true);
    }
}
