package ruleset.piece;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import debug.DebugInfo;
import ruleset.ply.Move;
import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * <p>A <code>Knight</code> represents a knight in a chess-resembling game. It
 * contains information about its color and the <code>Board</code> it is
 * associated to.</p>
 * 
 * <p>A <code>Knight</code> may move in a 2-dimensional <code>Board</code> to
 * any cell which is exactly two columns and one row or two rows and one column
 * away from the <code>Knight</code>'s position, as long as they are not
 * occupied by another <code>Piece</code> of the same color.</p>
 * 
 * <p><code>Knight</code>s are immutable.</p>
 * 
 * @see engine.adt.Piece
 * 
 * @specfield board // the <code>Board</code> to which this <code>Knight</code>
 * is associated to.
 */
public class Knight extends Piece {

	// Do AF and RI

	/**
	 * Returns a new <code>Knight</code> of the specified color and associates
	 * it to the provided <code>Board</code>.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>Knight</code> of the specified color. That is, a
	 * white <code>Knight</code> if <code>isWhite</code> is <code>true</code>,
	 * and a black <code>Knight</code> otherwise.
	 */
	public Knight(boolean isWhite, Board board) {
		super(isWhite,board);
		checkRep();
	}
	
	/**
	 * Updates the information a Knight needs to consider to determine its valid
	 * Plies. This method should be called after any Ply has been executed.
	 * 
	 * A Knight does not need any particular information to determine its Plies. 
	 */
	public void updateInfo(Ply ply) {
		checkRep();
		// Knight needs no info for Standard Antichess (no castling)
	}
	
	/**
	 * Notifies the Knight when its been added to its associated Board, so it
	 * can initialize any information it needs.
	 *
	 * A Knight does not need any particular information to determine its Plies.
	 */
	public void added() {
		checkRep();
		// Knight needs no info for Standard Antichess
	}
	
	/**
	 * Notifies the Knight when its been removed from its associated Board, so
	 * it can modify any information it needs. 
	 * 
	 * A Knight does not need any particular information to determine its Plies.
	 */
	public void removed() {
		checkRep();
		// Knight needs no info for Standard Antichess
	}
	
	/**
	 * Returns a list with the valid Plies of this <code>Knight</code>, as
	 * specified in the overview.
	 * 
	 * @return A <code>List</code> of valid <code>Plies</code> for this
	 * <code>Knight</code>; if the <code>Knight</code> is not currently in a
	 * <code>Board</code>, the <code>List</code> will be empty.
	 */
	public List<Ply> getPlies() {
		checkRep();
		Board board = getBoard();
		// Create list of valid cells to move to
		List<int[]> validCells = new LinkedList<int[]>();
		// Get the possible cell to move to
		List<int[]> possibleCells = getPossibleCells();
		// Check if cell is empty or occupied by Piece of another color
		for (int[] cell : possibleCells) {
			if (board.isEmpty(cell)) {
				validCells.add(cell);
			} else if ((board.getPiece(cell).isWhite() && !isWhite()) ||
					(!board.getPiece(cell).isWhite() && isWhite())) {
				validCells.add(cell);
			}
		}
		// Create list of valid moves
		List<Ply> validPlies = new ArrayList<Ply>(validCells.size());
		for (int[] validCell : validCells) {
			validPlies.add(new Move(board.getPosition(this),validCell));
		}
		checkRep();
		return validPlies;
	}
	
	@Override
	/**
	 * Specified by engine.adt.Piece
	 */
	public String getType() {
		checkRep();
		return "knight";
	}

	/**
	 * <p>Returns the initial position of a <code>Knight</code>. This method
	 * makes stronger assumption that the <code>Knight</code> plays in a
	 * <code>RectangularBoard</code> with dimensions greater than or equal to 8.
	 * Its initial position then corresponds to the initial position of knights
	 * in a regular game of chess. That is, if the <code>Knight</code> is white,
	 * the initial positions are b1 or g1, and b8 or g8 otherwise.</p>
	 * 
	 * <p>Note that use of this method is not necessary for the correct
	 * functioning of a <code>Knight</code>. In other words, a
	 * <code>Knight</code> could be placed in any arbitrary cell in a
	 * 2-dimensional <code>Board</code>, and it all its functionality will work
	 * appropriately.</p>
	 */
	protected int[] initialPos() {
		Board board = getBoard();
		int row;
		int[] pos = new int[2];
		if (isWhite()) {row = 0;} else {row = 7;}
		pos[1] = row;
		pos[0] = 1;
		if (!board.isEmpty(pos))
			pos[0] = 6;
		return pos;
	}
	
	/**
	 * Returns the possible cells a <code>Knight</code> could move to from its
	 * current cell. This does not take into account any rules other than how a
	 * <code>Knight</code> is allowed to move, and if the the possible cells are
	 * usable or not. If the <code>Knight</code> is not currently in a
	 * <code>Board</code>, the <code>List</code> will be empty.
	 */
	private List<int[]> getPossibleCells() {
		List<int[]> possibleCells = new ArrayList<int[]>();
		Board board = getBoard();
		if (board.contains(this)) {
			int[] cell = board.getPosition(this);
			int[] temp = new int[2];
			// northwest
			temp[0] = cell[0] - 1;
			temp[1] = cell[1] + 2;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// northeast
			temp[0] = cell[0] + 1;
			temp[1] = cell[1] + 2;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// eastnorth
			temp[0] = cell[0] + 2;
			temp[1] = cell[1] + 1;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// eastsouth
			temp[0] = cell[0] + 2;
			temp[1] = cell[1] - 1;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// southeast
			temp[0] = cell[0] + 1;
			temp[1] = cell[1] - 2;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// southwest
			temp[0] = cell[0] - 1;
			temp[1] = cell[1] - 2;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// westsouth
			temp[0] = cell[0] - 2;
			temp[1] = cell[1] - 1;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
			// westnorth
			temp[0] = cell[0] - 2;
			temp[1] = cell[1] + 1;
			if (board.isUsable(temp))
				possibleCells.add(temp.clone());
		}		
		return possibleCells;
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
	}
	
}