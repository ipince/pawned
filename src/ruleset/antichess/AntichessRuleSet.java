package ruleset.antichess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import debug.DebugInfo;

import ruleset.board.*;
import engine.adt.Board;
import engine.adt.Parser;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.adt.RuleSet;
import engine.exception.InvalidInitialPositionException;
import engine.game.GameInfo;
import engine.game.GameMessage;
import engine.game.GameTermination;

/**
 * A general <code>RuleSet</code> for antichess. It encodes the rules for
 * playing antichess, as specified by
 * <a href="http://www.mit.edu/~6.170/assignments/antichess/antichess.html#antichess_rules">
 * this</a>.
 */
public abstract class AntichessRuleSet implements RuleSet {

	// Termination conditions
	
	/**
	 * The <code>String</code> representation of the checkmate termination
	 * condition.
	 */
	public static final String CHECKMATE = "checkmate";

	/**
	 * The <code>String</code> representation of the piece depletion
	 * termination condition.
	 */
	public static final String PIECE_DEPLETION = "piece depletion";
	
	/**
	 * The <code>String</code> representation of the stalemate termination
	 * condition.
	 */
	public static final String STALEMATE = "stalemate"; 

	/**
	 * Enum class containing the <code>Piece</code>s supported by this
	 * <code>RuleSet</code>. These are king, queen, rook, bishop, knight,
	 * and pawn.
	 */
	enum PieceName {
		king, queen, rook, bishop, knight, pawn;
	}

	/**
	 * A <code>BoardFactory</code> to create new <code>Board</code>s supported
	 * by any <code>AntichessRuleSet</code>.
	 */
	// This may change later, so that we can allow different settings for
	// different Antichess RuleSets.
	private BoardFactory boardFactory = new BoardFactory() {

		/**
		 * Returns a blank <code>Board</code> supported by an
		 * <code>AntichessRuleSet</code>. That is, an 8 by 8
		 * <code>RectangularBoard</code>.
		 */
		public Board getBlankBoard() {
			return new RectangularBoard();
		}

		/**
		 * Returns a <code>Board</code> that has the initial configuration
		 * that is being used in this <code>AntichessRuleSet</code>. 
		 */
		public Board getInitialBoard() {
			return initialBoard.clone();
		}

		/**
		 * Returns a supported <code>Board</code> out of the given
		 * <code>String</code>.
		 */
		public Board getBoard(String settings) {
			return XmlBoardFactory.xmlToBoard(settings);
		}

	};

	// Fields

	/**
	 * A <code>Board</code> of the supported type, set up with the initial
	 * configuration.
	 */
	private final Board initialBoard;

	// Constructors

	/**
	 * Constructs a new <code>AntichessRuleSet</code> with a default antichess
	 * initial <code>Board</code> as its initial <code>Board</code>.
	 */
	public AntichessRuleSet() {
		this.initialBoard = setUpInitialBoard();
		checkRep();
	}

	/**
	 * Constructs a new <code>AntichessRuleSet</code> with the <code>Board</code>
	 * that corresponds to <code>settings</code> as its initial
	 * <code>Board</code>.
	 * 
	 * @see controller.XmlFactory
	 * @param settings <code>String</code> that represents a <code>Board</code>
	 * in XML format. The format used is described in <code>XmlFactory</code>.
	 */
	public AntichessRuleSet(String settings) {
		this.initialBoard = boardFactory.getBoard(settings);
		checkRep();
	}
	
	// Methods

	/**
	 * Returns a <code>PieceFactory</code> that can create any of the supported
	 * <code>Piece</code>s, given its type and a <code>Board</code> to associate
	 * them to.
	 * 
	 * @see engine.adt.RuleSet
	 */
	public abstract PieceFactory pieceFactory();
	
	/**
	 * Returns a <code>PlyFactory</code> that can create any of the supported
	 * <code>Plies</code> from its <code>String</code> representation and a
	 * <code>Board</code>.
	 * 
	 * @see engine.adt.RuleSet
	 */
	public abstract PlyFactory plyFactory();

	/**
	 * Returns a <code>BoardFactory</code> that can create supported
	 * <code>Board</code>s. These can be either a blank <code>Board</code>, a
	 * <code>Board</code> with the initial configuration that <code>this</code>
	 * used, or a supported <code>Board</code> with any arbitrary configuration
	 * given by its representation in XML format.
	 * 
	 * @see engine.adt.RuleSet, controller.XmlFactory
	 */
	public BoardFactory boardFactory() {
		checkRep();
		return boardFactory;
	}

