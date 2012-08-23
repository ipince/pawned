package engine.game;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.adt.RuleSet;

/**
 * A Game constitutes the central mechanism for game control. It contains
 * information about the game rules using a <code>RuleSet</code>. Additionally, 
 * it contains a <code>Board</code> representing the current configuration along 
 * with captured <code>Pieces</code> and <code>Ply</code> and turn histories, 
 * a set of possible <code>Plies</code> and the current state, and 
 * <code>GameMessages</code>.
 *  
 * The state can either be either active or terminated (along with its 
 * <code>GameTermination</code>, and <code>GameMessages</code> can be any 
 * type of condition that is occuring in the game, such as a 'score' in 
 * 'Scrabble'.
 *  
 * <code>Games</code> are not thread safe. 
 * 
 * @specfield board : Board          //The configuration of the present board
 * @specfield captured :Set &lt;Piece&gt;   //The set of captured pieces for 
 * 											   each player
 * @specfield history : Game History //The history of the game
 * @specfield status : State		 //The state of the game (finished or running)
 * 
 * 
 * @see Board
 * @see GameMessage
 * @see Ply
 */


public class Game {
	
	//AF(x) 
	// Abstraction Function:
	//    AF(c) = game G such that
	//
	//      B.board = c.board
	//      B.captured = capturedWhite U capturedBlack
	//      B.history = {(a,b)}_n =  ({a}_n, {b}_n}
	//          where {a_n} is the list of plies : c.plyHistory
	//          and   {b_n} is the list of turns : c.turnHistory
	//      B.status = running, if c.status == null,
	//                 finalized, otherwise.

	// Rep Invariant
	//
	// board != null, 
	// capturedWhite, capturedBlack != null
	// plyHistory, turnHistory != null
	//
	// status != null => validPlies.isEmpty()
	//
	
	
	//Fields
	private static final int MAX_ROLLBACK = 0; 
	private RuleSet rs;
	private Board board;
	private List<GameMessage> message; 
	
	private List<Ply> validPlies; 
	
	private List<Ply> plyHistory; 
	private List<Boolean> turnHistory; 
	
	private GameTermination status = null; 
	
	private Set<Piece> capturedWhite; 
	private Set<Piece> capturedBlack;
	
	/**
	 * Builds a <code>Game</code> based on the <code>RuleSet</code>
	 * <code>ruleset</code> and the standard initial board specified by the 
	 * ruleset
	 * 
	 * @param ruleset the ruleset to be used in <tt>this</tt>. 
	 * 
	 * @throws IllegalArgumentException if <code>ruleset == null</code> 
	 * 
	 * @see engine.adt.RuleSet 
	 */
	public Game(RuleSet ruleset) {
		this(ruleset, ruleset.boardFactory().getInitialBoard());
	}
	
	/**
	 * Builds a <code>Game</code> based on the <code>RuleSet</code>
	 * <code>ruleset</code> and a specified board configuration.
	 * 
	 * @param ruleset the ruleset to be used in <tt>this</tt>. 
	 * @param board the board to be used in <tt>this</tt> as the starting
	 * 		  board. 
	 * 
	 * @throws IllegalArgumentException if <code>ruleset,board == null</code> 
	 */
	public Game(RuleSet ruleset, Board board) {
		if (ruleset == null || board == null)
			throw new IllegalArgumentException("Arguments cannot be null for Game");
		this.rs = ruleset;
		this.board = board;
		this.message = new LinkedList<GameMessage>();
		
		
		this.turnHistory = new LinkedList<Boolean>();
		this.capturedBlack = new HashSet<Piece>();
		this.capturedWhite = new HashSet<Piece>();
	
		this.plyHistory = new LinkedList<Ply>(); 
		
		try {
			updateInfo();
		}
		catch (GameTermination gt) {
			throw new RuntimeException("Game ended at start time!");
		}		
		
		
		checkRep(); 
	}
	
