package ruleset.piece.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the functionality of the concrete Pieces (King, Queen, Bishop,
 * Rook, Knight, Pawn). Tests whether they set up correctly and return the
 * correct valid Plies.
 */
public class PieceTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(); 

		suite.addTestSuite(KingTest.class);
		suite.addTestSuite(QueenTest.class);
		suite.addTestSuite(BishopTest.class);
		suite.addTestSuite(KnightTest.class);
		suite.addTestSuite(RookTest.class);
		suite.addTestSuite(PawnTest.class);
		suite.addTestSuite(GravityChipTest.class);

		return suite;
	}

}
