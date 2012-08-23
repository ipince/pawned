package ruleset.piece;

import java.util.*;

import engine.adt.*;
import debug.*;
import ruleset.ply.Castle;
import ruleset.ply.Move;

/**
 * <p>A <code>King</code> represents a king in a chess-resembling game. It
 * contains information about its color and the <code>Board</code> it is
 * associated to.</p>
 * 
 * <p>A <code>King</code> may move in a 2-dimensional <code>Board</code> to
 * the eight cells immediately around it, as long as they are not occupied by
 * another <code>Piece</code> of the same color. Additionally, a
 * <code>King</code> may be enabled to perform <code>Castle</code>s. In castling
 * the <code>King</code> moves two spaces to either its left or right, and the
 * player's <code>Rook</code> on that side moves over the <code>King</code> to
 * occupy the space through which the <code>King</code> traveled. The following
 * requirements must be met for the <code>King</code> to be able to
 * <code>Castle<code>:
 * 
 * <ul>
 * 	<li> the <code>King</code> must never have been previously moved during the
 * game.</li>
 * 	<li> the <code>Rook</code> used in castling must never have been previously
 * moved during the game.</li>
 * 	<li> all the cells between the <code>Rook</code> and <code>King</code> must
 * be vacant.</li>
 * </ul></p>
 * 
 * <p>Note that these requirements are not enough for a king to actually castle
 * in a game of chess of antichess. The extra conditions should be checked by
 * the <code>RuleSet</code> that chooses to support this <code>Piece</code> and
 * provide the official rules for castling.</p>
 *  
 * @see Piece, Castle
 * 
 * @specfield board // the <code>Board</code> to which this <code>King</code>
 * is associated to.
 */
public class King extends Piece {
	
	/**
	 * Determine whether this <code>King</code> is allowed to castle, or not.
	 */
	private final boolean castlingEnabled;
	
	/**
	 * Determine whether the <code>King</code> can castle king side. This will
	 * be false only if the <code>King</code> or the respective
	 * <code>Rook</code> has ever moved in the game.
	 */
	private boolean couldCastleKingSide = true;
	
	/**
	 * Determine whether the <code>King</code> can castle king side. This will
	 * be false only if the <code>King</code> or the respective
	 * <code>Rook</code> has ever moved in the game.
	 */
	private boolean couldCastleQueenSide = true;
	
	// Abstraction Function:
	// 	AF(c) = a king such that:
	//			if isWhite()==true, the king is white
	//			otherwise it is black.
	
	// Representation Invariant:
	// * board != null
	
	/**
	 * Returns a new <code>King</code> of the specified color and associates
	 * it to the provided <code>Board</code>. The returned <code>King</code> has
	 * castling disabled.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>King</code> of the specified color with castling
	 * disabled. That is, a white <code>King</code> if <code>isWhite</code> is
	 * <code>true</code>, and a black <code>King</code> otherwise.
	 */
	public King(boolean isWhite, Board board) {
		this(isWhite,board,false);
	}
	
	/**
	 * Returns a new <code>King</code> of the specified color, associates
	 * it to the provided <code>Board</code>, and enables or disables its
	 * ability to castle as specified by <code>castlingEnabled</code>.
	 * 
	 * @requires <code>board!=null</code>.
	 * @return A new <code>King</code> of the specified color. That is, a
	 * white <code>King</code> if <code>isWhite</code> is <code>true</code>,
	 * and a black <code>King</code> otherwise. Also, if
	 * <code>castlingEnabled</code> is <code>true</code>, then the
	 * <code>King</code> can castle; otherwise, it cannot.
	 */
	public King(boolean isWhite, Board board, boolean castlingEnabled) {
		super(isWhite,board);
		this.castlingEnabled = castlingEnabled;
		checkRep();
	}
	
	/**
	 * <p>Updates the information a <code>Piece</code> needs to determine its
	 * valid <code>Plies</code>. This method should be called after any
	 * <code>Ply</code> has been executed in the <code>Board</code> at which
	 * the <code>Piece</code> is currently at.</p>
	 * 
	 * <p>This method is only used if the <code>King</code> has castling
	 * enabled. If so, it determines whether it can castle or not (and to which
	 * side), based on the information it can obtain from <code>ply</code>. To
	 * do this, it only considers whether any of the <code>Rook</code>s of
	 * interest are moving, or if it itself is moving.</p>
	 * 
	 * <p>This method assumes that a <code>Piece</code> is 'moved' when it is 
	 * going to be the <code>Piece</code> that is removed by a remove
	 * <code>Action</code>. It might be the case that a <code>Ply</code> removes
	 * the <code>Piece</code> from the <code>Board</code>, but adds it back to
	 * the same place. In this case, the <code>King</code> believes it has been
	 * moved. Also, this assumes that the <code>Rook</code>s start at their
	 * initial positions, and only checks if they are ever 'moved' (as in the
	 * definition above). This means that if a <code>Rook</code> is placed at
	 * some arbitrary position in the <code>Board</code> that is not its initial
	 * position, then, if it has not moved, the <code>King</code> will still
	 * think that it can castle.</p>
	 */
	public void updateInfo(Ply ply) {
		checkRep();
		if (castlingEnabled) {
			if (couldCastleKingSide || couldCastleQueenSide) { // only check if its possible
				Board board = getBoard();
				int[] pos = board.getPosition(this);
				for (Action a : ply)
					if (a.getType()==Action.REMOVE)
						// Check if this CastlingKing is being moved
						if (Arrays.equals(a.getCell(), pos)) {
							couldCastleKingSide = false;
							couldCastleQueenSide = false;
						} else {
							Piece toBeMoved = board.getPiece(a.getCell());
							// Check if a Rook is being moved and if its of the same color
							if (toBeMoved!=null && toBeMoved.getType().equals("rook") &&
									((isWhite() && toBeMoved.isWhite()) ||
											(!isWhite() && !toBeMoved.isWhite())))
								// If so, determine whether its king side or queen side
								if (a.getCell()[0]<pos[0])
									couldCastleQueenSide = false;
								else
									couldCastleKingSide = false;
						}
			}
		}
		checkRep();
	}
	
