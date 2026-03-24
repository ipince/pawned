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
 * <p>A <code>Bishop</code> represents a bishop in a chess-resembling game. It
 * contains information about its color and the <code>Board</code> it is
 * associated to.</p>
 * 
 * <p>A <code>Bishop</code> may move in a 2-dimensional <code>Board</code> along
 * diagonals in any direction through contiguous sequences of usable and empty
 * cells. A <code>Bishop</code> may also move through those cells and onto a
 * cell occupied by a <code>Piece</code> of another color (capturing it).
 * <code>Bishop</code>s may never jump over any <code>Piece</code>s while
 * traveling along diagonals.</p>
 * 
 * <p><code>Bishop</code>s are immutable.</p>
 * 
 * @see engine.adt.Piece
 * 
 * @specfield board // the <code>Board</code> to which this <code>Bishop</code>
 * is associated to.
 */
public class Bishop extends Piece {

	// Do AF and RI
	
	/**
	 * Returns a new <code>Bishop</code> of the specified color and associates
	 * it to the provided <code>Board</code>.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>Bishop</code> of the specified color. That is, a
	 * white <code>Bishop</code> if <code>isWhite</code> is <code>true</code>,
	 * and a black <code>Bishop</code> otherwise.
	 */
	public Bishop(boolean isWhite, Board board) {
		super(isWhite,board);
		checkRep();
	}
	
	/**
	 * Updates the information a <code>Piece</code> needs to determine its
	 * valid <code>Plies</code>. This method should be called after any
	 * <code>Ply</code> has been executed in the <code>Board</code> at which
	 * the <code>Piece</code> is currently at.
	 * 
	 * A <code>Bishop</code> does not need any particular information to
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
	 * A <code>Bishop</code> does not need any particular information to
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
	 * A <code>Bishop</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void removed() {
		checkRep();
		// no info needed
	}
	
	/**
	 * Returns a list with the valid Plies of this <code>Bishop</code>, as
	 * specified in the overview.
	 * 
	 * @return A <code>List</code> of valid <code>Plies</code> for this
	 * <code>Bishop</code>; if the <code>Bishop</code> is not currently in a
	 * <code>Board</code>, the <code>List</code> will be empty.
	 */
	public List<Ply> getPlies() {
		checkRep();
		Board board = getBoard();
		// Create list of valid cells to move to
		List<int[]> validCells = new LinkedList<int[]>();
		List<Ply> validPlies = new ArrayList<Ply>();
		if (board.contains(this)) {
			int[] pos = board.getPosition(this);
			int[] temp = new int[2];
			// Get positions where bishop can move (diagonally)
			boolean[] forwardOrBack = {true, false};
			boolean[] rightOrLeft = {true, false};
			for (int i = 0; i<forwardOrBack.length; i++) {
				for (int j = 0; j<rightOrLeft.length; j++) {
					temp = oneCellDiagonally(pos,forwardOrBack[i],rightOrLeft[j]);
					while (board.isUsable(temp) && (board.isEmpty(temp) ||
							((board.getPiece(temp).isWhite() && !isWhite()) ||
									(!board.getPiece(temp).isWhite() && isWhite())))) {
						validCells.add(temp.clone());
						if (!board.isEmpty(temp) && 
								((board.getPiece(temp).isWhite() && !isWhite()) ||
								(!board.getPiece(temp).isWhite() && isWhite())))
							break;
						temp = oneCellDiagonally(temp,forwardOrBack[i],rightOrLeft[j]);
					}
				}
			}
			// Create list of valid moves
			for (int[] validCell : validCells)
				validPlies.add(new Move(pos,validCell));
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
		return "bishop";
	}

	/**
	 * <p>Returns the initial position of a <code>Bishop</code>. This method
	 * makes stronger assumption that the <code>Bishop</code> plays in a
	 * <code>RectangularBoard</code> with dimensions greater than or equal to 8.
	 * Its initial position then corresponds to the initial position of bishops
	 * in a regular game of chess. That is, if the <code>Bishop</code> is white,
	 * the initial positions are c1 or f1, and c8 or f8 otherwise.</p>
	 * 
	 * <p>Note that use of this method is not necessary for the correct
	 * functioning of a <code>Bishop</code>. In other words, a
	 * <code>Bishop</code> could be placed in any arbitrary cell in a
	 * 2-dimensional <code>Board</code>, and it all its functionality will work
	 * appropriately.</p>
	 */
	protected int[] initialPos() {
		Board board = getBoard();
		int row;
		int[] pos = new int[2];
		if (isWhite()) {row = 0;} else {row = 7;}
		pos[1] = row;
		pos[0] = 2;
		if (!board.isEmpty(pos))
			pos[0] = 5;
		return pos;
	}
	
	/**
	 * Given a cell, returns a cell that is one cell diagonally next to it,
	 * considering whether we are going north, south, east or west.
	 */
	private int[] oneCellDiagonally(int[] cell, boolean goNorth, boolean goEast) {
		if (goNorth) {
			if (goEast) {
				int[] pos = {cell[0] + 1, cell[1] + 1};
				return pos;
			} else { 
				int[] pos = {cell[0] - 1, cell[1] + 1};
				return pos;
			}
		} else {
			if (goEast) {
				int[] pos = {cell[0] + 1, cell[1] - 1};
				return pos;
			} else { 
				int[] pos = {cell[0] - 1, cell[1] - 1};
				return pos;
			}
		}
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
