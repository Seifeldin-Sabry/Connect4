package game;

import java.sql.*;
import java.util.Scanner;

import static game.Grid.COLUMNS_AMOUNT;
import static game.Grid.ROWS_AMOUNT;

/**
 @author Seifeldin Ismail
 @author Peter Buschenreiter */

public class SaveGame {
	private final Utilities utilities;
	private final Connection connection;
	private final Scanner scanner;
	private final Leaderboard leaderboard;


	public SaveGame(Utilities utilities, Connection connection, Scanner scanner, Leaderboard leaderboard) {
		this.utilities = utilities;
		this.connection = connection;
		this.scanner = scanner;
		this.leaderboard = leaderboard;
		createSaveGameTables();
	}


	/**
	 Creating all tables
	 */
	public void createSaveGameTables() {
		Statement stmt;
		String CreateSql;

		try {
			stmt = connection.createStatement();

			CreateSql = """
					                  
					CREATE SEQUENCE IF NOT EXISTS player_id_seq AS INT
					INCREMENT BY 1
					START WITH 1
					NO MAXVALUE;
					                        
					CREATE SEQUENCE IF NOT EXISTS game_id_seq AS INT
					START WITH 1
					INCREMENT BY 1
					NO MAXVALUE;

					                        
					CREATE TABLE IF NOT EXISTS int_gamesession(
					    game_id INT PRIMARY KEY,
					    last_played_at TIMESTAMP DEFAULT NOW()
					);
					CREATE TABLE IF NOT EXISTS int_player(
					    game_id INT REFERENCES int_gamesession(game_id) ON DELETE CASCADE,
					    player_id INT PRIMARY KEY,
					    name VARCHAR(20) NOT NULL
					);
					CREATE TABLE IF NOT EXISTS int_score(
					    player_id INT PRIMARY KEY REFERENCES int_player(player_id) ON DELETE CASCADE ,
					    moves INT NOT NULL,
					    game_duration INT NOT NULL
					);
					CREATE TABLE IF NOT EXISTS int_spot(
					    x INT NOT NULL,
					    y INT NOT NULL,
					    sign CHAR(1) DEFAULT NULL,
					    game_id INT REFERENCES int_gamesession(game_id) ON DELETE CASCADE,
					    CONSTRAINT spot_pk PRIMARY KEY (x,y,game_id)
					);
					""";
			stmt.executeUpdate(CreateSql);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error while trying to create save & load tables.");
		}
	}


	/**
	 Ask user for a name and looks for a saved game under that name

	 @param scanner <code>Scanner</code>
	 */
	public void tryToLoadGame() {
		String name;
		GameSession gameSession;
		boolean gamesAvailable;

		utilities.printNewScreen();
		gamesAvailable = printSavedGames();
		if (!gamesAvailable) {
			System.out.println("No saved games found");
			utilities.dotDotDot();
			return;
		}

		System.out.print("Enter your name to look for a saved game: ");
		name = scanner.nextLine();
		gameSession = loadGame(name);

		if (gameSession != null) {
			gameSession.playGame();
		}
	}


	/**
	 Saves the game

	 @param playerName <code>String</code>
	 @param moves      <code>int</code>
	 @param duration   <code>long</code>
	 */
	public void saveGame(String playerName, int moves, int duration, Grid grid) {
		String insertSql;
		PreparedStatement pstmt;
		PreparedStatement pstmt2;

		try {
			checkAndDeletePlayerIfExists(playerName);

			pstmt = connection.prepareStatement("""
					INSERT INTO int_gamesession (game_id)
					VALUES (NEXTVAL('game_id_seq'));

					INSERT INTO int_player (player_id,name,game_id)
					VALUES (NEXTVAL('player_id_seq'),?,CURRVAL('game_id_seq'));

					INSERT INTO int_score (player_id, moves, game_duration)
					VALUES (CURRVAL('player_id_seq'),?,?);

					""");
			pstmt.setString(1, playerName);
			pstmt.setInt(2, moves);
			pstmt.setInt(3, duration);
			pstmt.executeUpdate();


			insertSql = """
					INSERT INTO int_spot (x,y,sign,game_id)
					VALUES (?,?,?,CURRVAL('game_id_seq'))
					""";
			pstmt2 = connection.prepareStatement(insertSql);

			for (int row = 0; row < ROWS_AMOUNT; row++) {
				pstmt2.setInt(2, row);
				for (int col = 0; col < COLUMNS_AMOUNT; col++) {
					pstmt2.setInt(1, col);
					if (grid.getSpot(row, col).getCoin() != null) {
						pstmt2.setString(3, String.valueOf(grid.getSpot(row, col).getCoin().getSign()));
					} else {
						pstmt2.setString(3, null);
					}
					pstmt2.executeUpdate();
				}
			}

			pstmt.close();
			pstmt2.close();
		} catch (SQLException e) {
			System.out.println("Error while trying to save a game.");
		}
	}

