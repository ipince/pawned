package ruleset.board.test;

import ruleset.antichess.StandardAC;
import engine.adt.Board;
import engine.adt.RuleSet;
import junit.framework.TestCase;

/**
 * Unit tests for XmlBoardFactory
 */
public class XmlBoardFactoryTest extends TestCase {
	
	/**
	 * Tests that a trivial String with the representation of a Board
	 * results in the correct Board.
	 */
	public void testSimpleBoard() {
		String settings = ("<pieces>" + 
		            "<square id=\"e3\" side=\"white\" piece=\"rook\" />\n" +
		            "<square id=\"d2\" side=\"black\" piece=\"bishop\" />\n" + 
		            "<square id=\"g5\" side=\"black\" piece=\"pawn\" />\n" +       
		            "</pieces>");
		RuleSet rs = new StandardAC();
		Board board = rs.boardFactory().getBoard(settings);
		assertTrue("Parsing failed",board.getPieces(true).size()==1);
		assertTrue("Parsing failed",board.getPieces(false).size()==2);
		assertTrue("Parsing failed",board.getPiece(new int[] {4,2}).isWhite());
		assertTrue("Parsing failed",!board.getPiece(new int[] {3,1}).isWhite());
		assertTrue("Parsing failed",!board.getPiece(new int[] {6,4}).isWhite());
		assertTrue("Parsing failed",
				board.getPiece(new int[] {4,2}).getType().equals("rook"));
		assertTrue("Parsing failed",
				board.getPiece(new int[] {3,1}).getType().equals("bishop"));
		assertTrue("Parsing failed",
				board.getPiece(new int[] {6,4}).getType().equals("pawn"));
	}
	
	/**
	 * Test that an empty Board results from an "empty" String. The String is
	 * not empty as is "", but as in "<pieces>\n</pieces>"
	 */
	public void testEmptyBoard() {
		String settings = ("<pieces>\n</pieces>");
	RuleSet rs = new StandardAC();
	Board board = rs.boardFactory().getBoard(settings);
	assertTrue("Parsing failed",board.getPieces(true).size()==0);
	assertTrue("Parsing failed",board.getPieces(false).size()==0);
	}

}
