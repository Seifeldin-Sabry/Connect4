package game;

import java.util.Scanner;

public class Game {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Database db = new Database(scanner);
		Leaderboard leaderboard = new Leaderboard(db.getConnection());
		Utilities utilities = new Utilities(leaderboard, scanner);
		SaveGame saveGame = new SaveGame(utilities, db.getConnection(), scanner, leaderboard);
		String input;

		while (true) {
			utilities.printWelcomeScreen();

			input = null;
			while (input == null) {
				input = utilities.askAndGetInput();
				switch (input) {
					case "n", "l", "e", "i", "s", "d" -> {}
					default -> {
						System.out.println("Incorrect input.");
						input = null;
					}
				}
			}

			switch (input) {
				case "n" -> {
					utilities.printNewScreen();
					utilities.printLogo();
					String name = utilities.askForName();

					GameSession gameSession = new GameSession(name, utilities, saveGame, leaderboard);
					gameSession.playGame();
				}
				case "e" -> {
					System.out.println("Closing game...");
					db.closeConnection();
					System.exit(0);
				}
				case "l" -> saveGame.tryToLoadGame();
				case "i" -> utilities.printInstructions();
				case "s" -> {
					String name;

					utilities.printNewScreen();
					System.out.print("Enter a name to look for in the leaderboard: ");
					name = scanner.nextLine();
					System.out.println();
					leaderboard.searchPlayer(name);
					utilities.pressEnterToContinue();
				}
				case "d" -> {
					db.deleteData();
					System.out.print("Leaderboard and saved games deleted");
					utilities.dotDotDot();
					leaderboard.createLeaderboardTable();
					saveGame.createSaveGameTables();
				}
			}
		}
	}
}