	/**
	 * Gives information on the current <code>Player</code> required to move.
	 * Describes the next player that should execute a move. That is,
	 * returns the name of the player that is being waited for. 
	 * 
	 * @return <code>null</code> if the game is finished or otherwise
	 * 		   terminated. Returns <code>true</code> if the next player
	 * 		   is white, or <code>false</code> otherwise. 
	 *
	 */
	public Boolean isNextWhite() {
		checkRep(); 

		if (status != null) //game has ended. no ones turn 
			return null; 
		else 				//last person in turn history
			return turnHistory.get(turnHistory.size() - 1);	
		
		
	}
	
	/**
	 * Executes a <code>Ply</code> in the current game. After the ply 
	 * is executed, the status of the game is executed to reflect the new 
	 * state. 
	 * 
	 * @param ply The Ply to be executed on <tt>this</tt>
	 * 
	 * @throws 	<code>IllegalArgumentException</code> if the ply
	 *          is invalid. A ply p is invalid if <code>p.isValid()</code>
	 *          is false. 

	 * @throws <code>GameTermination</code> if the move
	 * 		   leads to a termination condition. If a <code>GameTermination</code>
	 * 		   is thrown, the game is considered ended. 
	 * 
	 * @modifies <tt>this</tt>
	 * @effects executes ply and updates the 
	 * 			state of <tt>this</tt> accordingly. 
	 * 
	 */
	public void executePly(Ply ply) throws GameTermination {
		checkRep(); 
		
		Ply validEquivalent = isValid(ply);
		if (validEquivalent != null) {
			Collection<Piece> capturedPieces = board.executePly(validEquivalent);
			plyHistory.add(validEquivalent);
			updateInfo();
			
			for (Piece piece: capturedPieces ) {
				if (piece.isWhite())
					capturedWhite.add(piece);
				else
					capturedBlack.add(piece); 
			}
		} else {
			throw new RuntimeException("Invalid ply for game: " + ply);
		}
	}
	
	/**
	 * Execute a list of <code>Plies</code> in order. without checking for validity.
	 * That is, 
	 * 
	 * 
	 * @requires plies.size() = turns.size() > 0
	 * @requires plies is a set of plies such that given a <code>Game</code> g, 
	 *           if {p_k} is the sequence of ply executions represented by 
	 * 			 <code>g.executePly(plies.get(0)), 
	 *                 g.executePly(plies.get(1)), ... , 
	 *                 g.executePly(plies.get(k))</code>, 
	 * 			 where <code> 0 <= k <= plies.size() - 1 </code>, then for all k
	 * 			 in [0,plies.size() - 2}, 
	 * 			 {p_k} implies <code>g.isValid(plies.get(k + 1))</code> is true. 
	 * 			 In other words, the list of plies should be all valid when executed
	 * 			 one after the other within the current game. 
	 * 
	 * @modifies <tt>this</tt>
	 * @effects executes the list of plies in order and updates the 
	 * 			state of <tt>this</tt> accordingly. 
	 */
//	public void executePlies(List<String> plies, List<Boolean> turns)
//				throws GameTermination {
//		
//		checkRep();
//		
//		if (plies.size() != turns.size())
//			throw new IllegalArgumentException("List of Plies and turns are " +
//					"not of equal length");
//		for (int i = 0; i < plies.size(); i++) {
//			//Execute plies without verification
//			Ply p = rs.plyFactory().getPly(plies.get(i), board);
//			board.executePly(p);
//			plyHistory.add(p);
//			turnHistory.add(turns.get(i));
//		}
//				
//		updateInfo();
//	}
//	
	public void executePlies(List<String> plies, List<Boolean> turns)
	throws GameTermination {
		
		//the plyHistory already has some information about the first move,
		//by calling updateInfo() in the constructor.
		//we need to remove it. 
		turnHistory.clear();
		message.clear(); 
		//Fast forward to last move 
		for (int i = 0; i < plies.size() ; i++) {
			//Execute plies without verification
			Ply p = rs.plyFactory().getPly(plies.get(i), board);
			Collection<Piece> capturedPieces = board.executePly(p);
			plyHistory.add(p);
			turnHistory.add(turns.get(i));
			
			for (Piece piece: capturedPieces ) {
				if (piece.isWhite())
					capturedWhite.add(piece);
				else
					capturedBlack.add(piece); 
			}
		}
		
		updateInfo();
	}
	/**
	 * Determines whether a <code>Ply</code> would be valid in the current 
	 * <code>Game</code>. 
	 * 
	 * A ply is valid if it is similar to any ply <code>p</code> in contained in 
	 * <code>this.getValidPlies()</code> 
	 * 
	 * 
	 * @param ply The ply which validity is to be determined. 
	 * 
	 * @return If the ply is invalid, returns <code>null</code>.
	 * 		   Otherwise, otherwise returns the ply <code>p</code> in 
	 *         <code>this.getValidPlies()</code> such that 
	 *         <code>p.similar(ply)</code> is true. 
	 */
	public Ply isValid(Ply ply) {
		checkRep();
		
		for (Ply validPly : validPlies) {
			if (validPly.similar(ply))
				return validPly;
		}
		return null;
	}
	
