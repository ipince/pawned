package player;

/**
 * A <code>BoardNode</code> represents a node in a game tree. That is, 
 * a node containing information about the move and the score associated 
 * with a particular choice.
 * 
 * A score of a node gives information regarding how good a particular ply 
 * is during the specific stage of the game that is being played.
 *           
 */

public class BoardNode {
	//Fields 
	private String move; 
	private int value; 

	//Constructor
	public BoardNode(String move, int value) {
		this.move = move;
		this.value = value;
	}

	//Methods
	public String toString() {
		return "[Move=" + move + ".Value="+value+"]" ; 
	}

	public String getMove() {
		return move;
	}

	public void setMove(String move) {
		this.move = move;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}