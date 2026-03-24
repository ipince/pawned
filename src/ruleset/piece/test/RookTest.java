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
import ruleset.piece.Rook;
import ruleset.ply.Move;
import junit.framework.TestCase;

/**
 * Unit tests for <code>Rook</code>.
 */
public class RookTest extends TestCase {
	
	/**
	 * An empty 8 by 8 <code>RectangularBoard</code>.
	 */
	private Board board;
	
	/**
	 * A white <code>Rook</code>.
	 */
	private Piece whiteRook;
	
	/**
	 * A black <code>Rook</code>.
	 */
	private Piece blackRook;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a <code>Rook</code>
	 * located at [4,5] (e6) in <code>board</code>.
	 */
	private Set<Ply> pliesAtFourFive;
	
	/**
	 * Sets the valid plies in <code>pliesAtFourFive</code>.
	 */
	public RookTest() {
		pliesAtFourFive = new HashSet<Ply>();
		int[] start = {4,5};
		pliesAtFourFive.add(new Move(start, new int[] {0,5}));
		pliesAtFourFive.add(new Move(start, new int[] {1,5}));
		pliesAtFourFive.add(new Move(start, new int[] {2,5}));
		pliesAtFourFive.add(new Move(start, new int[] {3,5}));
		pliesAtFourFive.add(new Move(start, new int[] {5,5}));
		pliesAtFourFive.add(new Move(start, new int[] {6,5}));
		pliesAtFourFive.add(new Move(start, new int[] {7,5}));
		pliesAtFourFive.add(new Move(start, new int[] {4,7}));
		pliesAtFourFive.add(new Move(start, new int[] {4,6}));
		pliesAtFourFive.add(new Move(start, new int[] {4,4}));
		pliesAtFourFive.add(new Move(start, new int[] {4,3}));
		pliesAtFourFive.add(new Move(start, new int[] {4,2}));
		pliesAtFourFive.add(new Move(start, new int[] {4,1}));
		pliesAtFourFive.add(new Move(start, new int[] {4,0}));
	}
	
