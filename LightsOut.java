import java.awt.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
public class LightsOut extends JFrame {
	private final int GRID_SIZE = 5;
	private final int WINDOW_WIDTH = 700; // Window width
	private final int WINDOW_HEIGHT = 650;

	private Panel NorthPanel;
	private Panel BoardPanel;
	private Panel SouthPanel;	

	private GridButton[][] Board;
	private JButton ActionButton;
	private JLabel Status;
	private JButton Solve;
	private JButton Undo;

	private ArrayList<Integer> AllRowMoves;
	private ArrayList<Integer> AllColMoves;

	private Timer SolveTimer;
	private Timer BugPrevention;
	private boolean recordMove;
	private boolean gameBegin;
	private int rowSolver;				//Used in Solve function

	public LightsOut(){
		init();
		boardLayout();
		BugPrevention.start(); //Does not allow the user to use the other buttons while the solver is running
	}
	/*
	 * Boring Stuff
	 */
	private void init(){
		Board = new GridButton[GRID_SIZE][GRID_SIZE];

		BoardPanel = new Panel();
		SouthPanel = new Panel();
		NorthPanel = new Panel();

		BugPrevention = new Timer(0, new EventsTrafficer());
		SolveTimer = new Timer(250, new SolveTimerListener());

		AllRowMoves = new ArrayList<Integer>();
		AllColMoves = new ArrayList<Integer>();

		gameBegin = true;
		recordMove = true;

		rowSolver = 1;

		//Botton Row Buttons init
		ActionButton = new JButton("Scramble");
		Undo = new JButton("Undo");
		Solve = new JButton("Solve");
	}
	private void boardLayout(){
		setTitle("Lights Out");
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		BoardPanel.setLayout(new GridLayout(GRID_SIZE,GRID_SIZE));
		SouthPanel.setLayout(new FlowLayout());
		NorthPanel.setLayout(new FlowLayout());
		Status = new JLabel("Press Scramble to Start");
		NorthPanel.add(Status);
		//GridButton init
		for (int i = 0; i < GRID_SIZE; i++){
			for (int j = 0; j < GRID_SIZE; j++){
				Board[i][j] = new GridButton(i,j);
				Board[i][j].addActionListener(new BoardListener());
				BoardPanel.add(Board[i][j]);
			}
		}
		SouthPanel.add(ActionButton);
		SouthPanel.add(Solve);
		SouthPanel.add(Undo);
		Solve.addActionListener(new SolveButtonListener());
		ActionButton.addActionListener(new ButtonListener());
		Undo.addActionListener(new UndoListener());
		//Panel addition
		add(NorthPanel, BorderLayout.NORTH);
		add(BoardPanel, BorderLayout.CENTER);
		add(SouthPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	private void toggleButtons(GridButton buttonClicked){
		int c = buttonClicked.getCol();
		int r = buttonClicked.getRow();
		buttonClicked.buttonPressed();
		buttonClicked.toggleLit();
		toggle(buttonClicked.getRow(), buttonClicked.getCol());
		gameBegin = false;
		if (recordMove){
			AllRowMoves.add(r);			//Disables the Undo
			AllColMoves.add(c);
		}
	}
	private void toggle(int r, int c){
		toggleUp(r , c);
		toggleDown(r , c);
		toggleLeft(r , c);
		toggleRight(r , c);
	}
	private void toggleUp(int r, int c){
		if (r>0){
			Board[r-1][c].toggleLit();
		}
	}
	private void toggleDown(int r, int c){
		if (r<4){
			Board[r+1][c].toggleLit();
		}
	}
	private void toggleRight(int r, int c){
		if (c != 0){
			Board[r][c-1].toggleLit();
		}
	}
	private void toggleLeft (int r, int c){
		if (c != 4){
			Board[r][c+1].toggleLit();
		}
	}

	/*
	 * Returns false if the game is not over
	 */
	private boolean isGameOver(){
		int r = 0;
		int c = 0;
		while ((r != 4 || c != 4) && !Board[r][c].isLit()){
			c++;
			r = r + c/5;
			c%=5;
		}
		if(r == Board.length - 1 && c == Board[r].length-1){
			resetMoveRecord();
			return true;
		}
		else {
			return false;
		}
	}
	/*
	 * Deletes the data used for Undo so the User cannot go back into the last game
	 * Called in isGameOver()
	 */
	private void resetMoveRecord(){
		AllRowMoves = new ArrayList<Integer>();
		AllColMoves = new ArrayList<Integer>();
		recordMove = true;
	}

	/*
	 * Called in SolveTimerListener
	 * Triggered in SolveButton Listener 
	 * Repeats until the Game is solved
	 */
	private void Solve(){
		System.out.println(rowSolver);
		if (rowSolver < GRID_SIZE && nextPos(rowSolver) >= 0 && nextPos(rowSolver) < 5){
			toggleButtons(Board[rowSolver][nextPos(rowSolver)]);
		}
		if (nextPos(rowSolver) == -1){
			rowSolver++;
		}
		if (!isGameOver() && rowSolver > 4){
			rowSolver = 1;
			lastRow (Board[4]);
		}
		else if (isGameOver()){
			rowSolver = 1;
			SolveTimer.stop();
			Solve.setText("Solve");
		}
	}

	/* 
	 * finds the next column position in which is needed in order to Solve the game
	 * Called by Solve
	 * Returns -1 if the row is finished
	 */
	private int nextPos(int row){
		int pos = 0;
		while (rowSolver < GRID_SIZE && pos < GRID_SIZE && !Board[rowSolver-1][pos].isLit()){
			pos ++;
		}
		if (pos == 5){
			pos = -1;
		}
		return pos;
	}

	/*
	 * Used when Solve gets to the end of the board
	 * Toggles the first row of buttons in order for the board to be solved
	 * Called by Solve()
	 */
	private boolean lastRow(GridButton[] buttons){
		boolean[] litButtons = new boolean[buttons.length];
		boolean output = true;
		for (int i = 0; i < litButtons.length; i++){
			if (buttons[i].isLit()){
				litButtons[i] = true;
			}
		}
		if (litButtons[0] && litButtons[1] && litButtons[3] && litButtons[4]){
			toggleButtons(Board[0][2]);
		}

		else if (litButtons[0] && litButtons[1] && litButtons[2]){
			toggleButtons(Board[0][1]);
		}
		else if (litButtons[2] && litButtons[3] && litButtons[4]){
			toggleButtons(Board[0][3]);
		}		
		else if (litButtons[0] && litButtons[2] && litButtons[3]){
			toggleButtons(Board[0][4]);
		}
		else if (litButtons[1] && litButtons[2] && litButtons[4]){
			toggleButtons(Board[0][0]);
		}
		else if (litButtons[0] && litButtons[4]){
			toggleButtons(Board[0][0]);
			toggleButtons(Board[0][1]);
		}
		else if (litButtons[1] && litButtons[3]){
			toggleButtons(Board[0][0]);
			toggleButtons(Board[0][3]);
		}

		else{
			output = false;
		}
		return output;
	}

	private class BoardListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			GridButton buttonClicked = (GridButton)e.getSource();
			toggleButtons(buttonClicked);
			if (isGameOver()){
				Status.setText("Game Over");
			}
			else{
				Status.setText(" ");
			}
		}
	}

	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			recordMove=false;
			for (int i = 0; i < Math.random()*30 + 10; i++){
				int c = (int) (Math.random()*(GRID_SIZE));
				int r = (int) (Math.random()*(GRID_SIZE));
				toggleButtons(Board[r][c]);
			}
			resetMoveRecord();
			recordMove=true;
			Status.setText("Make all the Buttons Yellow");
		}
	}

	private class SolveButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if (!SolveTimer.isRunning() && !gameBegin){
				SolveTimer.start();
				Solve.setText("Stop Solve");
			} else{
				Solve.setText("Solve");
				SolveTimer.stop();
			}
		}
	}

	private class SolveTimerListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			Solve();
		}
	}

	/*
	 * Controlled by BugPrevention Timer
	 * Turns off Undo and ActionButton if the Solver is running
	 * If Solver is not running it displays Undo and Solver
	 * It also controls the Status Banner if the Game is Over
	 */
	private class EventsTrafficer implements ActionListener {
		public void actionPerformed(ActionEvent e){
			if (SolveTimer.isRunning()){
				Undo.setVisible(false);
				ActionButton.setVisible(false);
				Status.setText("Solving");
			}
			else {
				Undo.setVisible(true);
				ActionButton.setVisible(true);
			}
			if (isGameOver() && !gameBegin){
				Status.setText("Game Over! Play Again");
			}
		}
	}
	private class UndoListener implements ActionListener {
		public void actionPerformed(ActionEvent e){
			AllRowMoves.trimToSize();
			AllColMoves.trimToSize();
			if (AllRowMoves.size() > 0){
				recordMove = false;
				int r = AllRowMoves.get(AllRowMoves.size()-1);
				int c = AllColMoves.get(AllColMoves.size()-1);
				toggleButtons(Board[r][c]);
				AllRowMoves.remove(AllColMoves.size()-1);
				AllColMoves.remove(AllColMoves.size()-1);
				recordMove = true;
			}
			else{
				Undo.setVisible(false);
			}
		}
	}
}
