package game;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 @author Peter Buschenreiter
 */
public class Utilities {
	private final Leaderboard leaderboard;
	private final Scanner scanner;

	public Utilities(Leaderboard leaderboard, Scanner scanner) {
		this.leaderboard = leaderboard;
		this.scanner = scanner;
	}


	/**
	 Asks user to enter their name, removes leading and

	 @return <code>String</code> name of player
	 */
	public String askForName() {
		System.out.print("Type in your name: ");
		String name;
		name = scanner.nextLine().trim();
		while (name.length() > 20 || name.length() == 0) {
			System.out.println("Name has invalid length. Please use between 1 and 20 characters.");
			System.out.print("Type in your name: ");
			name = scanner.nextLine().trim();
		}
		return name;
	}


	/**
	 "Input: " gets printed on screen

	 @param scanner needed to read input

	 @return input as <code>String</code>
	 */
	public String askAndGetInput() {
		System.out.println();
		System.out.print("Input: ");
		return scanner.nextLine();
	}


	/**
	 program waits for specified time

	 @param milliseconds 1000 == 1 second
	 */
	public void sleep(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 Moves the screen up by 25 rows to clear the screen
	 */
	public void printNewScreen() {
		final int MAX_LINES = 35;
		for (int i = 0; i < MAX_LINES; i++) {
			System.out.println();
		}
	}


	/**
	 Prints "Press Enter to continue" and waits for a new line input
	 */
	public void pressEnterToContinue() {
		System.out.println();
		System.out.print("Press Enter to continue ");
		scanner.nextLine();
	}


	/**
	 Prints three dots with half a second in between each dot
	 */
	public void dotDotDot() {
		sleep(500);
		System.out.print(".");
		sleep(500);
		System.out.print(".");
		sleep(500);
		System.out.print(".");
		sleep(500);
		System.out.println();
	}


	public void printLogo() {
		String logo = """
				  ______   ______   .__   __. .__   __.  _______   ______ .___________.    _  _
				 /      | /  __  \\  |  \\ |  | |  \\ |  | |   ____| /      ||           |   | || |
				|  ,----'|  |  |  | |   \\|  | |   \\|  | |  |__   |  ,----'`---|  |----`   | || |_
				|  |     |  |  |  | |  . `  | |  . `  | |   __|  |  |         |  |        |__   _|
				|  `----.|  `--'  | |  |\\   | |  |\\   | |  |____ |  `----.    |  |           | |
				 \\______| \\______/  |__| \\__| |__| \\__| |_______| \\______|    |__|           |_|
				""";
		System.out.println(logo);
	}


	public void printGameOver() {
		String gameOver = """
				  _______      ___      .___  ___.  _______      ______   ____    ____  _______ .______     \s
				 /  _____|    /   \\     |   \\/   | |   ____|    /  __  \\  \\   \\  /   / |   ____||   _  \\    \s
				|  |  __     /  ^  \\    |  \\  /  | |  |__      |  |  |  |  \\   \\/   /  |  |__   |  |_)  |   \s
				|  | |_ |   /  /_\\  \\   |  |\\/|  | |   __|     |  |  |  |   \\      /   |   __|  |      /    \s
				|  |__| |  /  _____  \\  |  |  |  | |  |____    |  `--'  |    \\    /    |  |____ |  |\\  \\----.
				 \\______| /__/     \\__\\ |__|  |__| |_______|    \\______/      \\__/     |_______|| _| `._____|
				                                                                                            \s
				""";
		System.out.println(gameOver);
	}


	public void printYouWin() {
		String youWin = """
				  ____    ____  ______    __    __     ____    __    ____  __  .__   __.\s
				  \\   \\  /   / /  __  \\  |  |  |  |    \\   \\  /  \\  /   / |  | |  \\ |  |\s
				   \\   \\/   / |  |  |  | |  |  |  |     \\   \\/    \\/   /  |  | |   \\|  |\s
				    \\_    _/  |  |  |  | |  |  |  |      \\            /   |  | |  . `  |\s
				      |  |    |  `--'  | |  `--'  |       \\    /\\    /    |  | |  |\\   |\s
				      |__|     \\______/   \\______/         \\__/  \\__/     |__| |__| \\__|\s
				""";
		System.out.println(youWin);
	}


	public void printInstructions() {
		String instructions = """
				Rules and instructions:
				 - Your objective is to be the first to connect 4
						
				 - You can connect four vertically, horizontally, diagonally
				    
				 - The game ends when a player connects four or all squares are filled (draw)
				    
				    
				""";
		printNewScreen();
		System.out.print(instructions);
		pressEnterToContinue();
	}


	/**
	 Prints welcome screen including top 5 leaderboard
	 */
	public void printWelcomeScreen() {
		String options = """ 
				Start a new game..........n
				Load a saved game.........l
				Search the leaderboard....s
				View game instruction.....i
				Exit game.................e
				Delete database...........d
				""";
		printNewScreen();
		printLogo();
		leaderboard.printTop5Scores();
		System.out.println();
		System.out.println();
		System.out.print(options);
	}
}
