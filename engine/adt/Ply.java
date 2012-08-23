package engine.adt;

import java.util.Iterator;

/** 
 * A <code>Ply</code> is a sequence of <code>Actions</code> that represent a player's turn in a 
 * sequential game. 
 * 
 * Some examples of Plies are: 
 * 
 * <ul>
 * 	<li>In Antichess, a pawn moving from a cell to another empty cell.</li>
 *  <li>In Antichess, a pawn moving from a cell to another occupied cell.</li>
 *  <li>In Chess, a <a href="http://en.wikipedia.org/wiki/Castling"> castling move.</a></li>
 * </ul>
 * 
 * 
 * A Ply is immutable. 
 
 * @see engine.adt.Action
 * @author José Alberto Muñiz
 * 
 * @specfield actions : sequence // the sequence of actions. All 
 * 								    classes that concrete this
 * 								    object should provide this 
 * 								    specfield
 */

public abstract class Ply implements Iterable<Action> {

	/** 
	 * Returns an iterator over the sequence of actions represented by this Ply. 
	 * 
	 * @return An iterator over all the objects of type <code>Action</code> represented
	 * 			by <code>this</code>. This sequence must traverse the actions in the 
	 *          order they should be executed by the board of type <code>Board</code>. 
	 */
	abstract public Iterator<Action> iterator();	
	
	
	
	/** 
	 * Returns a string representation of the Ply such that 
	 * if a = <code>RuleSet.plyFactory().makePly(ply.toString)</code>
	 * then <code> a.similar(ply) </code> 
	 *  
	 * In other words, this method returns a unique identifier for this
	 * particular ply. An example of a representation for a ply could be
	 * "c2-c3", where "c2-c3" represents a chess move: 
	 * <ul>
	 *  <li> REMOVE [2,2] 
	 *  <li> REMOVE [2,1]
	 *  <li> ADD last;  
	 * </ul> 
	 * 
	 * 
	 *  @see engine.adt.RuleSet
	 */ 
	abstract public String toString(); 
	

	/**
	 * Verifies that the two plies are equal. 
	 * A ply <tt>a</tt> is equal to a ply <tt>b</tt> if all the
	 * actions are equal and they appear in the same order. 
	 * 
	 * @param o the object to be compared with <tt> this </tt>
	 * 
	 * @return <code>true</code> if both objects are equal, 
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @see Board
	 */
	public boolean equals(Object o) {
		if (! (o instanceof Ply))
			return false; 
		else {
			Ply toCompare = (Ply)o;
			Iterator<Action> actions = this.iterator();
			Iterator<Action> oActions = toCompare.iterator();
			
			while (actions.hasNext() && oActions.hasNext()) {
				Action action = actions.next();
				Action oAction = oActions.next(); 
				
				if (! action.equals(oAction)) 
					return false;
			}
			if (actions.hasNext() || oActions.hasNext())
				return false;
			else
				return true;
		}		
	}
	
	/**
	 * Determines whether a given Ply is similar to another.
	 * Two plies are similar if the actions it contains are 
	 * similar and they appear in the same order. 
	 * 
	 * @param p2 The ply to be compared with <code>this</code>. 
	 * @return <code>true</code> if both plies are equal, 
	 * 		   <code>false</code> otherwise. 
	 * 
	 * @see engine.adt.Action
	 */
	public boolean similar(Ply p2) {

		if (p2 == null)
			return false; 
		
		Iterator<Action> actions = this.iterator();
		Iterator<Action> oActions = p2.iterator();
		
		while (actions.hasNext() && oActions.hasNext()) {
			Action action = actions.next();
			Action oAction = oActions.next(); 
			
			if (! action.similar(oAction)) 
				return false;
		}
		if (actions.hasNext() || oActions.hasNext())
			return false;
		else
			return true;
	}
	
	/**
	 * Returns a valid hash code for this <code>Action</code>
	 * 
	 * @return An integer valued hash code
	 */
	public int hashCode() {
		int code = 0;
		for (Action action : this)
			code += action.hashCode();
		return code;
	}
	
}
