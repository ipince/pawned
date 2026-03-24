package engine.game;

/**
 * A <tt>GameMessage</tt> contains information about the status of a 
 * <tt>Game</tt> at some point in time, pertaining to a <tt>Player</tt>.
 *  For example, in 6.170 Antichess, a <tt>GameMessage</tt>
 *  might indicate the black Player that he/she is in check.
 *  
 * A <tt>GameMessage</tt> is immutable. 
 * 
 * @specfield isWhite: boolean //the color of the player
 * 							   //to which the message refers. 
 * @specfield message : string //a description of the message 
 * 
 */
public class GameMessage {
	
	//AF(x) = A GameMessage G such that
	//       G.isWhite = x.isWhite
	//       G.message = x.message
	
	//Representation Invariant
	//      * message != null
	
	/**
	 * Player of which this information is about.
	 */
	private final Boolean isWhite;
	
	/**
	 * Message for the Player.
	 */
	private final String message;
	
	/**
	 * Construcs a new GameMessage out of the specified message and Player.
	 * 
	 * @param isWhite <tt>true</tt> if the player to which this message
	 * 			refers is white, <tt>false</tt> if the player
	 * 			to which this message refers is black, or <tt>null</tt> if the message
	 * 		    if the message is directed towards the ruleset. 
	 * @param message A string representation of the message 
	 */
	public GameMessage(Boolean isWhite, String message) {
		this.isWhite = isWhite;
		this.message = message;

		checkRep(); 
	}
	
	/**
	 * Returns the player of which this <tt>GameMessage</tt>  is about.
	 * 
	 * @return <tt>true</tt> if the player is white, <tt>false</tt>
	 * 		if the player is black, or <tt>null</tt> if the message
	 * 		is directed towards the ruleset. 
	 */
	public Boolean isWhite() {
		checkRep();
		
		return isWhite;
	}
	
	/**
	 * Returns the message for the Player.
	 * 
	 * @return a string containing the game message of the player. 
	 */
	public String getMessage() {
		checkRep(); 
		
		return message;
	}
	
	/**
	 * Verifies that the invariant holds
	 */
	private void checkRep() {
		if (debug.DebugInfo.DEBUG_GAME) {
			if (message == null)
				throw new RuntimeException("Invariant violated for"
						+ "GameMessage");
		}
	}
}
