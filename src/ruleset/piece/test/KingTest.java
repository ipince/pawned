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
import ruleset.piece.King;
import ruleset.piece.Knight;
import ruleset.piece.Pawn;
import ruleset.piece.Rook;
import ruleset.ply.Castle;
import ruleset.ply.Move;
import junit.framework.TestCase;

/**
 * Unit tests for <code>King</code>.
 */
public class KingTest extends TestCase {
	
	/**
	 * An empty 8 by 8 <code>RectangularBoard</code>.
	 */
	private Board board;
	
	/**
	 * A white <code>King</code> with castling disabled.
	 */
	private Piece whiteKing;
	
	/**
	 * A white <code>King</code> with castling enabled.
	 */
	private Piece whiteCastlingKing;
	
	/**
	 * A white <code>Rook</code> on the left to castle with.
	 */
	private Piece whiteLeftRook;
	
	/**
	 * A white <code>Rook</code> on the right to castle with.
	 */
	private Piece whiteRightRook;
	
	/**
	 * A black <code>King</code> with castling disabled.
	 */
	private Piece blackKing;
	
	/**
	 * A black <code>King</code> with castling disabled.
	 */
	private Piece blackCastlingKing;
	
	/**
	 * A black <code>Rook</code> on the left to castle with.
	 */
	private Piece blackLeftRook;
	
	/**
	 * A black <code>Rook</code> on the right to castle with.
	 */
	private Piece blackRightRook;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a <code>King</code>
	 * located at [4,5] (e6) in <code>board</code>.
	 */
	private Set<Ply> pliesAtFourFive;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a white
	 * <code>King</code> with castling enabled at its initial position.
	 */
	private Set<Ply> correctWhitePlies = new HashSet<Ply>();
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a black
	 * <code>King</code> with castling enabled at its initial position.
	 */
	private Set<Ply> correctBlackPlies = new HashSet<Ply>();

	
	/**
	 * Sets the valid plies in <code>pliesAtFourFive</code>,
	 * <code>correctWhitePlies</code>, and <code>correctBlackPlies</code>.
	 */
	public KingTest() {
		pliesAtFourFive = new HashSet<Ply>();
		int[] start = {4,5};
		pliesAtFourFive.add(new Move(start, new int[] {4,6}));
		pliesAtFourFive.add(new Move(start, new int[] {5,6}));
		pliesAtFourFive.add(new Move(start, new int[] {5,5}));
		pliesAtFourFive.add(new Move(start, new int[] {5,4}));
		pliesAtFourFive.add(new Move(start, new int[] {4,4}));
		pliesAtFourFive.add(new Move(start, new int[] {3,4}));
		pliesAtFourFive.add(new Move(start, new int[] {3,5}));
		pliesAtFourFive.add(new Move(start, new int[] {3,6}));
		int[] whiteStart = {4,0};
		correctWhitePlies.add(new Move(whiteStart, new int[] {3,0}));
		correctWhitePlies.add(new Move(whiteStart, new int[] {3,1}));
		correctWhitePlies.add(new Move(whiteStart, new int[] {4,1}));
		correctWhitePlies.add(new Move(whiteStart, new int[] {5,1}));
		correctWhitePlies.add(new Move(whiteStart, new int[] {5,0}));
		correctWhitePlies.add(new Castle(whiteStart, new int[] {2,0}));
		correctWhitePlies.add(new Castle(whiteStart, new int[] {6,0}));		
		int[] blackStart = {4,7};
		correctBlackPlies.add(new Move(blackStart, new int[] {3,7}));
		correctBlackPlies.add(new Move(blackStart, new int[] {3,6}));
		correctBlackPlies.add(new Move(blackStart, new int[] {4,6}));
		correctBlackPlies.add(new Move(blackStart, new int[] {5,6}));
		correctBlackPlies.add(new Move(blackStart, new int[] {5,7}));
		correctBlackPlies.add(new Castle(blackStart, new int[] {2,7}));
		correctBlackPlies.add(new Castle(blackStart, new int[] {6,7}));
	}
	
