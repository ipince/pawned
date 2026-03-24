package engine.adt.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import engine.adt.Action;
import engine.adt.Board;
import engine.adt.Ply;
import junit.framework.TestCase;
import engine.adt.Piece; 
import engine.exception.UnusableCellException;

/**
 * Unit tests for the Board class. These tests focus on validating the
 * unusability of a Board.
 */
public class UnusableBoardTest extends TestCase {
	
	/**
	 * Tests that all attempts to add pieces in an unusable board throw an
	 * UnusableCellException. 
	 */
	public void testAddUnusable(Board board, Piece piece, int[] coord) { 
		try {
			board.addPiece(piece, coord);
		}
		catch (UnusableCellException re) {
			return;
		}
		fail("Board adds a piece instead of throwing an exception");
	}

	/**
	 * Tests that all attempts to add null Pieces or Pieces that are not
	 * associated with the Board they are being added to throw a
	 * RuntimeException.
	 */
	public void testAddRuntime(Board board, Piece piece, int[] coord) { 
		try { 
			board.addPiece(piece, coord);
		}
		catch (RuntimeException re) {
			return;
		}
		fail("Board adds a piece instead of throwing an exception");
	}

	/**
	 * Test Piece addition to a UselessBoard.
	 */
	public void testAdd() {
		Board board = new UselessBoard();
		
		// Valid Board, Piece, and position => UnusableCellException
		testAddUnusable(board, new SimplePiece(false, board), new int[] {1,3});
		// Valid Board, Piece; null position => UnusableCellException
		testAddUnusable(board, new SimplePiece(false, board), null);
		// Valid Board, position; null Piece => RuntimeException
		testAddRuntime(board, null, new int[] {1,3});
		// Valid Piece, position; null Board => ???
		// This test doesnt make any sense... youre catching a NullPointerException. This has nothing to do with the specs of Board.addPiece.
		testAddRuntime(null, new SimplePiece(false, board), new int[] {1,3});
		// Valid Board, position; invalid Piece => RuntimeException
		testAddRuntime(new UselessBoard(), new SimplePiece(false, board), new int[] {1,3});
	}
	
	/**
	 * Test removePiece()
	 */
	public void testRemove() {
		fail("Test not yet implemented");
	}
	
	
	/**
	 * Test that after a piece has been unsucessfully
	 * added, it is impossible for it to contain 
	 * it. 
	 *
	 */
	public void testContains() { 
		Board board = new UselessBoard(); 
		Piece w1 = new SimplePiece(true, board);
		try {
			board.addPiece(w1, new int[] {2,3});
		}
		catch (UnusableCellException re) {
			assertFalse("Board shouldn't contain any piece", 
					board.contains(w1)); 
			assertFalse("Board shouldn't contain any piece", 
					board.contains(new SimplePiece(false, board))); 
			assertFalse("Board shouldn't contain any piece", 
					board.contains(null)); 
			
		}
	}
			
	/**
	 * Tests getPiece()
	 * Tests that getPiece() returns null after 
	 * failed attempts to add a piece into a board. 
	 *
	 */
	public void testGetPiece() {
		Board board = new UselessBoard();
		Piece w1 = new SimplePiece(true, board);
		try {
			board.addPiece(w1, new int[] {2,3});
		}
		catch (UnusableCellException re) {
			assertNull("UselessBoard should always return null for getPiece()", 
					board.getPiece(new int[] {2,3}));
			assertNull("UselessBoard should always return null for getPiece()", 
					null); // What's being tested here?
			assertNull("UselessBoard should always return null for getPiece()", 
					board.getPiece(new int[] {-1,3}));
			assertNull("UselessBoard should always return null for getPiece()", 
					board.getPiece(new int[] {Integer.MAX_VALUE,Integer.MIN_VALUE, 0}));
			assertFalse("Piece list not empty or null", board.getPieces(true).iterator().hasNext()); 
		}
	}
	
	/**
	 * Tests getPosition()
	 * Tests that the getPosition() returns null for all 
	 * positions that are empty or unusable. 
	 *
	 */
	public void testGetPosition() {
		Board board = new UselessBoard();
		Piece w1 = new SimplePiece(true, board);
		try {
			board.addPiece(w1, new int[] {2,5,7});
		}
		catch (UnusableCellException re) {
			assertNull(board.getPosition(w1)); 
			assertNull(board.getPosition(null)); 
			assertNull(board.getPosition(new SimplePiece(true, board)));
			return;
		}
		fail("Board adds a thing when it should not");
	}

	/**
	 * Tests getPieces()
	 * Tests that testGetPieces() returns a non-null
	 * but empty set when there are no pieces
	 * in the board. 
	 *
	 */
	public void testGetPieces() { 
		Board board = new UselessBoard(); 
		Piece w1 = new SimplePiece(true, board);
		try {
			board.addPiece(w1, new int[] {2,5,7});
		}
		catch (UnusableCellException re) {
			assertFalse("List of pieces is either not empty or null", 
					board.getPieces(true).iterator().hasNext());
			assertFalse("List of pieces is either not empty or null", 
					board.getPieces(false).iterator().hasNext());
			return;
		}
		fail("Board adds a thing when it should not");
	}
	
	/**
	 * Tests that empty Ply is executed.
	 */
	public void testEmptyPly() {
		Board board = new UselessBoard();
		final List<Action> list = new ArrayList<Action>();
		Ply ply = new Ply()  {

			@Override
			public Iterator<Action> iterator() {
				return list.iterator();
			}

			@Override
			public String toString() {
				return "testing";
			}
			 
		};
		try {
			board.executePly(ply);
		}
		catch (RuntimeException re){
			fail("Invalidly fails ply");
		}
		return; 
	}
	
	/**
	 * Tests that no Ply can be executed except for the empty one.
	 * Tests that no ply can be executed in a board with 
	 * all cells unusable, except for the empty ply
	 *
	 */
	public void testAddPlies() {
		Board board = new UselessBoard(); 
		Piece w1 = new SimplePiece(true, board);

		Action a1 = Action.makeAdd(new int[] {1,2,3}, w1);
		Action a2 = Action.makeRemove(new int[] {1,2,3});
		Action a3 = Action.makeAdd(new int[] {4,1,2}, w1);
		
		final List<Action> list = Arrays.asList(a1,a2,a3);
		
		Ply ply = new Ply()  {

			@Override
			public Iterator<Action> iterator() {
				return list.iterator(); 
			}

			@Override
			public String toString() {
				return "testing";
			}	 
		};
		
		try { 
			board.executePly(ply); 
		}
		catch (RuntimeException re) {
			// (if glass-box) This should be an IllegalArgumentException
			// because the first ADD Action tries to add something to an
			// unusable cell.
			// System.out.println(re.getMessage()); // "Malformed ply"
			return;
		}
		
		fail("Invalidly executed ply"); 
		
	}
	
	/**
	 * Test isUsable()
	 */
	public void testIsUsable() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test isEmpty()
	 */
	public void testIsEmpty() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test clone()
	 */
	public void testClone() {
		fail("Test not yet implemented");
	}
	
}