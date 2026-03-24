package ruleset.board.test;

import junit.framework.TestCase;
import ruleset.board.RectangularBoard;
import ruleset.piece.Pawn;
import engine.adt.Board;
import engine.adt.Piece;



/**
 * Unit tests for RectangularBoard.
 */
public class RectangularBoardTest extends TestCase {
	// Notice that Board has been thoroughly tested already (under 
	// engine.adt.test). Therefore, the tests made here are fairly simple.
	
	/**
	 * Tests that the Board clones itself correctly.
	 */
	public void testClone() {
		 Board board = new RectangularBoard();
		 Piece pawn1 = new Pawn(true, board);
		 Piece pawn2 = new Pawn(false, board);
		 
		 board.addPiece(pawn1, new int[] {1,1}); 
		 board.addPiece(pawn2, new int[] {2,1});

		 assertTrue(board.contains(pawn1)); // sanity check
		 assertTrue(board.contains(pawn2)); // sanity check
		 
		 Board clonedBoard = board.clone();

		 assertFalse("Board failed to clone itself and return a different Board",
				 clonedBoard == board);
		 assertTrue("Board failed to clone its attributes",
				 board.getPieces(true).size()==clonedBoard.getPieces(true).size());
		 assertTrue("Board failed to clone its attributes",
				 board.getPieces(false).size()==clonedBoard.getPieces(false).size());
		 assertFalse("Board failed to reproduce its Pieces for cloning",
				 clonedBoard.contains(pawn1));
		 assertFalse("Board failed to reproduce its Pieces for cloning",
				 clonedBoard.contains(pawn2));
		 assertFalse("Board failed to add the Pieces to its clone at the same cell",
				 clonedBoard.isEmpty(new int[] {1,1}));
		 assertFalse("Board failed to add the Pieces to its clone at the same cell",
				 clonedBoard.isEmpty(new int[] {2,1}));
		 assertTrue("Board modified itself when cloning",
				 board.contains(pawn1));
		 assertTrue("Board modified itself when cloning",
				 board.contains(pawn2));
		 assertTrue("Board failed to add the same type of Piece",
				 clonedBoard.getPiece(board.getPosition(pawn1)).toString().equals(pawn1.toString()));
		 
//		 assertFalse("Board failed to clone its rep",
//				 clonedBoard.getCell(new int[] {2,1})==board.getCell(new int[] {2,1}));
		 // Commented out because it uses getCell, which is protected. This was tested
		 // thouroughly and passed all tests.
		 
		 clonedBoard.removePiece(clonedBoard.getPiece(new int[] {2,1}));
		 		 
		 assertTrue(clonedBoard.isEmpty(new int[] {2,1}));
		 assertFalse(board.isEmpty(new int[] {2,1}));
	}

}
