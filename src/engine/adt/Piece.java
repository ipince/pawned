package engine.adt;

import java.util.List;
import debug.*;
import engine.exception.InvalidBoardException;
import engine.exception.InvalidInitialPositionException;
import engine.exception.InvalidPositionException;
import engine.exception.UnusableCellException;

/** 
 * <p>A <code>Piece</code> represents an object that is placeable in a cell within
 * a specific <code>Board</code> of a specific type. When a <code>Piece</code> is
 * created, it is associated to a <code>Board</code>. This association specifies
 * the <code>Board</code> in which the <code>Piece</code> can play. Thus, the
 * <code>Piece</code> can only ever be added (or removed) to (from) that
 * <code>Board</code>.</p>
 * 
 * <p><code>Piece</code>s can be asked to return a <code>List</code> of the valid
 * <code>Plies</code> they can perform. No <code>Piece</code> can move to an
 * unusable cell in a <code>Board</code>.</p>
 * 
 * <p> A <code>Piece</code> contains information about its color (black or white),
 * type, initial position in a specific <code>Board</code>, as well as other
 * information it finds necessary to be able to determine its valid
 * <code>Plies</code>. </p>
 * 
 * @see <code>engine.adt.Board, engine.adt.Ply</code>
 * 
 * @specfield color : {black, white} // A color representing this piece 
 * @specfield associated-board : Board // The board to which this piece 
 * 										  is associated
 * 
 * @author Rodrigo Ipince, Jose Alberto Muniz
 * 
 */
public abstract class Piece implements Cloneable {

	// Abstraction Function:
	// 	AF(c) = a piece P such that:
	//			* if isWhite()==true, the P.piece is white
	//			  otherwise it is black.
	//          * P.associated-board = board
	
	// Representation Invariant:
	// * board != null	

	
	// Fields
	
	/**
	 * Color of this <code>Piece</code>.
	 */
	private final boolean isWhite;
	
	/**
	 * <code>Board</code> associated to this <code>Piece</code>.
	 */
	private Board board;
	
	// Constructor
	
	/**
	 * Creates a new <code>Piece</code> of the specified color and associates it
	 * to the provided <code>Board</code>.
	 * 
	 * @param isWhite <code>true</code> if the piece is white, false otherwise.
	 * @param board the <code>Board</code> this piece is to be associated with.
	 * 
	 * @return A new <code>Piece</code> of the specified color. That is, a white
	 * piece if <code>isWhite</code> is <code>true</code>, and a black piece
	 * otherwise.
	 * @throws <code>InvalidBoardException</code> if <code>board</code> is invalid or
	 * <code>null</code>.
	 */
	public Piece(boolean isWhite, Board board) {
		this.isWhite = isWhite;
		if (board!=null) {
			this.board = board;
		} else {
			throw new InvalidBoardException("Piece cannot be associated to null." +
					" It can only be associated to a Board");
		}
		checkRep();
	}
	
	/**
	 * Returns the color of this <code>Piece</code>.
	 * 
	 * @return <code>true</code> if this <code>Piece</code> is white, and
	 * <code>false</code> otherwise.
	 */
	public boolean isWhite() {
		checkRep();
		return isWhite;
	}
	
	/**
	 * Determines whether this <code>Piece</code> is associated to
	 * <code>board</code> or not.
	 * 
	 * @param board the <code>Board</code> to be tested for association. 
	 * @return <code>true</code> if <code>this</code> is associated to
	 * <code>board</code>, and <code>false</code> otherwise.
	 */
	public boolean associatedTo(Board board) {
		// Note referential equality!
		// This is only useful for a Board to check whether it can add this
		// Piece to itself or not. By passing in the Board, we prevent rep
		// exposure of the Piece's associated Board.
		checkRep();
		return getBoard()==board;
	}
	
	/**
	 * Returns the type of this <code>Piece</code>, as a <code>String</code>. The
	 * type of a <code>Piece</code> is just an identifier. For example, a game of
	 * chess could expect piece types such as "pawn", "rook", etc. 
	 * 
	 * @return A <code>String</code> representation of <tt>this</tt>.
	 */
	public abstract String getType();
	
	/**
	 * Adds this <code>Piece</code> to its associated <code>Board</code> at its
	 * initial position, if it was not there already.
	 * 
	 * @see #initialPos()
	 * 
	 * @requires <code>this.board.contains(this)==false</code>
	 * @modifies <code>this.board</code>
	 * @effects Add this <code>Piece</code> to the <code>Board</code> at its
	 * initial position.
	 * @throws <code>InvalidInitialPositionException</code> if the
	 * <code>Piece</code> fails to add itself to the <code>Board</code> at its
	 * initial position, either because the cell was not usable or because it was
	 * already occupied.
	 */
	public void setUp() throws InvalidInitialPositionException {
		checkRep();
		if (getBoard().contains(this)) {
			throw new IllegalStateException(toString() + " has already been" +
					" set up or is currently inside its associated Board");
		} else {
			try {
				getBoard().addPiece(this, initialPos());
			} catch (InvalidPositionException e) {
				throw new InvalidInitialPositionException(e.getMessage());
			} catch (UnusableCellException e) {
				throw new InvalidInitialPositionException(e.getMessage());
			}
		}
		checkRep();
	}
	