	/**
	 * Notifies the <code>Piece</code> when its been added to its associated
	 * <code>Board</code>, so it can initialize any information it needs.
	 *
	 * A <code>King</code> does not need any particular information to
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
	 * A <code>King</code> does not need any particular information to
	 * determine its <code>Plies</code>.
	 */
	public void removed() {
		checkRep();
		// no info needed
	}
	
	/**
	 * Returns a list with the valid Plies of this <code>King</code>, as
	 * specified in the overview.
	 * 
	 * @return A <code>List</code> of valid <code>Plies</code> for this
	 * <code>King</code>; if the <code>King</code> is not currently in a
	 * <code>Board</code>, the <code>List</code> will be empty.
	 */
	public List<Ply> getPlies() {
		checkRep();
		Board board = getBoard();
		int[] pos = board.getPosition(this);
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
		for (int[] validCell : validCells)
			validPlies.add(new Move(pos,validCell));
		// See if castling is possible and if so, add it to the valid Plies.
		if (castlingEnabled) {
			if (couldCastleKingSide && cellsAreEmpty(true))
				validPlies.add(new Castle(pos, new int[] {pos[0]+2,pos[1]}));
			if (couldCastleQueenSide && cellsAreEmpty(false))
				validPlies.add(new Castle(pos, new int[] {pos[0]-2, pos[1]}));
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
		return "king";
	}

	/**
	 * <p>Returns the initial position of a <code>King</code>. This method
	 * makes stronger assumption that the <code>King</code> plays in a
	 * <code>RectangularBoard</code> with dimensions greater than or equal to 8.
	 * Its initial position then corresponds to the initial position of kings
	 * in a regular game of chess. That is, if the <code>King</code> is white,
	 * the initial position is e1, and e8 otherwise.</p>
	 * 
	 * <p>Note that use of this method is not necessary for the correct
	 * functioning of a <code>King</code>. In other words, a
	 * <code>King</code> could be placed in any arbitrary cell in a
	 * 2-dimensional <code>Board</code>, and it all its functionality will work
	 * appropriately.</p>
	 */
	protected int[] initialPos() {
		if (isWhite()) {
			int[] pos = {4, 0};
			return pos;
		} else {
			int[] pos = {4, 7};
			return pos;
		}
	}
	
	/**
	 * Returns the possible cells a <code>King</code> could move to from its
	 * current cell. This does not take into account any rules other than how a
	 * <code>King</code> is allowed to move, and if the the possible cells are
	 * usable or not. If the <code>King</code> is not currently in a
	 * <code>Board</code>, the <code>List</code> will be empty.
	 */
	private List<int[]> getPossibleCells() {
		List<int[]> possibleCells = new ArrayList<int[]>();
		Board board = getBoard();
		if (board.contains(this)) {
			int[] cell = board.getPosition(this);
			int[] temp = new int[2];
			int[] delta = {-1,0,1};
			for (int x : delta)
				for (int y : delta) {
					temp[0] = cell[0] + x;
					temp[1] = cell[1] + y;
					if (board.isUsable(temp) && (x!=0 || y!=0))
						possibleCells.add(temp.clone());
				}
		}		
		return possibleCells;
	}
	
	/**
	 * Determines whether the cells involving a <code>Castle</code> are empty.
	 * This is necessary to determine whether a <code>Castle</code> is possible
	 * or not. <code>isWhite</code> and <code>kingSide</code> determine whether
	 * this check should be made for a white or black <code>King</code> trying
	 * to castle king side or queen side.
	 * 
	 * Note: this method highly depends on the initial position of the
	 * </code>King<code>.
	 */
	private boolean cellsAreEmpty(boolean kingSide) {
		Board board =  getBoard();
		if (board.contains(this)) {
			int[] pos = board.getPosition(this);
			if (kingSide) // King moves right
				return (board.isEmpty(new int[] {pos[0]+1,pos[1]}) &&
						board.isEmpty(new int[] {pos[0]+2,pos[1]}));
			else // King moves left
				return (board.isEmpty(new int[] {pos[0]-1,pos[1]}) &&
						board.isEmpty(new int[] {pos[0]-2,pos[1]}) &&
						board.isEmpty(new int[] {pos[0]-3,pos[1]}));
		}
		return false;
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
