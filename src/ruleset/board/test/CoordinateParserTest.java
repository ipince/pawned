package ruleset.board.test;

import java.util.Arrays;

import ruleset.board.CoordinateParser;
import junit.framework.TestCase;

public class CoordinateParserTest extends TestCase {

	public void testToArrayIllegalArgs(String arg) {

		try {
			CoordinateParser.parseString(arg);
		}
		catch (IllegalArgumentException iae) {
			return;
		}
		catch (Throwable r) {
			fail("wrong error sent to user with illegal string " + arg +" \n" 
					+  r.toString());
		}
		
		fail("The parser wrongly succeeds to parse an illegal string " + arg + 
				" to " + Arrays.toString(CoordinateParser.parseString(arg)));
	}
	

	/** 
	 * Test illegal arguments for toArray
	 *
	 */
	public void testToArrayIllegalArgs() { 
		testToArrayIllegalArgs((String) null);
		testToArrayIllegalArgs("");
		testToArrayIllegalArgs("fg989k");
		testToArrayIllegalArgs("f9-9");
		testToArrayIllegalArgs("f-9");
		testToArrayIllegalArgs("f9g");
		testToArrayIllegalArgs("9f");
		testToArrayIllegalArgs("a0");
		testToArrayIllegalArgs("9");
		testToArrayIllegalArgs("f");
		
		
		
	}
	
	public void testNormalToArray(String test, int[] expected) { 
		assertTrue("Incorrect parsing of " +  test + ". Actual: " + 
					Arrays.toString(CoordinateParser.parseString(test)), 
					Arrays.equals(CoordinateParser.parseString(test),expected));
	}

	/**
	 * Test normal arguments for toArray
	 */
	public void testNormalToArray() { 
		testNormalToArray("a1", new int[] {0,0});
		testNormalToArray("g4", new int[] {6,3});
		testNormalToArray("a10", new int[] {0,9});
		testNormalToArray("c11", new int[] {2,10});
		testNormalToArray("z10", new int[] {25,9});
		testNormalToArray("aa1", new int[] {26,0});
		testNormalToArray("aa3000085", new int[] {26,3000084});
		
		testNormalToArray("ab1", new int[] {27,0});
		
	}

	public void testToStringIllegalArgs(int[]  arg) {

		try {
			CoordinateParser.parseCoord(arg);
		}
		catch (IllegalArgumentException iae) {
			return;
		}
		catch (Throwable r) {
			fail("wrong error sent to user with illegal string " + arg +" \n" 
					+  r.toString());
		}
		
		fail("The parser wrongly succeeds to parse an illegal string " + arg + 
				" to " + CoordinateParser.parseCoord(arg));
	}
	
	/**
	 * Test illegal arguments for toString
	 *
	 */
	public void testToStringIllegalArgs() { 
		testToStringIllegalArgs(new int[] {-1,-1});
		testToStringIllegalArgs(new int[] {-1,0});
		testToStringIllegalArgs(new int[] {0,-1});		
	}
	
	/** 
	 * Test normal behavior in standard cases for toString	
	 */
	public void testNormalToString() {
		assertEquals("a1", CoordinateParser.parseCoord(new int[] {0,0}));
		assertEquals("g4", CoordinateParser.parseCoord(new int[] {6,3}));
		assertEquals("bn66", CoordinateParser.parseCoord(new int[] {'A','A'}));
		assertEquals("a10", CoordinateParser.parseCoord(new int[] {0,9}));
		assertEquals("c11", CoordinateParser.parseCoord(new int[] {2,10}));
		assertEquals("z10", CoordinateParser.parseCoord(new int[] {25,9}));
		assertEquals("aa1", CoordinateParser.parseCoord(new int[] {26,0}));
		assertEquals("ab1", CoordinateParser.parseCoord(new int[] {27,0}));
		assertEquals("az1", CoordinateParser.parseCoord(new int[] {51,0}));
		assertEquals("ba1", CoordinateParser.parseCoord(new int[] {52,0}));
		assertTrue("BA1".equalsIgnoreCase(CoordinateParser.parseCoord(new int[] {52,0})));

		assertEquals("aa3000085", CoordinateParser.parseCoord(
				new int[] {26,3000084}));
	}
	
	public void testIdentity(String string) { 
		assertEquals("parseCoord( parseString (x) ) should equal x" , 
			CoordinateParser.parseCoord(CoordinateParser.parseString(string)), 
			string); 
	}
	
	/**
	 * Test that the array produced by a String gives back the String itself
	 *
	 */
	public void testIdentity() {
		testIdentity("aaaaa998");
		testIdentity("acafa998");
		testIdentity("zz1");
		testIdentity("oufa1");
		testIdentity("aaaaaf1");
		testIdentity("zoucaf1");
	
		
	}
	/**
	 * Test that large values work by verifying the identity f ( finv ( x ) ) = x
	 */
	public void testIt() { 
		for (int i = Integer.MAX_VALUE - 10; i < Integer.MAX_VALUE; i++) {
			int a[] = {i,0}; 
			String parsed = CoordinateParser.parseCoord(a); 
			assertTrue(Arrays.equals(a,CoordinateParser.parseString(parsed)) );
		}
	}
}