	/**
	 * Notification mechanism for ply update. An object of type
	 * <code>Piece</code> will be notified of a <code>Ply</code> within a
	 * <code>Board</code> by a call to <code>updateInfo(Ply)</code>. A
	 * <code>Piece</code> may or may not use this method to update the
	 * information it needs to determine its valid <code>Plies</code>.
	 * 
	 * @param ply the <code>Ply</code> as it is to be executed by the
	 * <code>Board</code>.
	 * 
	 * @see <code>engine.adt.Board</code>
	 */
	public abstract void updateInfo(Ply ply);
	
	/**
	 * Notifies the <code>Piece</code> when its been added to its associated
	 * <code>Board</code>, so it can initialize any information it needs.
	 */
	public abstract void added();
	
	/**
	 * Notifies the <code>Piece</code> when its been removed from its associated
	 * <code>Board</code>, so it can modify any information it needs. 
	 */
	public abstract void removed();
	
	/**
	 * Returns a <code>List</code> with the possible <code>Plies</code> a
	 * <code>Piece</code> can perfom according to its local (motion) rules. These
	 * rules are particular for each <code>Piece</code>. If the
	 * <code>Piece</code> does not have any valid <code>Plies</code>, an empty
	 * <code>List</code> is returned.
	 */
	public abstract List<Ply> getPlies();
	
	/**
	 * Returns a <code>String</code> representation of this <code>Piece</code>.
	 * This representation depends on the type of the <code>Piece</code>. The
	 * <code>String</code> representation is underspecified.
	 * 
	 * A programmer of a <code>RuleSet</code> will generally be interested in
	 * calling the <code>getType()</code> method instead.
	 */
	public String toString() {
		checkRep();
		return getType();
	}
	
	/**
	 * Determines whether two <code>Piece</code>s are similar. Two
	 * <code>Piece</code>s are similar if they are of the same type and they have
	 * the same color.
	 * 
	 * @param p2 the piece to be compared with <tt>this</tt>.
	 * @return <code>true</code> if <tt> this </tt> is similar to
	 * <code>p2</code>.
	 */
	public boolean similar(Piece p2) {
		checkRep();
		if (p2 == null)
			return false; 
		else
			return (p2.isWhite == isWhite) &&
			   (p2.getType().equals(getType()));
	}

	/**
	 * Determines referential equality.
	 * 
	 * @param obj the <code>Object</code> to be compared with <tt>this</tt>. 
	 * @return <code>true</code> if <code>this==o</code>, and <code>false</code>
	 * otherwise.
	 */
	public boolean equals(Object obj) {
		checkRep();
		return super.equals(obj);
	}
	
	/**
	 * Clone is not supported via <code>Object</code>'s <code>clone()</code>
	 * mechanism. When a copy of a <code>Piece</code> is required, the method
	 * <code>reproduce(Board)</code> provides a similar functionality than that
	 * of <code>clone()<code>.
	 * 
	 * @throws <code>CloneNotSupportedException</code> 
	 */
	public Board clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException("A Piece cannot be cloned. It must" +
				" be reproduced instead.");
	}
	
	/**
	 * Makes a copy of this <code>Piece</code> and associates it to the given
	 * <code>Board</code>. That is, returns a new <code>Piece</code> of the same
	 * type and color, associated to <code>board</code>.
	 * 
	 * @param board the <code>Board</code> for the new <code>Piece</code> to be
	 * associated with. 
	 * @return the new <code>Piece</code>.
	 * @throws <code>InvalidBoardException</code> if <code>board == null</code>.
	 */
	public Piece reproduce(Board board) {
		checkRep();
		if (board == null)
			throw new InvalidBoardException("Board cannot be null");
		Piece copy = null;
		try {
			copy = (Piece) super.clone();
			// Never fails because Object supports clone() 
		} catch (CloneNotSupportedException e) {}
		copy.board = board;
		checkRep();
		return copy;
	}

	/**
	 * Returns the <code>Board</code> this <code>Piece</code> is associated to.
	 * Notice that the <code>Board</code> returned is not a copy of the
	 * <code>Board</code> this <code>Piece</code> is associated to, but rather a
	 * reference to the actual instance. 
	 * 
	 * @return A <code>Board</code> b such that b is associated to <tt>this</tt>.
	 */
	protected Board getBoard() {
		// Note: Be very careful about rep exposure. This is only meant to be
		// used by Objects that extend Piece and need access to the Board.
		return board;
	}
	
	/**
	 * Attempts to return a coordinate X = [x1,x2,...,xn] such that X is a
	 * valid position in a game start for <tt>this.getType()</tt>. The method
	 * returns any of these coordinates if the corresponding cell in the
	 * associated <code>Board</code> is usable.
	 * 
	 * Whenever more than one position is valid for a given <code>Piece</code>,
	 * the method returns one of them. For example, a white pawn in chess can be
	 * placed in several different initial positions (all in row 2). However, if 
	 * all the possible valid positions are unusable or not empty, the return
	 * value of this method is not specified. 
	 * 
	 * @see <code>engine.adt.Board</code>
	 * @return If any of the possible initial positions are empty in the 
	 * associated <code>Board</code>, returns a coordinate X such that X is a
	 * valid initial position.
	 *  
	 */
	protected abstract int[] initialPos();
	
	/**
	 * Checks that the representation invariant holds.
	 */
	private void checkRep() {
		if (DebugInfo.DEBUG_ADT) {
			// Check Board nullity
			if (board==null)
				throw new RuntimeException("A Piece's associated Board may never" +
						" be null.");
		}
	}
	
}