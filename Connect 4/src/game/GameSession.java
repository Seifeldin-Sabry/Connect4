package game;

/**
 @author Peter Buschenreiter
 */
public class GameSession {
	private final Grid grid;
	private boolean winner = false;
	private boolean continuePlaying = true;
	PlayerHuman playerHuman;
	PlayerCPU playerCPU;
	private final Utilities utilities;
	private final SaveGame saveGame;
	private final Leaderboard leaderboard;


	/**
	 Constructor for new games

	 @param name <code>String</code> Name of the player
	 */
	public GameSession(String name, Utilities utilities, SaveGame saveGame, Leaderboard leaderboard) {
		grid = new Grid();
		playerHuman = new PlayerHuman(name, grid, new Score());
		playerCPU = new PlayerCPU(grid, new Score());
		this.utilities = utilities;
		this.saveGame = saveGame;
		this.leaderboard = leaderboard;
	}


	/**
	 Constructor for loaded games

	 @param grid        <code>Grid</code>
	 @param playerHuman <code>PlayerHuman</code>
	 @param playerCPU   <code>PlayerCPU</code>
	 */
	public GameSession(Grid grid, PlayerHuman playerHuman, PlayerCPU playerCPU, Utilities utilities, SaveGame saveGame, Leaderboard leaderboard) {
		this.grid = grid;
		this.playerHuman = playerHuman;
		this.playerCPU = playerCPU;
		this.utilities = utilities;
		this.saveGame = saveGame;
		this.leaderboard = leaderboard;
	}


	/**
	 Plays an entire game
	 */
	public void playGame() {
		String input;

		gameLoop:
		while (grid.gridHasSpace()) {
			utilities.printNewScreen();
			System.out.println(grid);
			System.out.printf("%s's turn\n", playerHuman.getNAME());

			//      playerHuman's turn
			boolean turnComplete = false;
			while (!turnComplete) {
				input = utilities.askAndGetInput();
				continuePlaying = checkAndDoSideAction(input);
				if (!continuePlaying) break gameLoop;

				Integer columnChoice = getNumber(input);
				turnComplete = isInBounds(columnChoice);
				if (turnComplete) {
					boolean colFull = playerHuman.dropCoin(columnChoice);
					if (colFull) {
						System.out.println("Column already full!");
						turnComplete = false;
					}
				}
			}

			utilities.printNewScreen();
			System.out.println(grid);

			//      check if playerHuman won
			winner = grid.checkWin();
			if (winner) {
				System.out.printf("Player %s won!\n", playerHuman.getNAME());
				utilities.printYouWin();
				utilities.pressEnterToContinue();
				leaderboard.insertToLeaderboard(playerHuman.getNAME(), playerHuman.getMoves(), playerHuman.getDuration());
				saveGame.checkAndDeletePlayerIfExists(playerHuman.getNAME());
				break;
			}

			//      CPU's turn
			System.out.printf("\n%s's turn\n\n", playerCPU.getNAME());
			System.out.print("Calculating best move");
			utilities.dotDotDot();
			playerCPU.dropCoin();

			//      check if CPU won
			winner = grid.checkWin();
			if (winner) {
				System.out.printf("%s won!\n", playerCPU.getNAME());
				utilities.printGameOver();
				saveGame.checkAndDeletePlayerIfExists(playerHuman.getNAME());
				utilities.pressEnterToContinue();
				break;
			}
		}

		//      no winner & no back to main menu command was given --> TIE
		if (!winner && continuePlaying) {
			utilities.printNewScreen();
			System.out.println(grid);
			System.out.println();
			System.out.print("Game tied");
			utilities.dotDotDot();
			System.out.println(" Better luck next time!");
			saveGame.checkAndDeletePlayerIfExists(playerHuman.getNAME());
			utilities.pressEnterToContinue();
		}
	}


	/**
	 Checks not null and within boundaries (1-7)

	 @param val <code>Integer</code>

	 @return <code>boolean</code>
	 */
	public boolean isInBounds(Integer val) {
		return val != null && val > 0 && val < 8;
	}


	/**
	 Tries to parse a <code>String</code> into an <code>Integer</code>. Returns <code>NULL</code> otherwise

	 @param input user input as <code>String</code>

	 @return <code>Integer</code> or <code>NULL</code>
	 */
	public Integer getNumber(String input) {
		try {
			return Integer.parseInt(input);
		} catch (NumberFormatException e) {
			return null;
		}
	}


	/**
	 Performs one of the secondary game actions like saving, printing instructions, ending the game and returning to main menu

	 @param input <code>String</code>
	 */
	public boolean checkAndDoSideAction(String input) {
		switch (input) {
			case "s" -> {
				saveGame.saveGame(playerHuman.getNAME(), playerHuman.getMoves(), playerHuman.getDuration(), grid);
				System.out.print("Saving Game");
				utilities.dotDotDot();
				System.out.print("Game Saved! Returning to Main Menu");
				utilities.dotDotDot();
				return false;
			}
			case "i" -> {
				utilities.printInstructions();
				utilities.printNewScreen();
				System.out.println(grid);
				return true;
			}
			case "e" -> {
				System.out.println("Closing game...");

				System.exit(0);
			}
			case "q" -> {
				System.out.print("Returning to main menu");
				utilities.dotDotDot();
				return false;
			}
		}
		return true;
	}
}
