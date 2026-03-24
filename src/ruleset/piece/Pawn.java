package ruleset.piece;

import java.util.*;

import ruleset.board.CoordinateParser;
import ruleset.ply.Coronation;
import ruleset.ply.EnPassantCapture;
import ruleset.ply.Move;
import engine.adt.*;
import debug.*;

/**
 * <p>A <code>Pawn</code> represents a pawn in a chess-resembling game. It
 * contains information about its color and the <code>Board</code> it is
 * associated to.</p>
 * 
 * <p>A <code>Pawn</code> may move in a 2-dimensional <code>Board</code> either
 * forward one cell into an unoccupied cell or one cell diagonally forward into
 * a cell that is occupied by a <code>Piece</code> of another color (capturing
 * it). Here, moving forward means moving towards the opponent's side.
 * Moreover, two other moves are possible. If the <code>Pawn</code> has not
 * moved from its original position (as specified by <code>initialPos()</code>),
 * then it may also move two cells forward into an unoccupied cell. And if a
 * <code>Pawn</code> reaches the end of the <code>Board</code> (it cannot move
 * forward anymore), then it gets crowned and transforms itself into a
 * <code>Queen</code>. Additionally, <code>Pawn</code> may be enabled to perform
 * capture <i>en passant</i>. This can occur when a <code>Pawn</code> of the
 * opposite color moves two cells forward, and ends in a cell that is in the
 * same row as that of the first <code>Pawn</code> and is adjacent to it. In
 * this situtation, the first <code>Pawn</code> may move diagonally forward into
 * the empty cell through which the opponent <code>Pawn</code> traveled, and
 * captures it (removing it from the <code>Board</code>). This move can only
 * be executed in the immediate next turn following the second
 * <code>Pawn</code>'s movement.</p>
 * 
 * @see engine.adt.Piece
 * 
 * @specfield board // the <code>Board</code> to which this <code>Pawn</code>
 * is associated to.
 */
public class Pawn extends Piece {
	
	/**
	 * Determine whether this <code>Pawn</code> is allowed to capture en 
	 * passant, or not.
	 */
	private final boolean enPassantEnabled;
	
	/**
	 * Determine whether the <code>Pawn</code> can capture en passant to the
	 * right (in the <code>Board</code>).
	 */
	private boolean canCaptureRight = false;
	
	/**
	 * Determine whether the <code>Pawn</code> can capture en passant to the
	 * left (in the <code>Board</code>).
	 */
	private boolean canCaptureLeft = false;
	
	// Do AF, RI.
	
	/**
	 * Returns a new <code>Pawn</code> of the specified color and associates
	 * it to the provided <code>Board</code>. The returned <code>Pawn</code> has
	 * en passant capturing disabled.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>Pawn</code> of the specified color with en passant
	 * capturing disabled. That is, a white <code>Pawn</code> if
	 * <code>isWhite</code> is <code>true</code>, and a black <code>Pawn</code>
	 * otherwise.
	 */
	public Pawn(boolean isWhite, Board board) {
		this(isWhite, board, false);
	}
	
	/**
	 * Returns a new <code>Pawn</code> of the specified color, associates
	 * it to the provided <code>Board</code>, and enables or disables its
	 * ability to capture en passant as specified by
	 * <code>enPassantEnabled</code>.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>Pawn</code> of the specified color. That is, a
	 * white <code>Pawn</code> if <code>isWhite</code> is <code>true</code>,
	 * and a black <code>Pawn</code> otherwise. Also, if
	 * <code>enPassantEnabled</code> is <code>true</code>, then the
	 * <code>Pawn</code> can capture en passant; otherwise, it cannot.
	 */
	public Pawn(boolean isWhite, Board board, boolean enPassantEnabled) {
		super(isWhite,board);
		this.enPassantEnabled = enPassantEnabled;
		checkRep();
	}
	
