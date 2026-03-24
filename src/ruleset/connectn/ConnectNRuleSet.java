package ruleset.connectn;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ruleset.board.CoordinateParser;
import ruleset.board.RectangularBoard;
import ruleset.board.RectangularParser;
import ruleset.board.XmlBoardFactory;
import ruleset.piece.GravityChip;
import ruleset.ply.Add;
import debug.DebugInfo;
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
 * A general <code>RuleSet</code> for connect-n. It encodes the rules for
 * playing connect-n. This game is a simply generalization of the widely known
 * connect-4. The rules are the same as those described
 * <a href="http://en.wikipedia.org/wiki/Connect_Four"> here</a>, but 
 * replacing "4" with "n". Notice that when playing on an x by y board, then
 * n must be less than max(x,y) to allow for a possible victory.
 */
public class ConnectNRuleSet implements RuleSet {

	// Termination conditions and constants
	
	/**
	 * The <code>String</code> representation of the connecting pieces
	 * termination condition.
	 */
	public static final String CONNECTED = "connecting pieces";
	
	/**
	 * The <code>String</code> representation of the draw termination
	 * condition (no more valid <code>Plies</code>).
	 */
	public static final String DRAW = "board space depletion";

	/**
	 * Enum class containing the <code>Piece</code>s supported by this
	 * <code>RuleSet</code>. This is only one, a chip.
	 */
	enum PieceName {
		chip;
	}
	
	/**
	 * Default length of the <code>Board</code> this <code>RuleSet</code> will
	 * use.
	 */
	public static final int BOARD_LENGTH = 7;
	
	/**
	 * Default height of the <code>Board</code> this <code>RuleSet</code> will
	 * use.
	 */
	public static final int BOARD_HEIGHT = 6;
	
	/**
	 * Default value of 'n' used to play connect-n.
	 */
	public static final int N = 4;
	
	/**
	 * A <code>PieceFactory</code> that can create any of the supported
	 * <code>Piece</code>s, given its type and a <code>Board</code> to associate
	 * them to. The only <code>Piece</code>s supported by
	 * <code>ConnectNRuleSet</code> are <code>GravityChip</code>s.
	 * 
	 * @see engine.adt.RuleSet
	 */
	private PieceFactory pieceFactory = new PieceFactory() {
		
		/**
		 * Returns a new <code>Piece</code> of the specified type and color,
		 * which is associated to the provided <code>Board</code>. Notice that
		 * the provided <code>Board</code> must be of the dimensions this
		 * <code>RuleSet</code> is working with.
		 * 
		 * @requires <code>name</code> to represent the type of a
		 * <code>Piece</code> supported by this <code>RuleSet</code>,
		 * <code>board</code> to be of the dimensions supported by this
		 * <code>RuleSet</code>.
		 */
		public Piece getPiece(String name, Board board, boolean isWhite) {
			if (name == null)
				throw new IllegalArgumentException("Illegal argument for name");

			PieceName pieceName = PieceName.valueOf(name);
			if (pieceName == PieceName.chip)
				try {
					return new GravityChip(isWhite, board, boardLength, boardHeight);
				} catch (RuntimeException re) {
					throw new IllegalArgumentException("Illegal argument for board");
				}
			else
				throw new IllegalArgumentException("Illegal argument " + name); 
		}

		/**
		 * Returns the set of <code>Piece</code>s that this <code>RuleSet</code>
		 * supports. The <code>Piece</code>s are associated to the given
		 * <code>Board</code> and are of the specified color.
		 */ 
		public Set<Piece> getSupportedPieces(Board board, boolean isWhite) {
			Set<Piece> pieces = new HashSet<Piece>();
			for (PieceName name : PieceName.values()) {
				pieces.add(getPiece(name.toString(), board, isWhite));
			}
			return pieces;
		}
		
		/**
		 * Returns a <code>List</code> with the types of <code>Piece</code>s
		 * that this <code>RuleSet</code> supports in some order determined
		 * by the <code>RuleSet</code>.
		 */
		public List<String> getOrderedPieces() {
			List<String> orderedPieces = new ArrayList<String>(PieceName.values().length);
			for (PieceName name : PieceName.values())
				orderedPieces.add(name.toString());
			return orderedPieces;
		}
		
	};
	
