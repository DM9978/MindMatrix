package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.Timer;

public class GameMenu extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;

    JButton startButton, settingsButton, aboutButton, loginButton, helpButton;
    JLabel backgroundLabel, logoLabel, highestScoreLabel, scoreValueLabel, helloLabel, userLabel;
    Font customFont, customFontAgency;
    private JButton profileButton;
    private Point initialClick;
    private JButton[] menuButtons;
    private int currentIndex = -1;
    private boolean navigationStarted = false;
    private JLabel leftLineLabel;
    private JLabel rightLineLabel;


    public GameMenu() {
        setSize(690, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Welcome to MindMatrix");

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        try {
            InputStream isRog = getClass().getResourceAsStream("/resources/rog.otf");
            customFont = Font.createFont(Font.TRUETYPE_FONT, isRog).deriveFont(Font.BOLD, 14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);

            InputStream isAgency = getClass().getResourceAsStream("/resources/eras.TTF");
            customFontAgency = Font.createFont(Font.TRUETYPE_FONT, isAgency).deriveFont(Font.BOLD,20f);
            ge.registerFont(customFontAgency);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/resources/bg.png"));
        backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(null);

        helloLabel = new JLabel("Hello!");
        helloLabel.setBounds(15, 5, 90, 40);
        helloLabel.setFont(customFontAgency.deriveFont(17.5f));
        helloLabel.setForeground(Color.BLACK);
        backgroundLabel.add(helloLabel);

        userLabel = new JLabel("Guest");
        userLabel.setBounds(70, 5, 150, 40);
        userLabel.setFont(customFontAgency.deriveFont(17f));
        userLabel.setForeground(Color.BLACK);
        backgroundLabel.add(userLabel);

        highestScoreLabel = new JLabel("Best Score:");
        highestScoreLabel.setBounds(getWidth() - 140, 5, 150, 40);
        highestScoreLabel.setFont(customFontAgency.deriveFont(17f));
        highestScoreLabel.setForeground(Color.BLACK);
        backgroundLabel.add(highestScoreLabel);

        scoreValueLabel = new JLabel("00");
        scoreValueLabel.setBounds(getWidth() - 50, 5, 100, 40);
        scoreValueLabel.setFont(customFontAgency.deriveFont(17.5f));
        scoreValueLabel.setForeground(Color.BLACK);
        backgroundLabel.add(scoreValueLabel);

        ImageIcon leftLineIcon = new ImageIcon(getClass().getResource("/resources/ln4.png"));
        ImageIcon rightLineIcon = new ImageIcon(getClass().getResource("/resources/ln3.png"));

        leftLineLabel = new JLabel(leftLineIcon);
        leftLineLabel.setBounds(15, 38, leftLineIcon.getIconWidth(), leftLineIcon.getIconHeight());
        backgroundLabel.add(leftLineLabel);

        rightLineLabel = new JLabel(rightLineIcon);
        rightLineLabel.setBounds(getWidth() - 183, 38, rightLineIcon.getIconWidth(), rightLineIcon.getIconHeight());
        backgroundLabel.add(rightLineLabel);

        fetchAndDisplayUserData();
        updateLabels();

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

        AudioManager.getInstance();

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

    private void fetchAndDisplayUserData() {
        if (Login.loggedInUserId > 0) {
            try {
                Connection conn = DatabaseConnection.getConnection();
                String query = "SELECT username, highest_score FROM users WHERE player_id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setInt(1, Login.loggedInUserId);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    String username = rs.getString("username");
                    int highestScore = rs.getInt("highest_score");
                    String formattedScore = String.format("%02d", highestScore);
                    userLabel.setText(username);
                    scoreValueLabel.setText(formattedScore);
                }

                rs.close();
                ps.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error fetching user data.");
            }
        }
    }

    private void updateLabels() {
        boolean isLoggedIn = Login.isLoggedIn;

        if (userLabel != null) {
            userLabel.setText(isLoggedIn ? userLabel.getText() : "Guest");
        }
        if (scoreValueLabel != null) {
            scoreValueLabel.setText(isLoggedIn ? scoreValueLabel.getText() : "00");
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
                AudioManager.getInstance().playHoverSound();
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
                AudioManager.getInstance().playClickSound();
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

    private boolean startButtonClicked = false;
    private boolean loginButtonClicked = false;
    private boolean settingsButtonClicked = false;
    private boolean helpButtonClicked = false;
    private boolean aboutButtonClicked = false;

    @Override
    public void actionPerformed(ActionEvent e) {
        resetHoverEffects();
        AudioManager.getInstance().playClickSound();

        if (e.getSource() == startButton) {
            if (Login.isLoggedIn) {
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
            } else {
                JWindow overlay = new JWindow();
                overlay.setLocation(GameMenu.this.getLocation());
                overlay.setLayout(null);
                overlay.setBackground(new Color(57, 57, 57, 74));
                overlay.setSize(getSize(GameMenu.this.getSize()));
                overlay.setVisible(true);

                JDialog dialog = new JDialog((Frame) null, "Login Required", false);
                dialog.setUndecorated(true);
                dialog.setLayout(new BorderLayout());

                dialog.getContentPane().setBackground(new Color(246, 246, 246, 255));
                JLabel customMessage = new JLabel("Please Login First!", SwingConstants.CENTER);
                customMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                customMessage.setFont(customFontAgency);
                dialog.add(customMessage, BorderLayout.CENTER);

                dialog.getRootPane().setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(197, 2, 2, 187), 3),
                        BorderFactory.createEmptyBorder(0, 0, 0, 0)
                ));

                dialog.setSize(240, 60);
                dialog.setLocationRelativeTo(GameMenu.this);
                dialog.setVisible(true);
                dialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        overlay.dispose();
                    }
                });

                Timer timer = new Timer(800, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        dialog.dispose();

                        if (!loginButtonClicked) {
                            loginButtonClicked = true;
                            SwingUtilities.invokeLater(() -> {
                                Login login = new Login(GameMenu.this);
                                login.setVisible(true);
                                login.addWindowListener(new WindowAdapter() {
                                    @Override
                                    public void windowClosed(WindowEvent e) {
                                        loginButtonClicked = false;
                                        updateButtonVisibility();
                                    }
                                });
                            });
                        }
                    }
                });
                timer.setRepeats(false);
                timer.start();
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
            AudioManager.getInstance().playClickSound();

        } else if (e.getSource() == settingsButton) {
            if (!settingsButtonClicked) {
                settingsButtonClicked = true;
                SettingsMenu settings = new SettingsMenu(this);
                settings.setVisible(true);
                settings.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        settingsButtonClicked = false;
                    }
                });
                this.dispose();
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
        AudioManager.getInstance().playClickSound();
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
