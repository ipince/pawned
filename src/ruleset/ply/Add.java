package ruleset.ply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ruleset.board.CoordinateParser;

import engine.adt.Action;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * <p>An <code>Add</code> represents an addition of a <code>Piece</code> to a
 * cell within a 2-dimensional board.</p>
 *
 * <p>An <code>Add</code> contains information about the cell in which it will
 * add the <code>Piece</code> and the <code>Piece</code> it will add there.</p>
 * 
 * <p><code>Add</code>s are immutable.</p>
 * 
 * @see ruleset.ply.Coronation
 * 
 * @specfield cell // the cell where the piece will be added
 */
public class Add extends Ply {
	
	/**
	 * An ordered <code>List</code> with the <code>Action</code>s of this
	 * <code>Ply</code>.
	 */
	private List<Action> list;
	
	/**
	 * A <code>String</code> representation of the cell of this
	 * <code>Ply</code>.
	 */
	private String cell;
	
	/**
	 * The color of the <code>Piece</code> that is being added by this
	 * <code>Ply</code>.
	 */
	private boolean isWhite;
	
	// Do AF, RI, and checkRep()

	/**
	 * Constructs a new <code>Add</code>. This composite command will add
	 * the given <code>Piece</code> to the specified cell.
	 * 
	 * @param cell the cell where <code>piece</code> will be added to.
	 * @requires <code>cell.size >= 2</code>, each coordinate is positive.
	 * @throws IllegalArgumentException if <code>cell</code> is
	 * <code>null</code>.
	 */
	public Add(int[] cell, Piece piece) {

		if (cell == null) 
			throw new 
			IllegalArgumentException("Cannot construct a move with null " +
			"arguments");
		try {
			if (cell[0] < 0 || cell[1] < 0)
				throw new 
				IllegalArgumentException("Cannot construct a move from a " +
				"negative position");
		}
		catch (IndexOutOfBoundsException iobe){
			throw new 
			IllegalArgumentException("Cannot construct a move in less than " +
			"two dimensions"); 
		}
		
		list = new ArrayList<Action>(2);

		list.add(Action.makeRemove(cell));
		list.add(Action.makeAdd(cell, piece));

		this.cell = CoordinateParser.parseCoord(cell);
		this.isWhite = piece.isWhite();

	}
	
	
	@Override
	/**
	 * Specified by Ply.
	 */
	public Iterator<Action> iterator() {
		return new ArrayList<Action>(list).iterator();
	}

	/** 
	 * Returns a <code>String</code> representation of the <code>Add</code>.
	 * The format is in the form xna, where x is either "t" or "f" and
	 * determines whether the <code>Piece</code> being added is white
	 * or not; n is a letter from from a-h representing the column, and a is
	 * a number from 1 to 8 representing the row.
	 * 
	 * @return <code>String</code> representation of <tt>this</tt>.
	 */
	public String toString() {		
			return ((isWhite ? "t":"f") + "+" + cell);
	}

}
