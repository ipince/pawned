package engine.adt;

import java.util.Arrays;

/**
 * An <code>Action</code> represents an atomic change in a <code>Board</code>.
 * 
 * <p>There are two types of <code>Actions</code>: 'add' and 'remove'. An 'add' 
 * <code>Action</code> adds a <code>Piece</code> to a cell in a <code>Board</code>. 
 * A 'remove' <code>Action</code> removes the <code>Piece</code> from a cell
 * in a <code>Board</code>, if any.</p>
 * 
 * <p><code>Actions</code> depend on a cell and a <code>Piece</code>, which specify 
 * what <code>Piece</code> to add (if the <code>Action</code> is of type 'add'), 
 * and what cell to act upon. Thus, an <code>Action</code> contains information 
 * about its type, the cell, and the <code>Piece</code> it refers to.</p>
 * 
 * <p>Since <code>Actions</code> can only be executed in a <code>Board</code> in the 
 * context of a <code>Ply</code> (a set of <code>Actions</code>), it is possible to 
 * refer to a piece that has been previously been removed from the board in an ADD 
 * action by specifying a null piece. For more information about this, refer to 
 * <code> Ply </code>.</p>
 * 
 * <code>Actions</code> are immutable.
 * 
 * @see engine.adt.Ply 
 * @see engine.adt.Board
 * @author José Alberto Muñiz 
 * 
 * @specfield type = {ADD, REMOVE} // the type of action
 * @specfield piece  : Piece // If type == ADD, the piece to be added to the board
 * @specfield pos : coordinate //the coordinate of the position
 */
public class Action {
	
	// Abstraction Function:
	// AF(x) = An Action A such that: 
	// 			* A.type = x.type
	//			* A.pos = x.cell
	//			* A.piece = x.piece whenever (x.type == Action.ADD) 
	//			                  and (x.piece != null)
	//			* A.piece = last piece removed from board whenever 
	//							 (x.type == Action.ADD) and (x.piece == null)
	// 

	// Representation Invariant:
	// 	* type = Action.REMOVE or type = Action.ADD
	// 	* cell != null
	//  * type = Action.REMOVE => piece = null


	
	// Types of actions
	public static final int ADD = 1;
	public static final int REMOVE = 2;

	// Fields
	/**
	 * The type of this Action.
	 */
	private final int type;
	
	/**
	 * The cell this Action refers to.
	 */
	private final int[] cell;
	
	/**
	 * The Piece this Action refers to.
	 */
	private final Piece piece;
	
	
	// Constructors
	/**
	 * Returns a new <code>Action</code> with the specified information.
	 *  
	 * @param type The type of action this <code>Action</code> represents: 
	 *             <code>Action.ADD</code> or <code>Action.REMOVE</code>
	 * @param cell An integer array specifying the coordinate upon which
	 * 				the action should be executed
	 * @param piece The piece associated with this action. Notice that 
	 * 				if the type of this instance of <code>Action</code> is
	 * 				<code>Action.REMOVE</code>, this argument should equal 
	 * 				<code>null</code>.
	 */
	private Action(int type, int[] cell, Piece piece) {
		this.type = type;
		this.cell = cell;
		this.piece = piece;
		
		checkRep();
	}
	
	/**
	 * Returns a new Action that does not depend on a Piece.
	 * 
	 * @param type The type of action this <code>Action</code> represents: 
	 *             <code>Action.ADD</code> or <code>Action.REMOVE</code>
	 * @param cell An integer array specifying the coordinate upon which
	 * 				the action should be executed 
	 */
	private Action(int type, int[] cell) {
		this(type, cell, null);
	}
	
	// Methods
	
	/**
	 * Returns the type of this <code>Action</code>.
	 * 
	 * @return <code>Action.ADD</code> or <code>Action.REMOVE</code> 
	 */
	public int getType() {
		checkRep();
		return this.type;
	}
	
	/**
	 * Returns the cell this <code>Action</code> refers to.
	 * 
	 * @return An integer array [a1,a2,...an] representing the target of this
	 * 		   instance of <code>Action</code>
	 */
	public int[] getCell() {
		checkRep();
		return this.cell;
	}
	
	/**
	 * Return the <code>Piece</code> this Action refers to. If this is an 
	 * <code>Action.REMOVE</code> <code>Action</code> or an <code>Action.ADD
	 * </code> referring to a previous piece, the return value should equal 
	 * <code>null</code>.
	 * 
	 * @return The <code>Piece</code> associated with <code>this</code>
	 * 
	 */
	public Piece getPiece() {
		checkRep();
		return this.piece;
	}
	
