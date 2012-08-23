package engine.adt;

/** 
 * Represents a convertion mechanism from arrays of integers 
 * representing the coordinates to a <code>String</code> representation for 
 * a given <code>RuleSet</code>. 
 * 
 */
public interface Parser {
	
	/**
	 * Returns a string representation of this 
	 * coordinate 
	 * 
	 * @param coord The coordinates to be transformed into <code>String</code>
	 * @return A <code>String</code> representation of <code>coord</code> 
	 */
	public String parseCoord(int[] coord);

	/**
	 * Returns the coordinate represented by this <code>String </code>
	 * 
	 * @param coord The <code>String</code> representation of the coordinate
	 * @return A coordinate integer array represented by <code>coord</code> 
	 */
	public int[] parseString(String coord);
}
