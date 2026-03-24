package ruleset.piece;

import java.util.ArrayList;
import java.util.List;

import ruleset.board.RectangularBoard;
import ruleset.ply.Add;

import debug.DebugInfo;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * A <code>GravityChip</code> represents a simple piece that resembles a poker
 * chip. <code>GravityChip</code>s can play games on
 * <code>RectangularBoard</code>s and have very simple motion rules. Its valid
 * <code>Plies</code> are simply adding itself to the <code>Board</code> at some
 * cell. These available cells are determined by taking a cell in each column,
 * such that the cell is exactly above the topmost unavailable (unusable or
 * occupied) cell of that column. Thus, a <code>GravityChip</code> acts as if it
 * was being dropped from above the <code>Board</code> into a column, and
 * gravity takes it down as far as it can go.
 * 
 * @see engine.adt.Piece
 * @specfield board // the <code>Board</code> to which this
 * <code>GravityChip</code> is associated to.
 */
public class GravityChip extends Piece {
	
	/**
	 * Default length of the <code>Board</code> this <code>GravityChip</code>
	 * assumes to be playing at.
	 */
	public static final int BOARD_LENGTH = 7;
	
	/**
	 * Default height of the <code>Board</code> this <code>GravityChip</code>
	 * assumes to be playing at.
	 */
	public static final int BOARD_HEIGHT = 6;
	
	/**
	 * The length of the <code>Board</code> this <code>GravityChip</code>
	 * assumes to be playing at.
	 */
	private final int boardLength;
	
	/**
	 * The height of the <code>Board</code> this <code>GravityChip</code>
	 * assumes to be playing at.
	 */
	private final int boardHeight;
	
	// Abstraction Function:
	// 	AF(c) = a chip such that:
	//			if isWhite()==true, the chip is white
	//			otherwise it is black.
	
	// Representation Invariant:
	// * board != null
	// * board.length == boardLength
	// * board.height == boardHeight
	
	/**
	 * Returns a new <code>GravityChip</code> of the specified color and
	 * associates it to the provided <code>Board</code>. The returned
	 * <code>GravityChip</code> will assume it plays on a <code>Board</code>
	 * of the default dimensions.
	 * 
	 * @requires <code>board!=null</code>, board is of the default dimensions.
	 */
	public GravityChip(boolean isWhite, Board board) {
		this(isWhite, board, BOARD_LENGTH, BOARD_HEIGHT);
	}
	
	/**
	 * Returns a new <code>GravityChip</code> of the specified color and
	 * associates it to the provided <code>Board</code>. The returned
	 * <code>GravityChip</code> will assume it plays on a <code>Board</code>
	 * of the specified dimensions.
	 * 
	 * @requires <code>board!=null</code>, board is of the specified dimensions.
	 */
	public GravityChip(boolean isWhite, Board board, int length, int height) {
		super(isWhite, board);
		boardLength = length;
		boardHeight = height;
		checkRep();
	}
	
	/**
	 * Updates the information a <code>Piece</code> needs to determine its
	 * valid <code>Plies</code>. This method should be called after any
	 * <code>Ply</code> has been executed in the <code>Board</code> at which
	 * the <code>Piece</code> is currently at.
	 * 
	 * A <code>GravityChip</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void updateInfo(Ply ply) {
		checkRep();
		// no info needed
	}
	
	/**
	 * Notifies the <code>Piece</code> when its been added to its associated
	 * <code>Board</code>, so it can initialize any information it needs.
	 *
	 * A <code>GravityChip</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void added() {
		checkRep();
		// no info needed
	}

	/**
	 * Notifies the <code>Piece</code> when its been removed from its associated
	 * <code>Board</code>, so it can modify any information it needs.
	 * 
	 * A <code>GravityChip</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void removed() {
		checkRep();
		// no info needed
	}

	/**
	 * Returns a list with the valid Plies of this <code>GravityChip</code>, as
	 * specified in the overview.
	 * 
	 * @return A <code>List</code> of valid <code>Plies</code> for this
	 * <code>GravityChip</code>; if the <code>GravityChip</code> is not currently
	 * in a <code>Board</code>, the <code>List</code> will be empty.
	 */
	public List<Ply> getPlies() {
		checkRep();
		List<Ply> validPlies = new ArrayList<Ply>();
		if (!getBoard().contains(this)) {
			int[] temp = null;
			for (int i = 0; i < boardLength; i++) {
				temp = fallDownOnColumn(i);
				if (temp!=null) {
					validPlies.add(new Add(temp.clone(), this));
				}
			}
		}
		checkRep();
		assert validPlies.size() <= boardLength;
		return validPlies;
	}

	@Override
	/**
	 * Specified by engine.adt.Piece
	 */
	public String getType() {
		checkRep();
		return "chip";
	}

	/**
	 * Returns the initial position of a <code>GravityChip</code>, which is
	 * actually outside a <code>Board</code>. Thus, when a
	 * <code>GravityChip</code> is asked to set itself up, it will simply not
	 * add itself to its <code>Board</code> and say it is done setting up.
	 */
	protected int[] initialPos() {
		// Does not have an initial position;
		return null;
	}
	
	/**
	 * For column <code>column</code>, returns the lowest available cell in that
	 * column such that all the cells above it are available too.
	 */
	private int[] fallDownOnColumn(int column) {
		Board board = getBoard();
		int[] initial = {column, boardHeight-1};
		if (board.isUsable(initial) && board.isEmpty(initial))
			return fallDownHelper(initial);
		else
			return null;
	}
	
	/**
	 * Given a cell, returns the lowest available cell in its column such that
	 * all the cells above it are available too and such that is is below the
	 * initially provided cell. This works by using recursion: if the cell just
	 * below the provided cell is available, then it recurses by calling itself
	 * with the next cell; otherwise, it return the provided cell.
	 * 
	 * @requires <code>getBoard().isUsable(current) &&
	 * getBoard().isEmpty(current)<code>
	 */
	private int[] fallDownHelper(int[] current) {
		Board board = getBoard();
		int[] next = {current[0], current[1]-1};
		if (board.isUsable(next) && board.isEmpty(next))
			return fallDownHelper(next);
		else
			return current;
	}
	
	/**
	 * Checks that the representation invariant holds.
	 */
	private void checkRep() {
		if (DebugInfo.DEBUG_RULESET)
			// Check Board nullity
			if (getBoard()==null)
				throw new RuntimeException("A Piece's associated Board may " +
						"never be null.");
			RectangularBoard board = (RectangularBoard) getBoard();
			if (board.getLength() != boardLength)
				throw new RuntimeException("A GravityChip cannot play on a " +
						"Board of a different length than " + boardLength);
			if (board.getHeight() != boardHeight)
				throw new RuntimeException("A GravityChip cannot play on a " +
						"Board of a different height than " + boardHeight);
	}

}