	/**
	 * <p>Updates the information a <code>Piece</code> needs to determine its
	 * valid <code>Plies</code>. This method should be called after any
	 * <code>Ply</code> has been executed in the <code>Board</code> at which
	 * the <code>Piece</code> is currently at.</p>
	 * 
	 * <p>This method is only used if the <code>Pawn</code> has en passant
	 * capturing enabled. If so, it determines whether it can capture en passant
	 * or not (and to which side), based on the information it can obtain from
	 * <code>ply</code>. To do this, it determines if <code>ply</code>
	 * corresponds to a two cell movement from an opponent <code>Pawn</code>
	 * whose ending cell is either the cell to its right or the one to its
	 * left.</p>
	 */
	public void updateInfo(Ply ply) {
		checkRep();
		if (enPassantEnabled) {
			canCaptureRight = false; // reset it at the beginning of every move\
			canCaptureLeft = false;
			Board board = getBoard();
			int[] pos = board.getPosition(this);
			// Check if ply meet desired conditions in terms of positions
			int[] start = CoordinateParser.getCell(ply.toString(), true);
			int[] end = CoordinateParser.getCell(ply.toString(), false);
			if (end[1]==pos[1] && start[0]==end[0] &&
					(Math.abs(pos[0]-end[0])==1) && Math.abs(start[1]-end[1])==2)
				// If so, check if the Piece being moved is a Pawn
				for (Action a : ply)
					if (a.getType()==Action.REMOVE) {
						Piece toBeMoved = board.getPiece(a.getCell());
						if (toBeMoved!=null && toBeMoved.getType().equals("pawn"))
							if (pos[0]<end[0])
								canCaptureRight = true;
							else
								canCaptureLeft = true;
					}
		}
		checkRep();
	}
	
