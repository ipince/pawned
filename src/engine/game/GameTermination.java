package engine.game;

/**
 * <tt>Throwable</tt> object used to indicate that a <tt>Game<tt> has 
 * terminated, due to a termination condition, as specified in the 
 * <tt>RuleSet</tt>. It contains information
 * about who won the <tt>Game</tt> (or if it was a draw), and why.
 * 
 * GameTerminations are immutable
 * 
 * @specfield type : String // the type of this termination
 * @specfield winner : {white, black, draw} //who won this game
 */
public class GameTermination extends Throwable {

	//AF(x) = A GameTermination G such that
	//         G.type = x.type,
	//         G.winner = "white" if x.winnerIsWhite
	//                    "black" if !x.winnerIsWhite
	//                    "draw" if x.winnerIsWhite == null

	//Representation Invariant
	// * type != null
	
	/**
	 * Auto-generated serial version ID.
	 */
	private static final long serialVersionUID = -3152442348300647126L;

	/**
	 * Holds the winner of the game. If winnerIsWhite is true, then white Player
	 * won, else, black Player won. If it is null, then there was a draw.
	 * 
	 */
	private final Boolean winnerIsWhite;
	
	/**
	 * Holds the reason why the Game ended.
	 */
	private final String type;
	
	/**
	 * Constructs a GameTermination instance with the given parameters.
	 * 
	 * @param winnerIsWhite <tt>true</tt> if the winner was the
	 * 				white player, <tt>false</tt> if the black 
	 * 				player was the winner, <tt>null</tt> in case
	 * 				of a draw.
	 * 
	 * @param type The reason why the game was finalized. 
	 */
	public GameTermination(Boolean winnerIsWhite, String type) {
        super();
		this.winnerIsWhite = winnerIsWhite;
		this.type = type;
		
		checkRep();
    }
    
	/**
	 * Constructs a GameTermination instance with the given parameters.
	 * 
	 * @param winnerIsWhite <tt>true</tt> if the winner was the
	 * 				white player, <tt>false</tt> if the black 
	 * 				player was the winner, <tt>null</tt> in case
	 * 				of a draw.
	 * 
	 * @param type The reason why the game was finalized.
	 * 
	 * @param s The name of this GameTermination
	 */
    public GameTermination(String s, Boolean winnerIsWhite, String type) {
        super(s);
		this.winnerIsWhite = winnerIsWhite;
		this.type = type;
		
		checkRep();
    }
    
    /**
     * Determines whether the winner is the white Player or the black Player. If
     * isWhite() returns null, then there was a draw.
     * 
     * @return <tt>true</tt> if the winner was the
	 * 				white player, <tt>false</tt> if the black 
	 * 				player was the winner, <tt>null</tt> in case
	 * 				of a draw.
     */
	public Boolean winnerIsWhite() {
		checkRep();
		
		return winnerIsWhite;
	}
	
	/**
	 * Returns the reason why this Game was terminated.
	 * 
	 * @return A string representing the type of <tt>this</tt> 
	 */
	public String getType() {
		checkRep();
		
		return type;
	}
	
	/**
	 * Returns a String representation of this <tt>GameTermination</tt>.
	 * 
	 * @return A string representation of this termination. The output
	 * 			is not specified, but a common string representation 
	 * 			of a termination is: 
	 * 			"Game was won by winnerType, due to type" 
	 * 
	 */
	public String toString() {
		checkRep();
		
		if (winnerIsWhite==null)
			return "Game was drawed, due to " + type + ".";
		else
			return "Game was won by " + (winnerIsWhite ? "white":"black") +
					" player, due to " + type + ".";
	}
    
	
	/** 
	 * Verifies that the invariant holds
	 */
	private void checkRep() {
		if (debug.DebugInfo.DEBUG_GAME) {
			if (type == null)
				throw new RuntimeException("Invariant violated for " +
						"GameTermination"); 
		}
	}
}
