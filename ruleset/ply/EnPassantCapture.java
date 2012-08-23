package ruleset.ply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ruleset.board.CoordinateParser;
import engine.adt.Action;
import engine.adt.Ply;

/**
 * <p>An <code>EnPassantCapture</code> represents a capture of a piece by
 * another piece, which transitions from one cell to another within a
 * 2-dimensional board. The captured piece need not be in any of these two
 * cells.</p>
 * 
 * <p>An <code>EnPassantCapture</code> contains information about the starting
 * cell of the capturing piece, as well as the ending cell where the piece will
 * be located. The piece that gets captured (if any) is the one located in the
 * cell that results from taking the row of the starting cell and the column of
 * the ending cell.</p>
 *
 * <p>For example, if a white pawn has a piece located to its right, and it
 * performs an <code>EnPassantCapture</code> that takes it diagonally from its
 * current cell to the cell just on top of the neighboring piece, then the
 * neighboring piece will be captured and removed from the board.</p>
 * 
 * <p><code>EnPassantCapture</code>s are immutable.</p>
 * 
 * @specfield start // the start point of the ply
 * @specfield end // the end point of the ply
 */
public class EnPassantCapture extends Ply {
	
	/**
	 * An ordered <code>List</code> with the <code>Action</code>s of this
	 * <code>Ply</code>.
	 */
	private List<Action> list;
	
	/**
	 * A <code>String</code> representation of the starting cell of this
	 * <code>Ply</code>.
	 */
	private String start;
	
	/**
	 * A <code>String</code> representation of the ending cell of this
	 * <code>Ply</code>.
	 */
	private String end;
	
	// Do AF, RI, and checkRep()
	
	/**
	 * Constructs a new <code>EnPassantCapture</code>. This composite command
	 * will instruct the piece at <code>start</code> to move to the place
	 * denoted by <code>end</code>, displacing any pieces present in the ending
	 * position. Additionally, it removes the piece located at the cell given by
	 * taking the row of the <code>start</code> position and the column of the
	 * <code>end</code> position.
	 * 
	 * @param start the starting position
	 * @param end the ending position
	 * @requires <code>start.size >= 2</code>, <code>end.size >= 2</code>,
	 *          each coordinate is positive.
	 * @throws IllegalArgumentException if <code>start</code> or
	 * <code>end</code> is <code>null</code>.
	 */
	public EnPassantCapture(int[] start, int[] end) {

	if (start == null || end == null) 
	   throw new 
   	    IllegalArgumentException("Cannot construct a move with null " +
   	    						 "arguments");
	try {
		if (start[0] < 0 || end[0] < 0 || 
			start[1] < 0 || end[1] < 0)
			throw new 
			IllegalArgumentException("Cannot construct a move from a " +
									"negative position");
	}
	catch (IndexOutOfBoundsException iobe){
		throw new 
		IllegalArgumentException("Cannot construct a move in less than " +
								 "two dimensions"); 
	}
		list = new ArrayList<Action>(4);

		list.add(Action.makeRemove(new int[] {end[0], start[1]}));
		list.add(Action.makeRemove(end));
		list.add(Action.makeRemove(start));
		list.add(Action.makeAdd(end, null));
		
		this.start = CoordinateParser.parseCoord(start);
		this.end = CoordinateParser.parseCoord(end);
		
	}
	
	@Override
	/**
	 * Specified by Ply.
	 */
	public Iterator<Action> iterator() {
		return new ArrayList<Action>(list).iterator();
	}

	/**
	 * Returns a <code>String</code> representation of the
	 * <code>EnPassantCapture</code>. The format is in the form na-mb, where n,m
	 * are letters from a-h representing the column, and a,b are numbers from 1
	 * to 8 representing the row.
	 * 
	 * For example, a pawn that captures en passant from [3,4] to [2,5] will
	 * have a <code>String</code> representation "d5-c6".
	 * 
	 * @return <code>String</code> representation of <tt>this</tt>.
	 */
	public String toString() {		
			return  start + "-" + end;
	}

}