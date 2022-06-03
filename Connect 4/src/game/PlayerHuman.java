package game;

/**
 @author Peter Buschenreiter
 */
public class PlayerHuman extends Player {

	public PlayerHuman(String name, Grid grid, Score score) {
		super(name, 'O', grid, score);
	}

	public boolean dropCoin(Integer col) {
		if (col == null) return true;
		col--;
		Integer lowestFreeSpot = findLowestFreeSpot(col);
		if (lowestFreeSpot == null) {
			return true;
		}
		getGrid().getSpot(lowestFreeSpot, col).setCoin(new Coin(this));
		getGrid().addCoin();
		getScore().addMove();
		return false;
	}
}
