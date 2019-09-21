import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * GUI for playing a TicTacToeGame.
 * @author mvail
 */
public class TicTacToeGUI extends JPanel {
	private static final long serialVersionUID = 1L;
	private final String PLAYER = "X";
	private final String COMPUTER = "O";
	private final String OPEN = "";
	private final String TIE = "T";
	private final int DIM = 3;
	private JButton[][] gameGrid;
	private JTextArea movesTextArea;
	private JButton newGameButton;
	private TicTacToeGame game;
	private int gameCount;
	private ArrayList<String> gameStat;
	private JButton statsButton;
	private JOptionPane gameBar;
	
	/** Initialize the GUI. */
	public TicTacToeGUI() {
		game = new TicTacToeGame();
		gameCount = 0;
		gameStat = new ArrayList<String>();
		Font bigFont = new Font("Serif", Font.PLAIN, 48);
		Font smallFont = new Font("Serif", Font.PLAIN, 36);
		gameGrid = new JButton[DIM][DIM];
		ButtonListener buttonListener = new ButtonListener();
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				gameGrid[row][col] = new JButton(OPEN);
				gameGrid[row][col].addActionListener(buttonListener);
				gameGrid[row][col].setPreferredSize(new Dimension(128,128));
				gameGrid[row][col].setFont(bigFont);
			}
		}
		
		this.setLayout(new BorderLayout());
		
		JPanel controlsPanel = new JPanel(); //default FlowLayout
		newGameButton = new JButton("New Game");
		newGameButton.setFont(bigFont);
		newGameButton.addActionListener(buttonListener);
		controlsPanel.add(newGameButton);
		statsButton = new JButton("Game Stats");
		statsButton.setFont(bigFont);
		statsButton.setBackground(Color.CYAN);
		statsButton.addActionListener(new StatsButtonListener());
		controlsPanel.add(statsButton);
		this.add(controlsPanel, BorderLayout.SOUTH);
		
		
		JPanel movesPanel = new JPanel(); //vertical BoxLayout
		movesPanel.setLayout(new BoxLayout(movesPanel, BoxLayout.Y_AXIS));
		JLabel movesLabel = new JLabel("Moves");
		movesLabel.setFont(smallFont);
		movesPanel.add(movesLabel);
		movesTextArea = new JTextArea();
		movesTextArea.setPreferredSize(new Dimension(256,512));
		movesTextArea.setFont(smallFont);
		movesTextArea.setEnabled(false);
		movesPanel.add(movesTextArea);
		this.add(movesPanel, BorderLayout.EAST);
		
		JPanel gamePanel = new JPanel();
		gamePanel.setLayout(new GridLayout(DIM,DIM));
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				gamePanel.add(gameGrid[row][col]);
			}
		}
		this.add(gamePanel, BorderLayout.CENTER);
	}
	
	/** Reset the game and corresponding GUI controls. */
	private void resetGame() {
		//reset the game
		game.newGame();
		//clear the GUI board
		for(int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				gameGrid[row][col].setText(OPEN);
				gameGrid[row][col].setEnabled(true);
				gameGrid[row][col].setBackground(null);
			}
		}
		//clear visual list
		movesTextArea.setText("");
		//refresh GUI
		revalidate();
	}
	
	/** Disable GUI game board and display game results */
	private void endGame() {
		//disable game board buttons
		for (int row = 0; row < DIM; row++) {
			for (int col = 0; col < DIM; col++) {
				gameGrid[row][col].setEnabled(false);
			}
		}
		//display the moves history and winner
		movesTextArea.setText("");
		Point[] moves = game.getMoves();
		for (int i = 0; i < moves.length; i++) {
			if (i %2 == 0) {
				movesTextArea.append("X:");
			} else {
				movesTextArea.append("O:");
			}
			movesTextArea.append("row " + moves[i].x + ", col " + moves[i].y + "\n"); 
		}
		if (game.winner() == TicTacToe.Winner.TIE) {
			movesTextArea.append("NO WINNER \n");
			gameStat.add("Game " + gameCount + " | Winner: NO WINNER\n");
			gameCount++;
		} else if (game.winner() == TicTacToe.Winner.X) {
			movesTextArea.append("Winner: X \n");
			gameStat.add("Game " + gameCount + " | Winner: X \n");
			gameCount++;
		} else if (game.winner() == TicTacToe.Winner.O) {
			movesTextArea.append("Winner: O \n");
			gameStat.add("Game " + gameCount + " | Winner: O\n");
			gameCount++;
		} else {
			movesTextArea.append("ERROR\n");
		}
	}
	
	/** Start the GUI
	 * @param args unused
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame("Tic Tac Toe");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new TicTacToeGUI());
		frame.pack();
		frame.setVisible(true);
	}
	
	/**
	 * Private inner class to respond to game button clicks.
	 * Update 'gameGrid' if the button is not already claimed.
	 * Make a computer move after player moves.
	 * Check for game over conditions.
	 */
	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			JButton button = (JButton)(arg0.getSource());
			if (button == newGameButton) {
				resetGame();
			} else {
				if (game.gameOver()) {
					endGame();
				} else {
					//call choose(X, row, col) corresponding to clicked button
					for (int row = 0; row < DIM; row++) {
						for (int col = 0; col < DIM; col++) {
							if (button == gameGrid[row][col]) {
								//if the position is successfully claimed
								if (game.choose(TicTacToe.Player.X, row, col)) {
									button.setText(PLAYER);
									button.setBackground(Color.RED);
									button.setForeground(Color.BLACK);
									if (game.gameOver()) { //did the player just win?
										endGame();
									} else { //make a random move for the computer
										Random rand = new Random();
										boolean done = false;
										while (!done) {
											int cRow = rand.nextInt(DIM);
											int cCol = rand.nextInt(DIM);
											if (game.choose(TicTacToe.Player.O, cRow, cCol)) {
												gameGrid[cRow][cCol].setText(COMPUTER);
												gameGrid[cRow][cCol].setBackground(Color.GREEN);
												gameGrid[cRow][cCol].setForeground(Color.BLACK);
												done = true;
											}
										}
										if (game.gameOver()) { //did the computer just win?
											endGame();
										}
									}
								}//if player clicked an unclaimed position
							}//if this is the button that was pressed
						}//for col
					}//for row
				}//if game not over
			}//if game board button pressed
		}//actionPerformed()
	}//ButtonListener
	private class StatsButtonListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			String gameStatString = "";
			System.out.println("stats");
			for (int i=0; i<gameStat.size(); i++) {
			 gameStatString += gameStat.get(i) + "\n";
			}
			gameBar = new JOptionPane();
			gameBar.showMessageDialog(null, gameStatString, "Stats", JOptionPane.INFORMATION_MESSAGE);
			
		}
		
	}
}