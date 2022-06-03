package game;

public class Spot {
	private final int row;
	private final int col;
	private Coin coin;
	private static final String EMPTY_SPOT = "_";

	public Spot(int row, int col, Coin coin) {
		this.row = row;
		this.col = col;
		this.coin = coin;
	}

	public void setCoin(Coin coin) {
		this.coin = coin;
	}

	public Coin getCoin() {
		return coin;
	}

	@Override
	public String toString() {
		return coin == null ? EMPTY_SPOT : coin.toString();
	}
}
