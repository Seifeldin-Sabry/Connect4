package game;

import java.util.Random;

import static game.Grid.COLUMNS_AMOUNT;

/**
 @author Peter Buschenreiter
 */
public class PlayerCPU extends Player {
	private final Random random = new Random();

	public PlayerCPU(Grid grid, Score score) { super("Skynet",'X',grid,score); }


	public void dropCoin() {
		int col;
		Integer lowestFreeSpot;
		col = random.nextInt(COLUMNS_AMOUNT);
		do {
			col = (col + 1) % COLUMNS_AMOUNT;
			lowestFreeSpot = findLowestFreeSpot(col);
		} while (lowestFreeSpot == null);

		getGrid().getSpot(lowestFreeSpot, col).setCoin(new Coin(this));
		getGrid().addCoin();
	}
}
