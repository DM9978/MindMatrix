package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;

public class SettingsMenu extends JFrame implements ChangeListener {
    private static final long serialVersionUID = 1L;

    private JCheckBox musicCheckBox, sfxCheckBox;
    private JSlider musicVolumeSlider, sfxVolumeSlider;
    private JButton backButton;
    private JLabel backgroundLabel, logoLabel, volumeLabel, sfxVolumeLabel;
    private Font customFont;
    private JFrame previousMenu;
    private boolean backButtonSelected = false;

    public SettingsMenu(JFrame previousMenu) {
        this.previousMenu = previousMenu;
        setSize(690, 520);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Settings");

        ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
        setIconImage(icon.getImage());

        Point location = previousMenu.getLocation();
        setLocation(location.x, location.y);

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

        musicCheckBox = new JCheckBox();
        musicCheckBox.setSelected(AudioManager.getInstance().isMusicOn());
        musicCheckBox.setBounds(380, 150, 80, 30);
        musicCheckBox.setFont(customFont);
        musicCheckBox.setOpaque(false);
        musicCheckBox.addActionListener(e -> AudioManager.getInstance().setMusicOn(musicCheckBox.isSelected()));

        JLabel musicLabel = new JLabel("Music :");
        musicLabel.setBounds(290, 150, 90, 30);
        musicLabel.setFont(customFont);

        sfxCheckBox = new JCheckBox();
        sfxCheckBox.setSelected(AudioManager.getInstance().isSFXOn());
        sfxCheckBox.setBounds(380, 250, 130, 30);
        sfxCheckBox.setFont(customFont);
        sfxCheckBox.setOpaque(false);
        sfxCheckBox.addActionListener(e -> AudioManager.getInstance().setSFXOn(sfxCheckBox.isSelected()));

        JLabel sfxLabel = new JLabel("SFX :");
        sfxLabel.setBounds(290, 250, 300, 30);
        sfxLabel.setFont(customFont);

        volumeLabel = new JLabel("Music Volume :");
        volumeLabel.setBounds(170, 200, 200, 30);
        volumeLabel.setFont(customFont);

        musicVolumeSlider = new JSlider(0, 100, (int)(AudioManager.getInstance().getVolume() * 100));
        musicVolumeSlider.setBounds(330, 193, 200, 50);
        musicVolumeSlider.addChangeListener(this);
        musicVolumeSlider.setOpaque(false);
        musicVolumeSlider.setBackground(new Color(0, 0, 0, 0));

        sfxVolumeLabel = new JLabel("SFX Volume :");
        sfxVolumeLabel.setBounds(170, 300, 200, 30);
        sfxVolumeLabel.setFont(customFont);

        sfxVolumeSlider = new JSlider(0, 100, (int)(AudioManager.getInstance().getSFXVolume() * 100));
        sfxVolumeSlider.setBounds(330, 293, 200, 50);
        sfxVolumeSlider.addChangeListener(this);
        sfxVolumeSlider.setOpaque(false);
        sfxVolumeSlider.setBackground(new Color(0, 0, 0, 0));

        ImageIcon buttonBackgroundIcon = new ImageIcon(getClass().getResource("/resources/button.png"));
        backButton = createCustomButton("Back", buttonBackgroundIcon);
        int buttonWidth = 150;
        int buttonHeight = 50;
        int frameWidth = getWidth();
        backButton.setBounds((frameWidth - buttonWidth) / 2, 380, buttonWidth, buttonHeight);
        backButton.addActionListener(e -> {
            AudioManager.getInstance().playClickSound();
            previousMenu.setVisible(true);
            this.dispose();
        });

        backgroundLabel.add(logoLabel);
        backgroundLabel.add(volumeLabel);
        backgroundLabel.add(musicLabel);
        backgroundLabel.add(musicCheckBox);
        backgroundLabel.add(sfxLabel);
        backgroundLabel.add(sfxCheckBox);
        backgroundLabel.add(musicVolumeSlider);
        backgroundLabel.add(sfxVolumeSlider);
        backgroundLabel.add(sfxVolumeLabel);
        backgroundLabel.add(backButton);

        setContentPane(backgroundLabel);
        setFocusable(true);
        requestFocusInWindow();


        /**Keyboard navigation method from chatGPT**/

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!backButtonSelected) {
                        backButtonSelected = true;
                        updateButtonFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (backButtonSelected) {
                        backButtonSelected = true;
                        updateButtonFocus();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (backButtonSelected) {
                        backButton.doClick();
                    }
                }
            }
        });
        setVisible(true);
    }

    private void updateButtonFocus() {
        if (backButtonSelected) {
            backButton.setForeground(Color.RED);
            backButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            AudioManager.getInstance().playHoverSound();
        } else {
            backButton.setForeground(null);
            backButton.setBorder(null);
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
        addHoverEffect(button);
        return button;
    }

    private void addHoverEffect(JButton button) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalForeground = button.getForeground();

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!backButtonSelected) {
                    button.setForeground(Color.RED);
                    AudioManager.getInstance().playHoverSound();
                    button.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!backButtonSelected && !button.getModel().isPressed()) {
                    button.setForeground(originalForeground);
                    button.setBorder(null);
                }
            }
        });
    }


    /**Slider from chatGPT**/

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == musicVolumeSlider) {
            double volume = musicVolumeSlider.getValue() / 100.0;
            AudioManager.getInstance().setVolume(volume);
        } else if (e.getSource() == sfxVolumeSlider) {
            double volume = sfxVolumeSlider.getValue() / 100.0;
            AudioManager.getInstance().setSFXVolume(volume);
        }
    }

    public static void main(String[] args) {
        new SettingsMenu(new GameMenu()).setVisible(true);
    }
}