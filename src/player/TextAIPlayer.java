package player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import engine.adt.Ply;

/**
 * This is a temporary Player that issues deterministic moves, as
 * <code>DeterministicAiPlayer</code> does. This class is used for
 * TextUI testing.
 */
public class TextAIPlayer {

	/**
	 * Returns the next move deterministically, after sleeping a certain
	 * amount of time (1.5 seconds). It returns the first move out of the
	 * list of valid plies, after ordering them alphanumerically.
	 */
	public static String getNextMove(List<Ply> plies) {
		try {
			Thread.sleep(1500);
		} catch (Exception e) {}
		List<String> pliesAsString = new ArrayList<String>(plies.size());
		for (Ply ply : plies) {
			pliesAsString.add(ply.toString());
		}
		Collections.sort(pliesAsString);
		return pliesAsString.get(0);
	}
}