	/**
	 * Returns a list of valid plies. That is, returns a list of plies that
	 * are allowed in the game in its current state. If there are no plies
	 * allowed in the current state, returns the empty list. 
	 * 
	 * @return empty list if no valid plies, otherwise a list of 
	 * 		 a list of the valid plies for the current state. This list
	 * 		 is immutable. That is, removal and addition of plies is expected
	 * 		 to throw an <code>UnsupportedOperationException</code>. 
	 */
	public List<Ply> getValidPlies() {
		return Collections.unmodifiableList(validPlies); 
	}

	/**
	 * Returns the Set of captured Pieces for a given player. A piece is a
	 * captured pieces if there is some ply that has been executed in the
	 * history that resulted in the removal of this piece from the board. 
	 * 
	 * @param isWhite Identifies the color of the captured pieces.
	 * 
	 * @return If isWhite() is true, then returns the set of captured
	 * 		   white Pieces. If isWhite() is false, returns a set of
	 * 		   captured Black pieces.  This set
	 * 		   is immutable. That is, removal and addition of plies is expected
	 * 		   to throw an <code>UnsupportedOperationException</code>. 
	 */
	public Set<Piece> getCapturedPieces(boolean isWhite) {
		checkRep();
		
		return Collections.unmodifiableSet(isWhite? capturedWhite:capturedBlack);
	}
	
	/**
	 * Returns a list of <code>Plies</code> that have been executed in this 
	 * game in ascending chronological order.
	 * 
	 * @return A list s.t. if i < j, gameHistory().get(i) happened before
	 * 			gameHistory().get(j).  This list
	 * 		    is immutable. That is, removal and addition of plies is expected
	 * 		    to throw an <code>UnsupportedOperationException</code>. 
	 */
	public List<Ply> getGameHistory() {
		checkRep(); 
		
		return Collections.unmodifiableList(plyHistory);
	}

	/**
	 * Returns a list of <code>Booleans</code> representing the turns 
	 * that have been executed in this game in ascending chronological order.
	 * 
	 * For a given turn, the <code>Boolean</code> is <code>true</code>, then 
	 * the ply was executed by the white player, and otherwise it was executed
	 * by the black player. 
	 * 
	 *  
	 * @return A list s.t. if i < j, getTurnHistory().get(i) happened before
	 * 			getTurnHistory().get(j).  This list
	 * 		    is immutable. That is, removal and addition of plies is expected
	 * 		    to throw an <code>UnsupportedOperationException</code>. 
	 */
	public List<Boolean> getTurnHistory() {
		checkRep(); 
		
		List<Boolean> turns =  new ArrayList<Boolean>(turnHistory);
	
		if (status == null) { //Game hasn't ended 
			
			//remove current turn 
			turns.remove(turns.size() - 1);
		}
	
		return Collections.unmodifiableList(turns); 
	}
	
