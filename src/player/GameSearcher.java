package player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import engine.adt.Board;
import engine.adt.Ply;
import engine.adt.RuleSet;
import engine.game.GameInfo;
import engine.game.GameTermination;

/**
 * <code>GameSearcher</code> is a compendium of game searching static 
 * utilities for search in game trees. Given a <code>RuleSet</code>, 
 * a <code>GameSearcher</code> provides different static methods that
 * will solve a game using different variants of the minmax algorithm.
 *
 */
public class GameSearcher {

	/**
	 * Uses the <a href=http://en.wikipedia.org/wiki/Minimax>Minimax</a>
	 * algorithm to determine the <code>BoardNode</code> that is most 
	 * appropriate to use, given a particular game tree which 
	 * root is the moment specified by <code>turnHistory</code> and 
	 * <code>info</code> which expansion can be obtained using the
	 * <code>rs</code>.
	 * 
	 * @param rs The ruleset to be used to expand the tree
	 * @param evaluator An evaluator that given a board and a current turn
	 * 				returns a heuristic value from 
	 * 				[Integer.MIN_VALUE + 1, Integer.MAX_VALUE] such that
	 * 				a larger value indicates that the board depicts a more
	 * 				favorable situation for the current player, and 
	 * 				a smaller (or more negative) value indicates that 
	 * 				the board is less favorable for the current player. 
	 * @param board The board representing the current state of the game
	 * @param turnHistory The history of the turns from the beginning
	 * 					  of the game up to the current point. 
	 * @param info The current status of the game. That is, the set of
	 * 				plies, the current turn, and the game messages that
	 * 				are associated with the current state of the game. 
	 * 
	 * @param depth The size of the tree that should be evaluted. 
	 * 				A depth of 1 means that only the immediate
	 * 				plies specified by <code>info</code> should be 
	 * 				considered. 
	 * 
	 * @return A <code>BoardNode</code> that maximizes the minimum 
	 * 			utility. 
	 * 
	 */
	public static BoardNode minimax(RuleSet rs, Evaluator evaluator,
			Board board, List<Boolean> turnHistory, GameInfo info, 
			int depth) {
		if (depth == 0)
			return new BoardNode(null,evaluator.evaluate(board, info.getTurn()));


		List<BoardNode> winners = new ArrayList<BoardNode>(); 

		for (Ply p : info.getPlies()) {
			List<Boolean> newHistory = new ArrayList<Boolean>(turnHistory);
			newHistory.add(info.getTurn());

			Board newBoard = rs.executeFakePly(board, p);
			try {
				GameInfo newInfo = rs.continueGame(
						newBoard, newHistory, info.getMessages());

				BoardNode candidate = null;
				
				
				if (info.getTurn() == newInfo.getTurn()) //same guy's turn
					candidate = new BoardNode(p.toString(), 
							minimax(rs, evaluator, newBoard, newHistory, newInfo, 
									depth - 1).getValue()); 

				else 
					candidate = new BoardNode(p.toString(), 
							-minimax(rs, evaluator, newBoard, newHistory, newInfo, 
									depth - 1).getValue()); 				

				//if (depth  == 7)
					//System.out.println(candidate);
				
				if (winners.size() == 0)
					winners.add(candidate);
				else {//Otherwise we check if this option is better
					//check if our dude is better
					if (candidate.getValue() > winners.get(0).getValue()) {
						winners.clear(); 
						winners.add(candidate); 
					}
					//check if our dude is equally good
					else if (candidate.getValue() == winners.get(0).getValue()) {
						winners.add(candidate);
					}
				}			

			}
			catch (GameTermination ge) {
				if (ge.winnerIsWhite() == null) //DRAW
					return new BoardNode(p.toString(), 0); 
				else if (ge.winnerIsWhite() == info.getTurn())
					return new BoardNode(p.toString(), Integer.MAX_VALUE - 8 + depth); 
				//current player won
				else
					return new BoardNode(p.toString(), Integer.MIN_VALUE + 8 - depth + 1); 
				//current player lost


			}
		}
		
		//if (depth == 7)
		//System.out.println("winnres" + winners);
		return winners.get(new Random().nextInt(winners.size()));
		}


	
	/**
	 * Uses the <a href=http://en.wikipedia.org/wiki/Alpha_beta_pruning>
	 * Minimax with Alpha-Beta Pruning</a>
	 * algorithm to determine the <code>BoardNode</code> that is most 
	 * appropriate to use, given a particular game tree which 
	 * root is the moment specified by <code>turnHistory</code> and 
	 * <code>info</code> which expansion can be obtained using the
	 * <code>rs</code>.
	 * 
	 * @param rs The ruleset to be used to expand the tree
	 * @param evaluator An evaluator that given a board and a current turn
	 * 				returns a heuristic value from 
	 * 				[Integer.MIN_VALUE + 1, Integer.MAX_VALUE] such that
	 * 				a larger value indicates that the board depicts a more
	 * 				favorable situation for the current player, and 
	 * 				a smaller (or more negative) value indicates that 
	 * 				the board is less favorable for the current player. 
	 * @param board The board representing the current state of the game
	 * @param turnHistory The history of the turns from the beginning
	 * 					  of the game up to the current point. 
	 * @param info The current status of the game. That is, the set of
	 * 				plies, the current turn, and the game messages that
	 * 				are associated with the current state of the game. 
	 * 
	 * @param depth The size of the tree that should be evaluted. 
	 * 				A depth of 1 means that only the immediate
	 * 				plies specified by <code>info</code> should be 
	 * 				considered. 
	 * @param alpha A value such that A* >= alpha, where A* is the value
	 * 			associated with the optimal choice. In other words,
	 * 			alpha is a lower bound for the optimal choice. 
	 * @param beta A value such that A* <= beta, where A* is the value
	 * 			associated with the optimal choice. In other words,
	 * 			beta is an upper bound for the optimal choice.
	 * 
	 * @return A <code>BoardNode</code> that maximizes the minimum 
	 * 			utility. 
	 * 
	 */	
	public static BoardNode alphabeta(RuleSet rs, Evaluator evaluator,
			Board board, List<Boolean> turnHistory, GameInfo info, 
			int depth, int alpha, int beta) {

			return alphabeta(rs, evaluator, board, turnHistory, info, depth, 
					alpha, beta, depth); 
		}

		
	


	
	/**
	 * Runs the alpha beta algorithm over the game specified by the ruleset and
	 * info. 
	 * 
	 * @param origDepth the original number of levels to do alpha beta on 
	 * 
	 */
	private static BoardNode alphabeta(RuleSet rs, Evaluator evaluator,
			Board board, List<Boolean> turnHistory, GameInfo info, 
			int depth, int alpha, int beta, int origDepth) {
		//base case
		if (depth == 0)
			return new BoardNode(null,evaluator.evaluate(board, info.getTurn()));

		//the accumulated list of winners
		List<BoardNode> winners = new ArrayList<BoardNode>(); 

		//for each possible move from this node 
		for (Ply p : info.getPlies()) {

			//try each possible combination
			List<Boolean> newHistory = new ArrayList<Boolean>(turnHistory);
			newHistory.add(info.getTurn());

			Board newBoard = rs.executeFakePly(board, p);
			try {

				GameInfo newInfo = rs.continueGame(
						newBoard, newHistory, info.getMessages());

				BoardNode candidate = null;

				//obtain minimax recursively 
				if (info.getTurn() == newInfo.getTurn()) //same guy's turn
					candidate = new BoardNode(p.toString(), 
							alphabeta(rs, evaluator, newBoard, newHistory, newInfo, 
									depth - 1, alpha, beta, origDepth).getValue()); 

				else 
					candidate = new BoardNode(p.toString(), 
							-alphabeta(rs, evaluator, newBoard, newHistory, newInfo, 
									depth - 1, -beta,-alpha, origDepth).getValue()); 				

				//if (depth  == origDepth)
					//System.out.println(candidate + "-depth=" + origDepth);
				//do max of winners.
				//keep list of max scorers
				if (winners.size() == 0) {
					alpha = candidate.getValue(); 
					winners.add(candidate);
				}

				else {//We check if this option is better
					//check if this is not possible
					//no more computing required
					if (candidate.getValue() >= beta) { //TODO do we want >=? loss of diversity. gain of speed. 
						winners.clear(); 
						winners.add(new BoardNode(null, candidate.getValue())); 
						//return winners.get(new java.util.Random().nextInt(winners.size())); 
						return winners.get(new Random().nextInt(winners.size())); 
					}		
					//otherwise, check if we can do better
					if (candidate.getValue() > alpha) {
						alpha = candidate.getValue(); 
						winners.clear(); 
						winners.add(candidate); 
					}			
					else if (candidate.getValue() == 
						winners.get(0).getValue() && candidate.getMove() != null) {
						winners.add(candidate);						
					}					
				}			

			}
			catch (GameTermination ge) {

				if (ge.winnerIsWhite() == null) //DRAW
					return new BoardNode(null, 0); 
				else if (ge.winnerIsWhite() == info.getTurn())
					return new BoardNode(p.toString(), Integer.MAX_VALUE - 
							(origDepth - depth)); 
				//current player won
				else //add 1 because (- Integer.MIN_VALUE) is not an int 
					return new BoardNode(p.toString(), Integer.MIN_VALUE + 1
							+ (origDepth - depth)); 
				//current player lost


			}
		}
		
		//if (depth == 7)
			//System.out.println("winnres" + winners);
		return winners.get(new Random().nextInt(winners.size()));		
	}
	

}