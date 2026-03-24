package ruleset.ply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ruleset.board.CoordinateParser;
import engine.adt.Action;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * <p>A <code>Coronation</code> represents a ply that involves a transition of a
 * piece from a cell in a 2-dimensional board to another, and the replacement of
 * that piece (in the ending cell) by another.</p>
 * 
 * <p>A <code>Coronation</code> contains information about the starting cell of
 * the moving piece, and its "expected" ending cell (which will have the new
 * piece after the <code>Coronation</code> has been performed).</p>
 * 
 * <p>For example, in standard antichess, when a pawn can reach the end of the
 * board, it may execute a <code>Coronation</code>, which would remove the pawn
 * from the board, and set a queen in the position where the pawn would have
 * been if it had moved to the end of the board.</p>
 * 
 * <p><code>Coronation</code>s are immutable.</p>
 * 
 * @specfield start // the start point of the ply
 * @specfield end // the end point of the ply
 */
public class Coronation extends Ply {
	
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
	 * Constructs a new <code>Coronation</code>. This composite command will
	 * remove the piece at <code>start</code>, and place the given
	 * <code>Piece</code> at <code>end</code>, displacing any pieces present
	 * in the ending position.
	 * 
	 * @param start the starting position
	 * @param end the ending position
	 * @requires <code>start.size >= 2</code>, <code>end.size >= 2</code>,
	 *          each coordinate is positive.
	 * @throws IllegalArgumentException if <code>start</code> or
	 * <code>end</code> is <code>null</code>.
	 */
	public Coronation(int[] start, int[] end, Piece piece) {

	if (start == null || end == null || piece == null) 
	   throw new 
   	    IllegalArgumentException("Cannot construct a move with null " +
   	    						 "arguments");
	try {
		if (start[0] < 0 || end[0] < 0 || 
			start[1] < 0 || end[1] < 0)
			throw new 
			IllegalArgumentException("Cannot construct a coronation from a " +
									"negative position");
	}
	catch (IndexOutOfBoundsException iobe){
		throw new 
		IllegalArgumentException("Cannot construct a coronation in less than " +
								 "two dimensions"); 
	}
		list = new ArrayList<Action>(3);

		list.add(Action.makeRemove(end));
		list.add(Action.makeRemove(start));
		list.add(Action.makeAdd(end, piece));
		
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
	 * <code>Coronation</code>. The format is in the form na-mb, where n,m are
	 * letters from a-h representing the column, and a,b are numbers from 1 to
	 * 8 representing the row.
	 * 
	 * For example, a pawn that coronates from [0,6] to [0,7] will have a
	 * <code>String</code> representation "a7-a8".
	 * 
	 * @return <code>String</code> representation of <tt>this</tt>.
	 */
	public String toString() {		
			return  start + "-" + end;
	}

}