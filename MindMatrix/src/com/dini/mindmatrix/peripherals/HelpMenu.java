package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.event.*;

public class HelpMenu extends JFrame {

    private static final long serialVersionUID = 1L;
    private JButton backButton;
    private JTextPane helpTextPane;
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

        helpTextPane = new JTextPane();
        helpTextPane.setBounds(50, 30, 590, 355);
        helpTextPane.setEditable(false);
        helpTextPane.setFocusable(false);
        helpTextPane.setText("What is this game?\nWelcome to Mind Matrix, a mathematical puzzle game that challenges your problem-solving skills!\n\n"
                + "How to Play?\nThe game consists of six equations arranged vertically and horizontally. Each equation may involve addition, "
                + "subtraction, multiplication, or division. Your goal is to find the missing digit.\n\n"
                + "Rules:\nYou have 5 lives. Each incorrect answer will cost you one life. The time limit for each game decreases as you advance "
                + "to harder levels.\n\n"
                + "Objective:\nSolve the equations, find the missing values, and beat the clock! Keep your lives intact as you progress through "
                + "increasingly difficult puzzles.\n");

        backButton = new JButton("Back");
        backButton.setBounds(280, 395, 130, 50);

        helpButtons = new JButton[]{backButton};

        backButton.addActionListener(e -> {
            gameMenu.setVisible(true);
            this.dispose();
        });

        setFocusable(true);
        setLayout(null);
        add(helpTextPane);
        add(backButton);
        setVisible(true);
    }

    public static void main(String[] args) {
        new HelpMenu(new GameMenu()).setVisible(true);
    }
}