	/**
	 * Returns the game to the state where it was before the last <code>i</code> 
	 * plies where executed
	 * 
	 * @param i Any number between 0 and MAX_ROLLBACK, where 0 is to be interpreted
	 * 			as the current state, and <code>i<code> as the state before the last
	 * 			<code>i</code> plies were executed, for all other <code>i</code>. If
	 * 			less than <code>i</code> plies have been executed, then returns the
	 * 			game to its initial state. 
	 * 
	 * <b>This method is not supported and is expected to throw a 
	 * <code>UnsupportedOperationException</code> exception</b> 
	 * 
	 * @throws IllegalArgumentException if <code> i > MAX_ROLLBACK </code>
	 * @throws UnsupportedOperationException
	 * 
	 * @modifies <tt>this</tt>
	 * @effects returns the state of <tt>this</tt> to <tt>i</tt> plies
	 * 			before. 
	 */
	public void rollback(int i) {
		checkRep();
		
		if (i > MAX_ROLLBACK)
			throw new IllegalArgumentException("invalid arguments");
		
		throw new UnsupportedOperationException("not yet implemented");
	}
	
	/**
	 * Returns the board in the current state within the gameplay. Notice
	 * that the board is not a copy, but the actual object of type <code>
	 * Board</code> associated with <code>this</code>. 
	 * 
	 * @return The board associated with <tt>this</tt>.
	 */
	public Board getBoard() {
		checkRep(); 
		
		return board;
	}
	
	/**
	 * Returns the status of this <code>Game</code>. As previously stated, 
	 * the state can either be either active or terminated (along with its 
	 * <code>GameTermination</code>
	 * 
	 * @return If a <code>Game</code> is still running, returns null. 
	 * 		   Else, returns a <code>GameTermination</code> specifying the 
	 * 		   type of termination by which the game ended.  
	 * 
	 */
	public GameTermination getStatus() {
		checkRep(); 
		
		return status; // GameTermination is immutable
	}
	
	/** 
	 * Returns the list  of <code>GameMessages</code> that currently apply to 
	 * this <code>Game</code>. If there are no messages, returns the empty list. 
	 *  
	 * @return The list of messages, or the empty list, in case no message 
	 * 			is associated with the current state. 
	 */
	public List<GameMessage> getMessages() {
		checkRep(); 
		
		return Collections.unmodifiableList(message);
	}


	/**
	 * Update the current status by querying the ruleset. 
	 * 
	 * When this method is called, the board and turnHistory are 
	 * expected to be out-of-synch with the list of valid plies,
	 * captured pieces, messages, and status. Upon exit of this method,
	 * all the status should be synchronized. 

	 * @throws GameTermination if the game has ended
	 * 
	 * @modifies <tt>this</tt>
	 * @effects synchronizes the set of valid plies, captured pieces,
	 * 			messages, and status with turnHistory, board
	 * 
	 */
	private void updateInfo() throws GameTermination { 
		
		GameInfo info = null;
		//Populate possible plies, turn, and
		//initial methods
		try {
			info = rs.continueGame(board, turnHistory, message);
			message = info.getMessages();
			turnHistory.add(info.getTurn());
			validPlies = info.getPlies();
		}
		catch (GameTermination gt) {
			validPlies.clear(); 	
			status = gt;
			throw gt;
		}
	}
	
	/**
	 * Verify that the invariant holds. 
	 */
	private void checkRep() {
		if (debug.DebugInfo.DEBUG_GAME) {
			if (board == null || capturedWhite == null || 
					capturedBlack == null || plyHistory == null ||
					turnHistory == null)
				throw new RuntimeException("Invariant violated for " +
				"Game");
			
			if (status != null && !validPlies.isEmpty())
				throw new RuntimeException("Invariant violated for " +
				"Game");
		}			
	}
}
