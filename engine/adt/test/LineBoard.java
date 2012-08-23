package engine.adt.test;

import engine.adt.Board;

/**
 * A LineBoard is a simple one dimensional board consisting of 
 * exactly 2 cells, [1], and [2]. Every other cell is unusable.
 *  
 *
 */
class LineBoard extends Board  implements Cloneable{

	private Cell[] array; 
	
	public LineBoard() { 
		array = new Cell[] {null, new Cell(null), new Cell(null)}; 
	}

	@Override
	protected Cell getCell(int[] coord) {
		if (coord == null)
			return null;
		if (coord.length != 1)
			return null;
		if (coord[0] < 0 || coord[0] >= array.length)
			return null; 
		else 
			return array[coord[0]];
	} 
	
	
	public LineBoard clone() {
		return (LineBoard)(super.clone());
	}

	@Override
	protected void safeCopy() {
		array = array.clone();
		
		array[1] = array[1].clone();
		array[2] = array[2].clone();
	}
}