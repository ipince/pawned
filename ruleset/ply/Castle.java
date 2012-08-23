package ruleset.ply;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ruleset.board.CoordinateParser;

import engine.adt.Action;
import engine.adt.Ply;

/**
 * <p>A <code>Castle</code> represents a ply that involves two transitions
 * within a 2-dimensional board whose length is at least 8. In a
 * <code>Castle</code>, two pieces move from two starting positions to two
 * different ending positions in the board.</p>
 * 
 * <p>A <code>Castle</code> contains information about the starting and ending
 * cells of one of the transitions. This first transition occurs just an it
 * would in a regular <code>Move</code>. The other transition depends on this
 * first one, and its starting and ending cells are determined as follows:
 * 
 * <ul>
 * 	<li>If the first transition involves a piece moving to the left, then the
 * starting cell of the second transition will be the one whose row is the same
 * as that of the ending cell of the first transition, and whose column is
 * column 0. The ending cell will the the cell right next to the ending cell of
 * the first transition, on its right.</li>
 *  <li>If the first transition involves a piece moving to the right, then the
 * starting cell of the second transition will be the one whose row is the same
 * as that of the ending cell of the first transition, and whose column is
 * column 7. The ending cell will the the cell right next to the ending cell of
 * the first transition, on its left.</li>
 * </ul></p>
 * 
 * <p>For example, if a king is located on its initial position, it could
 * perform a <code>Castle</code> and move two squares to its right. Assuming
 * there is also a rook located in its initial position (in the right), then the
 * rook would also move and end on the king's left. Any pieces that were located
 * in the two ending cells before the <code>Castle</code> is performed are
 * removed from the board.</p>
 * 
 * <p><code>Castle</code>s are immutable.</p>
 * 
 * @specfield start // the start point of the ply
 * @specfield end // the end point of the ply
 */
public class Castle extends Ply {

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
	 * Constructs a new <code>Castle</code>. This composite command
	 * will instruct the piece at <code>start</code> to move to the place
	 * denoted by <code>end</code>, displacing any pieces present in the ending
	 * position. Additionally, it will move the piece located at the cell whose
	 * row is the same as that of <code>end</code>, and whose column is either
	 * 0 or 7, depending on whether the first transition moves left or right,
	 * respectively. It will move this piece to the right or left of
	 * <code>end</code>, depending on whether the first transition moves left
	 * or right, respectively, displacing any pieces present in that cell 
	 * previously.
	 * 
	 * @param start the starting position
	 * @param end the ending position
	 * @requires <code>start.size >= 2</code>, <code>end.size >= 2</code>,
	 *          each coordinate is positive.
	 * @throws IllegalArgumentException if <code>start</code> or
	 * <code>end</code> is <code>null</code>.
	 */
	public Castle(int[] start, int[] end) {

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
		list = new ArrayList<Action>(6);

		// where is the second piece right now?
		int secondPiecePos = (end[0]<start[0]) ? 0:7;
		
		// where is it going to be at? (depends on where the initial transition goes)
		int[] secondEndPos = new int[] {(end[0]<start[0]) ? (end[0]+1) : (end[0]-1), end[1]};
		
		list.add(Action.makeRemove(end));
		list.add(Action.makeRemove(secondEndPos));
		list.add(Action.makeRemove(new int[] {secondPiecePos, end[1]}));
		list.add(Action.makeRemove(start));
		list.add(Action.makeAdd(end, null));
		list.add(Action.makeAdd(secondEndPos, null));

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

	@Override
	/** 
	 * Returns a <code>String</code> representation of the
	 * <code>Castle</code>. The format is in the form na-mb, where n,m
	 * are letters from a-h representing the column, and a,b are numbers from 1
	 * to 8 representing the row.
	 * 
	 * For example, a king that castles from [4,0] to [2,0] will
	 * have a <code>String</code> representation "e1-c1".
	 * 
	 * @return <code>String</code> representation of <tt>this</tt>.
	 */
	public String toString() {		
			return  start + "-" + end;
	}
	
}
