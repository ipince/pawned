package ruleset.eval;

import player.Evaluator;
import engine.adt.Board;
import engine.adt.Piece;

public class Evaluator2 implements Evaluator {

	public Evaluator2() {}
	
	public int evaluate(Board board, boolean isWhite) {
		
		int myBoardScore = 0;
		int opponentBoardScore = 0;
		
		for (Piece p : board.getPieces(isWhite)){
			if (p.getType().equals("pawn"))
				myBoardScore += 2;
			else 
				myBoardScore++;	
		}
		
		for (Piece p : board.getPieces(!isWhite)){
			if (p.getType().equals("pawn"))
				opponentBoardScore += 2;
			else 
				opponentBoardScore++;
		}
			
			
		return opponentBoardScore - myBoardScore;
	
	}
	
	public int getType() {
		return 2;
	}
}
