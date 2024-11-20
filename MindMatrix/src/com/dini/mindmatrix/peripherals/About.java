package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

public class About extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel backgroundLabel;
    private JButton backButton;
    private JLabel aboutLabel;
    private Font customFontRog;
    private Font customFontAgency;
    private int selectedIndex = -1;

    public About(JFrame parent) {
        setTitle("About");
        setSize(320, 420);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream isRog = getClass().getResourceAsStream("/resources/agency.ttf");
            customFontRog = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.BOLD, 20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFontRog);

            InputStream isAgency = getClass().getResourceAsStream("/resources/rog.otf");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.PLAIN, 14f);
            ge.registerFont(customFontAgency);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/bg.png"));
        backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());

        ImageIcon rrIcon = new ImageIcon(getClass().getResource("/resources/rr1.png"));
        JLabel rrLabel = new JLabel(rrIcon);
        rrLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rrLabel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));
        backgroundLabel.add(rrLabel, BorderLayout.NORTH);

        aboutLabel = new JLabel("<html><div style='text-align: center;'>Mind Matrix - a Puzzle Game<br><br>Version 1.0.0<br><br>Used API - Banana (Licensed by <br> Marc Conard)<br><br>Developed by DiniK</div></html>");
        aboutLabel.setFont(customFontRog);
        aboutLabel.setOpaque(false);
        aboutLabel.setHorizontalAlignment(SwingConstants.CENTER);
        aboutLabel.setVerticalAlignment(SwingConstants.CENTER);
        aboutLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        backgroundLabel.add(aboutLabel, BorderLayout.CENTER);

        ImageIcon buttonBackgroundIcon = new ImageIcon(getClass().getResource("/resources/button.png"));
        backButton = createCustomButton("Back", buttonBackgroundIcon);
        backButton.setHorizontalAlignment(SwingConstants.CENTER);

        backButton.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        backButton.addActionListener(e -> {
            AudioManager.getInstance().playClickSound();
            Point location = parent.getLocation();
            parent.dispose();
            GameMenu newGameMenu = new GameMenu();
            newGameMenu.setLocation(location);
            newGameMenu.setSize(parent.getSize());
            newGameMenu.setVisible(true);
            this.dispose();
        });

        backgroundLabel.add(backButton, BorderLayout.SOUTH);

        setContentPane(backgroundLabel);
        setVisible(true);

        JButton[] buttons = {backButton};
        setButtonNavigation(buttons);
    }

    private JButton createCustomButton(String text, ImageIcon backgroundIcon) {
        JButton button = new JButton(text);
        button.setIcon(backgroundIcon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(customFontAgency);
        addHoverEffect(button);
        return button;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalForeground = button.getForeground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setForeground(Color.RED);
                AudioManager.getInstance().playHoverSound();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!button.getModel().isPressed()) {
                    button.setForeground(originalForeground);
                }
            }
        });
    }

    /**Keyboard navigation method from chatGPT**/

    private void setButtonNavigation(JButton[] buttons) {
        selectedIndex = -1;

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (selectedIndex == -1) {
                    selectedIndex = 0;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    selectedIndex = (selectedIndex + 1) % buttons.length;
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    selectedIndex = (selectedIndex - 1 + buttons.length) % buttons.length;
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buttons[selectedIndex].doClick();
                }
                updateButtonFocus(selectedIndex, buttons);
            }
        });

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                if (selectedIndex == -1) {
                    return;
                }
                updateButtonFocus(selectedIndex, buttons);
            }
        });

        this.requestFocusInWindow();
    }

    private void updateButtonFocus(int index, JButton[] buttons) {
        for (int i = 0; i < buttons.length; i++) {
            JButton button = buttons[i];
            if (i == index) {
                button.setForeground(Color.RED);
                AudioManager.getInstance().playHoverSound();
            } else {
                button.setForeground(Color.BLACK);
            }
        }

        this.revalidate();
        this.repaint();
    }

}
