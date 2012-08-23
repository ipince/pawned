package ruleset.board;

import java.util.*;
import engine.adt.Board;
import debug.*;

/**
 * Spec needs to be revised.
 * A RectangularBoard is a rectangular Board. If it has x rows and y columns,
 * then it can have at most x*y cells.
 * 
 * A RectangularBoard is represented as a . This is not finished!
 * makes it easy to refer to a cell within a RectangularBoard; its position is
 * determined by its coordinates. For example, if x = 3 and y = 2, then the
 * RectangularBoard would look like this:
 * 
 *         1   2
 * 	       __ __
 * 		3 |__|__|
 * 		2 |__|__|
 * 		1 |__|__| <----- this cell's position is (2,1)
 * 
 * Notice that a RectangularBoard can have at most x*y
 *
 */
public class RectangularBoard extends Board {

	//Default Settings
	public static final int defaultLength = 8;  //corresponds to x-axis
	public static final int defaultHeight = 8;  //corresponds to y-axis
	public static final List<int[]> defaultUnusable = new ArrayList<int[]>();
	
	//Fields
	
	/**
	 * Length of the Board.
	 */
	private final int length;
	
	/**
	 * Height of the Board.
	 */
	private final int height;
	
	/**
	 * List of unusable cells in this Board.
	 */
	private List<int[]> unusable;
	
	/**
	 * Array of Pieces in this Board.
	 */
	private Cell[][] pieces;
	
 //Constructors
	
	/**
	 * Returns a new RectangularBoard with the default settings. That is,
	 * returns a square Board of 
	 */
	public RectangularBoard() {
		this(defaultLength, defaultHeight, defaultUnusable); 
	}
	
	/**
	 * Returns a new RectangularBoard with the specified dimensions
	 */
	public RectangularBoard(int length, int height) {
		this(length, height, defaultUnusable);		
	}
	
	/**
	 * Returns a new RectangularBoard with the specified dimensions and list of
	 * unusable cells.
	 */
	public RectangularBoard(int length, int height, List<int[]> unusable) {
		this(new int[] {length, height}, unusable);
	}
	
	/**
	 * Returns a new RectangularBoard with the specified dimensions and list of
	 * unusable cells.
	 */
	private RectangularBoard(int[] dimensions, List<int[]> unusable) {
		super();
		// Set dimensions
		this.length = dimensions[0];
		this.height = dimensions[1];
		// Set unusable cells
		if (unusable == null)
			this.unusable = defaultUnusable;
		else
			this.unusable = new ArrayList<int[]>(unusable);
		// Create a cell array of the specified dimensions to hold the cells
		pieces = new Cell[length][height];
		checkRep();
	}
	
	public RectangularBoard clone() {
		return (RectangularBoard) super.clone();
	}

	/**
	 * Returns the Cell at this board that is at the specified coordinate.
	 * 
	 * @requires coord.length==2
	 * @see Board
	 */
	@Override
	public Cell getCell(int[] coord) {
		// Get coordinate indeces
		int length = coord[0];
		int height = coord[1];
		// Handle out of index coordinates
		if ((length < 0 || length >= this.length) || 
				(height < 0 || height >= this.height))
			return null;
		// If there is no corresponding cell, its either unusable or empty.
		if (pieces[length][height] == null) {
			for (int[] array : unusable) {
				if (Arrays.equals(coord, array))
					return null; // cell is unusable
			}
			// cell is usable, but empty (never used yet)
			Cell cell = new Cell(null); 
			pieces[length][height] = cell;
			return cell;
		}
		else {
			// cell is usable, might be empty or occupied
			return pieces[length][height]; 
		}
	}
	
	
	protected void safeCopy() {
		
		Cell[][] before = pieces;
		
		unusable = new ArrayList<int[]>(unusable);
		
		pieces = new Cell[length][height];
		// TODO as it is now, to copy the rep, we just iterate over the
		// whole array. This defeats the purpose of having a "lazy-array"
		// in which Cells were only added if they were ever used. Ideally,
		// we should just iterate through the cells that exist, in order
		// to achieve the expected perfomance. This will be done later, when
		// there's time (it could be done with a HashMap or by keeping a 
		// record of activated Cells), but for now it is left like this
		// because it works.
		for (int i = 0; i < length; i++)
			for (int j = 0; j < height; j++)
				if (before[i][j]!=null) {
					pieces[i][j] = before[i][j].clone();
				}
	}
	/**
	 * Checks that the representation invariant holds.
	 */
	private void checkRep() {
		if (DebugInfo.DEBUG_RULESET) {
		}
	}

	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
}