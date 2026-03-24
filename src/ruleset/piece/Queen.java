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
 * <p>A <code>Queen</code> represents a queen in a chess-resembling game. It
 * contains information about its color and the <code>Board</code> it is
 * associated to.</p>
 * 
 * <p>A <code>Queen</code> may move in a 2-dimensional <code>Board</code> along
 * rows, columns, or diagonals in any direction through contiguous sequences of
 * usable and empty cells. A <code>Queen</code> may also move through those
 * cells and onto a cell occupied by a <code>Piece</code> of another color
 * (capturing it). <code>Queen</code>s may never jump over any
 * <code>Piece</code>s while traveling along rows, columns, or diagonals.</p>
 * 
 * <p><code>Queen</code>s are immutable.</p>
 * 
 * @see engine.adt.Piece
 * 
 * @specfield board // the <code>Board</code> to which this <code>Queen</code>
 * is associated to.
 */
public class Queen extends Piece {

	// Do AF and RI

	/**
	 * Returns a new <code>Queen</code> of the specified color and associates
	 * it to the provided <code>Board</code>.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>Queen</code> of the specified color. That is, a
	 * white <code>Queen</code> if <code>isWhite</code> is <code>true</code>,
	 * and a black <code>Queen</code> otherwise.
	 */
	public Queen(boolean isWhite, Board board) {
		super(isWhite,board);
		checkRep();
	}
	
	/**
	 * Updates the information a <code>Piece</code> needs to determine its
	 * valid <code>Plies</code>. This method should be called after any
	 * <code>Ply</code> has been executed in the <code>Board</code> at which
	 * the <code>Piece</code> is currently at.
	 * 
	 * A <code>Queen</code> does not need any particular information to
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
	 * A <code>Queen</code> does not need any particular information to
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
	 * A <code>Queen</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void removed() {
		checkRep();
		// no info needed
	}
	
	/**
	 * Returns a list with the valid Plies of this <code>Queen</code>, as
	 * specified in the overview.
	 * 
	 * @return A <code>List</code> of valid <code>Plies</code> for this
	 * <code>Queen</code>; if the <code>Queen</code> is not currently in a
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
			// Get positions where queen can move (horizontally or vertically)
			for (int i = 0; i<4; i++) {
				// 0 = north; 1 = east; 2 = south; 3 = west
				temp = oneCellNext(pos,i);
				while (board.isUsable(temp) && (board.isEmpty(temp) ||
						((board.getPiece(temp).isWhite() && !isWhite()) ||
								(!board.getPiece(temp).isWhite() && isWhite())))) {
					validCells.add(temp.clone());
					if (!board.isEmpty(temp) && 
							((board.getPiece(temp).isWhite() && !isWhite()) ||
							(!board.getPiece(temp).isWhite() && isWhite())))
						break;
					temp = oneCellNext(temp,i);
				}
			}
			// Get positions where queen can move (diagonally)
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
		return "queen";
	}

	/**
	 * 
	 * <p>Returns the initial position of a <code>Queen</code>. This method
	 * makes stronger assumption that the <code>Queen</code> plays in a
	 * <code>RectangularBoard</code> with dimensions greater than or equal to 8.
	 * Its initial position then corresponds to the initial position of queens
	 * in a regular game of chess. That is, if the <code>Queen</code> is white,
	 * the initial position is d1, and d8.</p>
	 * 
	 * <p>Note that use of this method is not necessary for the correct
	 * functioning of a <code>Queen</code>. In other words, a
	 * <code>Queen</code> could be placed in any arbitrary cell in a
	 * 2-dimensional <code>Board</code>, and it all its functionality will work
	 * appropriately.</p>
	 */
	protected int[] initialPos() {
		if (isWhite()) {
			int[] pos = {3, 0};
			return pos;
		} else {
			int[] pos = {3, 7};
			return pos;
		}
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
	 * Given a cell, returns a cell that is one cell horizontally or vertically
	 * next to it, considering whether we are going north, south, east or west.
	 */
	private int[] oneCellNext(int[] cell, int direction) {
		// 0 = north; 1 = east; 2 = south; 3 = west
		int[] pos = new int[2];
		switch (direction) {
		case 0: // North
			pos[0] = cell[0];
			pos[1] = cell[1] + 1;
			break;
		case 1: // East
			pos[0] = cell[0] + 1;
			pos[1] = cell[1];
			break;
		case 2: // South
			pos[0] = cell[0];
			pos[1] = cell[1] - 1;
			break;
		case 3: // West
			pos[0] = cell[0] - 1;
			pos[1] = cell[1];
			break;
		}
		return pos;
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