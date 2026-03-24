package ruleset.piece.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.exception.InvalidInitialPositionException;
import ruleset.board.RectangularBoard;
import ruleset.piece.Bishop;
import ruleset.piece.Knight;
import ruleset.piece.Pawn;
import ruleset.ply.Move;
import junit.framework.TestCase;

/**
 * Unit tests for <code>Knight</code>.
 */
public class KnightTest extends TestCase {
	
	/**
	 * An empty 8 by 8 <code>RectangularBoard</code>.
	 */
	private Board board;
	
	/**
	 * A white <code>Knight</code>.
	 */
	private Piece whiteKnight;
	
	/**
	 * A black <code>Knight</code>.
	 */
	private Piece blackKnight;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a <code>Knight</code>
	 * located at [4,5] (e6) in <code>board</code>.
	 */
	private Set<Ply> pliesAtFourFive;
	
	/**
	 * Sets the valid plies in <code>pliesAtFourFive</code>.
	 */
	public KnightTest() {
		pliesAtFourFive = new HashSet<Ply>();
		int[] start = {4,5};
		pliesAtFourFive.add(new Move(start, new int[] {5,7}));
		pliesAtFourFive.add(new Move(start, new int[] {6,6}));
		pliesAtFourFive.add(new Move(start, new int[] {6,4}));
		pliesAtFourFive.add(new Move(start, new int[] {5,3}));
		pliesAtFourFive.add(new Move(start, new int[] {3,3}));
		pliesAtFourFive.add(new Move(start, new int[] {2,4}));
		pliesAtFourFive.add(new Move(start, new int[] {2,6}));
		pliesAtFourFive.add(new Move(start, new int[] {3,7}));
	}
	