	/**
	 * JUnit calls this before any test. Initializes <code>board</code>,
	 * <code>King</code>s, and <code>Rook</code>s.
	 */
	protected void setUp() {
		board = new RectangularBoard();
		whiteKing = new King(true, board);
		blackKing = new King(false, board);
		whiteCastlingKing = new King(true, board,true);
		blackCastlingKing = new King(false, board,true);
		whiteLeftRook = new Rook(true, board);
		whiteRightRook = new Rook(true, board);
		blackLeftRook = new Rook(false, board);
		blackRightRook = new Rook(false, board);
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up in a <code>Board</code>
	 * successfully and correctly. It is set up as many times as it should
	 * handle.
	 */
	public void testSetUpNormal() {
		checkSetUpNormal(whiteKing, new int[] {4,0});
		checkSetUpNormal(blackKing, new int[] {4,7});
		setUp();
		checkSetUpNormal(whiteCastlingKing, new int[] {4,0});
		checkSetUpNormal(blackCastlingKing, new int[] {4,7});
	}
	
	/**
	 * Test that the <code>Piece</code> fails to be set up more than once.
	 */
	public void testSetUpTwice() {
		// For a white non-castling Piece
		while (true) {
			try {
				whiteKing.setUp();
			} catch (IllegalStateException e) {
				assertTrue("White Piece was not added to the Board after being set up",
						board.contains(whiteKing));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackKing));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				assertEquals("White Piece did not set up itself at the correct position",
						whiteKing, board.getPiece(new int[] {4,0}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("White Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
		// For a black non-castling Piece
		while (true) {
			try {
				blackKing.setUp();
			} catch (IllegalStateException e) {
				assertTrue("Black Piece was not added to the Board after being set up",
						board.contains(blackKing));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				assertEquals("Black Piece did not set up itself at the correct position",
						blackKing, board.getPiece(new int[] {4,7}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("Black Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
		setUp();
		// For a white castling Piece
		while (true) {
			try {
				whiteCastlingKing.setUp();
			} catch (IllegalStateException e) {
				assertTrue("White Piece was not added to the Board after being set up",
						board.contains(whiteCastlingKing));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackCastlingKing));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				assertEquals("White Piece did not set up itself at the correct position",
						whiteCastlingKing, board.getPiece(new int[] {4,0}));
				break;
			} catch (InvalidInitialPositionException e) {
				fail("White Piece threw an InvalidInitialPositionException while " +
						"setting up more than once. This should not happen.");
			}
		}
		// For a black castling Piece
		while (true) {
			try {
				blackCastlingKing.setUp();
			} catch (IllegalStateException e) {
				assertTrue("Black Piece was not added to the Board after being set up",
						board.contains(blackCastlingKing));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				assertEquals("Black Piece did not set up itself at the correct position",
						blackCastlingKing, board.getPiece(new int[] {4,7}));
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
		// For a white non-castling Piece
		Piece newKing = null;
		while (true) {
			try {
				newKing = new King(true, board);
				newKing.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("White Piece threw an IllegalStateException while set" +
						"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("White Piece was added to the Board after failing to set up",
						board.contains(newKing));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackKing));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				break;
			}
		}
		// For a black non-castling Piece
		while (true) {
			try {
				newKing = new King(false, board);
				newKing.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("Black Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("Black Piece was added to the Board after failing to set up",
						board.contains(newKing));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(false).size());
				break;
			}
		}
		setUp();
		// For a white castling Piece
		while (true) {
			try {
				newKing = new King(true, board,true);
				newKing.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("White Piece threw an IllegalStateException while set" +
						"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("White Piece was added to the Board after failing to set up",
						board.contains(newKing));
				assertFalse("A black Piece was added to the Board while testing a " +
						"white Piece", board.contains(blackCastlingKing));
				assertEquals("More than one white Piece (or no Pieces) was (were) " +
						"added when setting up", 1, board.getPieces(true).size());
				break;
			}
		}
		// For a black Piece
		while (true) {
			try {
				newKing = new King(false, board,true);
				newKing.setUp();
			} catch (IllegalStateException e) { // not really needed
				fail("Black Piece threw an IllegalStateException while set" +
				"ting up to an occupied cell. This should not happen.");
			} catch (InvalidInitialPositionException e) {
				assertFalse("Black Piece was added to the Board after failing to set up",
						board.contains(newKing));
				assertEquals("More than one black Piece (or no Pieces) was (were) " +
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
		unusable.add(new int[] {4,0});
		unusable.add(new int[] {4,7});
		Board newBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight,unusable);
		Piece newKing = new King(true, newBoard);
		try {
			newKing.setUp();
		} catch (InvalidInitialPositionException e1) {
			newKing = new King(false, newBoard);
			try {
				newKing.setUp();
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
		board.addPiece(whiteKing, new int[] {4,5});
		Set<Ply> whitePlies = new HashSet<Ply>(whiteKing.getPlies());
		Set<Ply> correctPlies = pliesAtFourFive;
		assertEquals("White Piece fails to produce correct set of Plies", whitePlies,
				correctPlies);
		// For black Piece
		board.removePiece(whiteKing);
		board.addPiece(blackKing, new int[] {4,5});
		Set<Ply> blackPlies = new HashSet<Ply>(blackKing.getPlies());
		assertEquals("Black Piece fails to produce correct set of Plies", blackPlies,
				correctPlies);
	}

	/**
	 * Test valid <code>Plies</code> among <code>Piece</code>s of the same
	 * color. The <code>Plies</code> should exclude those that end in a cell
	 * occupied by another <code>Piece</code> of the same color.
	 */
	public void testPliesSameColor() {
		// For white Piece
		Piece whitePawn = new Pawn(true,board);
		board.addPiece(whitePawn, new int[] {5,6});
		board.addPiece(whiteKing, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!ply.equals(new Move(new int[] {4,5}, new int[] {5,6})))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteKing);
		// For black Piece
		board.removePiece(whitePawn);
		board.removePiece(whiteKing);
		Piece blackPawn = new Pawn(false,board);
		board.addPiece(blackPawn, new int[] {5,6});
		board.addPiece(blackKing, new int[] {4,5});
		checkPlies(correctPlies, blackKing);
	}
	
	/**
	 * Test valid <code>Plies</code> among <code>Piece</code>s of different
	 * color. The <code>Plies</code> should not exclude anything.
	 */
	public void testPliesDiffColor() {
		// For white Piece
		Piece blackPawn = new Pawn(false,board);
		Piece blackBishop = new Bishop(false,board);
		board.addPiece(blackPawn, new int[] {5,6});
		board.addPiece(blackBishop, new int[] {4,4});
		board.addPiece(whiteKing, new int[] {4,5});
		Set<Ply> correctPlies = pliesAtFourFive;
		checkPlies(correctPlies, whiteKing);
		// For black Piece
		board.removePiece(blackPawn);
		board.removePiece(blackBishop);
		board.removePiece(whiteKing);
		Piece whitePawn = new Pawn(true,board);
		Piece whiteBishop = new Bishop(true,board);
		board.addPiece(whitePawn, new int[] {5,6});
		board.addPiece(whiteBishop, new int[] {4,4});
		board.addPiece(blackKing, new int[] {4,5});
		checkPlies(correctPlies, blackKing);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (user-defined). The
	 * <code>Plies</code> should exclude those that end in unusable cells.
	 */
	public void testPliesUnusableCenter() {
		// For white Piece with holes (unusable)
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {4,6});
		unusable.add(new int[] {3,4});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece newKing = new King(true, holedBoard);
		holedBoard.addPiece(newKing, new int[] {4,5});
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : pliesAtFourFive)
			if (!ply.equals(new Move(new int[] {4,5}, new int[] {4,6})) &&
					!ply.equals(new Move(new int[] {4,5}, new int[] {3,4})))
				correctPlies.add(ply);
		checkPlies(correctPlies, newKing);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (sides). The
	 * <code>Plies</code> should be restricted by the limits of the
	 * <code>Board</code>.
	 */
	public void testPliesUnusableSides() {
		// For black Piece
		board.addPiece(blackKing, new int[] {0,7});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {0,7}, new int[] {0,6}));
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,6}));
		correctPlies.add(new Move(new int[] {0,7}, new int[] {1,7}));
		checkPlies(correctPlies, blackKing);
	}
	
	/**
	 * Test valid <code>Plies</code> in a complex setting, i.e. a combination
	 * of the previous tests.
	 */
	public void testPliesComplex() {
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {6,3});
		unusable.add(new int[] {6,5});
		Board holedBoard = new RectangularBoard(RectangularBoard.defaultLength,
								RectangularBoard.defaultHeight, unusable);
		Piece whiteKnight = new Knight(true, holedBoard);
		Piece blackBishop = new Bishop(false, holedBoard);
		Piece newKing = new King(true, holedBoard);
		holedBoard.addPiece(whiteKnight, new int[] {7,5});
		holedBoard.addPiece(blackBishop, new int[] {6,4});
		holedBoard.addPiece(newKing, new int[] {7,4});
		Set<Ply> correctPlies = new HashSet<Ply>();
		correctPlies.add(new Move(new int[] {7,4}, new int[] {6,4}));
		correctPlies.add(new Move(new int[] {7,4}, new int[] {7,3}));
		checkPlies(correctPlies, newKing);
	}
	
	/**
	 * Test valid <code>Plies</code> when the <code>Piece</code> is not in a
	 * <code>Board</code>. There should be no valid <code>Plies</code>.
	 */
	public void testPliesNone() {
		assertEquals("White Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), whiteKing.getPlies());
		assertEquals("Black Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), blackKing.getPlies());
		assertEquals("White Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), whiteCastlingKing.getPlies());
		assertEquals("Black Piece fails to produce correct set of Plies",
				new ArrayList<Ply>(), blackCastlingKing.getPlies());
	}
	
	/**
	 * Test that valid <code>Plies</code> in a normal setting include castling
	 * on both sides (for <code>King</code>s with castling enabled).
	 */
	public void testCastleNormal() {
		setUpPiecesForCastling();
		// For white Piece
		checkPlies(correctWhitePlies, whiteCastlingKing);
		// For black Piece
		checkPlies(correctBlackPlies, blackCastlingKing);
	}

	/**
	 * Test that after moving the left <code>Rook</code>, a <code>King</code>
	 * cannot castle queen side, even if the <code>Rook</code> returns to its
	 * original spot.
	 */
	public void testNoCastleLeftRookMoved() {
		setUpPiecesForCastling();
		// For white Piece
		board.executePly(new Move(new int[] {0,0}, new int[] {0,3}));
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : correctWhitePlies)
			if (!ply.equals(new Castle(new int[] {4,0}, new int[] {2,0})))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteCastlingKing);
		board.executePly(new Move(new int[] {0,3}, new int[] {0,0}));
		checkPlies(correctPlies, whiteCastlingKing);
		// For black Piece
		board.executePly(new Move(new int[] {0,7}, new int[] {0,3}));
		correctPlies = new HashSet<Ply>();
		for (Ply ply : correctBlackPlies)
			if (!ply.equals(new Castle(new int[] {4,7}, new int[] {2,7})))
				correctPlies.add(ply);
		checkPlies(correctPlies, blackCastlingKing);
		board.executePly(new Move(new int[] {0,3}, new int[] {0,7}));
		checkPlies(correctPlies, blackCastlingKing);
	}
	
	/**
	 * Test that after moving the right <code>Rook</code>, a <code>King</code>
	 * cannot castle king side, even if the <code>Rook</code> returns to its
	 * original spot.
	 */
	public void testNoCastleRightRookMoved() {
		setUpPiecesForCastling();
		// For white Piece
		board.executePly(new Move(new int[] {7,0}, new int[] {7,3}));
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : correctWhitePlies)
			if (!ply.equals(new Castle(new int[] {4,0}, new int[] {6,0})))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteCastlingKing);
		board.executePly(new Move(new int[] {7,3}, new int[] {7,0}));
		checkPlies(correctPlies, whiteCastlingKing);
		// For black Piece
		board.executePly(new Move(new int[] {7,7}, new int[] {7,3}));
		correctPlies = new HashSet<Ply>();
		for (Ply ply : correctBlackPlies)
			if (!ply.equals(new Castle(new int[] {4,7}, new int[] {6,7})))
				correctPlies.add(ply);
		checkPlies(correctPlies, blackCastlingKing);
		board.executePly(new Move(new int[] {7,3}, new int[] {7,7}));
		checkPlies(correctPlies, blackCastlingKing);
	}
	
	/**
	 * Test that after moving the <code>King</code>, it cannot castle on either
	 * side, even if it returns to its original spot.
	 */
	public void testNoCastleKingMoved() {
		setUpPiecesForCastling();
		// For white Piece
		assertEquals("White Piece fails to produce correct number of Plies",
				7, whiteCastlingKing.getPlies().size());
		board.executePly(new Move(new int[] {4,0}, new int[] {4,1}));
		assertEquals("White Piece fails to produce correct number of Plies",
				8, whiteCastlingKing.getPlies().size());
		board.executePly(new Move(new int[] {4,1}, new int[] {4,0}));
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : correctWhitePlies)
			if (!ply.equals(new Castle(new int[] {4,0}, new int[] {6,0})) &&
				!ply.equals(new Castle(new int[] {4,0}, new int[] {2,0})))
				correctPlies.add(ply);
		checkPlies(correctPlies, whiteCastlingKing);
		// For black Piece
		assertEquals("Black Piece to produce correct number of Plies",
				7, blackCastlingKing.getPlies().size());
		board.executePly(new Move(new int[] {4,7}, new int[] {4,6}));
		assertEquals("Black Piece to produce correct number of Plies",
				8, blackCastlingKing.getPlies().size());
		board.executePly(new Move(new int[] {4,6}, new int[] {4,7}));
		correctPlies = new HashSet<Ply>();
		for (Ply ply : correctBlackPlies)
			if (!ply.equals(new Castle(new int[] {4,7}, new int[] {6,7})) &&
				!ply.equals(new Castle(new int[] {4,7}, new int[] {2,7})))
				correctPlies.add(ply);
		checkPlies(correctPlies, blackCastlingKing);
	}
	
	
	/**
	 * Test the getType() method.
	 */
	public void testGetType() {
		assertEquals("The toString() method is not returning what it should",
				new String("king"), whiteKing.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("king"), blackKing.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("king"), whiteCastlingKing.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("king"), blackCastlingKing.getType());
	}
	
	// No need to test added() and removed() because they dont do anything.
	// updateInfo was implicitly tested within the castling tests.
	
	/**
	 * Check that the given <code>Piece</code> sets up correctly to the given
	 * position.
	 */
	private void checkSetUpNormal(Piece piece, int[] position) {
		try {
			piece.setUp();
		} catch (Exception e) {
			fail((piece.isWhite() ? "White":"Black") + " Piece failed to set" +
					" up appropriately");
		}
		assertTrue((piece.isWhite() ? "White":"Black") + " Piece was not added" +
				" to the Board after being set up", board.contains(piece));
		assertEquals("More than one Piece (or no Pieces) was (were) added when " +
				"setting up", 1, board.getPieces(piece.isWhite()).size());
		assertEquals((piece.isWhite() ? "White":"Black") + " Piece did not set" +
				" up itself at the correct position", piece, board.getPiece(position));
	}
	
	/**
	 * Sets up the <code>Rook</code>s and <code>King</code>s in the
	 * <code>Board</code>.
	 */
	private void setUpPiecesForCastling() {
		try {
			whiteCastlingKing.setUp();
			whiteLeftRook.setUp();
			whiteRightRook.setUp();
			blackCastlingKing.setUp();
			blackLeftRook.setUp();
			blackRightRook.setUp();
		} catch (InvalidInitialPositionException iipe) {
			fail("Pieces fail to set themselves up appropriately");
		}
	}
	
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