package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

public class PauseMenu extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    private GameGUI gameGUI;
    private Font customFont;
    private JButton resumeButton, exitButton, mainMenuButton;
    private Point mouseClickPoint;
    private JButton[] menuButtons;
    private int currentIndex = -1;
    private boolean navigationStarted = false;

    public PauseMenu(GameGUI gameGUI) {
        this.gameGUI = gameGUI;
        setTitle("Pause Menu");
        setSize(680, 515);
        setLocation(gameGUI.getLocation().x + 5, gameGUI.getLocation().y);
        setUndecorated(true);
        setLayout(null);
        setBackground(new Color(10, 0, 0, 150));

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

        ImageIcon buttonBackgroundIcon = new ImageIcon(getClass().getResource("/resources/PauseMenuButton.png"));

        resumeButton = createCustomButton("Resume", buttonBackgroundIcon);
        exitButton = createCustomButton("Exit", buttonBackgroundIcon);
        mainMenuButton = createCustomButton("Main Menu", buttonBackgroundIcon);

        int buttonWidth = 200;
        int buttonHeight = 50;
        int centerX = (getWidth() - buttonWidth) / 2;
        int startY = (getHeight() - 340);
        int buttonSpacing = 14;

        resumeButton.setBounds(centerX, startY, buttonWidth, buttonHeight);
        exitButton.setBounds(centerX, startY + buttonHeight + buttonSpacing, buttonWidth, buttonHeight);
        mainMenuButton.setBounds(centerX, startY + 2 * (buttonHeight + buttonSpacing), buttonWidth, buttonHeight);

        add(resumeButton);
        add(exitButton);
        add(mainMenuButton);

        addHoverEffect(resumeButton);
        addHoverEffect(exitButton);
        addHoverEffect(mainMenuButton);


        /**Keyboard navigation method from chatGPT**/

        menuButtons = new JButton[]{resumeButton, exitButton, mainMenuButton};

        resumeButton.addActionListener(this);
        exitButton.addActionListener(this);
        mainMenuButton.addActionListener(this);

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
        setModal(true);
        setVisible(true);
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

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            Color originalForeground = button.getForeground();

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.RED);
                AudioManager.getInstance().playHoverSound();
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(originalForeground);
                button.setBorder(null);
            }
        });
    }

    private void updateButtonFocus(int index) {
        for (int i = 0; i < menuButtons.length; i++) {
            JButton button = menuButtons[i];
            if (i == index) {
                button.setForeground(Color.RED);
                button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                AudioManager.getInstance().playHoverSound();
            } else {
                button.setForeground(Color.BLACK);
                button.setBorder(null);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AudioManager.getInstance().playClickSound();
        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case "Resume":
                gameGUI.resumeGame();
                dispose();
                break;
            case "Exit":
                System.exit(0);
                break;
            case "Main Menu":
                gameGUI.setVisible(false);
                GameMenu menu = new GameMenu();
                menu.setLocation(getLocation());
                menu.setVisible(true);
                dispose();
                break;
        }
    }
}
