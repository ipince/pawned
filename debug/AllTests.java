package debug;

import controller.test.TimerTest;
import controller.test.XmlFactTest;
import interfaces.test.TextUIScriptFileTests;
import junit.framework.Test;
import junit.framework.TestSuite;
import engine.adt.test.AdtTests;
import engine.game.test.GameTest;
import ruleset.test.RuleSetTests;

/**
 * Runs all the tests.
 */
public class AllTests {
	
	public static Test suite() {
		
		TestSuite suite = new TestSuite();
		
		suite.addTest(AdtTests.suite());
		suite.addTest(RuleSetTests.suite());
		suite.addTest(TextUIScriptFileTests.suite());
		suite.addTestSuite(GameTest.class);
//		suite.addTestSuite(TimerTest.class);
		suite.addTestSuite(XmlFactTest.class);

		return suite;
	}

}