	/**
	 * A <code>PlyFactory</code> that can create any type of <code>Plies</code>
	 * supported by this <code>RuleSet</code>. For <code>ConnectNRuleSet</code>,
	 * these include only <code>Add</code>s.
	 * 
	 * @see ruleset.ply.Add
	 */
	private PlyFactory plyFactory = new PlyFactory() {

		/**
		 * Returns a <code>Ply</code> whose <code>String</code> representation
		 * matches the provided one, <code>name</code>. Any <code>Piece</code>s
		 * that form part of the returned <code>Ply</code> are associated to the
		 * given <code>Board</code>. Notice that the returned <code>Ply</code>
		 * is not checked for validity in the given <code>Board</code>. This
		 * method only guarantees that the returned <code>Ply</code> is
		 * supported within <code>ConnectNRuleSet</code>.
		 * 
		 * @see ruleset.board.RectangularParser
		 * 
		 * @requires <code>name</code> to be in the format supported by this
		 * <code>RuleSet</code> (this format is specified in the
		 * <code>Parser</code> for this <code>RuleSet</code>),
		 * <code>board</code> to be of the dimensions supported by this
		 * <code>RuleSet</code>.
		 */
		public Ply getPly(String name, Board board) {
			String[] ply = name.split("\\+");
			try {
				int[] cell = CoordinateParser.getCell(ply[1], true);
				Piece chip = pieceFactory.getPiece(PieceName.chip.toString(), board, ply[0].equals("t"));
				return new Add(cell, chip);
			} catch (IllegalArgumentException iae) {
				throw new IllegalArgumentException("Illegal arguments for getPly");
			}
		}
	};
	
