package ruleset.test;

import ruleset.antichess.test.StandardACTest;
import ruleset.board.test.CoordinateParserTest;
import ruleset.board.test.RectangularBoardTest;
import ruleset.piece.test.PieceTests;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Contains all the tests for specific RuleSets. This includes everything
 * that involves any given RuleSet.
 */
public class RuleSetTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(); 

		suite.addTest(PieceTests.suite());
		suite.addTestSuite(CoordinateParserTest.class);
		suite.addTestSuite(RectangularBoardTest.class);
		suite.addTestSuite(StandardACTest.class);

		return suite; 
	}
	
}