	/**
	 * Returns an object of type <code>Action</code> to add <tt>piece</tt> 
	 * to an object of type <code>Board</code> at <tt>pos</tt>. 
	 * When this action is applied by a board, results
	 * will be consistent with the board's own <tt>add</tt> method
	 * 
	 * @param pos The position the piece will be added to. 
	 * @param piece
	 * @throws IllegalArgumentException if piece = null or pos = null
	 * @return An Action to add piece at a given position 
	 * 
	 * @see Board
	 */
	public static Action makeAdd(int[] pos, Piece piece) {
		if (pos == null)
			throw new IllegalArgumentException("Cannot build Add Action with null position");


		return new Action(ADD, pos, piece);
	}
	
	/**
	 * Returns an action to remove the piece located at <tt>pos</tt> 
	 * @param pos The position the piece will be removed from. 
	 * 
	 * @throws IllegalArgumentException if pos = null
	 * @return An Action to remove piece at a given position 
	 */
	public static Action makeRemove(int[] pos) {
		return new Action(REMOVE, pos);
	}
	
	
	/** 
	 * Checks whether <tt>o</tt> is equal to this object. Two actions are considered 
	 * equal if they can be applied to the board associated with the piece, if such 
	 * a board is defined, or more generally to any board if no piece is defined in 
	 * this action, and yield the same result. 
	 * 
	 * In particular, if two Actions a,b are of type REMOVE and a.pos = b.pos, 
	 * then a equals b. 
	 * 
	 * On the other hand, if two actions a,b are of type ADD and a.pos = b.pos, 
	 * a.piece = b.piece, then the two actions are equal.
	 * 
	 * @param o the object to be compared with <code>this</code>
	 * @return <code> true </code> if both objects are <code>equals</code>, and
	 * 		   <code> false </code> otherwise. 
	 * 
	 */
	public boolean equals(Object o) {
		if (! (o instanceof Action))
			return false;
		else {
			Action a = (Action)o;
			return (a.type == type) &&
				   (piece == null ||  piece.equals(a.piece))  &&
				   Arrays.equals(cell,a.cell);
		}
	}
	
	/** 
	 * Checks whether two actions are similar. Two actions are considered
	 * similar if they can be applied to a board with a similar configuration 
	 * and yield the same result. 
	 * 
	 * In particular, if two Actions a,b are of type REMOVE and a.pos = b.pos, 
	 * then a is similar to b.
	 * 
	 * On the other hand, if two actions a,b are of type ADD and a.pos = bos.pos, 
	 * a.piece is similar to b.piece, then the two actions are equal. 
	 * 
	 * @return <code> true </code> if <code>a</code> is similar to <code>this</code>.
	 * 			Returns <code>false</code> otherwise. 
	 * 
	 * @see engine.adt.Piece
	 */
	public boolean similar(Action a) {
		return (a.type == type) &&
		   (piece == null ||  piece.similar(a.piece))  &&
		   Arrays.equals(cell,a.cell);
	}
	/**
	 * Returns a valid hash code for this <code>Action</code>
	 * 
	 * @return An integer valued hash code
	 */
	public int hashCode() {
		return type*13 + (piece==null ? 0 : piece.hashCode()*19);
	}
	
	/** 
	 * Returns a String representation of this Action. 
	 * The exact format is not specified, but common command outputs 
	 * would be approximately of the form:
	 * 
	 * <ul>
	 * 	<li>Add last piece at [0,4] </li> 
	 * 	<li>Add rook to [0,4] </li>
	 *  <li>Remove piece from [0,5] </li> 
	 * </ul>
	 */
	public String toString() {
		switch (type) {
		case ADD:
			return "Add " +  ((piece == null) ? "last piece" : piece) + 
				   "at " + Arrays.asList(cell);
		case REMOVE:
			return "Remove piece from " + Arrays.asList(cell); 
		default:
			return null;
		}
	}

	/**
	 * Checks that the representation invariant holds.
	 */
	private void checkRep() {
		if (debug.DebugInfo.DEBUG_ADT) {
			// Check type
			if ((type!=ADD && type!=REMOVE ) || (cell == null))
				throw new RuntimeException("Invalid Action type");
			if ((type == REMOVE && piece != null))
				throw new RuntimeException("Invariant violtaed for Action \n" +
						"REMOVE Action should not contain a piece"); 
		}
	}
	
	
}