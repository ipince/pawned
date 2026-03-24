package engine.player;

import controller.Controller;

/**
 * A <code>Player</code> performs moves in a sequential game controlled by 
 * a <code>Controller</code>. 
 * 
 * @see engine.player.GameObserver
 */
public interface Player extends GameObserver {
	
	/**
	 * Asks the Player to submit a Ply. An implementation 
	 * of <tt>GameObserver</tt> should ensure that whenever
	 * the thread is interrupted, the method throws an 
	 * <tt>InterruptedException</tt> as soon as possible. 
	 * 
	 * 
	 * @return The string representing a move to be passed
	 * 			to the controller 
	 * 
	 * @throws InterruptedException if the thread is interrupted
	 * 			while executing this methud
	 */
	public String submitPly() throws InterruptedException;
	
	/**
	 * Sets the controller from which the player is receiving 
	 * events. 
	 * 
	 * @param controller the controller to be associated with
	 * 		<tt>this</tt>
	 * @requires the controller is a valid controller, i.e.
	 * 			that the parameter is a controller from which
	 * 			this object's <tt>submitPly()</tt> will be
	 * 			executed. 
	 */
	public void setController(Controller controller);

}
