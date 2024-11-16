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
    JLabel backgroundLabel, logoLabel;
    Font customFont;
    private JButton profileButton;
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
        profileButton = createCustomButton("Profile", buttonBackgroundIcon);

        startButton.setBounds(240, 125, 200, 50);
        loginButton.setBounds(240, 185, 200, 50);
        profileButton.setBounds(240, 185, 200, 50);
        settingsButton.setBounds(240, 245, 200, 50);
        helpButton.setBounds(240, 305, 200, 50);
        aboutButton.setBounds(240, 365, 200, 50);

        profileButton.setVisible(Login.isLoggedIn);
        loginButton.setVisible(!Login.isLoggedIn);
        if (Login.isLoggedIn) {
            menuButtons = new JButton[]{startButton, profileButton, settingsButton, helpButton, aboutButton};
        } else {
            menuButtons = new JButton[]{startButton, loginButton, settingsButton, helpButton, aboutButton};
        }

        backgroundLabel.add(logoLabel);
        backgroundLabel.add(startButton);
        backgroundLabel.add(loginButton);
        backgroundLabel.add(profileButton);
        backgroundLabel.add(settingsButton);
        backgroundLabel.add(helpButton);
        backgroundLabel.add(aboutButton);

        setContentPane(backgroundLabel);

        startButton.addActionListener(this);
        loginButton.addActionListener(this);
        profileButton.addActionListener(this);
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
        addMouseHoverEffect(profileButton);
        addMouseHoverEffect(settingsButton);
        addMouseHoverEffect(helpButton);
        addMouseHoverEffect(aboutButton);
    }

    public void updateButtonVisibility() {
        if (Login.isLoggedIn) {
            loginButton.setVisible(false);
            profileButton.setVisible(true);
        } else {
            loginButton.setVisible(true);
            profileButton.setVisible(false);
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

                JWindow loadingWindow = createLoadingWindow();
                loadingWindow.setVisible(true);

                SwingWorker<GameGUI, Void> worker = new SwingWorker<GameGUI, Void>() {
                    @Override
                    protected GameGUI doInBackground() throws Exception {
                        GameGUI game = new GameGUI();
                        Point menuLocation = GameMenu.this.getLocation();
                        game.setLocation(menuLocation);
                        return game;
                    }

                    @Override
                    protected void done() {
                        try {
                            GameGUI game = get();
                            game.setVisible(true);
                            loadingWindow.dispose();
                            GameMenu.this.dispose();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        } finally {
                            startButtonClicked = false;
                        }
                    }
                };
                worker.execute();
            }

        } else if (e.getSource() == loginButton) {
            if (!loginButtonClicked) {
                loginButtonClicked = true;
                Login login = new Login(this);
                login.setVisible(true);
                login.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        loginButtonClicked = false;
                        updateButtonVisibility();
                    }
                });
            }

        } else if (e.getSource() == profileButton) {
            PlayerProfile profile = new PlayerProfile(this);
            profile.setLocationRelativeTo(this);
            profile.setVisible(true);

        } else if (e.getSource() == settingsButton) {
            if (!settingsButtonClicked) {
                settingsButtonClicked = true;
            }
        } else if (e.getSource() == helpButton) {
            if (!helpButtonClicked) {
                helpButtonClicked = true;
                HelpMenu help = new HelpMenu(this);
                help.setVisible(true);
                help.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        helpButtonClicked = false;
                    }
                });
                this.dispose();
            }
        } else if (e.getSource() == aboutButton) {
            if (!aboutButtonClicked) {
                aboutButtonClicked = true;
                About about = new About(this);
                about.setVisible(true);
                about.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        aboutButtonClicked = false;
                    }
                });
            }
        }
    }


    private JWindow createLoadingWindow() {
        JWindow loadingWindow = new JWindow(this);
        loadingWindow.setSize(680, 518);
        loadingWindow.setLocationRelativeTo(this);
        loadingWindow.setLayout(new BorderLayout());
        loadingWindow.setBackground(new Color(0, 0, 0, 120));
        loadingWindow.getRootPane().setOpaque(false);
        loadingWindow.getContentPane().setBackground(new Color(0, 0, 0, 0));

        Font agencyFont = null;
        try {
            InputStream fontStream = getClass().getResourceAsStream("/resources/sego.ttf");
            agencyFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(Font.BOLD, 18f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(agencyFont);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel loadingLabel = new JLabel("Starting MindMatrix");
        loadingLabel.setFont(agencyFont);
        loadingLabel.setForeground(Color.BLACK);
        loadingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loadingLabel.setVerticalAlignment(SwingConstants.TOP);
        loadingLabel.setOpaque(false);
        loadingLabel.setBorder(BorderFactory.createEmptyBorder(253, 0, 0, 0));
        loadingWindow.add(loadingLabel, BorderLayout.CENTER);

        Timer timer = new Timer(100, new ActionListener() {
            int dotCount = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                dotCount = (dotCount % 3) + 1;
                StringBuilder loadingText = new StringBuilder("Starting MindMatrix");
                for (int i = 0; i < dotCount; i++) {
                    loadingText.append(".");
                }
                loadingLabel.setText(loadingText.toString());
            }
        });
        timer.start();
        return loadingWindow;
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
