import java.sql.*;


public class SaveGame {
    public static void main(String[] args) {
        Leaderboard.createLeaderboardTable();
        createALLTABLES();
        saveGame("Peter",5,23);
    }




    public static void createALLTABLES(){
        /*
          These are for executing queries through jdbc
         */
        Statement stmt = null;
        String CreateSql = null;

        /*
          This is the login info for the Database
         */
        String jdbc = "jdbc:postgresql://localhost:5432/Connect 4 Database";
        String username = "postgres";
        String password = "Student_1234";

        try {
            Connection connection = DriverManager.getConnection(jdbc, username, password);
            stmt = connection.createStatement();

            CreateSql = """
                        
                        CREATE SEQUENCE IF NOT EXISTS player_id_seq
                        increment by 1
                        Start with 1
                        NO MAXVALUE;
                        
                        CREATE SEQUENCE IF NOT EXISTS game_id_seq
                        increment by 1
                        Start with 1
                        NO MAXVALUE;
                        
                        CREATE SEQUENCE IF NOT EXISTS score_id_seq
                        increment by 1
                        Start with 1
                        NO MAXVALUE;
                
                        
                        CREATE TABLE IF NOT EXISTS INT_gameSession(
                            game_id INT primary key,
                            last_played_at timestamp default now()
                        );
                        CREATE TABLE IF NOT EXISTS INT_score(
                            score_id INT primary key,
                            moves INT,
                            game_duration INT
                        );
                        CREATE TABLE IF NOT EXISTS INT_player(
                            game_id INT references INT_gameSession(game_id) ON DELETE CASCADE,
                            player_id INT primary key,
                            name VARCHAR(20),
                            score_id INT references INT_score(score_id) ON DELETE CASCADE
                        );
                        
                        CREATE TABLE IF NOT EXISTS INT_spot(
                            x INT,
                            y INT,
                            game_id INT references INT_gameSession(game_id) ON DELETE CASCADE,
                            CONSTRAINT spot_pk PRIMARY KEY (x,y,game_id)
                        );
                        """;
            stmt.executeUpdate(CreateSql);
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error in connection to PostgreSQL server");
            e.printStackTrace();
        }
    }

    public static void saveGame(String playerName, int moves, int duration){
        Statement stmt=null;

        String insertSql=null;

        String jdbc = "jdbc:postgresql://localhost:5432/Connect 4 Database";
        String username = "postgres";
        String password = "Student_1234";
        try {
            Connection connection = DriverManager.getConnection(jdbc, username, password);
            stmt = connection.createStatement();

            //Method checks for previous record of same player name and deletes it if so.
            checkAndDeletePlayerIfExist(playerName);

            insertSql= "INSERT INTO int_gamesession" +
                    " VALUES(nextval('game_id_seq'));" +
                    "INSERT INTO int_score (score_id,moves, game_duration) " +
                    "VALUES(nextval('score_id_seq'),'"+moves+"','"+duration+"');" +
                    " INSERT INTO int_player (game_id,name,score_id,player_id)" +
                    "VALUES (currval('game_id_seq'),'"+playerName+"',currval('score_id_seq'),nextval('player_id_seq'));";

            stmt.executeUpdate(insertSql);
            stmt.close();
            connection.close();

            System.out.println("Game Saved!");

        } catch (SQLException e) {
            System.out.println("Error in connection to PostgreSQL server");
            e.printStackTrace();
        }
    }

    public static void checkAndDeletePlayerIfExist(String playerName){
        PreparedStatement pstmt1=null;
        PreparedStatement pstmt2=null;
        Statement stmt=null;
        String jdbc = "jdbc:postgresql://localhost:5432/Connect 4 Database";
        String username = "postgres";
        String password = "Student_1234";
        try {
            Connection connection = DriverManager.getConnection(jdbc, username, password);

            stmt = connection.createStatement();

            pstmt2 = connection.prepareStatement("SELECT * FROM int_player WHERE UPPER(name) = ?");
            pstmt2.setString(1,playerName.toUpperCase());

            ResultSet rs = pstmt2.executeQuery();
            if (rs.next()){

                pstmt1 = connection.prepareStatement(""" 
                                                            DELETE FROM int_gamesession
                                                            WHERE game_id in (SELECT game_id
                                                            FROM int_player
                                                            WHERE UPPER(name) = ? );
                                                            DELETE FROM int_spot
                                                            WHERE game_id in (SELECT game_id
                                                                              FROM int_player
                                                                              WHERE UPPER(name) = ? );
                                                            DELETE FROM int_score
                                                            WHERE score_id in (SELECT score_id
                                                            FROM int_player WHERE upper(name) = ? );
                                                            """);
                pstmt1.setString(1,playerName.toUpperCase());
                pstmt1.setString(2,playerName.toUpperCase());
                pstmt1.setString(3,playerName.toUpperCase());
                pstmt1.executeUpdate();

            }


        } catch (SQLException e) {
            System.out.println("Error in connection to PostgreSQL server");
            e.printStackTrace();
        }
    }


}
