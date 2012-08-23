package engine.player.test;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import controller.Controller;
import engine.game.GameTermination;
import engine.player.Player;

/**
 * <p>A <code>DeterministicAiPlayer</code> is a <code>Player</code> that plays
 * automatically. It pretends to use artificial intelligence to determine its
 * moves, but its moves are determined in a deterministic way. This allows
 * testing, because we can know beforehand what the moves of this player will
 * be.</p>
 * 
 * <p>The <code>DeterministicAiPlayer</code> can operate in two ways. In the
 * default mode, it always returns the first valid ply it can play, after
 * ordering the list of valid plies alphanumerically. It can also play a
 * predetermined list of plies sequentially. To use this mode, this list needs
 * to be defined in the constructor. When the list expires, the
 * <code>Player</code> will throw an exception.</p>
 */
public class DeterministicAiPlayer implements Player {

	/**
	 * The <code>Controller</code> operating the game this <code>Player</code>
	 * is playing.
	 */
	private Controller controller = null;
	
	/**
	 * A <code>List</code> of plies for the <code>DeterministicAiPlayer</code>
	 * to use, instead of computing its owns.
	 */
	private List<String> predeterminedPlies;

	/**
	 * Creates a new <code>DeterministicAiPlayer</code>. The player will
	 * determine its moves by selecting the first valid <code>Ply</code>,
	 * after sorting the list of valid <code>Plies</code> alphanumerically.
	 * That is, if its valid <code>Plies</code> are "a2-a4" and "a1-b3", then
	 * it will play "a1-b3".
	 */
	public DeterministicAiPlayer() {
		predeterminedPlies = null;
	}
	
	/**
	 * Creates a new <code>DeterministicAiPlayer</code>. The player will play
	 * the given plies sequentially.
	 */
	public DeterministicAiPlayer(List<String> plies) {
		predeterminedPlies = plies;
	}

	/**
	 * Sets the <code>Controller</code> for this <code>Player</code>.
	 */
	public synchronized void setController(Controller controller) {
		if (this.controller==null)
			this.controller = controller;
	}

	/**
	 * Returns the ply to be played, upon <code>Controller</code> request.
	 */
	public synchronized String submitPly() throws InterruptedException {

		if (controller==null)
			throw new RuntimeException("Player's controller is not set!");

		if (predeterminedPlies==null) {
		List<String> validPlies = controller.getValidPlies();
		List<String> sortedPlies = new ArrayList<String>(validPlies);
		Collections.sort(sortedPlies);
		return sortedPlies.get(0);
		} else {
			if (predeterminedPlies.isEmpty())
				throw new RuntimeException("Player has no more plies to make");
			else {
				String ply = predeterminedPlies.get(0);
				predeterminedPlies.remove(0);
				return ply;
			}
		}
	}

	/**
	 * Informs the <code>Player</code> of the last executed <code>Ply</code>. A
	 * <code>DeterministicAiPlayer</code> does not need any information about
	 * this.
	 */
	public synchronized void inform(String ply) {
		// do nothing (Player goes "Aight, cool...")		
	}

	/**
	 * Informs about a termination condition in the game being played.
	 */
	public synchronized void inform(GameTermination termination) {
		// do nothing (Player goes yay or nay)
	}
}
