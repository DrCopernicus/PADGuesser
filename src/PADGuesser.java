import java.util.ArrayList;
import java.util.Scanner;


public class PADGuesser {
	private Orb[][] board;
	private Scanner scan;
	private static final Orb[] intToOrb = {Orb.RED, Orb.BLUE, Orb.GREEN, Orb.PURPLE, Orb.PINK, Orb.YELLOW};
	private int[] guesses;
	private int[] winningGuesses;
	private double winningCombos;
	private static final int numberOfGuesses = 1000;
	
	public static void main(String[] args) {
		PADGuesser pg = new PADGuesser();
	}
	
	public PADGuesser() {
		board = new Orb[6][5];
		scan = new Scanner(System.in);
		System.out.println("RED = 0, BLUE, GREEN, PURPLE, PINK, YELLOW");
		for (int x = 0; x < board.length; x++) { //input current board
			for (int y = 0; y < board[x].length; y++) {
				System.out.println("x "+x+", y "+y+": ");
				board[x][y] = intToOrb[scan.nextInt()];
			}
		}
		guesses = new int[numberOfGuesses];
		winningGuesses = new int[numberOfGuesses];
		winningCombos = 0;
		findSolution(0); //recursive thing
	}
	
	private void findSolution(int level) {
		if (level == numberOfGuesses && getCombos() > winningCombos) {
			for (int i = 0; i < guesses.length; i++) {
				winningGuesses[i] = guesses[i];
			}
			winningCombos = getCombos();
			return;
		}
		for (int i = 0; i < 3; i++) {
			guesses[level] = (i >= guesses[level-1] ? i : i+1);
			findSolution(level+1);
		}
	}
	
	private double getCombos() {
		boolean[][] analyzed = new boolean[board.length][board[0].length];
		double baseDamage = 0; //running total
		int totalCombos = 0;
		
		for (int x = 0; x < analyzed.length; x++) {
			for (int y = 0; y < analyzed[x].length; y++) {
				if (!analyzed[x][y]) {
					int comboScore = findOneCombo(analyzed, x, y);
					if (comboScore > 0) {
						baseDamage += ((double)(comboScore+1))*0.25;	
					}
				}
			}
		}
		
		return baseDamage * (1.0+(totalCombos-1)*0.25);
	}
	
	//finds a combo at a spot (i.e. chain of orbs), returns the number of orbs inside it
	private int findOneCombo(boolean[][] identifiedBoard, int x, int y) {
		boolean[][] orbsInThisCombo = new boolean[board.length][board[0].length];
		ArrayList<CoordinatePair> coordinatesToSearch = new ArrayList<CoordinatePair>();
		
		while (coordinatesToSearch.size() > 0) {
			if (checkIfCanCombo(x,y,true)) {
				addOrbsHorizontally(x, y, coordinatesToSearch, orbsInThisCombo);
			}
			if (checkIfCanCombo(x,y,false)) {
				addOrbsVertically(x, y, coordinatesToSearch, orbsInThisCombo);
			}
		}
		
		for (int xiter = 0; x < identifiedBoard.length; x++) {
			for (int yiter = 0; y < identifiedBoard[0].length; y++) {
				identifiedBoard[x][y] = identifiedBoard[x][y] || orbsInThisCombo[x][y];
			}
		}
		
		for (int xiter = 0; x < orbsInThisCombo.length; x++) {
			for (int yiter = 0; y < orbsInThisCombo[0].length; y++) {
				orbsInThisCombo[x][y] = identifiedBoard[x][y] || orbsInThisCombo[x][y];
			}
		}
	}
	
	private void addOrbsHorizontally(int x, int y, ArrayList<CoordinatePair> coordinatesToSearch, boolean[][] orbsInThisCombo) {
		int xleft = x-1;
		int xright = x+1;
		while (orbMakesACombo(xleft, y, board[x][y]) && !orbsInThisCombo[xleft][y]) { //go to the left
			orbsInThisCombo[xleft][y] = true;
			coordinatesToSearch.add(new CoordinatePair(xleft,y));
			xleft--;
		}
		while (orbMakesACombo(xright, y, board[x][y]) && !orbsInThisCombo[xright][y]) { //go to the right
			orbsInThisCombo[xright][y] = true;
			coordinatesToSearch.add(new CoordinatePair(xright,y));
			xright++;
		}
		coordinatesToSearch.remove(0);
	}
	
	private void addOrbsVertically(int x, int y, ArrayList<CoordinatePair> coordinatesToSearch, boolean[][] orbsInThisCombo) {
		int yup = y+1;
		int ydown = y-1;
		while (orbMakesACombo(x, ydown, board[x][y]) && !orbsInThisCombo[x][ydown]) { //go down
			orbsInThisCombo[x][ydown] = true;
			coordinatesToSearch.add(new CoordinatePair(x,ydown));
			ydown--;
		}
		while (orbMakesACombo(x, yup, board[x][y]) && !orbsInThisCombo[x][yup]) { //go up
			orbsInThisCombo[x][yup] = true;
			coordinatesToSearch.add(new CoordinatePair(x,yup));
			yup++;
		}
		coordinatesToSearch.remove(0);
	}
	
	private boolean checkIfCanCombo(int x, int y, boolean horizontal) {
		boolean l, l2, r, r2;
		Orb checkColor = board[x][y];
		if (horizontal) {
			l = orbMakesACombo(x+1,y,checkColor);
			l2 = orbMakesACombo(x+2,y,checkColor);
			r = orbMakesACombo(x-1,y,checkColor);
			r2 = orbMakesACombo(x-2,y,checkColor);
		} else {
			l = orbMakesACombo(x,y+1,checkColor);
			l2 = orbMakesACombo(x,y+2,checkColor);
			r = orbMakesACombo(x,y-1,checkColor);
			r2 = orbMakesACombo(x,y-2,checkColor);
		}
		return (r&&r2)||(l&&l2)||(r&&l);
	}
	
	private boolean orbMakesACombo(int x, int y, Orb color) {
		return board[x][y].equals(color)&&x>=0&&x<board.length&&y>=0&&y<board[0].length;
	}
	
	private class CoordinatePair {
		public int x;
		public int y;
		public CoordinatePair(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
