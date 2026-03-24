package engine.game;

import java.util.List;

import engine.adt.Ply;

/**
 * Contains information about a <code>Game</code> at a particular point 
 * in time. 
 * 
 * This information includes the <code>List</code> of valid <code>Ply
 * </code> for the current Board configuration, the <code>Player</code>
 * for which these Plies are valid, and a list fo <code>GameMessages<code</code>
 *  that are applicable at the moment.
 *  
 *  @specfield validPlies : sequence     //the sequence of valid plies
 *  @specfield nextPlayerWhite : boolean //the player who should execute them
 *  @specfield messages : sequence  	 //the sequence of messages associated 
 *  									 //with the state.
 *  
 *  
 */
public class GameInfo {

	//AF(x) = a GameInfo G such that
	//	G.validPlies = x.validPlies;
	//  G.nextPlayerWhite = x.nextPlayerWhite;
	//  G.messages = x.messages 
	//
	
	//Representation Invariant
	//    * validPlies, messages  != null

	//Fields 
	private final List<Ply> validPlies;
	private final boolean nextPlayerWhite;
	private final List<GameMessage> messages;

	
	//Constructors
	
	public GameInfo(List<Ply> validPlies, boolean nextPlayerWhite, List<GameMessage> messages) {
		this.validPlies = validPlies;
		this.nextPlayerWhite = nextPlayerWhite;
		this.messages = messages;

		checkRep(); 

	}
	
	//Methods 
	/**
	 * Returns the list of valid plies for <tt>this</tt>. 
	 * 
	 * @return A list of valid plies associated with the current 
	 * 		   state of the game s.t. getPlies().size > 0. A 
	 * 		   list of valid plies can never be empty in 
	 * 		   a game information bean, since <tt>GameInfo</tt>
	 * 		   objects are only associated with running games. 
	 * 
	 */
	public List<Ply> getPlies() {
		checkRep(); 
		return validPlies;
	}
	
	/**
	 * Returns the type of player that should execute the next move. 
	 * That is, the next turn to be played in a game should be
	 * <tt>this.getTurn()</tt> 
	 * 
	 * @return <tt>true</tt> if the next player is white, <tt>false</tt>
	 * 		   otherwise. 
	 */
	public boolean getTurn() {
		checkRep(); 
		return nextPlayerWhite;
	}
	
	/**
	 * Returns a list of messages associated with the current state. 
	 * When there are no messages, this list is empty. 
	 * 
	 * @return The list of <tt>GameMessage</tt> objects associated 
	 * 			with <tt>this</tt>.
	 * 
	 */
	public List<GameMessage> getMessages() {
		checkRep(); 
		return messages;
	}
	
	/** 
	 * Ensure that the representation invariant holds. 
	 */
	private void checkRep() {
		if (debug.DebugInfo.DEBUG_GAME) {
			if (messages == null || validPlies == null)
				throw new RuntimeException("Invariant violated for " +
						"GameInfo"); 
		}
	}
}