	/**
	 * JUnit calls this before any test. Initializes <code>board</code> and
	 * <code>Rook</code>s.
	 */
	protected void setUp() {
		board = new RectangularBoard();
		whiteRook = new Rook(true, board);
		blackRook = new Rook(false, board);
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up in a <code>Board</code>
	 * successfully and correctly. It is set up as many times as it should
	 * handle.
	 */
	public void testSetUpNormal() {
		// For a white Piece
		Piece newRook = new Rook(true, board);
		try {
			whiteRook.setUp();
			newRook.setUp();
		} catch (Exception e) {
			fail("White Piece failed to set up appropriately");
		}
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(whiteRook));
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(newRook));
		assertEquals("More than two Pieces (or less) were added when " +
				"setting up", 2, board.getPieces(true).size());
		assertTrue("Another Piece was added to the Board",
				board.getPiece(new int[] {0,0}).getType().equals(new String("rook")) &&
				board.getPiece(new int[] {7,0}).getType().equals(new String("rook")));
		assertEquals("White Piece did not set up itself at the correct position",
				whiteRook, board.getPiece(new int[] {0,0})); // Notice order
		assertEquals("White Piece did not set up itself at the correct position",
				newRook, board.getPiece(new int[] {7,0}));
		// For a black Piece
		newRook = new Rook(false,board);
		try {
			blackRook.setUp();
			newRook.setUp();
		} catch (Exception e) {
			fail("Black Piece failed to set up appropriately");
		}
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(blackRook));
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(newRook));
		assertEquals("More than two Pieces (or less) were added when " +
				"setting up", 2, board.getPieces(false).size());
		assertTrue("Another Piece was added to the Board",
				board.getPiece(new int[] {0,7}).getType().equals(new String("rook")) &&
				board.getPiece(new int[] {7,7}).getType().equals(new String("rook")));
		assertEquals("Black Piece did not set up itself at the correct position",
				blackRook, board.getPiece(new int[] {0,7})); // Notice order
		assertEquals("Black Piece did not set up itself at the correct position",
				newRook, board.getPiece(new int[] {7,7}));
	}
	
	/**
	 * Test that the <code>Piece</code> fails to be set up more than once.
	 */
	public void testSetUpTwice() {
		// For a white Piece
		while (true) {
			try {
				whiteRook.setUp();
			} catch (IllegalStateException e) {
				assertTrue("White Piece was not added to the Board after being set up",
						board.contains(whiteRook));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackRook));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				assertEquals("White Piece did not set up itself at the correct position",
						whiteRook, board.getPiece(new int[] {0,0}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("White Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
		// For a black Piece
		while (true) {
			try {
				blackRook.setUp();
			} catch (IllegalStateException e) {
				assertTrue("Black Piece was not added to the Board after being set up",
						board.contains(blackRook));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				assertEquals("Black Piece did not set up itself at the correct position",
						blackRook, board.getPiece(new int[] {0,7}));
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
		Piece newRook = null;
		while (true) {
			try {
				newRook = new Rook(true, board);
				newRook.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("White Piece threw an IllegalStateException while set" +
						"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("White Piece was added to the Board after failing to set up",
						board.contains(newRook));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackRook));
				assertEquals("More than two white Pieces (or less) were " +
						"added when setting up", 2, board.getPieces(true).size());
				break;
			}
		}
		// For a black Piece
		while (true) {
			try {
				newRook = new Rook(false, board);
				newRook.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("Black Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("Black Piece was added to the Board after failing to set up",
						board.contains(newRook));
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
		unusable.add(new int[] {7,0});
		unusable.add(new int[] {0,7});
		Board newBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight,unusable);
		Piece newRook = new Rook(true, newBoard);
		try {
			newRook.setUp();
			assertEquals("Piece did not set up correctly",
					newRook, newBoard.getPiece(new int[] {0,0}));
			newRook = new Rook(true, newBoard);
			newRook.setUp();
			fail("Piece did not fail while setting up, as it should have");
		} catch (InvalidInitialPositionException e1) {
			try {
				newRook = new Rook(false, newBoard);
				newRook.setUp();
				assertEquals("Piece did not set up correctly",
						newRook, newBoard.getPiece(new int[] {7,7}));
				newRook = new Rook(false, newBoard);
				newRook.setUp();
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
		board.addPiece(whiteRook, new int[] {4,5});
		Set<Ply> correctPlies = pliesAtFourFive;
		checkPlies(correctPlies, whiteRook);
		// For black Piece
		board.removePiece(whiteRook);
		board.addPiece(blackRook, new int[] {4,5});
		checkPlies(correctPlies, blackRook);
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
		board.addPiece(whitePawn1, new int[] {4,6});
		board.addPiece(whitePawn2, new int[] {4,2});
		board.addPiece(whiteRook, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {4,7})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,6})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,2}))||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,0}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteRook);
		// For black Piece
		board.removePiece(whitePawn1);
		board.removePiece(whitePawn2);
		board.removePiece(whiteRook);
		Piece blackPawn1 = new Pawn(false,board);
		Piece blackPawn2 = new Pawn(false,board);
		board.addPiece(blackPawn1, new int[] {4,6});
		board.addPiece(blackPawn2, new int[] {4,2});
		board.addPiece(blackRook, new int[] {4,5});
		checkPlies(correctPlies, blackRook);
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
		board.addPiece(blackPawn, new int[] {4,6});
		board.addPiece(blackBishop, new int[] {4,2});
		board.addPiece(whiteRook, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {4,7})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,0}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteRook);
		// For black Piece
		board.removePiece(blackPawn);
		board.removePiece(blackBishop);
		board.removePiece(whiteRook);
		Piece whitePawn = new Pawn(true,board);
		Piece whiteBishop = new Bishop(true,board);
		board.addPiece(whitePawn, new int[] {4,6});
		board.addPiece(whiteBishop, new int[] {4,2});
		board.addPiece(blackRook, new int[] {4,5});
		checkPlies(correctPlies, blackRook);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (user-defined). The
	 * <code>Plies</code> should exclude those that end in unusable cells and
	 * any cells past the first one.
	 */
	public void testPliesUnusableCenter() {
		// For white Piece with holes (unusable)
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {2,5});
		unusable.add(new int[] {4,2});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece newRook = new Rook(true, holedBoard);
		holedBoard.addPiece(newRook, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {0,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {2,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,0})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,2}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, newRook);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (sides). The
	 * <code>Plies</code> should be restricted by the limits of the
	 * <code>Board</code>.
	 */
	public void testPliesUnusableSides() {
		// For black Piece
		Piece blackPawn = new Pawn(false,board);
		board.addPiece(blackRook, new int[] {0,7});
		board.addPiece(blackPawn, new int[] {2,7});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,7}));
		for (int col = 0; col < 7; col++)
			correctPlies.add(new Move(new int[] {0,7}, new int[] {0,col}));
		checkPlies(correctPlies, blackRook);
	}
	
	/**
	 * Test valid <code>Plies</code> in a complex setting, i.e. a combination
	 * of the previous tests.
	 */
	public void testPliesComplex() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {2,5});
		unusable.add(new int[] {4,0});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece whiteKnight = new Knight(true, holedBoard);
		Piece blackPawn = new Pawn(false, holedBoard);
		Piece newRook = new Rook(true, holedBoard);
		holedBoard.addPiece(whiteKnight, new int[] {4,2});
		holedBoard.addPiece(blackPawn, new int[] {6,5});
		holedBoard.addPiece(newRook, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {4,5}, new int[] {4,7}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {4,6}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {4,4}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {4,3}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {3,5}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {5,5}));
		correctPlies.add(new Move(new int[] {4,5}, new int[] {6,5}));
		checkPlies(correctPlies, newRook);
	}
	
	/**
	 * Test valid <code>Plies</code> when the <code>Piece</code> is not in a
	 * <code>Board</code>. There should be no valid <code>Plies</code>.
	 */
	public void testPliesNone() {
		// For white Piece
		assertEquals("White Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), whiteRook.getPlies());
		// For black Piece
		assertEquals("Black Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), blackRook.getPlies());
	}
	
	/**
	 * Test the getType() method.
	 */
	public void testGetType() {
		assertEquals("The toString() method is not returning what it should",
				new String("rook"), whiteRook.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("rook"), blackRook.getType());
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