	/**
	 * A <code>BoardFactory</code> to create new <code>Board</code>s supported
	 * by this <code>RuleSet</code>.
	 */
	private BoardFactory boardFactory = new BoardFactory() {

		/**
		 * Returns a blank <code>Board</code> of the default size that is
		 * supported by this <code>RuleSet</code>. That is a 
		 * <code>RectangularBoard</code> with 6 rows and 7 columns.
		 */
		public Board getBlankBoard() {
			return new RectangularBoard(boardLength, boardHeight);
		}

		/**
		 * Returns a <code>Board</code> that has the initial configuration
		 * that is being used in this <code>RuleSet</code>. 
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
	 * The length of the <code>Board</code> being used.
	 */
	private final int boardLength;
	
	/**
	 * The height of the <code>Board</code> being used.
	 */
	private final int boardHeight;
	
	/**
	 * The value of 'n' being used.
	 */
	private final int n;

	/**
	 * A <code>Board</code> of the supported type, set up with the initial
	 * configuration.
	 */
	private final Board initialBoard;
	
	// Constructors

	/**
	 * Constructs a new <code>RuleSet</code> with the default values as its
	 * parameters.
	 * @see #ConnectNRuleSet(int, int, int)
	 */
	public ConnectNRuleSet() {
		this(N, BOARD_LENGTH, BOARD_HEIGHT);
	}
	
	/**
	 * Constructs a new <code>RuleSet</code> for connect-n with the specified
	 * value for 'n' and a default <code>Board</code>.
	 * @param n The value this <code>RuleSet</code> will use to determine the
	 * rules.
	 * @see #ConnectNRuleSet(int, int, int)
	 */
	public ConnectNRuleSet(int n) {
		this(n, BOARD_LENGTH, BOARD_HEIGHT);
	}
	
	/**
	 * Constructs a new <code>RuleSet</code> for connect-n with the specified
	 * dimensions for a <code>Board</code> and the default value for 'n'.
	 * @param length The length of the <code>Board</code> being used.
	 * @param height The height of the <code>Board</code> being used.
	 * @see #ConnectNRuleSet(int, int, int)
	 */
	public ConnectNRuleSet(int length, int height) {
		this(N, length, height);
	}
	
	/**
	 * Constructs a new <code>RuleSet</code> for connect-n with the specified
	 * dimensions for a <code>Board</code> and the specified value for 'n'.
	 * That is, if <code>n = length = height = 3</code>, then the returned
	 * <code>RuleSet</code> will encode the rules for connect-3 in a 3 by 3
	 * <code>Board</code>.
	 * @param n The value this <code>RuleSet</code> will use to determine the
	 * rules.
	 * @param length The length of the <code>Board</code> being used.
	 * @param height The height of the <code>Board</code> being used.
	 */
	public ConnectNRuleSet(int n, int length, int height) {
		this.n = n;
		this.boardLength = length;
		this.boardHeight = height;
		this.initialBoard = setUpInitialBoard(length, height);
		checkRep();
	}

	/**
	 * Constructs a new <code>RuleSet</code> for connect-n with the
	 * <code>Board</code> that corresponds to <code>settings</code> as its
	 * initial <code>Board</code>. The value of 'n' for this
	 * <code>RuleSet</code> is the default one.
	 * 
	 * @see controller.XmlFactory
	 * @param settings <code>String</code> that represents a <code>Board</code>
	 * in XML format. The format used is described in <code>XmlFactory</code>.
	 */
	public ConnectNRuleSet(String settings) {
		this.n = N;
		this.boardLength = BOARD_LENGTH;
		this.boardHeight = BOARD_HEIGHT;
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
	public PieceFactory pieceFactory() {
		checkRep();
		return pieceFactory;
	}
	
	/**
	 * Returns a <code>PlyFactory</code> that can create any of the supported
	 * <code>Plies</code> from its <code>String</code> representation and a
	 * <code>Board</code>.
	 * 
	 * @see engine.adt.RuleSet
	 */
	public PlyFactory plyFactory() {
		checkRep();
		return plyFactory;
	}

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
	 * connect-n. It returns a <code>GameInfo</code> object with all the
	 * information needed to continue the game of connect-n. This includes a
	 * <code>List</code> of valid <code>Plies</code> for the next
	 * <code>Player</code>, the color of the next <code>Player</code>
	 * (determines whose turn is it next), and any relevant
	 * <code>GameMessage<code>s.
	 * 
	 * @see GameInfo, GameTermination, GameMessage
	 * 
	 * @throws <code>GameTermination</code> if any of the termination conditions
	 * have been met.
	 */
	public GameInfo continueGame(Board board, List<Boolean> turnHistory,
			List<GameMessage> messages) throws GameTermination {
		checkRep();

		// Determine tentative next Player
		boolean nextPlayerWhite;
		if (turnHistory.isEmpty()
				|| turnHistory.get(turnHistory.size() - 1) == null)
			nextPlayerWhite = true;
		else {
			nextPlayerWhite = !turnHistory.get(turnHistory.size() - 1);
		}
		
		// Determine the plies for the next player
		Piece chip = pieceFactory.getPiece(PieceName.chip.toString(), board, nextPlayerWhite);
		List<Ply> validPlies = chip.getPlies();
		
		// Check for termination conditions (first for a win, then draw)
		boolean terminated = false;
		Boolean winnerIsWhite = null;
		int[] cell = null;
		if (messages.isEmpty()) {
			// Check all cells
			for (int row = 0; row < boardLength; row++) {
				for (int col = 0; col < boardHeight; col++) {
					cell = new int[] {row, col};
					terminated = checkCell(cell, board);
					if (terminated) {
						winnerIsWhite = board.getPiece(cell).isWhite();
						break;
					}
				}
				if (terminated)
					break;
			}
		} else {
			// Determine which cell has changed
			String previousPlies = messages.get(messages.size()-1).getMessage();
			previousPlies = previousPlies.replaceAll("[\\[\\]]", "");
			String[] prevPlies = previousPlies.split(",");
			for (String playedPly : prevPlies) {
				cell = CoordinateParser.getCell(playedPly.split("\\+")[1], true);
				if (!board.isEmpty(cell))
					break;
			}
			// Check that cell
			terminated = checkCell(cell, board);
			if (terminated)
				winnerIsWhite = board.getPiece(cell).isWhite();
		}
		
		// Check for win
		if (terminated)
			throw new GameTermination(winnerIsWhite, CONNECTED);
		
		if (validPlies.isEmpty()) // Draw
			throw new GameTermination(null, DRAW);

		// Add available pieces as messages for next iteration.
		List<GameMessage> newMessages = new ArrayList<GameMessage>();
		newMessages.add(new GameMessage(null, validPlies.toString()));

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
		// add Pieces to the Board (e.g. Add)
		Ply clonedPly = plyFactory().getPly(ply.toString(), clonedBoard);
		clonedBoard.executePly(clonedPly); // Disregard captured Pieces
		checkRep();
		return clonedBoard;
	}
	
	/**
	 * Returns a <code>String</code> representation of <code>this</code>.
	 */
	public String toString() {
		return ("connect-" + n + "-" + boardLength + "-" + boardHeight);
	}
	
	/**
	 * Determines whether the given cell forms part of a shift which
	 * makes a termination condition for connecting pieces.
	 */
	private boolean checkCell(int[] cell, Board board) {
		return (checkLinearShift(cell, true, board) || checkLinearShift(cell, false, board) ||
				checkDiagonalShift(cell,true,board) || checkDiagonalShift(cell,false, board));
	}
	
	/**
	 * Determines whether <code>origin</code> forms part of a horizontal or
	 * vertical shift which makes a termination condition for connecting pieces.
	 */
	private boolean checkLinearShift(int[] origin, boolean horizontal, Board board) {
		if (!board.isUsable(origin) || board.isEmpty(origin))
			return false;
		int count = 0;
		int[] cell = null;
		final Boolean color = board.getPiece(origin).isWhite();
		// Travel right
		for (int i = origin[(horizontal ? 0:1)]; i < origin[(horizontal ? 0:1)]+n; i++) {
			cell = new int[] {(horizontal ? i : origin[0]), (horizontal ? origin[1] : i)};
			if (board.isUsable(cell) && !board.isEmpty(cell) && board.getPiece(cell).isWhite()==color)
				count++;
			else
				break;
		}
		// Travel left
		for (int i = origin[(horizontal ? 0:1)]; i > origin[(horizontal ? 0:1)]-n; i--) {
			cell = new int[] {(horizontal ? i : origin[0]), (horizontal ? origin[1] : i)};
			if (board.isUsable(cell) && !board.isEmpty(cell) && board.getPiece(cell).isWhite()==color)
				count++;
			else
				break;
		}
		return (count>n);
	}
	
	/**
	 * Determines whether <code>origin</code> forms part of a diagonal shift
	 * which makes a termination condition for connecting pieces.
	 */
	private boolean checkDiagonalShift(int[] origin, boolean positiveSlope, Board board) {
		if (!board.isUsable(origin) || board.isEmpty(origin))
			return false;
		int count = 0;
		int[] cell = null;
		final Boolean color = board.getPiece(origin).isWhite();
		// Travel right
		for (int i = 0; i < n; i++) {
			cell = new int[] {origin[0]+i, (positiveSlope ? origin[1]+i : origin[1]-i)};
			if (board.isUsable(cell) && !board.isEmpty(cell) && board.getPiece(cell).isWhite()==color)
				count++;
			else
				break;
		}
		// Travel left
		for (int i = 0; i < n; i++) {
			cell = new int[] {origin[0]-i, (positiveSlope ? origin[1]-i : origin[1]+i)};
			if (board.isUsable(cell) && !board.isEmpty(cell) && board.getPiece(cell).isWhite()==color)
				count++;
			else
				break;
		}
		return (count>n);
	}
	
	/**
	 * Returns a <code>Board</code> set up with the default settings. That is,
	 * a <code>RectangularBoard</code> with no unusable cells and the standard
	 * <code>Piece</code> layout. The standard <code>Piece</code> layout is
	 * determined by the supported <code>Piece</code>s themselves. Each of the
	 * supported <code>Piece</code>s is asked to set itself up in a blank 6 by
	 * 7 <code>RectangularBoard</code>.
	 */
	private Board setUpInitialBoard(int length, int height) {
		Board board = new RectangularBoard(length, height);
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
		assert (board.getPieces(true).size() == 0);
		assert (board.getPieces(false).size() == 0);
		return board;
	}
	
	/**
	 * Checks that the representation invariant holds.
	 */
	private void checkRep() {
		if (DebugInfo.DEBUG_RULESET) {
			if (!(n >= 2))
				throw new RuntimeException("Connect-n cannot be played if n is" +
						" less than 2");
			if (!(boardLength >= 2 && boardHeight >= 2))
				throw new RuntimeException("Connect-n cannot be played with a " +
						"board of dimensions " + boardLength + " by " + boardHeight);
			if (!(n <= Math.min(boardLength, boardHeight)))
				throw new RuntimeException("Connect-n cannot be played if " +
						"n is not smaller than the largest dimension of the " +
						"board being used.");
		}
	}

}
