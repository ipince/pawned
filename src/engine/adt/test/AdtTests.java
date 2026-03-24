package engine.adt.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AdtTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(); 
		
		suite.addTestSuite(LineBoardTest.class);
		suite.addTestSuite(UnusableBoardTest.class);

		return suite; 
	}

}
