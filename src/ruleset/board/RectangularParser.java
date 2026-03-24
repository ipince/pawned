package ruleset.board;

import engine.adt.Parser;
/**
 * Represents a parser for a RectangularBoard. Serves as a wrapper around
 * <code>CoordinateParser</code>.
 */
public class RectangularParser implements Parser {

	/** 
	 * Parses a String representation of a cell position and returns an 
	 * array of size two that locates the representation in a rectangular
	 * board
	 * @param coord
	 * @return a coordinate for this string representation
	 * @throws IllegalArgumentException if the String is incorrectly 
	 * 		  formatted. 
	 */
	public String parseCoord(int[] coord) {
		return CoordinateParser.parseCoord(coord);
	}

	/**
	 * Converts a coordinate within a rectangular board into a String
	 * representation. 
	 * @param coord
	 * @return a string representation for this coordinate 
	 * @throws IllegalArgumentException if the coordinate is not 
	 * 		  of size two and with nonnegative elements.
	 */	
	public int[] parseString(String coord) {
		return CoordinateParser.parseString(coord); 
	}

}
