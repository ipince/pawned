package ruleset.eval;

import player.Evaluator;
import engine.adt.Board;

// TODO escribir specs

public class Evaluator1 implements Evaluator{
	
	public Evaluator1 () {}
	
	public int evaluate(Board board, boolean isWhite) {
		int r = (board.getPieces(!isWhite).size() - board.getPieces(isWhite).size());
		return r;
	}
	
	public int getType() {
		return 1;
	}
	
}
