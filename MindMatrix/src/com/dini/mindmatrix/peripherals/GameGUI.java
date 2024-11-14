package com.dini.mindmatrix.peripherals;

import com.dini.mindmatrix.engine.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**Base code from HeartGame by Mark Conard**/

public class GameGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -107785653906635L;

	JLabel questArea = null;
	JLabel scoreLabel = null;
	GameEngine myGame = null;
	BufferedImage currentGame = null;
	JLabel infoLabel = null;
	JLabel levelLabel = null;
	JButton pauseButton = null;
	int level = 1;
	int score = 0;
	boolean isPaused = false;
	Font customFont = null;

	/**
	 * Initializes the game.
	 *
	 * @param player
	 */
	private void initGame(String player) {
		setSize(690, 520);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Mind Matrix");

		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
		setIconImage(icon.getImage());

		loadCustomFont();

		JPanel panel = new JPanel(new BorderLayout());
		myGame = new GameEngine(player);
		currentGame = myGame.nextGame();

		JPanel topPanel = new JPanel(new BorderLayout());


		levelLabel = new JLabel("Level: " + level);
		levelLabel.setFont(customFont.deriveFont(16f));
		levelLabel.setBounds(300, 4, 100, 30);
		topPanel.add(levelLabel, BorderLayout.CENTER);

		scoreLabel = new JLabel("Score: " + score);
		scoreLabel.setFont(customFont.deriveFont(10f));
		scoreLabel.setBorder(BorderFactory.createEmptyBorder(16, 5, 0, 0));
		topPanel.add(scoreLabel);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		ImageIcon pauseIcon = new ImageIcon(getClass().getResource("/resources/ttt.png"));
		ImageIcon hoverIcon = new ImageIcon(getClass().getResource("/resources/77.png"));

		pauseButton = new JButton(pauseIcon);
		pauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		pauseButton.setContentAreaFilled(false);
		pauseButton.setBorderPainted(false);
		pauseButton.setFocusPainted(false);
		pauseButton.setMargin(new Insets(-2, 53, 0, 0));
		pauseButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		pauseButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				pauseButton.setIcon(hoverIcon);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				pauseButton.setIcon(pauseIcon);
			}
		});

		pauseButton.addActionListener(e -> {
			pauseGame();
		});


		rightPanel.add(Box.createVerticalStrut(0));
		rightPanel.add(pauseButton);


		rightPanel.add(Box.createVerticalStrut(0));


		topPanel.add(rightPanel, BorderLayout.EAST);
		panel.add(topPanel, BorderLayout.NORTH);
		JPanel centerPanel = new JPanel(new BorderLayout());

		ImageIcon ii = new ImageIcon(currentGame);
		questArea = new JLabel(ii);
		JScrollPane questPane = new JScrollPane(questArea);
		questPane.setBorder(BorderFactory.createEmptyBorder());
		centerPanel.add(questPane, BorderLayout.CENTER);

		infoLabel = new JLabel("What is the missing digit?");
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoLabel.setFont(customFont.deriveFont(14f));
		infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		centerPanel.add(infoLabel, BorderLayout.SOUTH);
		panel.add(centerPanel, BorderLayout.CENTER);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		JButton[] numButtons = new JButton[10];

		buttonPanel.add(Box.createHorizontalStrut(20));



		for (int i = 0; i < 10; i++) {
			JButton btn = new JButton(String.valueOf(i));

			btn.setFont(customFont.deriveFont(14f));
			btn.setFocusPainted(false);
			numButtons[i] = btn;

			buttonPanel.add(btn);
			btn.addActionListener(this);
			buttonPanel.add(Box.createHorizontalStrut(10));
		}

		buttonPanel.add(Box.createHorizontalStrut(20));
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createVerticalStrut(20));
		panel.add(mainPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);

	}

	private void loadCustomFont() {
		try {
			InputStream is = getClass().getResourceAsStream("/resources/rog.otf");
			customFont = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			ge.registerFont(customFont);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void advanceLevel() {
		level++;
	}


	private void pauseGame() {
		isPaused = true;
		new PauseMenu(this);
	}

	public void resumeGame() {
		isPaused = false;
	}

	/**
	 * Default player is null.
	 */
	public GameGUI() {
		super();
		initGame(null);
	}

	/**
	 * Use this to start GUI, e.g., after login.
	 *
	 * @param player
	 */
	public GameGUI(String player) {
		super();
		initGame(player);
	}

	/**
	 * Main entry point into the equation game.
	 *
	 * @param args not used.
	 */
	public static void main(String[] args) {
		GameGUI myGUI = new GameGUI();
		myGUI.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		int solution = Integer.parseInt(e.getActionCommand());
		boolean correct = myGame.checkSolution(currentGame, solution);

		if (correct) {
			score++;
			scoreLabel.setText("Score: " + score);

			questArea.setVisible(false);

			ImageIcon correctGif = new ImageIcon(getClass().getResource("/resources/rrt.gif"));
			questArea.setIcon(correctGif);

			questArea.setPreferredSize(new Dimension(200, 150));
			questArea.setSize(questArea.getPreferredSize());
			questArea.setMinimumSize(new Dimension(200, 150));
			questArea.setMaximumSize(new Dimension(200, 150));

			questArea.setVisible(true);

			new Thread(() -> {
				try {
					Thread.sleep(1700);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				SwingUtilities.invokeLater(() -> {
					advanceLevel();
					currentGame = myGame.nextGame();
					ImageIcon ii = new ImageIcon(currentGame);
					questArea.setIcon(ii);
					questArea.setVisible(true);
				});
			}).start();
		} else {
			flashRedTint();
		}
	}


	/**Flashing window method from chatGPT**/

	private void flashRedTint() {
		JPanel flashPanel = new JPanel();
		flashPanel.setBackground(new Color(255, 0, 0, 150));
		flashPanel.setBounds(0, 0, 690, 520);

		this.getContentPane().add(flashPanel);
		this.getContentPane().setComponentZOrder(flashPanel, 0);
		this.getContentPane().repaint();
		this.getContentPane().revalidate();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			int flashCount = 0;

			@Override
			public void run() {
				if (flashCount < 4) {
					if (flashCount % 2 == 0) {
						flashPanel.setVisible(true);
					} else {
						flashPanel.setVisible(false);
					}
					flashCount++;
				} else {
					flashPanel.setVisible(false);
					GameGUI.this.getContentPane().remove(flashPanel);
					GameGUI.this.getContentPane().revalidate();
					GameGUI.this.getContentPane().repaint();
					timer.cancel();
				}
			}
		}, 0, 80);
	}
}
