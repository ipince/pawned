package interfaces.test;

import java.util.*;

import engine.adt.*;
import engine.player.GameObserver;
import engine.player.Player;
import controller.*;

/**
 * A fake Controller for testing
 * 
 * @author jchernan
 *
 */
public class Kontroller {

	// Fields
	
	private Player whitePlayer;
	private Player blackPlayer;
	private RuleSet ruleSet;
	
	private List<List<String>> plies = new ArrayList<List<String>>();
	private int count = -1;
	
	public Kontroller(RuleSet ruleSet, Player whitePlayer, Player blackPlayer, 
			int whiteTime, int blackTime, GameObserver ... observers) {
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		this.ruleSet = ruleSet;
		
		List<String> validPlies = new LinkedList<String>();
		validPlies.add("a2-a3");
		validPlies.add("a2-a4");
		validPlies.add("b2-b3");
		validPlies.add("b2-b4");
		validPlies.add("c2-c3");
		validPlies.add("c2-c4");
		validPlies.add("d2-d3");
		validPlies.add("d2-d4");
		validPlies.add("e2-e3");
		validPlies.add("e2-e4");
		validPlies.add("f2-f3");
		validPlies.add("f2-f4");
		validPlies.add("g2-g3");
		validPlies.add("g2-g4");
		validPlies.add("h2-h3");
		validPlies.add("h2-h4");
		validPlies.add("b1-a3");
		validPlies.add("b1-c3");
		validPlies.add("g1-f3");
		validPlies.add("g1-h3");
		
		plies.add(validPlies);
		
		validPlies = new LinkedList<String>();
		validPlies.add("a7-a6");
		validPlies.add("a7-a5");
		validPlies.add("b7-b6");
		validPlies.add("b7-b5");
		validPlies.add("c7-c6");
		validPlies.add("c7-c5");
		validPlies.add("d7-d6");
		validPlies.add("d7-d5");
		validPlies.add("e7-e6");
		validPlies.add("e7-e5");
		validPlies.add("f7-f6");
		validPlies.add("f7-f5");
		validPlies.add("g7-g6");
		validPlies.add("g7-g5");
		validPlies.add("h7-h6");
		validPlies.add("h7-h5");
		validPlies.add("b8-a6");
		validPlies.add("b8-c6");
		validPlies.add("g8-h6");
		validPlies.add("g8-f6");
		
		plies.add(validPlies);
		
	}
	
	// Accessors
	
	/**
	 * Returns the RuleSet being used
	 */
	public RuleSet getRuleSet() {
		throw new RuntimeException("Not yet implemented");
	}
	
	/**
	 * Returns the Board in which the Game is being played.
	 */
	public Board getBoard() {
		return ruleSet.boardFactory().getInitialBoard();
	}
	
	public List<String> getValidPlies(){	
		return plies.get(count); 
	}
	
	/**
	 * Returns the Ply history of the Game.
	 */
	public List<Ply> getMoveHistory() {
		throw new RuntimeException("Not yet implemented");
	}
	
	/**
	 * Determines whether a Ply is valid or not.
	 */
	public boolean isValid(String ply) {
		throw new RuntimeException("Not yet implemented");
	}
	
	/**
	 * Execute this Ply
	 */
	public void executePly(String ply) {
		//System.out.println("Ply " + ply + " executed");
		if (count == 0) {
			whitePlayer.inform(ply);
		} else {
			blackPlayer.inform(ply);
		}
	}
	
	/**
	 * Become a listener of this.
	 */
	public void subscribe(GameObserver observer) {
		throw new RuntimeException("Not yet implemented");
	}
	
	/**
	 * Saves the current Game.
	 */
	public void saveGame() {
		throw new RuntimeException("Not yet implemented");
	}
	
	
	public boolean isNextWhite() { 
		if (count == 0)
			return true;
		else 
			return false;
	}
	
	
	/**
	 * Determines whether the player can play or not.
	 */
	private boolean checkTime(boolean isWhite) {
		throw new RuntimeException("Not yet implemented");
	}
	
//	public void askMeForWhiteMove1() {
//		count++;
//		whitePlayer.submitPly();
//	}
//	
//	public void askMeForBlackMove1() {
//		count++;
//		blackPlayer.submitPly();
//	}
	
	
}
