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
 * Unit tests for <code>Bishop</code>.
 */
public class BishopTest extends TestCase {
	
	/**
	 * An empty 8 by 8 <code>RectangularBoard</code>.
	 */
	private Board board;
	
	/**
	 * A white <code>Bishop</code>.
	 */
	private Piece whiteBishop;
	
	/**
	 * A black <code>Bishop</code>.
	 */
	private Piece blackBishop;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a <code>Bishop</code>
	 * located at [4,5] (e6) in <code>board</code>.
	 */
	private Set<Ply> pliesAtFourFive;
	
	/**
	 * Sets the valid plies in <code>pliesAtFourFive</code>.
	 */
	public BishopTest() {
		pliesAtFourFive = new HashSet<Ply>();
		int[] start = {4,5};
		pliesAtFourFive.add(new Move(start, new int[] {2,7}));
		pliesAtFourFive.add(new Move(start, new int[] {3,6}));
		pliesAtFourFive.add(new Move(start, new int[] {5,6}));
		pliesAtFourFive.add(new Move(start, new int[] {6,7}));
		pliesAtFourFive.add(new Move(start, new int[] {5,4}));
		pliesAtFourFive.add(new Move(start, new int[] {6,3}));
		pliesAtFourFive.add(new Move(start, new int[] {7,2}));
		pliesAtFourFive.add(new Move(start, new int[] {3,4}));
		pliesAtFourFive.add(new Move(start, new int[] {2,3}));
		pliesAtFourFive.add(new Move(start, new int[] {1,2}));
		pliesAtFourFive.add(new Move(start, new int[] {0,1}));
	}
	
