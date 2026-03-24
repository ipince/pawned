package engine.player;

import engine.game.GameTermination;

/**
 * A <tt>GameObserver</tt> is an entity that can be subscribed to 
 * the listening message queues in a controller. 
 * 
 * An observer receives notification regarding the plies that 
 * are executed on a given gamem, along with information 
 * regarding the termination of the game. 
 * 
 */
public interface GameObserver {
	
	/**
	 * Method to be called when a ply is executed. 
	 * 
	 * The messaging queue is singly threaded, which implies that this method
	 * is expected to return fast. 
	 * 
	 * An implementation of <tt>GameObserver</tt> should consider 
	 * spawning a new <tt>Thread</tt> whenever lengthy processing
	 * is required. 
	 * 
	 * @param ply the string representation of the ply that was 
	 * 			just executed
	 */
	public void inform(String ply);
	
	/**
	 * Method to be called when the game where the 
	 * observer is subscribed has terminated. 

	 * The messaging queue is singly threaded, which implies that this method
	 * is expected to return fast. 
	 * 
	 * An implementation of <tt>GameObserver</tt> should consider 
	 * spawning a new <tt>Thread</tt> whenever lengthy processing
	 * is required. 
	 * 
	 * 
	 * @param termination the description of the termination 
	 * 		  condition 
	 */
	public void inform(GameTermination termination);

}