	/**
	 * Notifies the <code>Piece</code> when its been added to its associated
	 * <code>Board</code>, so it can initialize any information it needs.
	 *
	 * A <code>Pawn</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void added() {
		// no info needed
	}
	
	/**
	 * Notifies the <code>Piece</code> when its been removed from its associated
	 * <code>Board</code>, so it can modify any information it needs.
	 * 
	 * A <code>Pawn</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void removed() {
		// no info needed
	}
	
	/**
	 * Returns a list with the valid Plies of this <code>Pawn</code>, as
	 * specified in the overview.
	 * 
	 * @return A <code>List</code> of valid <code>Plies</code> for this
	 * <code>Pawn</code>; if the <code>Pawn</code> is not currently in a
	 * <code>Board</code>, the <code>List</code> will be empty.
	 */
	public List<Ply> getPlies() {
		checkRep();
		Board board = getBoard();
		// Create list of valid cells to move to
		List<int[]> validCells = new LinkedList<int[]>();
		List<Ply> validPlies = new ArrayList<Ply>();
		if (board.contains(this)) {
			boolean isBlocked = true;
			int[] pos = board.getPosition(this);
			int[] temp = new int[2];
			// Check if pawn can move diagonally
			temp = oneCellDiagonally(pos,true); // going left
			if (board.isUsable(temp) && !board.isEmpty(temp) &&
					((board.getPiece(temp).isWhite() && !isWhite()) ||
					 (!board.getPiece(temp).isWhite() && isWhite())))
				validCells.add(temp.clone());
			temp = oneCellDiagonally(pos,false); // going right
			if (board.isUsable(temp) && !board.isEmpty(temp) &&
					((board.getPiece(temp).isWhite() && !isWhite()) ||
					 (!board.getPiece(temp).isWhite() && isWhite())))
				validCells.add(temp.clone());
			// Check for optional en passant capture
			if (enPassantEnabled) {
				// Notice that even if we're adding a diagonal cell, it is
				// not ambiguous, because if a Pawn can capture en passant,
				// then the place where its going to end up MUST be empty, and
				// therefore, the cell has not been added previously.
				if (canCaptureLeft)
					validCells.add(oneCellDiagonally(pos,true).clone());
				else if (canCaptureRight)
					validCells.add(oneCellDiagonally(pos,false).clone());
			}
			// Check if pawn can move forward
			temp = oneCellForward(pos);
			if (board.isUsable(temp) && board.isEmpty(temp)) {
				validCells.add(temp.clone());
				isBlocked = false;
			}
			// Check if pawn can move two spaces forward
			temp = twoCellForward(pos);
			if (isInitial(pos) && !isBlocked && 
					board.isUsable(temp) && board.isEmpty(temp))
				validCells.add(temp.clone());
			// Create list of valid plies from valid cells
			for (int[] validCell : validCells) {
				if (isAtEnd(validCell)) { // Coronation
					Queen queen = new Queen(isWhite(),board);
					validPlies.add(new Coronation(pos,validCell,queen));
				} else if (pos[0]!=validCell[0] && board.isEmpty(validCell)) { // en passant
					validPlies.add(new EnPassantCapture(pos,validCell));
				} else { // Regular Move
					validPlies.add(new Move(pos,validCell));
				}
			}
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
		return "pawn";
	}
	
	/**
	 * <p>Returns the initial position of a <code>Pawn</code>. This method
	 * makes stronger assumption that the <code>Pawn</code> plays in a
	 * <code>RectangularBoard</code> with dimensions greater than or equal to 8.
	 * Its initial position then corresponds to the initial position of pawns
	 * in a regular game of chess. That is, if the <code>Pawn</code> is white,
	 * its initial position can be any cell in the second row (b# where # is any
	 * number between 0 and 7, inclusive). If the <code>Pawn</code> is black,
	 * its initial position can be any cell in the seventh row (g#).</p>
	 * 
	 * <p>Note that use of this method is not necessary for the correct
	 * functioning of a <code>Pawn</code>. In other words, a
	 * <code>Pawn</code> could be placed in any arbitrary cell in a
	 * 2-dimensional <code>Board</code>, and it all its functionality will work
	 * appropriately.</p>
	 */
	protected int[] initialPos() {
		Board board = getBoard();
		int row;
		int[] pos = new int[2];
		if (isWhite()) {row = 1;} else {row = 6;}
		pos[1] = row;
		for (int i = 0; i<8; i++) {
			pos[0] = i;
			if (board.isEmpty(pos))
				break;
		}
		return pos;
	}
	
	/**
	 * Given a cell, returns a cell one space forward (where 'forward' is as
	 * specified in the overview).
	 */
	private int[] oneCellForward(int[] cell) {
		if (isWhite()) {
			int[] pos = {cell[0], cell[1] + 1};
			return pos;
		} else {
			int[] pos = {cell[0], cell[1] - 1};
			return pos;
		}
	}
	
	/**
	 * Given a cell, returns a cell two spaces forward (where 'forward' is as
	 * specified in the overview).
	 */
	private int[] twoCellForward(int[] cell) {
		return oneCellForward(oneCellForward(cell));
	}
	
	/**
	 * Given a cell, returns a cell that is one cell diagonally forward to it,
	 * considering whether we are going diagonally left or right. The direction
	 * is with respect to the <code>Board</code>. In other words, it only
	 * considers the color of the <code>Pawn</code> to determine what 'forward'
	 * means, not to determine what 'left' and 'right' means.
	 */
	private int[] oneCellDiagonally(int[] cell, boolean isLeft) {
		int[] pos = new int[2];
		pos[0] = isLeft ? (cell[0]-1) : (cell[0]+1);
		pos[1] = isWhite() ? (cell[1]+1) : (cell[1]-1);
		return pos;
	}
	
	/**
	 * Determines whether a <code>Pawn</code> is at the end of its path (it
	 * cannot move forward anymore because it has reached an end of the board).
	 */
	private boolean isAtEnd(int[] cell) {
		if (isWhite()) {
			return (cell[1] == 7);
		} else {
			return (cell[1] == 0);
		}
	}
	
	/**
	 * Determines whether the given position is 'initial' for a
	 * <code>Pawn</code>. That is, if the position corresponds to a possible
	 * <code>Pawn</code> initial position. This is used to determine whether a
	 * <code>Pawn</code> can move two cells or not.
	 */
	private boolean isInitial(int[] cell) {
		if (isWhite()) {
			return (cell[1] == 1);
		} else {
			return (cell[1] == 6);
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