	/**
	 * JUnit calls this before any test. Initializes <code>board</code> and
	 * <code>Bishop</code>s.
	 */
	protected void setUp() {
		board = new RectangularBoard();
		whiteBishop = new Bishop(true, board);
		blackBishop = new Bishop(false, board);
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up in a <code>Board</code>
	 * successfully and correctly. It is set up as many times as it should
	 * handle.
	 */
	public void testSetUpNormal() {
		// For a white Piece
		Piece newBishop = new Bishop(true, board);
		try {
			whiteBishop.setUp();
			newBishop.setUp();
		} catch (Exception e) {
			fail("White Piece failed to set up appropriately");
		}
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(whiteBishop));
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(newBishop));
		assertEquals("More than two Pieces (or less) were added when " +
				"setting up", 2, board.getPieces(true).size());
		assertTrue("Another Piece was added to the Board",
				board.getPiece(new int[] {2,0}).getType().equals(new String("bishop")) &&
				board.getPiece(new int[] {5,0}).getType().equals(new String("bishop")));
		assertEquals("White Piece did not set up itself at the correct position",
				whiteBishop, board.getPiece(new int[] {2,0})); // Notice order
		assertEquals("White Piece did not set up itself at the correct position",
				newBishop, board.getPiece(new int[] {5,0}));
		// For a black Piece
		newBishop = new Bishop(false,board);
		try {
			blackBishop.setUp();
			newBishop.setUp();
		} catch (Exception e) {
			fail("Black Piece failed to set up appropriately");
		}
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(blackBishop));
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(newBishop));
		
		assertEquals("More than two Pieces (or less) were added when " +
				"setting up", 2, board.getPieces(false).size());
		assertTrue("Another Piece was added to the Board",
				board.getPiece(new int[] {2,7}).getType().equals(new String("bishop")) &&
				board.getPiece(new int[] {5,7}).getType().equals(new String("bishop")));
		assertEquals("Black Piece did not set up itself at the correct position",
				blackBishop, board.getPiece(new int[] {2,7})); // Notice order
		assertEquals("Black Piece did not set up itself at the correct position",
				newBishop, board.getPiece(new int[] {5,7}));
	}
	
	/**
	 * Test that the <code>Piece</code> fails to be set up more than once.
	 */
	public void testSetUpTwice() {
		// For a white Piece
		while (true) {
			try {
				whiteBishop.setUp();
			} catch (IllegalStateException e) {
				assertTrue("White Piece was not added to the Board after being set up",
						board.contains(whiteBishop));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackBishop));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				assertEquals("White Piece did not set up itself at the correct position",
						whiteBishop, board.getPiece(new int[] {2,0}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("White Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
		// For a black Piece
		while (true) {
			try {
				blackBishop.setUp();
			} catch (IllegalStateException e) {
				assertTrue("Black Piece was not added to the Board after being set up",
						board.contains(blackBishop));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				assertEquals("Black Piece did not set up itself at the correct position",
						blackBishop, board.getPiece(new int[] {2,7}));
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
		Piece newBishop = null;
		while (true) {
			try {
				newBishop = new Bishop(true, board);
				newBishop.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("White Piece threw an IllegalStateException while set" +
						"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("White Piece was added to the Board after failing to set up",
						board.contains(newBishop));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackBishop));
				assertEquals("More than two white Pieces (or less) were " +
						"added when setting up", 2, board.getPieces(true).size());
				break;
			}
		}
		// For a black Piece
		while (true) {
			try {
				newBishop = new Bishop(false, board);
				newBishop.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("Black Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("Black Piece was added to the Board after failing to set up",
						board.contains(newBishop));
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
	public void testSetUpUnusable() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {5,0});
		unusable.add(new int[] {2,7});
		Board newBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight,unusable);
		Piece newBishop = new Bishop(true, newBoard);
		try {
			newBishop.setUp();
			assertEquals("Piece did not set up correctly",
					newBishop, newBoard.getPiece(new int[] {2,0}));
			newBishop = new Bishop(true, newBoard);
			newBishop.setUp();
			fail("Piece did not fail while setting up, as it should have");
		} catch (InvalidInitialPositionException e1) {
			try {
				newBishop = new Bishop(false, newBoard);
				newBishop.setUp();
				assertEquals("Piece did not set up correctly",
						newBishop, newBoard.getPiece(new int[] {5,7}));
				newBishop = new Bishop(false, newBoard);
				newBishop.setUp();
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
		board.addPiece(whiteBishop, new int[] {4,5});
		Set<Ply> correctPlies = pliesAtFourFive;
		checkPlies(correctPlies, whiteBishop);
		// For black Piece
		board.removePiece(whiteBishop);
		board.addPiece(blackBishop, new int[] {4,5});
		checkPlies(correctPlies, blackBishop);
	}

	/**
	 * Test valid <code>Plies</code> among <code>Piece</code>s of the same
	 * color. The <code>Plies</code> should exclude those that end in a cell
	 * occupied by another <code>Piece</code> of the same color or any cells
	 * past that one.
	 */
	public void testPliesSameColor() {
		// For white Piece
		Piece whitePawn1 = new Pawn(true,board);
		Piece whitePawn2 = new Pawn(true,board);
		board.addPiece(whitePawn1, new int[] {5,6});
		board.addPiece(whitePawn2, new int[] {2,3});
		board.addPiece(whiteBishop, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {0,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {2,3}))||
					ply.equals(new Move(new int[] {4,5}, new int[] {5,6})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {6,7}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteBishop);
		// For black Piece
		board.removePiece(whitePawn1);
		board.removePiece(whitePawn2);
		board.removePiece(whiteBishop);
		Piece blackPawn1 = new Pawn(false,board);
		Piece blackPawn2 = new Pawn(false,board);
		board.addPiece(blackPawn1, new int[] {5,6});
		board.addPiece(blackPawn2, new int[] {2,3});
		board.addPiece(blackBishop, new int[] {4,5});
		checkPlies(correctPlies, blackBishop);
	}
	
	/**
	 * Test valid <code>Plies</code> among <code>Piece</code>s of different
	 * color. The <code>Plies</code> should include those that capture the 
	 * <code>Piece</code>, but exclude those that end in a cell past that one.
	 */
	public void testPliesDiffColor() {
		// For white Piece
		Piece blackPawn = new Pawn(false,board);
		Piece blackBishop = new Bishop(false,board);
		board.addPiece(blackPawn, new int[] {5,6});
		board.addPiece(blackBishop, new int[] {2,3});
		board.addPiece(whiteBishop, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {0,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {6,7}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteBishop);
		// For black Piece
		board.removePiece(blackPawn);
		board.removePiece(blackBishop);
		board.removePiece(whiteBishop);
		Piece whitePawn = new Pawn(true,board);
		Piece whiteBishop = new Bishop(true,board);
		board.addPiece(whitePawn, new int[] {5,6});
		board.addPiece(whiteBishop, new int[] {2,3});
		board.addPiece(blackBishop, new int[] {4,5});
		checkPlies(correctPlies, blackBishop);	
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (user-defined). The
	 * <code>Plies</code> should exclude those that end in unusable cells and
	 * any cells past the first one.
	 */
	public void testPliesUnusableCenter() {
		// For white Piece with holes (unusable)
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {5,6});
		unusable.add(new int[] {1,2});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece newBishop = new Bishop(true, holedBoard);
		holedBoard.addPiece(newBishop, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {0,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {5,6})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {6,7}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, newBishop);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (sides). The
	 * <code>Plies</code> should be restricted by the limits of the
	 * <code>Board</code>.
	 */
	public void testPliesUnusableSides() {
		// For black Piece
		Piece blackPawn = new Pawn(false,board);
		board.addPiece(blackBishop, new int[] {0,7});
		board.addPiece(blackPawn, new int[] {2,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,6}));
		checkPlies(correctPlies, blackBishop);
	}
	
	/**
	 * Test valid <code>Plies</code> in a complex setting, i.e. a combination
	 * of the previous tests.
	 */
	public void testPliesComplex() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {6,3});
		unusable.add(new int[] {0,1});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece whiteKnight = new Knight(true, holedBoard);
		Piece blackPawn = new Pawn(false, holedBoard);
		Piece newBishop = new Bishop(true, holedBoard);
		holedBoard.addPiece(whiteKnight, new int[] {2,3});
		holedBoard.addPiece(blackPawn, new int[] {5,6});
		holedBoard.addPiece(newBishop, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {4,5}, new int[] {2,7}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {3,6}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {5,6}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {3,4}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {5,4}));
		checkPlies(correctPlies, newBishop);
	}
	
	/**
	 * Test valid <code>Plies</code> when the <code>Piece</code> is not in a
	 * <code>Board</code>. There should be no valid <code>Plies</code>.
	 */
	public void testPliesNone() {
		// For white Piece
		assertEquals("White Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), whiteBishop.getPlies());
		// For black Piece
		assertEquals("Black Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), blackBishop.getPlies());
	}
	
	/**
	 * Test the getType() method.
	 */
	public void testGetType() {
		assertEquals("The toString() method is not returning what it should",
				new String("bishop"), whiteBishop.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("bishop"), blackBishop.getType());
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