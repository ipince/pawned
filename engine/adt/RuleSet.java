package engine.adt;

import java.util.*; 

import engine.game.GameInfo;
import engine.game.GameMessage;
import engine.game.GameTermination;

/** 
 *  A <code>RuleSet</code> represents the set of global and local rules upon which 
 *  a game can be fully operated. That is, a <code>RuleSet</code> is a mechanism
 *  for determining and creating all of the possible moves, pieces and boards
 *  supported by a particular game, as well as to indicate the set of possible 
 *  moves and <code>GameTermination</code>s that can happen given a 
 *  <code>Board</code> configuration.
 */
public interface RuleSet {
	
	/**
	 * A <code>PieceFactory</code> is a piece creation mechanism. It provides 
	 * information about the supported pieces and allows to create supported 
	 * pieces associated with a particular <code>Board</code>. 
	 */
	public interface PieceFactory {
		public Piece getPiece(String name, Board board, boolean isWhite);
		public Set<Piece> getSupportedPieces(Board board, boolean isWhite);
		public List<String> getOrderedPieces();
	}

	/**
	 * A <code>PlyFactory</code> is a ply creation mechanism. It allows for
	 * the creation of a <code>Ply</code> given a <code>Board</code>. 
	 */
	public interface PlyFactory {
		public Ply getPly(String name, Board board);
	}
	
	/**
	 * A <code>BoardFactory</code> is a board creation mechanism. It allows for
	 * the creation of a <code>Board</code> that the <code>RuleSet</code>
	 * supports.
	 */
	public interface BoardFactory {
		public Board getBlankBoard();
		
		/**
		 * Returns a Board with the initial configuration. A new Board is returned
		 * every time this method is called. Notice that an initial Board
		 * configuration is not necessarily deterministic. That is, a RuleSet might
		 * use a random configuration as its initial Board configuration. However,
		 * the RuleSet will know the initial configuration of the Board that it
		 * plays with, meaning that it will return a new Board, but with the same
		 * initial configuration, despite the randomness.
		 */
		public Board getInitialBoard();
		
		/**
		 * Return a board with an XML file describing it. :)( 
		 * @param settings
		 */
		public Board getBoard(String settings);
	}
	
	/**
	 * Returns a PieceFactory that can create the supported Pieces of this
	 * RuleSet.
	 * 
	 * @return A <code>PieceFactory</code> associated with <code>this</code>.
	 */
	public PieceFactory pieceFactory(); 
	
	/**
	 * Returns a PlyFactory that can create Plies that could potentially be
	 * valid in this RuleSet.
	 * 
	 * @return A <code>PlyFactory</code> associated with <code>this</code>.
	 */
	public PlyFactory plyFactory();
	
	/**
	 * Returns a BoardFactory that can create a blank Board, and also a Board
	 * having the initial configuration that the RuleSet is using.
	 * 
	 * @return A <code>BoardFactory</code> associated with <code>this</code>.
	 */
	public BoardFactory boardFactory();
	
	/**
	 * Returns a Parser associated with this type of RuleSet. This parser
	 * can be used to convert coordinates to and from its String representation
	 * @return a parser associated with <code>this</code> 
	 */
	public Parser getParser();
	
	/**
	 * Given a Board, the turn history, and the current GameMessages, this
	 * method attempts to continue the game. This means that it will try to
	 * determine the GameInfo necessary for it to continue. This includes the
	 * set of valid Plies for the current Board configuration, an indicator of
	 * whose turn it is, and a List of pertaining GameMessages. If a the Game
	 * cannot be continued because a termination condition has been met, it will
	 * throw a GameTermination.
	 * 
	 * @param board The board representing the game
	 * @param turnHistory a list containing the sequence of turns for the game, where
	 *  		<code>true</code> symbolizes white player's turn, while <code> false
	 *  		</code> symbolizes black player's turn. 
	 * @param messages a list containing the messages accumulated in the game.
	 */
	public GameInfo continueGame(Board board, List<Boolean> turnHistory, List<GameMessage> messages) 
						throws GameTermination;
	
	/**
	 * Returns a Board which is the result of cloning the given Board and
	 * executing the given Ply. This serves to determine what would happen in
	 * the hypothetical situation of the given Ply being executed on the given
	 * Board. Thus, its name "executeFAKEPly".
	 * 
	 * @param ply The ply to be simulated.
	 * @param board The board on which the ply is to be started.
	 * 
	 * @return A copy of <code>board</code>, with the ply executed on it. 
	 */
	public Board executeFakePly(Board board, Ply ply);
	
}


