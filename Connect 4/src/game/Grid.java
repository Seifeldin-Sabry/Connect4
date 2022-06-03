package game;

/**
 @author Peter Buschenreiter
 */
public class Grid {
	public static final int ROWS_AMOUNT = 6;
	public static final int COLUMNS_AMOUNT = 7;
	private static final char WALL = '|';
	private final Spot[][] spots = new Spot[ROWS_AMOUNT][COLUMNS_AMOUNT];
	private int amountOfCoins = 0;
	private final String[] controls = {
			"\t\t\tControls:",
			"\t\t\tSave...................s",
			"\t\t\tExit...................e",
			"\t\t\tInstructions...........i",
			"\t\t\tReturn to main menu....q",
			"\t\t\tChoose column........1-7",
	};

	public Grid() {
		for (int row = 0; row < ROWS_AMOUNT; row++) {
			for (int col = 0; col < COLUMNS_AMOUNT; col++) {
				spots[row][col] = new Spot(row, col, null);
			}
		}
	}

	public boolean checkWin() {
		int[][] directions = { { 0, -1 }, { 1, -1 }, { 1, 0 }, { 1, 1 } };      // int[col][row] -> up, up right, right, down right

		for (int row = 0; row < ROWS_AMOUNT; row++) {               // for every row
			for (int col = 0; col < COLUMNS_AMOUNT; col++) {        // for every column
				Coin coin = getSpot(row, col).getCoin();            // get Coin of current Spot
				if (coin == null) continue;                         // if no Coin in Spot, continue to next Spot
				char sign = coin.getSign();                         // get sign of current Spot
				for (int[] direction : directions) {
					boolean winnerFound = true;                     // reset variables for every direction
					int nextRow = row;
					int nextCol = col;
					for (int i = 0; i < 3; i++) {                   // check up to three neighbors in the same direction
						nextRow += direction[1];                    // go to neighboring Spot
						nextCol += direction[0];

						// out of bounds check
						if (nextRow < 0 || nextRow >= ROWS_AMOUNT || nextCol < 0 || nextCol >= COLUMNS_AMOUNT) {
							winnerFound = false;
							break;
						}

						// check if Coin in Spot
						Coin nextCoin = getSpot(nextRow, nextCol).getCoin();
						if (nextCoin == null) {
							winnerFound = false;
							break;
						}

						// check if nextSign is the same as sign of original Spot
						char nextSign = nextCoin.getSign();
						if (sign != nextSign) {
							winnerFound = false;
							break;
						}
					}

					// after 3 neighbors in the same direction have been checked we found a winner and return true
					if (winnerFound) {
						System.out.println("Winner found!");
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 Checks if there is still space in the grid.

	 @return <code>Boolean</code>
	 */
	public boolean gridHasSpace() {
		return amountOfCoins < 42;
	}

	/**
	 Adds 1 to the coin counter of grid keeping track of the amount of coins in it.
	 */
	public void addCoin() {
		amountOfCoins++;
	}

	public Spot getSpot(int row, int col) {
		return spots[row][col];
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		for (int row = 0; row < ROWS_AMOUNT; row++) {
			for (int col = 0; col < COLUMNS_AMOUNT; col++) {
				stringBuilder.append(String.format("%c %s ", WALL, getSpot(row, col).toString()));
			}
			stringBuilder.append(WALL).append(controls[row]).append("\n");
		}
		for (int i = 1; i <= COLUMNS_AMOUNT; i++) {
			stringBuilder.append(String.format("  %d ", i));
		}
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

}