	/**
	 Checks if a player exists in the save&load db and deletes that player

	 @param playerName <code>String</code>
	 */
	public void checkAndDeletePlayerIfExists(String playerName) {
		PreparedStatement pstmt1;
		PreparedStatement pstmt2;
		try {
			assert connection != null;
			pstmt1 = connection.prepareStatement("SELECT * FROM int_player WHERE name = ?");
			pstmt1.setString(1, playerName);

			ResultSet rs = pstmt1.executeQuery();
			if (rs.next()) {

				pstmt2 = connection.prepareStatement("""
						DELETE FROM int_gamesession
						USING int_player
						WHERE int_gamesession.game_id = int_player.game_id AND int_player.name = ?
						""");
				pstmt2.setString(1, playerName);
				pstmt2.executeUpdate();
			}
		} catch (SQLException e) {
			System.out.println("Error while trying to delete a saved game.");
		}
	}


	/**
	 Displays names, moves and durations of available games to load.

	 @return <code>boolean</code>: <code>True</code> if at least 1 game available to load, otherwise <code>False</code>
	 */
	public boolean printSavedGames() {
		Statement stmt;
		String presentQuery = """
				         SELECT name, moves, game_duration
				         FROM int_player JOIN int_score USING (player_id)
				         ORDER BY 1;
				""";
		try {
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(presentQuery);

			if (!rs.next()) {
				return false;
			}
			System.out.printf("%35s%10s%10s\n", "Name", "Moves", "Duration");
			System.out.println("-".repeat(55));
			do {
				System.out.printf("%35s%10d%10d\n", rs.getString(1), rs.getInt(2), rs.getInt(3));
			} while (rs.next());
			System.out.println("-".repeat(55));

		} catch (SQLException e) {
			System.out.println("Error while displaying saved games.");
		}
		return true;
	}


	/**
	 Loads a saved game if it exists

	 @param name <code>String</code>: name of the player

	 @return <code>GameSession</code> or <code>null</code> if no game is available to load under that name
	 */
	public GameSession loadGame(String name) {
		PlayerCPU playerCPU = null;
		PlayerHuman playerHuman = null;
		Grid grid = null;

		ResultSet playerQuery;
		PreparedStatement pstmt;

		String searchQuery = """
				SELECT name,game_duration,x,y,sign,moves
				FROM int_player
				JOIN int_spot USING (game_id)
				JOIN int_score USING (player_id)
				WHERE name=?
				""";

		try {
			pstmt = connection.prepareStatement(searchQuery);
			pstmt.setString(1, name);
			playerQuery = pstmt.executeQuery();

			if (!playerQuery.next()) {
				System.out.print("No saved progress");
				utilities.dotDotDot();
			} else {
				grid = new Grid();
				playerHuman = new PlayerHuman(playerQuery.getString("name"), grid, new Score(playerQuery.getInt("moves"), playerQuery.getInt("game_duration")));
				playerCPU = new PlayerCPU(grid, new Score());

				while (playerQuery.next()) {
					if (playerQuery.getString("sign") == null)                  // empty spot
						grid.getSpot(playerQuery.getInt("y"), playerQuery.getInt("x")).setCoin(null);

					else if (playerQuery.getString("sign").equals("O")) {       // Coin of human player
						grid.getSpot(playerQuery.getInt("y"), playerQuery.getInt("x")).setCoin(new Coin(playerHuman));
						grid.addCoin();     // to keep track of amount of coins in the grid
					} else {                                                                // Coin of CPU
						grid.getSpot(playerQuery.getInt("y"), playerQuery.getInt("x")).setCoin(new Coin(playerCPU));
						grid.addCoin();     // to keep track of amount of coins in the grid
					}

				}
			}
			pstmt.close();
		} catch (SQLException e) {
			System.out.println("Error while trying to load a game. ");
		}

		if (grid != null && playerCPU != null)
			return new GameSession(grid, playerHuman, playerCPU, utilities, this, leaderboard);
		else return null;
	}
}
