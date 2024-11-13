package com.dini.mindmatrix.peripherals;

import javax.swing.*;
import java.awt.*;

public class About extends JFrame {
    private static final long serialVersionUID = 1L;
    private JButton backButton;
    private JLabel aboutLabel;

    public About(Component parent) {
        setTitle("About");
        setSize(320, 380);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        aboutLabel = new JLabel("<html><div style='text-align: center;'>Mind Matrix - a Puzzle Game<br><br>Version 1.0.0<br><br>Developed by DiniK</div></html>", SwingConstants.CENTER);
        aboutLabel.setVerticalAlignment(SwingConstants.CENTER);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> this.dispose());

        setLayout(new BorderLayout());
        add(aboutLabel, BorderLayout.CENTER);
        add(backButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        new About(null);
    }
}
