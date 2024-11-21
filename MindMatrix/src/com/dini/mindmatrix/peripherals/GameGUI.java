package com.dini.mindmatrix.peripherals;

import com.dini.mindmatrix.engine.GameEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Timer;
import java.util.TimerTask;

/**Base code from HeartGame by Mark Conard**/

public class GameGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = -107785653906635L;

	JLabel questArea = null;
	JLabel scoreLabel = null;
	GameEngine myGame = null;
	private GameGUI gameGUI;
	BufferedImage currentGame = null;
	JLabel infoLabel = null;
	JButton pauseButton = null;
	JLabel timerLabel = null;
	JLabel levelLabel = null;
	JLabel livesLabel = null;
	Timer gameTimer = null;
	int timeRemaining = 120;
	int level = 1;
	int lives = 5;
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

		ImageIcon imageIcon = new ImageIcon(getClass().getResource("/resources/uuu.png"));
		JLabel imageLabel = new JLabel(imageIcon);
		imageLabel.setBounds(2, -2, imageIcon.getIconWidth(), imageIcon.getIconHeight());
		topPanel.add(imageLabel);

		timerLabel = new JLabel( formatTime(timeRemaining));
		timerLabel.setFont(new Font("Monospaced", Font.BOLD, 15));
		timerLabel.setBounds(30, -8, 100, 30);
		topPanel.add(timerLabel);

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
				AudioManager.getInstance().playHoverSound();
				pauseButton.setIcon(hoverIcon);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				pauseButton.setIcon(pauseIcon);
			}
		});

		pauseButton.addActionListener(e -> {
			AudioManager.getInstance().playClickSound();
			pauseGame();
		});


		rightPanel.add(Box.createVerticalStrut(0));
		rightPanel.add(pauseButton);

		livesLabel = new JLabel(getHearts());
		livesLabel.setBorder(BorderFactory.createEmptyBorder(-5, 0, 0, 0));
		livesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		rightPanel.add(Box.createVerticalStrut(0));
		rightPanel.add(livesLabel);

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

			JButton btn = new JButton(String.valueOf(i)) {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g.create();
					if (getModel().isPressed()) {
						g2d.setColor(new Color(221, 142, 228));
					} else if (getModel().isRollover()) {
						AudioManager.getInstance().playHoverSound();
						GradientPaint hoverPaint = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(96, 150, 223));
						g2d.setPaint(hoverPaint);
					} else {
						GradientPaint bluePaint = new GradientPaint(0, 0, Color.WHITE, getWidth(), getHeight(), new Color(253, 203, 203, 255));
						g2d.setPaint(bluePaint);
					}
					g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 0);

					super.paintComponent(g2d);
					g2d.dispose();
				}

				@Override
				protected void paintBorder(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setColor(new Color(0, 29, 99));
					g2d.setStroke(new BasicStroke(2));
					g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 1, 1);
				}

				@Override
				public boolean isContentAreaFilled() {
					return false;
				}

				@Override
				public Dimension getPreferredSize() {
					return new Dimension(30, 30);
				}
			};

			btn.setFont(customFont.deriveFont(14f));
			btn.setFocusPainted(false);
			numButtons[i] = btn;

			buttonPanel.add(btn);
			btn.addActionListener(this);
			buttonPanel.add(Box.createHorizontalStrut(10));
		}


		/**Keyboard number input method from chatGPT**/

		for (int i = 0; i < numButtons.length; i++) {
			int number = i;
			Action buttonAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					numButtons[number].doClick();
				}
			};

			String keyStrokeName = "pressed " + number;
			AudioManager.getInstance().playClickSound();
			KeyStroke keyStroke = KeyStroke.getKeyStroke(Character.forDigit(number, 10));
			InputMap inputMap = buttonPanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			inputMap.put(keyStroke, keyStrokeName);
			buttonPanel.getActionMap().put(keyStrokeName, buttonAction);
		}



		buttonPanel.add(Box.createHorizontalStrut(20));
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createVerticalStrut(20));
		panel.add(mainPanel, BorderLayout.SOUTH);
		getContentPane().add(panel);

		startTimer();
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


	private void updateHighestScore(int newScore) {
		int loggedInUserId = Login.loggedInUserId;

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try (Connection conn = DatabaseConnection.getConnection()) {
					String selectQuery = "SELECT highest_score FROM users WHERE player_id = ?";
					PreparedStatement selectPs = conn.prepareStatement(selectQuery);
					selectPs.setInt(1, loggedInUserId);
					ResultSet rs = selectPs.executeQuery();

					if (rs.next()) {
						int currentHighestScore = rs.getInt("highest_score");
						if (newScore > currentHighestScore) {
							String updateQuery = "UPDATE users SET highest_score = ? WHERE player_id = ?";
							PreparedStatement updatePs = ((Connection) conn).prepareStatement(updateQuery);
							updatePs.setInt(1, newScore);
							updatePs.setInt(2, loggedInUserId);
							updatePs.executeUpdate();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					SwingUtilities.invokeLater(() -> {
						JOptionPane.showMessageDialog(GameGUI.this, "Error updating the highest score.", "Error", JOptionPane.ERROR_MESSAGE);
					});
				}
				return null;
			}
		};

		worker.execute();
	}

	private static JFrame gameOverFrame = null;

	private int selectedIndex = 1;

	private void gameOver() {
		if (gameOverFrame != null && gameOverFrame.isVisible()) {
			return;
		}
		gameTimer.cancel();
		AudioManager.getInstance().pause();
		AudioManager.getInstance().playGameOverSound();

		updateHighestScore(score);
		gameOverFrame = new JFrame();
		gameOverFrame.setUndecorated(true);

		Dimension gameGUISize = GameGUI.this.getSize();
		gameOverFrame.setSize(gameGUISize);
		gameOverFrame.setLayout(new BorderLayout());

		ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icon.png"));
		gameOverFrame.setIconImage(icon.getImage());

		gameOverFrame.setBackground(new Color(0, 0, 0, 0));

		JPanel backgroundPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(0, 0, 0, 180));
				g.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		backgroundPanel.setOpaque(false);
		backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));

		JLabel gameOverLabel = new JLabel("GAME OVER!", JLabel.CENTER);
		gameOverLabel.setFont(customFont.deriveFont(30f));
		gameOverLabel.setForeground(Color.RED);
		gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton mainMenuButton = new JButton("Main Menu");
		JButton exitButton = new JButton("Exit");
		JButton restartButton = new JButton("Restart");

		applyButtonStyling(mainMenuButton);
		applyButtonStyling(exitButton);
		applyButtonStyling(restartButton);

		mainMenuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		backgroundPanel.add(Box.createVerticalGlue());
		backgroundPanel.add(gameOverLabel);
		backgroundPanel.add(Box.createRigidArea(new Dimension(0, 30)));
		backgroundPanel.add(mainMenuButton);
		backgroundPanel.add(Box.createRigidArea(new Dimension(0, 14)));
		backgroundPanel.add(exitButton);
		backgroundPanel.add(Box.createRigidArea(new Dimension(0, 14)));
		backgroundPanel.add(restartButton);
		backgroundPanel.add(Box.createVerticalGlue());

		gameOverFrame.add(backgroundPanel);

		mainMenuButton.addActionListener(e -> {
			AudioManager.getInstance().playClickSound();
			Point location = gameOverFrame.getLocationOnScreen();
			openGameMenu(location);
			gameOverFrame.dispose();
			if (AudioManager.getInstance().isMusicOn()) {
				AudioManager.getInstance().play();
			}
		});

		exitButton.addActionListener(e -> {
			AudioManager.getInstance().playClickSound();
			System.exit(0);
		});

		restartButton.addActionListener(e -> {
			AudioManager.getInstance().playClickSound();
			Point location = gameOverFrame.getLocationOnScreen();
			resetGame(location);
			gameOverFrame.dispose();
			if (AudioManager.getInstance().isMusicOn()) {
				AudioManager.getInstance().play();
			}
		});

		gameOverFrame.setLocation(GameGUI.this.getLocationOnScreen());
		JButton[] buttons = {mainMenuButton, exitButton, restartButton};
		setButtonNavigation(buttons);

		gameOverFrame.setVisible(true);
		gameOverFrame.requestFocusInWindow();

		java.util.Timer colorChangeTimer = new java.util.Timer();

		colorChangeTimer.scheduleAtFixedRate(new TimerTask() {
			private Color[] colors = {Color.RED, Color.GREEN};
			private int index = 0;

			@Override
			public void run() {
				SwingUtilities.invokeLater(() -> {
					gameOverLabel.setForeground(colors[index]);
				});

				index = (index + 1) % colors.length;
			}
		}, 0, 300);
	}


	/**Keyboard navigation method from chatGPT**/

	private void setButtonNavigation(JButton[] buttons) {
		selectedIndex = -1;

		gameOverFrame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (selectedIndex == -1) {
					selectedIndex = -1;
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

		gameOverFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent e) {
				if (selectedIndex == -1) {
					return;
				}
				updateButtonFocus(selectedIndex, buttons);
			}
		});
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
		gameOverFrame.revalidate();
		gameOverFrame.repaint();
	}

	private void applyButtonStyling(JButton button) {
		ImageIcon buttonIcon = new ImageIcon(getClass().getResource("/resources/PauseMenuButton.png"));

		button.setIcon(buttonIcon);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setHorizontalTextPosition(JButton.CENTER);
		button.setVerticalTextPosition(JButton.CENTER);

		button.setFont(customFont.deriveFont(14f));
		button.setForeground(Color.BLACK);

		button.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				AudioManager.getInstance().playHoverSound();
				button.setForeground(Color.RED);
			}
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setForeground(Color.BLACK);
			}
		});
	}

	private void openGameMenu(Point location) {
		GameMenu gameMenu = new GameMenu();
		gameMenu.setLocation(location);
		gameMenu.setVisible(true);
		this.dispose();
	}

	private void resetGame(Point location) {
		GameGUI newGameGUI = new GameGUI();
		newGameGUI.setLocation(location);
		newGameGUI.setVisible(true);
		this.dispose();
	}

	private void startTimer() {
		gameTimer = new Timer();
		gameTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (!isPaused) {
					timeRemaining--;
					timerLabel.setText( formatTime(timeRemaining));
					if (timeRemaining <= 0) {
						gameOver();
					}
				}
			}
		}, 1000, 1000);
	}

	private void resetTimer() {
		int newTime = 120 - (level - 1) * 5;
		if (newTime < 15) {
			timeRemaining = 15;
		} else {
			timeRemaining = newTime;
		}
		timerLabel.setText(formatTime(timeRemaining));
	}

	private String formatTime(int seconds) {
		int minutes = seconds / 60;
		int secs = seconds % 60;
		return String.format("%02d:%02d", minutes, secs);
	}

	private void advanceLevel() {
		level++;
		levelLabel.setText("Level: " + level);
		resetTimer();
	}

	private void loseLife() {
		lives--;
		livesLabel.setText(getHearts());
		if (lives <= 0) {
			gameOver();
		}
	}

	private String getHearts() {
		StringBuilder hearts = new StringBuilder("<html>");
		int totalHearts = 5;

		for (int i = 0; i < totalHearts - lives; i++) {
			hearts.append("<span style='color:gray; font-size: 12px;'>♥ </span>");
		}
		for (int i = 0; i < lives; i++) {
			hearts.append("<span style='color:#ff5d00; font-size: 12px;'>♥ </span>");
		}

		hearts.append("</html>");
		return hearts.toString();
	}

	private void pauseGame() {
		isPaused = true;
		gameTimer.cancel();
		new PauseMenu(this);
	}

	public void resumeGame() {
		isPaused = false;
		startTimer();
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
			AudioManager.getInstance().playSound("correct");

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
			loseLife();
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
		AudioManager.getInstance().playWrongSound();

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
