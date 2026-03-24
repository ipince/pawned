package player;

import engine.adt.Board;

// TODO escribir specs

public interface Evaluator {
	
	public int evaluate(Board board, boolean isWhite);
	
	public int getType();
	
}