	/**
	 * JUnit calls this before any test. Initializes <code>board</code> and
	 * <code>Knight</code>s.
	 */
	protected void setUp() {
		board = new RectangularBoard();
		whiteKnight = new Knight(true, board);
		blackKnight = new Knight(false, board);
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up in a <code>Board</code>
	 * successfully and correctly. It is set up as many times as it should
	 * handle.
	 */
	public void testSetUpNormal() {
		// For a white Piece
		Piece newKnight = new Knight(true, board);
		try {
			whiteKnight.setUp();
			newKnight.setUp();
		} catch (Exception e) {
			fail("White Piece failed to set up appropriately");
		}
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(whiteKnight));
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(newKnight));
		assertEquals("More than two Pieces (or less) were added when " +
				"setting up", 2, board.getPieces(true).size());
		assertTrue("Another Piece was added to the Board",
				board.getPiece(new int[] {1,0}).getType().equals(new String("knight")) &&
				board.getPiece(new int[] {6,0}).getType().equals(new String("knight")));
		assertEquals("White Piece did not set up itself at the correct position",
				whiteKnight, board.getPiece(new int[] {1,0})); // Notice order
		assertEquals("White Piece did not set up itself at the correct position",
				newKnight, board.getPiece(new int[] {6,0}));
		// For a black Piece
		newKnight = new Knight(false,board);
		try {
			blackKnight.setUp();
			newKnight.setUp();
		} catch (Exception e) {
			fail("Black Piece failed to set up appropriately");
		}
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(blackKnight));
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(newKnight));
		assertEquals("More than two Pieces (or less) were added when " +
				"setting up", 2, board.getPieces(false).size());
		assertTrue("Another Piece was added to the Board",
				board.getPiece(new int[] {1,7}).getType().equals(new String("knight")) &&
				board.getPiece(new int[] {6,7}).getType().equals(new String("knight")));
		assertEquals("Black Piece did not set up itself at the correct position",
				blackKnight, board.getPiece(new int[] {1,7})); // Notice order
		assertEquals("Black Piece did not set up itself at the correct position",
				newKnight, board.getPiece(new int[] {6,7}));
	}
	
	/**
	 * Test that the <code>Piece</code> fails to be set up more than once.
	 */
	public void testSetUpTwice() {
		// For a white Piece
		while (true) {
			try {
				whiteKnight.setUp();
			} catch (IllegalStateException e) {
				assertTrue("White Piece was not added to the Board after being set up",
						board.contains(whiteKnight));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackKnight));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				assertEquals("White Piece did not set up itself at the correct position",
						whiteKnight, board.getPiece(new int[] {1,0}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("White Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
		// For a black Piece
		while (true) {
			try {
				blackKnight.setUp();
			} catch (IllegalStateException e) {
				assertTrue("Black Piece was not added to the Board after being set up",
						board.contains(blackKnight));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				assertEquals("Black Piece did not set up itself at the correct position",
						blackKnight, board.getPiece(new int[] {1,7}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("Black Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up exactly as many times as
	 * it needs, assuming its initial positions correspond to usable cells.
	 */
	public void testSetUpLoopNormal() {
		// For a white Piece
		Piece newKnight = null;
		while (true) {
			try {
				newKnight = new Knight(true, board);
				newKnight.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("White Piece threw an IllegalStateException while set" +
						"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("White Piece was added to the Board after failing to set up",
						board.contains(newKnight));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(newKnight));
				assertEquals("More than two white Pieces (or less) were " +
						"added when setting up", 2, board.getPieces(true).size());
				break;
			}
		}
		// For a black Piece
		while (true) {
			try {
				newKnight = new Knight(false, board);
				newKnight.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("Black Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("Black Piece was added to the Board after failing to set up",
						board.contains(newKnight));
				assertEquals("More than two black Pieces (or less) were " +
						"added when setting up", 2, board.getPieces(false).size());
				break;
			}
		}
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up exactly as many times as
	 * it needs, when some of its initial positions correspond to unusable
	 * cells.
	 */
	public void testSetUpLoopUnusable() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {6,0});
		unusable.add(new int[] {1,7});
		Board newBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight,unusable);
		Piece newKnight = new Knight(true, newBoard);
		try {
			newKnight.setUp();
			assertEquals("Piece did not set up correctly",
					newKnight, newBoard.getPiece(new int[] {1,0}));
			newKnight = new Knight(true, newBoard);
			newKnight.setUp();
			fail("Piece did not fail while setting up, as it should have");
		} catch (InvalidInitialPositionException e1) {
			try {
				newKnight = new Knight(false, newBoard);
				newKnight.setUp();
				assertEquals("Piece did not set up correctly",
						newKnight, newBoard.getPiece(new int[] {6,7}));
				newKnight = new Knight(false, newBoard);
				newKnight.setUp();
				fail("Piece did not fail while setting up, as it should have");
			} catch (InvalidInitialPositionException e2) {
				assertEquals("Number of Pieces in Board is incorrect",
						1, newBoard.getPieces(true).size());
				assertEquals("Number of Pieces in Board is incorrect",
						1, newBoard.getPieces(false).size());
				return;
			}
			fail("Black Piece set itself up successfully to an unusable cell.");
		}
		fail("White Piece set itself up successfully to an unusable cell.");
	}
	
	/**
	 * Test valid <code>Plies</code> in a normal setting (no move-preventing
	 * cells).
	 */
	public void testPliesNormal() {
		// For white Piece
		board.addPiece(whiteKnight, new int[] {4,5});
		Set<Ply> correctPlies = pliesAtFourFive;
		checkPlies(correctPlies, whiteKnight);
		// For black Piece
		board.removePiece(whiteKnight);
		board.addPiece(blackKnight, new int[] {4,5});
		checkPlies(correctPlies, blackKnight);
	}

	/**
	 * Test valid <code>Plies</code> among <code>Piece</code>s of the same
	 * color. The <code>Plies</code> should exclude those that end in a cell
	 * occupied by another <code>Piece</code> of the same color.
	 */
	public void testPliesSameColor() {
		// For white Piece
		Piece whitePawn = new Pawn(true,board);
		board.addPiece(whitePawn, new int[] {5,7});
		board.addPiece(whiteKnight, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!ply.equals(new Move(new int[] {4,5}, new int[] {5,7})))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteKnight);
		// For black Piece
		board.removePiece(whitePawn);
		board.removePiece(whiteKnight);
		Piece blackPawn = new Pawn(false,board);
		board.addPiece(blackPawn, new int[] {5,7});
		board.addPiece(blackKnight, new int[] {4,5});
		checkPlies(correctPlies, blackKnight);
	}
	
	/**
	 * Test valid <code>Plies</code> among <code>Piece</code>s of different
	 * color. The <code>Plies</code> should not exclude anything.
	 */
	public void testPliesDiffColor() {
		// For white Piece
		Piece blackPawn = new Pawn(false,board);
		Piece blackBishop = new Bishop(false,board);
		board.addPiece(blackPawn, new int[] {5,7});
		board.addPiece(blackBishop, new int[] {3,3});
		board.addPiece(whiteKnight, new int[] {4,5});
		Set<Ply> correctPlies = pliesAtFourFive;
		checkPlies(correctPlies, whiteKnight);
		// For black Piece
		board.removePiece(blackPawn);
		board.removePiece(blackBishop);
		board.removePiece(whiteKnight);
		Piece whitePawn = new Pawn(true,board);
		Piece whiteBishop = new Bishop(true,board);
		board.addPiece(whitePawn, new int[] {5,7});
		board.addPiece(whiteBishop, new int[] {3,3});
		board.addPiece(blackKnight, new int[] {4,5});
		checkPlies(correctPlies, blackKnight);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (user-defined). The
	 * <code>Plies</code> should exclude those that end in unusable cells.
	 */
	public void testPliesUnusableCenter() {
		// For white Piece with holes (unusable)
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {5,7});
		unusable.add(new int[] {3,3});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece newKnight = new Knight(true, holedBoard);
		holedBoard.addPiece(newKnight, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!ply.equals(new Move(new int[] {4,5}, new int[] {5,7})) &&
					!ply.equals(new Move(new int[] {4,5}, new int[] {3,3})))
				correctPlies.add(ply);
		checkPlies(correctPlies, newKnight);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (sides). The
	 * <code>Plies</code> should be restricted by the limits of the
	 * <code>Board</code>.
	 */
	public void testPliesUnusableSides() {
		// For black Piece
		board.addPiece(blackKnight, new int[] {0,7});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,5}));
		correctPlies.add(new Move(new int[] {0,7}, new int[] {2,6}));
		checkPlies(correctPlies, blackKnight);
	}
	
	/**
	 * Test valid <code>Plies</code> in a complex setting, i.e. a combination
	 * of the previous tests.
	 */
	public void testPliesComplex() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {5,7});
		unusable.add(new int[] {3,3});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece whiteKnight = new Knight(true, holedBoard);
		Piece blackBishop = new Bishop(false, holedBoard);
		Piece newKnight = new Knight(true, holedBoard);
		holedBoard.addPiece(whiteKnight, new int[] {5,3});
		holedBoard.addPiece(blackBishop, new int[] {2,6});
		holedBoard.addPiece(newKnight, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {4,5}, new int[] {3,7}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {6,6}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {6,4}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {2,4}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {2,6}));
		checkPlies(correctPlies, newKnight);
	}
	
	/**
	 * Test valid <code>Plies</code> when the <code>Piece</code> is not in a
	 * <code>Board</code>. There should be no valid <code>Plies</code>.
	 */
	public void testPliesNone() {
		// For white Piece
		assertEquals("White Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), whiteKnight.getPlies());
		// For black Piece
		assertEquals("Black Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), blackKnight.getPlies());
	}
	
	/**
	 * Test the getType() method.
	 */
	public void testGetType() {
		assertEquals("The toString() method is not returning what it should",
				new String("knight"), whiteKnight.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("knight"), blackKnight.getType());
	}
	
	// No need to test added(), removed(), and updateInfo(), because they dont
	// do anything.
	
	/**
	 * Check that the <code>Plies</code> the <code>Piece</code> would return
	 * match the given set of correct <code>Plies</code>.
	 */
	private void checkPlies(Set<Ply> correctPlies, Piece piece) {
		Set<Ply> obtainedPlies = new HashSet<Ply>(piece.getPlies());
		assertEquals((piece.isWhite() ? "White":"Black") + " Piece fails to" +
				" produce correct set of Plies", correctPlies, obtainedPlies);
	}
	
}
