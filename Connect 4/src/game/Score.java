package game;

import java.util.Date;

/**
 @author Peter Buschenreiter
 */
public class Score {
	private int moves;
	private int duration;
	Date beginning = new Date();
	private final long startTime = beginning.getTime();

	/**
	 Constructor for a new game
	 */
	public Score() {
		moves = 0;
		duration = 0;
	}

	/**
	 Constructor for a loaded game

	 @param moves    <code>int</code>: Amount of moves played so far
	 @param duration <code>int</code>: Amount of seconds played so far
	 */
	public Score(int moves, int duration) {
		this.moves = moves;
		this.duration = duration;
	}

	public void addMove() {
		moves++;
	}

	public int getMoves() {
		return moves;
	}

	private void setDuration() {
		Date end = new Date();
		duration += (int) ((end.getTime() - startTime) / 1000);
	}

	public int getDuration() {
		setDuration();
		return duration;
	}
}
