package ruleset.board;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import engine.adt.Ply;


/** 
 * Collection of static converters of cell positions in a rectangular board
 * to and from its string representation. 
 * 
 * 
 * A representation of a rectangular board is an array of size two, with 
 * each of the cells greater or equal to zero. The first element contains 
 * the number of the column from left to right, starting from zero, 
 * @see RectangularBoard
 * 
 * A String representation of a rectangular board is col + row, 
 * where col is a string representation of the column, row is a string 
 * representation of its row, and + is the append operator. 
 * 
 * A string representation of the column is the translation of the 
 * column number into a number of base 26, where the numbers are
 * (a,b,c,...,z). For example, the string representation of column 
 * 1 is b, the string representation of column 25 is z. The string 
 * representation of column 27 is ab. String representations are not
 * case sensitive. 
 * 
 * A string representation of the row is the number itself plus one. 
 * For example, row 4 has a string representation "5". 
 * 
 * Notice that the longest string supported is String MAX_STRING, 
 * which is the result of transforming a column Integer.MAX_VALUE 
 * to its String representation; 
 */
public class CoordinateParser {
	/**
	 * The longest string supported
	 */
	public static final String MAX_STRING ; 
	static { 
		MAX_STRING = parseCoord(new int[] {Integer.MAX_VALUE, 0} );
	}

	
	/** 
	 * Parses a String representation of a cell position and returns an 
	 * array of size two that locates the representation in a rectangular
	 * board
	 * @param coordinate
	 * @return a coordinate for this string representation
	 * @throws IllegalArgumentException if the String is incorrectly 
	 * 		  formatted. 
	 */
	public static int[] parseString(String coordinate) {
		
		if (coordinate == null)
			throw new IllegalArgumentException("Coordinate cannot be null");
		
		
		int firstNumber = 0; 
		boolean inLetters = true; 
		
		for (firstNumber = 0; firstNumber < coordinate.length() && inLetters ; firstNumber++) { 
			char character = coordinate.charAt(firstNumber);
			if (! Character.isLetter(character)){
				inLetters = false; 
			}
			

		}
		
		if (firstNumber == 0) {
			throw new IllegalArgumentException("Incorrect format in " + coordinate );
		}
		

		int[] coord = { column(coordinate.substring(0, firstNumber - 1).toLowerCase()), 
				        row(coordinate.substring(firstNumber - 1))};
		return coord;
	}
	
	
	/* 
	 * Returns the column associated with the column representation 
	 * @require column is a word in lowercase (with at least one character)
	 */
	private static int column(String column) {
		if (column.equals(""))
			throw new IllegalArgumentException(column + "is not a valid argument");
		
		int total = 0; 
		for (int i = 0; i < column.length(); i++) { 
			char character = column.charAt(i);
			int num = Character.getNumericValue(character) - 9; 
			total += num * Math.round(Math.pow(26, column.length() - i - 1));
		}
		return total - 1; 
		
	}
	
	/*
	 *  Returns the row associated with the row representation
	 * 
	 */
	private static int row(String row) {
		try {
			if (Integer.parseInt(row) <= 0)
				throw new IllegalArgumentException("Row number " +
						"should be positive");
			return Integer.parseInt(row) - 1;
		}
		catch (NumberFormatException nfe) {
			throw new IllegalArgumentException("Unknown symbol in " + row); 
		}
	}
	
	
	/**
	 * Converts a coordinate within a rectangular board into a String
	 * representation. 
	 * @param coordinate
	 * @return a string representation for this coordinate 
	 * @throws IllegalArgumentException if the coordinate is not 
	 * 		  of size two and with nonnegative elements.
	 */
	public static String parseCoord(int[] coordinate) {
		if (coordinate.length != 2)
			throw new IllegalArgumentException("Wrong length for coordinate");
		return column(coordinate[0]) + row(coordinate[1]); 
	}

/* 
 * Returns the column representation for this column 
 */
	private static String column(int column) {
		int base = 26;
		if (column < 0)
			throw new IllegalArgumentException("column should be greater " +
											   "than 0"); 

		if (column < base) {
			byte[] columnAscii= {Integer.valueOf(97+column).byteValue()};
			
			String columnStr;
			try {
				columnStr = new String(columnAscii, "ascii");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("Unknown error while attempting" +
											"to parse"); 
			}	
		
			return columnStr;

		}
		else {
			return column((column/base) - 1) + column(column % base); 
		}
	}

/*
 *  Returns the row	 associated with the row representation
 * 
 */
	private static String row(int row) {
		if (row < 0)
			throw new IllegalArgumentException("Row should be nonnegative"); 
		return "" + (row + 1); 
	}

	
	/**
	 * Returns the starting or ending cells of a given list of Plies. This
	 * assumes that each Ply is represented as "start - end".
	 */
	public static List<int[]> getCells(List<Ply> plies, boolean starting) {
		List<int[]> cells = new LinkedList<int[]>();
		for (Ply ply : plies) 
			cells.add(getCell(ply.toString(), starting));

		return cells;
	}
	
	/**
	 * Returns the starting or ending cells of a given String representation
	 * of a Ply. This 
	 * assumes that the Ply is represented as "start-end"
	 */
	public static int[] getCell(String ply, boolean starting) {
		String[] end = ply.split("-");
		if (starting)
			return (CoordinateParser.parseString(end[0]));
		else
			return (CoordinateParser.parseString(end[1]));
		
	}
		
	
}