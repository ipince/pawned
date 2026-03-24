package ruleset.eval;

import player.Evaluator;
import engine.adt.*;
import ruleset.antichess.*;

public class Evaluator3 implements Evaluator {

	public Evaluator3() {}
	
	public int evaluate(Board board, boolean isWhite) {
		
		AntichessRuleSet rs = new StandardAC();
		
		double piecesUnderAttack = 0;
		
//		for (Piece p : board.getPieces(isWhite)) {
//			if (rs.isUnderAttack(board, board.getPosition(p), !isWhite))
//				piecesUnderAttack++;
//		}
		
		piecesUnderAttack = piecesUnderAttack/board.getPieces(isWhite).size();
		System.out.println("Using new evaluator " + piecesUnderAttack);
		
		double myBoardScore = 0;
		
		for (Piece p : board.getPieces(isWhite)){
			if (p.getType().equals("pawn"))
				myBoardScore += 5;
			else 
				myBoardScore++;	
		}
		
		myBoardScore = myBoardScore/48;
		
		double opponentBoardScore = 0;
		
			
		for (Piece p : board.getPieces(!isWhite)){
			if (p.getType().equals("pawn"))
				opponentBoardScore += 5;
			else 
				opponentBoardScore++;
		}
			
		opponentBoardScore = opponentBoardScore/48;
		
		
		
		Double overallScore = 100*piecesUnderAttack + 100*opponentBoardScore - 100*myBoardScore;
		
		return overallScore.intValue();
	
	}
	
	public int getType() {
		return 2;
	}
}