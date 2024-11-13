package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.swing.Timer;

public class GameMenu extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    JButton startButton, settingsButton, aboutButton, loginButton, helpButton;
    JLabel backgroundLabel, logoLabel, profileIconLabel;
    Font customFont;
    private Point initialClick;
    private JButton[] menuButtons;
    private int currentIndex = -1;
    private boolean navigationStarted = false;

    public GameMenu() {
        setSize(690, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Welcome to MindMatrix");

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream is = getClass().getResourceAsStream("/resources/rog.otf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/bg.png"));
        backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(null);

        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/rr.png"));
        logoLabel = new JLabel(logoIcon);
        logoLabel.setBounds((getWidth() - logoIcon.getIconWidth()) / 2, 40, logoIcon.getIconWidth(), logoIcon.getIconHeight());

        ImageIcon buttonBackgroundIcon = new ImageIcon(getClass().getResource("/resources/button.png"));
        startButton = createCustomButton("Start", buttonBackgroundIcon);
        settingsButton = createCustomButton("Settings", buttonBackgroundIcon);
        helpButton = createCustomButton("Help", buttonBackgroundIcon);
        aboutButton = createCustomButton("About", buttonBackgroundIcon);
        loginButton = createCustomButton("Login", buttonBackgroundIcon);

        startButton.setBounds(240, 125, 200, 50);
        loginButton.setBounds(240, 185, 200, 50);
        settingsButton.setBounds(240, 245, 200, 50);
        helpButton.setBounds(240, 305, 200, 50);
        aboutButton.setBounds(240, 365, 200, 50);

        backgroundLabel.add(logoLabel);
        backgroundLabel.add(startButton);
        backgroundLabel.add(loginButton);
        backgroundLabel.add(settingsButton);
        backgroundLabel.add(helpButton);
        backgroundLabel.add(aboutButton);

        setContentPane(backgroundLabel);

        startButton.addActionListener(this);
        loginButton.addActionListener(this);
        settingsButton.addActionListener(this);
        helpButton.addActionListener(this);
        aboutButton.addActionListener(this);


        /**Keyboard navigation method from chatGPT**/

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_DOWN) {
                    if (!navigationStarted) {
                        navigationStarted = true;
                        currentIndex = 0;
                    } else {
                        currentIndex = (currentIndex + 1) % menuButtons.length;
                    }
                    updateButtonFocus(currentIndex);
                } else if (keyCode == KeyEvent.VK_UP) {
                    if (!navigationStarted) {
                        navigationStarted = true;
                        currentIndex = 0;
                    } else {
                        currentIndex = (currentIndex - 1 + menuButtons.length) % menuButtons.length;
                    }
                    updateButtonFocus(currentIndex);
                } else if (keyCode == KeyEvent.VK_ENTER && navigationStarted) {
                    menuButtons[currentIndex].doClick();
                }
            }
        });

        setFocusable(true);
        setVisible(true);

        addMouseHoverEffect(startButton);
        addMouseHoverEffect(loginButton);
        addMouseHoverEffect(settingsButton);
        addMouseHoverEffect(helpButton);
        addMouseHoverEffect(aboutButton);
    }

    private JButton createCustomButton(String text, ImageIcon backgroundIcon) {
        JButton button = new JButton(text);
        button.setIcon(backgroundIcon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(customFont);
        return button;
    }

    private void addMouseHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.RED);
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Color.BLACK);
                button.setBorder(null);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    private void updateButtonFocus(int index) {
        for (int i = 0; i < menuButtons.length; i++) {
            JButton button = menuButtons[i];
            if (i == index) {
                button.setForeground(Color.RED);
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            } else {
                button.setForeground(Color.BLACK);
                button.setBorder(null);
            }
        }
    }

    private boolean startButtonClicked = false;
    private boolean loginButtonClicked = false;
    private boolean settingsButtonClicked = false;
    private boolean helpButtonClicked = false;
    private boolean aboutButtonClicked = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        resetHoverEffects();

        if (e.getSource() == startButton) {
            if (!startButtonClicked) {
                startButtonClicked = true;
                GameGUI game = new GameGUI();
                game.setVisible(true);
                this.dispose();

            }

        } else if (e.getSource() == loginButton) {
            if (!loginButtonClicked) {
                loginButtonClicked = true;
                Login login = new Login(this);
            }

        } else if (e.getSource() == settingsButton) {
            if (!settingsButtonClicked) {
                settingsButtonClicked = true;
            }
        } else if (e.getSource() == helpButton) {
            if (!helpButtonClicked) {
                helpButtonClicked = true;
                HelpMenu helpMenu = new HelpMenu(this);
                this.dispose();
            }
        } else if (e.getSource() == aboutButton) {
            if (!aboutButtonClicked) {
                aboutButtonClicked = true;
                About about = new About(this);

            }
        }
    }


    private void resetHoverEffects() {
        JButton[] buttons = {startButton, loginButton, settingsButton, helpButton, aboutButton};
        for (JButton button : buttons) {
            button.setForeground(Color.BLACK);
            button.setBorder(null);
        }
    }

    public static void main(String[] args) {
        new GameMenu();
    }
}
