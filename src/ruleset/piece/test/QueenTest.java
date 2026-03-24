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
import ruleset.piece.Queen;
import ruleset.ply.Move;
import junit.framework.TestCase;

/**
 * Unit tests for <code>Queen</code>.
 */
public class QueenTest extends TestCase {

	/**
	 * An empty 8 by 8 <code>RectangularBoard</code>.
	 */
	private Board board;
	
	/**
	 * A white <code>Queen</code>.
	 */
	private Piece whiteQueen;
	
	/**
	 * A black <code>Queen</code>.
	 */
	private Piece blackQueen;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a <code>Queen</code>
	 * located at [4,5] (e6) in <code>board</code>.
	 */
	private Set<Ply> pliesAtFourFive;

	/**
	 * Sets the valid plies in <code>pliesAtFourFive</code>.
	 */
	public QueenTest() {
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
	 * <code>Queen</code>s.
	 */
	protected void setUp() {
		board = new RectangularBoard();
		whiteQueen = new Queen(true, board);
		blackQueen = new Queen(false, board);
	}

	/**
	 * Test that the <code>Piece</code> sets itself up in a <code>Board</code>
	 * successfully and correctly. It is set up as many times as it should
	 * handle.
	 */
	public void testSetUpNormal() {
		// For a white Piece
		try {
			whiteQueen.setUp();
		} catch (Exception e) {
			fail("White Piece failed to set up appropriately");
		}
		assertTrue("White Piece was not added to the Board after being set up",
				board.contains(whiteQueen));
		assertEquals("More than one Piece (or no Pieces) was (were) added when " +
				"setting up", 1, board.getPieces(true).size());
		assertEquals("White Piece did not set up itself at the correct position",
				whiteQueen, board.getPiece(new int[] {3,0}));
		// For a black Piece
		try {
			blackQueen.setUp();
		} catch (Exception e) {
			fail("Black Piece failed to set up appropriately");
		}
		assertTrue("Black Piece was not added to the Board after being set up",
				board.contains(blackQueen));
		assertEquals("More than one Piece (or no Pieces) was (were) added when " +
				"setting up", 1, board.getPieces(false).size());
		assertEquals("Black Piece did not set up itself at the correct position",
				blackQueen, board.getPiece(new int[] {3,7}));
	}

	/**
	 * Test that the <code>Piece</code> fails to be set up more than once.
	 */
	public void testSetUpTwice() {
		// For a white Piece
		while (true) {
			try {
				whiteQueen.setUp();
			} catch (IllegalStateException e) {
				assertTrue("White Piece was not added to the Board after being set up",
						board.contains(whiteQueen));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackQueen));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				assertEquals("White Piece did not set up itself at the correct position",
						whiteQueen, board.getPiece(new int[] {3,0}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("White Piece threw an InvalidInitialPositionException while " +
				"setting up more than once. This should not happen.");
			}
		}
		// For a black Piece
		while (true) {
			try {
				blackQueen.setUp();
			} catch (IllegalStateException e) {
				assertTrue("Black Piece was not added to the Board after being set up",
						board.contains(blackQueen));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				assertEquals("Black Piece did not set up itself at the correct position",
						blackQueen, board.getPiece(new int[] {3,7}));
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
		Piece newQueen = null;
		while (true) {
			try {
				newQueen = new Queen(true, board);
				newQueen.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("White Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("White Piece was added to the Board after failing to set up",
						board.contains(newQueen));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackQueen));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				break;
			}
		}
		// For a black Piece
		while (true) {
			try {
				newQueen = new Queen(false, board);
				newQueen.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("Black Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("Black Piece was added to the Board after failing to set up",
						board.contains(newQueen));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
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
		unusable.add(new int[] {3,0});
		unusable.add(new int[] {3,7});
		Board newBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight,unusable);
		Piece newQueen = new Queen(true, newBoard);
		try {
			newQueen.setUp();
		} catch (InvalidInitialPositionException e1) {
			newQueen = new Queen(false, newBoard);
			try {
				newQueen.setUp();
			} catch (InvalidInitialPositionException e2) {
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
		board.addPiece(whiteQueen, new int[] {4,5});
		Set<Ply> correctPlies = pliesAtFourFive;
		checkPlies(correctPlies, whiteQueen);
		// For black Piece
		board.removePiece(whiteQueen);
		board.addPiece(blackQueen, new int[] {4,5});
		checkPlies(correctPlies, blackQueen);
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
		Piece whitePawn3 = new Pawn(true,board);
		Piece whitePawn4 = new Pawn(true,board);
		board.addPiece(whitePawn1, new int[] {4,6});
		board.addPiece(whitePawn2, new int[] {4,2});
		board.addPiece(whitePawn3, new int[] {5,6});
		board.addPiece(whitePawn4, new int[] {2,3});
		board.addPiece(whiteQueen, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {4,7})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,6})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,2}))||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,0})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {0,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {2,3})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {5,6})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {6,7}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteQueen);
		// For black Piece
		board.removePiece(whitePawn1);
		board.removePiece(whitePawn2);
		board.removePiece(whitePawn3);
		board.removePiece(whitePawn4);
		board.removePiece(whiteQueen);
		Piece blackPawn1 = new Pawn(false,board);
		Piece blackPawn2 = new Pawn(false,board);
		Piece blackPawn3 = new Pawn(false,board);
		Piece blackPawn4 = new Pawn(false,board);
		board.addPiece(blackPawn1, new int[] {4,6});
		board.addPiece(blackPawn2, new int[] {4,2});
		board.addPiece(blackPawn3, new int[] {5,6});
		board.addPiece(blackPawn4, new int[] {2,3});
		board.addPiece(blackQueen, new int[] {4,5});
		checkPlies(correctPlies, blackQueen);
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
		board.addPiece(blackBishop, new int[] {2,3});
		board.addPiece(whiteQueen, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {4,7})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {0,1}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteQueen);
		// For black Piece
		board.removePiece(blackPawn);
		board.removePiece(blackBishop);
		board.removePiece(whiteQueen);
		Piece whitePawn = new Pawn(true,board);
		Piece whiteBishop = new Bishop(true,board);
		board.addPiece(whitePawn, new int[] {4,6});
		board.addPiece(whiteBishop, new int[] {2,3});
		board.addPiece(blackQueen, new int[] {4,5});
		checkPlies(correctPlies, blackQueen);
	}

	/**
	 * Test valid <code>Plies</code> among unusable cells (user-defined). The
	 * <code>Plies</code> should exclude those that end in unusable cells and
	 * any cells past the first one.
	 */
	public void testPliesUnusableCenter() {
		// For white Piece with holes (unusable)
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {2,3});
		unusable.add(new int[] {2,5});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
				RectangularBoard.defaultHeight, unusable);
		Piece newQueen = new Queen(true, holedBoard);
		holedBoard.addPiece(newQueen, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {0,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {2,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {0,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {2,3}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, newQueen);
	}

	/**
	 * Test valid <code>Plies</code> among unusable cells (sides). The
	 * <code>Plies</code> should be restricted by the limits of the
	 * <code>Board</code>.
	 */
	public void testPliesUnusableSides() {
		// For black Piece
		Piece blackPawn1 = new Pawn(false,board);
		Piece blackPawn2 = new Pawn(false,board);
		Piece blackPawn3 = new Pawn(false,board);
		board.addPiece(blackQueen, new int[] {0,7});
		board.addPiece(blackPawn1, new int[] {0,5});
		board.addPiece(blackPawn2, new int[] {2,5});
		board.addPiece(blackPawn3, new int[] {3,7});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {0,7}, new int[] {0,6}));
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,6}));
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,7}));
		correctPlies.add(new Move(new int[] {0,7}, new int[] {2,7}));
		checkPlies(correctPlies, blackQueen);
	}

	/**
	 * Test valid <code>Plies</code> in a complex setting, i.e. a combination
	 * of the previous tests.
	 */
	public void testPliesComplex() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {2,5});
		unusable.add(new int[] {6,3});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
				RectangularBoard.defaultHeight, unusable);
		Piece whiteKnight = new Knight(true, holedBoard);
		Piece blackPawn = new Pawn(false, holedBoard);
		Piece newQueen = new Queen(true, holedBoard);
		holedBoard.addPiece(whiteKnight, new int[] {4,2});
		holedBoard.addPiece(blackPawn, new int[] {6,5});
		holedBoard.addPiece(newQueen, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!(ply.equals(new Move(new int[] {4,5}, new int[] {0,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {1,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {2,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {7,5})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {6,3})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {7,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,2})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,1})) ||
					ply.equals(new Move(new int[] {4,5}, new int[] {4,0}))))
				correctPlies.add(ply);
		checkPlies(correctPlies, newQueen);
	}

	/**
	 * Test valid <code>Plies</code> when the <code>Piece</code> is not in a
	 * <code>Board</code>. There should be no valid <code>Plies</code>.
	 */
	public void testPliesNone() {
		// For white Piece
		assertEquals("White Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), whiteQueen.getPlies());
		// For black Piece
		assertEquals("Black Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), blackQueen.getPlies());
	}

	/**
	 * Test the getType() method.
	 */
	public void testGetType() {
		assertEquals("The toString() method is not returning what it should",
				new String("queen"), whiteQueen.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("queen"), blackQueen.getType());
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