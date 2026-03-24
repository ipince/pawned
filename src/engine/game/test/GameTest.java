package engine.game.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ruleset.antichess.StandardAC;
import ruleset.ply.Move;
import engine.adt.Ply;
import engine.adt.RuleSet;
import engine.game.Game;
import engine.game.GameTermination;
import junit.framework.TestCase;

/**
 * Unit tests for the class Game.
 */
public class GameTest extends TestCase {
	
	/**
	 * Test of the given example file. A bug was found while executing the Game,
	 * so this test was created to find it.
	 */
	public void testExampleGame() {
		RuleSet rs = new StandardAC();
		Game game = new Game(rs);
		Set<Ply> correctPlies = new HashSet<Ply>(initialPlies(true));
		assertEquals("Initial valid Plies are not the same",
				correctPlies, new HashSet<Ply>(game.getValidPlies()));
		System.out.println(game.getBoard().getPiece(new int[] {3,7}).toString());
		assertTrue("1Queen is not where its supposed to be",
				game.getBoard().getPiece(new int[] {3,7}).toString().equals("queen"));
		// Now make the moves of the game
		try {
			game.executePly(rs.plyFactory().getPly("c2-c3", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("c7-c6", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("b2-b3", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("b8-a6", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("b3-b4", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("a6-b4", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("c3-b4", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("c6-c5", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("b4-c5", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		try {
			game.executePly(rs.plyFactory().getPly("b7-b6", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}		try {
			game.executePly(rs.plyFactory().getPly("c5-b6", game.getBoard()));
		} catch (GameTermination gt) {
			fail("Game terminated prematurely");
		}
		System.out.println(game.getValidPlies());
		assertTrue("Queen is not where its supposed to be",
				game.getBoard().getPiece(new int[] {3,7}).toString().equals("queen"));
//		List<Ply> plies = new LinkedList<Ply>();
//		for (Ply ply : plies)
//			try {
//				game.executePly(ply);
//			} catch (GameTermination gt) {
//				fail("Game terminated prematurely.");
//			}
	}
	
	/**
	 * Returns the a Collection of the valid initial Plies
	 */
	private Collection<Ply> initialPlies(boolean isWhite) {
		Collection<Ply> plies = new ArrayList<Ply>(20);
		if (isWhite) {
			for (int i=0; i<8; i++) {
				plies.add(new Move(new int[] {i,1}, new int[] {i,2}));
				plies.add(new Move(new int[] {i,1}, new int[] {i,3}));
			}
			plies.add(new Move(new int[] {1,0}, new int[] {0,2}));
			plies.add(new Move(new int[] {1,0}, new int[] {2,2}));
			plies.add(new Move(new int[] {6,0}, new int[] {5,2}));
			plies.add(new Move(new int[] {6,0}, new int[] {7,2}));
		} else {
			for (int i=0; i<8; i++) {
				plies.add(new Move(new int[] {i,6}, new int[] {i,5}));
				plies.add(new Move(new int[] {i,6}, new int[] {i,4}));
			}
			plies.add(new Move(new int[] {1,7}, new int[] {0,5}));
			plies.add(new Move(new int[] {1,7}, new int[] {2,5}));
			plies.add(new Move(new int[] {6,7}, new int[] {5,5}));
			plies.add(new Move(new int[] {6,7}, new int[] {7,5}));
			}
		return plies;
	}

}