	/**
	 * Returns a <code>Parser</code> that can facilitate the conversion from an
	 * integer array representing the coordinates of a cell in a
	 * <code>Board</code> to its <code>String</code> representation, and
	 * vice-versa.
	 */
	public Parser getParser() {
		checkRep();
		return new RectangularParser();
	}

	/**
	 * Given a <code>Board</code> and a turn history, tries to continue the game
	 * that the given <code>Board</code> represents, according to the rules of
	 * antichess. It returns a <code>GameInfo</code> object with all the
	 * information needed to continue the game of antichess. This includes a
	 * <code>List</code> of valid <code>Plies</code> for the next
	 * <code>Player</code>, the color of the next <code>Player</code>
	 * (determines whose turn is it next), and any relevant
	 * <code>GameMessage<code>s.
	 * 
	 * @see GameInfo, GameTermination, GameMessage
	 * 
	 * @throws <code>GameTermination</code> if any of the termination conditions
	 * have been met. For an <code>AntichessRuleSet</code>, these include only
	 * checkmate and piece depletion.
	 */
	public GameInfo continueGame(Board board, List<Boolean> turnHistory,
			List<GameMessage> messages) throws GameTermination {
		checkRep();

		// ***** Set up all the variables and stuff needed *****

		// Determine tentative next Player
		boolean nextPlayerWhite;
		if (turnHistory.isEmpty()
				|| turnHistory.get(turnHistory.size() - 1) == null)
			nextPlayerWhite = true;
		else {
			nextPlayerWhite = !turnHistory.get(turnHistory.size() - 1);
		}
		// Determine what Pieces/Plies belong to which player
		List<Piece> nextPlayerPieces = new LinkedList<Piece>(board
				.getPieces(nextPlayerWhite));
		List<Ply> nextPlayerPlies = getPossiblePlies(board, nextPlayerWhite);
		List<Ply> opponentPlayerPlies = getPossiblePlies(board,
				!nextPlayerWhite);

		// Filter Plies. if isInCheck and no plies -> checkmate.
		// if no pieces other player -> piece depletion.
		// if no plies -> stalemate.
		// else return the plies happily.

		List<Ply> validPlies = filterPlies(board, nextPlayerPlies,
				nextPlayerWhite);
		// Check for termination conditions
		if (isInCheck(board, nextPlayerWhite) && validPlies.isEmpty())
			throw new GameTermination(!nextPlayerWhite, AntichessRuleSet.CHECKMATE);
		if (nextPlayerPieces.size() == 1)
			throw new GameTermination(nextPlayerWhite,
					AntichessRuleSet.PIECE_DEPLETION);
		if (validPlies.isEmpty()) { // stalemate - skip turn
			nextPlayerWhite = !nextPlayerWhite;
			validPlies = filterPlies(board, opponentPlayerPlies,nextPlayerWhite);
			if (validPlies.isEmpty()) // No player can move. Is this even possible?
				throw new GameTermination(null, AntichessRuleSet.STALEMATE);
		}

		// Determine new messages, if any (not used for AntichessRuleSet)
		List<GameMessage> newMessages = new ArrayList<GameMessage>();

		checkRep();
		return new GameInfo(validPlies, nextPlayerWhite, newMessages);
	}
	
	/**
	 * Returns a <code>Board</code> which is the result of cloning the given
	 * <code>Board</code> and executing the given <code>Ply</code> on it. This
	 * serves to determine what would happen in the hypothetical situation of
	 * the given <code>Ply</code> being executed on the provided
	 * <code>Board</code>.
	 */
	public Board executeFakePly(Board board, Ply ply) {
		checkRep();
		Board clonedBoard = board.clone();
		// Note that here we have to "clone" the Ply to account for Plies that
		// add Pieces to the Board (e.g. Coronations)
		Ply clonedPly = plyFactory().getPly(ply.toString(), clonedBoard);
		clonedBoard.executePly(clonedPly); // Disregard captured Pieces
		checkRep();
		return clonedBoard;
	}
	
	/**
	 * Returns a <code>String</code> representation of <code>this</code>.
	 */
	public abstract String toString();
	
	/**
	 * Given a <code>Board</code>, a list of <code>Plies</code>, and the color
	 * of the next player, filters out the invalid <code>Plies</code> according
	 * to the rules of the game being played. For antichess, plies that would
	 * put the player in check are filtered out; also, if there are any
	 * <code>Plies</code> that capture a <code>Piece</code> of the opposite
	 * player, then those <code>Plies</code> that do not are filtered out.
	 */
	protected abstract List<Ply> filterPlies(Board board,
			List<Ply> toBeFiltered, boolean isWhite);

