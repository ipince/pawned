package ruleset.piece.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ruleset.board.RectangularBoard;
import ruleset.piece.GravityChip;
import ruleset.ply.Add;
import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.exception.InvalidInitialPositionException;
import junit.framework.TestCase;

/**
 * Unit tests for <code>GravityChip</code>.
 */
public class GravityChipTest extends TestCase {
	
	/**
	 * An empty 7 by 6 <code>RectangularBoard</code>.
	 */
	private Board board;
	
	/**
	 * A white <code>GravityChip</code>.
	 */
	private Piece whiteChip;
	
	/**
	 * A black <code>GravityChip</code>.
	 */
	private Piece blackChip;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a white
	 * <code>GravityChip</code> in an empty <code>Board</code>.
	 */
	private Set<Ply> whiteInitialPlies;
	
	/**
	 * <code>Set</code> with valid <code>Plies</code> of a black
	 * <code>GravityChip</code> in an empty <code>Board</code>.
	 */
	private Set<Ply> blackInitialPlies;
	
	/**
	 * Sets the valid plies in <code>correctInitialPlies</code>.
	 */
	public GravityChipTest() {
	}
	
	/**
	 * JUnit calls this before any test. Initializes <code>board</code>,
	 * <code>GravityChip</code>s, and <code>correctInitialPlies</code>.
	 */
	protected void setUp() {
		board = new RectangularBoard(GravityChip.BOARD_LENGTH, GravityChip.BOARD_HEIGHT);
		whiteChip = new GravityChip(true, board);
		blackChip = new GravityChip(false, board);
		whiteInitialPlies = new HashSet<Ply>();
		blackInitialPlies = new HashSet<Ply>();
		for (int i = 0; i < GravityChip.BOARD_LENGTH; i++) {
			whiteInitialPlies.add(new Add(new int[] {i, 0}, whiteChip));
			blackInitialPlies.add(new Add(new int[] {i, 0}, blackChip));
		}
	}
	
	/**
	 * Test that the <code>Piece</code> sets itself up in a <code>Board</code>
	 * successfully and correctly. It is set up as many times as it should
	 * handle.
	 */
	public void testSetup() {
		Piece chip = new GravityChip(true, board);
		try {
			chip.setUp();
		} catch (InvalidInitialPositionException ipe) {
			assertFalse("Piece was added to the Board after being set up",
					board.contains(chip));
			return;
		}
		fail("GravityChip set itself up when it shouldn't have");
	}
	
	/**
	 * Test valid <code>Plies</code> in a normal setting (no move-preventing
	 * cells).
	 */
	public void testPliesNormal() {
		checkPlies(whiteInitialPlies, whiteChip);
		checkPlies(blackInitialPlies, blackChip);
	}
	
	/**
	 * Test valid <code>Plies</code> among other <code>Piece</code>s. The
	 * <code>Plies</code> should exclude those that involve a cell occupied
	 * by another <code>Piece</code> or any cells past that one.
	 */
	public void testPliesOccupied() {
		board.addPiece(new GravityChip(true, board), new int[] {0,3});
		board.addPiece(new GravityChip(false, board), new int[] {0,4});
		board.addPiece(new GravityChip(false, board), new int[] {3,3});
		Set<Ply> correctPlies = new HashSet<Ply>();
		// For white piece
		for (Ply ply : whiteInitialPlies)
			if (!(ply.similar(new Add(new int[] {0,0}, new GravityChip(true, board))) ||
				  ply.similar(new Add(new int[] {3,0}, new GravityChip(true, board)))))
				correctPlies.add(ply);
		correctPlies.add(new Add(new int[] {0,5}, whiteChip));
		correctPlies.add(new Add(new int[] {3,4}, whiteChip));
		checkPlies(correctPlies, whiteChip);
		// For black piece
		correctPlies = new HashSet<Ply>();
		for (Ply ply : blackInitialPlies)
			if (!(ply.similar(new Add(new int[] {0,0}, new GravityChip(false, board))) ||
				  ply.similar(new Add(new int[] {3,0}, new GravityChip(false, board)))))
				correctPlies.add(ply);
		correctPlies.add(new Add(new int[] {0,5}, blackChip));
		correctPlies.add(new Add(new int[] {3,4}, blackChip));
		checkPlies(correctPlies, blackChip);
	}
	
	/**
	 * Test valid <code>Plies</code> among unusable cells (user-defined). The
	 * <code>Plies</code> should exclude those that involve unusable cells and
	 * any cells past the first one.
	 */
	public void testPliesUnusable() {
		// For white Piece with holes (unusable)
		List<int[]> unusable = new ArrayList<int[]>(2);
		unusable.add(new int[] {5,4});
		unusable.add(new int[] {1,2});
		Board holedBoard = new RectangularBoard(GravityChip.BOARD_LENGTH,
								GravityChip.BOARD_HEIGHT, unusable);
		Piece newChip = new GravityChip(true, holedBoard);
		holedBoard.addPiece(new GravityChip(true, holedBoard), new int[] {5,3});
		Set<Ply> correctPliesTemp = new HashSet<Ply>();
		for (int i = 0; i < GravityChip.BOARD_LENGTH; i++)
			correctPliesTemp.add(new Add(new int[] {i, 0}, newChip));
		Set<Ply> correctPlies = new HashSet<Ply>();
		for (Ply ply : correctPliesTemp)
			if (!(ply.similar(new Add(new int[] {5,0}, new GravityChip(true, holedBoard))) ||
				  ply.similar(new Add(new int[] {1,0}, new GravityChip(true, holedBoard)))))
				correctPlies.add(ply);
		correctPlies.add(new Add(new int[] {5,5}, newChip));
		correctPlies.add(new Add(new int[] {1,3}, newChip));
		checkPlies(correctPlies, newChip);
	}
	
	/**
	 * Test valid <code>Plies</code> when a <code>GravityChip</code> cannot move
	 * at all in some columns.
	 */
	public void testPliesOverflow() {
		board.addPiece(whiteChip, new int[] {0,5});
		assertTrue("Piece fail to produce correct number of Plies",
				blackChip.getPlies().size()==6);
	}
	
	/**
	 * Tests that a <code>GravityChip</code> has no valid <code>Plies</code>
	 * when it is already in a <code>Board</code>.
	 */
	public void testPliesNone() {
		// For white piece
		board.executePly(whiteChip.getPlies().get(0));
		assertTrue("Piece didnt submit a proper ply", board.contains(whiteChip));
		checkPlies(new HashSet<Ply>(), whiteChip);
		// For black piece
		board.executePly(blackChip.getPlies().get(0));
		assertTrue("Piece didnt submit a proper ply", board.contains(blackChip));
		checkPlies(new HashSet<Ply>(), blackChip);
	}
	
	/**
	 * Test the getType() method.
	 */
	public void testGetType() {
		assertEquals("The toString() method is not returning what it should",
				new String("chip"), whiteChip.getType());
		assertEquals("The toString() method is not returning what it should",
				new String("chip"), blackChip.getType());
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
