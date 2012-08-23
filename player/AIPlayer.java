package player;

import controller.Controller;
import engine.adt.Board;
import engine.adt.RuleSet;
import engine.game.GameInfo;
import engine.game.GameMessage;
import engine.game.GameTermination;
import engine.player.Player;
import engine.adt.Ply; 

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * An <code>AIPlayer</code> plays with artificial intelligence.
 */
public class AIPlayer implements Player {

	/**
	 * The controller operating this game 
	 */
	private Controller controller = null;

	private boolean useMultiProc;
	
	private Evaluator evaluator = null; 

	/**
	 * Determines the depth this AIPlayer plays with.
	 */
	private int depth;
	
	public AIPlayer(boolean useMultiProc, Evaluator eval, int depth) {	
		this.evaluator = eval; 
		this.depth = depth; 
		this.useMultiProc = useMultiProc;


	}
	public synchronized void setController(Controller controller) {
		this.controller = controller;
	}

	public synchronized String submitPly() throws InterruptedException {
		if (evaluator == null) {
			try {
				Thread.sleep(500);
			} catch (Exception e){}
			List<String> plies = controller.getValidPlies();
			if (plies.size()==0)
				return null;
			else
				return plies.get(new Random().nextInt(plies.size()));
		}
		


		//Get Valid plies
		RuleSet rs = controller.getRuleSet(); 
		final Board board = controller.getBoard();

		List<Ply> plies = new ArrayList<Ply>(); 
		for (String plyString : controller.getValidPlies()) {
			plies.add(rs.plyFactory().getPly(plyString, board)); 
		}


		//Divide into different tasks 
		int numProc = Runtime.getRuntime().availableProcessors(); 
		if (!useMultiProc) 
			numProc = 1; 
//		else 
//			depth += 1;

		List<List<Ply>> lists = split(numProc, plies);
		
		//Now we have the list of distributed plies. Create
		//separate FutureTasks to run each and run them

		List<FutureTask<BoardNode>> tasks =
			new LinkedList<FutureTask<BoardNode>>(); 


		for (int j = 0; j < numProc; j++) {
			List<Ply> recipient = lists.get(j);
			if (recipient.size() != 0) {
				tasks.add(new FutureTask<BoardNode>(new MinimaxTask(
						rs, board, controller.getTurnHistory(), 
						new GameInfo(recipient, controller.isNextWhite(), new ArrayList<GameMessage>())
						,depth )));
				
				tasks.get(j).run(); 
			}
		}

		//Now we have the tasks. We should get our GameBoards now. 
		List<BoardNode> winners = new ArrayList<BoardNode>(); 
		try {
			for (FutureTask<BoardNode> task : tasks) {
				if (winners.size() == 0) {
					//empty. put something
					winners.add(task.get()); 
				}
				else if (winners.get(0).getValue() < task.get().getValue()) {
					//new winner
					winners.clear();
					winners.add(task.get());
				}
				else if (winners.get(0).getValue() == task.get().getValue()) {
					//same
					winners.add(task.get()); 
				}
			}
		}
		catch (Exception e) {
			return ""; 
		}
		if (winners.size() == 0)
			return ""; 
		else 
			return winners.get(new Random().nextInt(winners.size())).getMove(); 


	}

	public synchronized void inform(String ply) {
		// do nothing (Player goes "Aight, cool...")		
	}

	public synchronized void inform(GameTermination termination) {
		// do nothing (Player goes yay or nay)

	}

	/**
	 * Split a list into n sublists of size >= 0, such that the maximum
	 * size difference between each of the sublists is at 
	 * most of one element. 
	 * 
	 * @param n The number of sublists the list is to be divided in.
	 * @param list The list to be split. 
	 * @return A list of the n sublists.
	 * @throws IllegalArgumentException if list == null or n <= 0 
	 */
	private static List<List<Ply>> split(int n, List<Ply> list) {
		if (n <= 0 || list == null) 
			throw new IllegalArgumentException("Bad arguments for split"); 
		
		List<Ply> copy = new ArrayList<Ply>(list); 
		
		List<List<Ply>> lists = new ArrayList<List<Ply>>(n); 
		for (int i = 0; i < n; i++)
			lists.add(new LinkedList<Ply>()); 

		int i = 0; 

		while (copy.size() > 0) {
			//Assign each ply to a different thread
			List<Ply> recipient = lists.get(i++ % n); 
			recipient.add(copy.get(0)); 
			copy.remove(0); 			
		}

		return lists; 
	}
	
	/**
	 * A <code>MinimaxTask</code> is a callable task that evaluates
	 * the minimax 
	 *
	 */
	class MinimaxTask implements Callable<BoardNode> {
		private RuleSet rs;
		private Board board; 
		private List<Boolean> turnHistory; 
		private GameInfo info; 
		private int depth; 

		public MinimaxTask(RuleSet rs, Board board, List<Boolean> turnHistory, GameInfo info, 
				int depth) {
			this.rs = rs;
			this.board = board; 
			this.turnHistory = turnHistory; 
			this.info = info; 
			this.depth = depth;
		}

		public BoardNode call() {
//			System.out.println("AB");
//			return GameSearcher.alphabeta(rs, evaluator, board, turnHistory, 
//					info, depth, -Integer.MAX_VALUE, Integer.MAX_VALUE);
//			System.out.println("MM");
				return GameSearcher.minimax(rs, evaluator, board, turnHistory, 
						info, depth);
		}
	}
}



class DumbEvaluator implements Evaluator {
	public int evaluate(Board board, boolean isWhite) {
		//let the score be equal to the difference in pieces 
		//if score > 0, then that's bad for white.
		return board.getPieces(!isWhite).size() -
			   board.getPieces(isWhite).size();
//		int score = board.getPieces(true).size() - 
//		board.getPieces(false).size();
//
//		if (isWhite)
//			return - score; 
//		else
//			return score;

	}

	public int getType() {
		return 0; 
	}
}



