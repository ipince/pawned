package ruleset.ply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ruleset.board.CoordinateParser;
import engine.adt.Action;
import engine.adt.Ply;


/**
 * <p>A <code>Move</code> represents a transition from one cell to another
 * within a 2-dimensional board.</p>
 *
 * <p>In other words, a <code>Move</code> contains information about the
 * starting cell within the board, along with the information about the final
 * cell in which the piece shall be located.</p>
 * 
 * <p>A <code>Move</code> can <b>only</b> contain this transition. Therefore,
 * for example, the coronation of a pawn in antichess would not be considered a
 * simple <code>Move</code>, since it contains an addition of a new queen apart
 * from the displacement of the pawn.</p>
 * 
 * <p><code>Move</code>s are immutable.</p>
 * 
 * @see ruleset.ply.Coronation
 * 
 * @specfield start // the start point of the ply
 * @specfield end // the end point of the ply
 */
public class Move extends Ply {
	
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
	 * Constructs a new <code>Move</code>. This composite command will instruct
	 * the piece at <code>start</code> to move to the place denoted by
	 * <code>end</code>, displacing any pieces present in the ending position.
	 * 
	 * @param start the starting position
	 * @param end the ending position
	 * @requires <code>start.size >= 2</code>, <code>end.size >= 2</code>,
	 *          each coordinate is positive.
	 * @throws IllegalArgumentException if <code>start</code> or
	 * <code>end</code> is <code>null</code>.
	 */
	public Move(int[] start, int[] end) {

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
		list = new ArrayList<Action>(3);

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
	 * Returns a <code>String</code> representation of the <code>Move</code>.
	 * The format is in the form na-mb, where n,m are letters from a-h
	 * representing the column, and a,b are numbers from 1 to 8 representing
	 * the row.
	 * 
	 * For example, a pawn that moves from [0,3] to [0,4] will have a
	 * <code>String</code> representation "a4-a5".
	 * 
	 * @return <code>String</code> representation of <tt>this</tt>.
	 */
	public String toString() {		
			return  start + "-" + end;
	}

}