	/**
	 * Given a <code>Board</code> and a player color, determines whether that
	 * player is in check or not.
	 */
	protected boolean isInCheck(Board board, boolean isWhite) {
		// Get pieces of the player
		List<Piece> pieces = new LinkedList<Piece>(board.getPieces(isWhite));
		// Get players King and its position
		List<Piece> kings = getPieces(pieces, PieceName.king.toString());
		if (kings.size() != 1)
			throw new RuntimeException("A player must have exactly one " +PieceName.king.toString());
		int[] kingPos = board.getPosition(kings.get(0));
		// Check if king is under attack by the other player
		return isUnderAttack(board, kingPos, !isWhite);
	}

	/**
	 * <p>Determines whether a cell in a given <code>Board</code> is under attack
	 * by the specified player or not. In here, a cell is considered under attack
	 * if any valid <code>Ply</code> from the opponent player has the cell in
	 * question as its ending cell, as determined by the <code>String</code>
	 * representation of the <code>Ply</code>.</p>
	 * 
	 * <p><b>Warning:</b> notice that a <code>Ply</code> may be 'attacking'
	 * some cells other than its ending cell as determined by its
	 * <code>String</code> representation. Such is the case of a
	 * <code>Castle</code>, for example, because in this ply, a rook will switch
	 * places and is therefore 'attacking' its new cell. Thus, this method
	 * should be used with great care.</p>
	 */
	protected boolean isUnderAttack(Board board, int[] pos, boolean attackerColor) {
		List<Ply> possiblePlies = getPossiblePlies(board, attackerColor);
		// Check if any of the attacker's plies attack the position
		List<int[]> opponentEndingCells = CoordinateParser.getCells(
				possiblePlies, false);
		for (int[] endingPos : opponentEndingCells)
			if (Arrays.equals(endingPos, pos))
				return true;
		return false;
	}
	
	/**
	 * Determines whether the given cell represents a cell from the topmost line
	 * for the given color. White <code>Piece</code>s in antichess have the
	 * eighth row as the topmost, and Black <code>Piece</code>s have the first
	 * row as the topmost.
	 */
	protected static boolean isAtEnd(int[] cell, boolean isWhite) {
		if (isWhite) {
			return (cell[1] == 7);
		} else {
			return (cell[1] == 0);
		}
	}

	/**
	 * Given a set of <code>Pieces</code> and a type of <code>Piece</code>,
	 * returns a <code>List</code> containing all the occurrences of the
	 * specified type of <code>Piece</code> in the given set.
	 */
	private List<Piece> getPieces(List<Piece> pieceList, String pieceType) {
		List<Piece> pieces = new LinkedList<Piece>();
		for (Piece piece : pieceList)
			if (piece.getType().equals(pieceType))
				pieces.add(piece);
		return pieces;
	}
	
	/**
	 * Returns a <code>List</code> of all the possible <code>Plies</code> for a
	 * given player in a given <code>Board</code>.
	 * 
	 * @requires <code>board!=null</code>
	 */
	private List<Ply> getPossiblePlies(Board board, boolean isWhite) {
		if (board == null)
			throw new RuntimeException("The Board cant be null");
		List<Ply> possiblePlies = new LinkedList<Ply>();
		for (Piece piece : board.getPieces(isWhite))
			possiblePlies.addAll(piece.getPlies());
		return possiblePlies;
	}

	/**
	 * Returns a <code>Board</code> set up with the default settings. That is,
	 * a <code>RectangularBoard</code> with no unusable cells and the standard
	 * <code>Piece</code> layout. The standard <code>Piece</code> layout is
	 * determined by the supported <code>Piece</code>s themselves. Each of the
	 * supported <code>Piece</code>s is asked to set itself up in a blank 8 by
	 * 8 <code>RectangularBoard</code>.
	 */
	private Board setUpInitialBoard() {
		Board board = new RectangularBoard();
		boolean[] colors = { true, false };
		for (boolean color : colors)
			for (Piece prototype : pieceFactory().getSupportedPieces(board,
					color))
				while (true) {
					try {
						Piece actualPiece = pieceFactory().getPiece(
								prototype.getType(), board, color);
						actualPiece.setUp();
					} catch (InvalidInitialPositionException e) {
						break;
					}
				}
		assert (board.getPieces(true).size() == 16);
		assert (board.getPieces(false).size() == 16);
		return board;
	}
	
	/**
	 * Checks that the representation invariant holds.
	 */
	private void checkRep() {
		if (DebugInfo.DEBUG_RULESET)
			// Check Board nullity
			if (initialBoard==null)
				throw new RuntimeException("A RuleSet's initial Board may " +
						"never be null.");
	}
}