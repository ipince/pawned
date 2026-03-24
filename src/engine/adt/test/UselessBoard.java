package engine.adt.test; 

import engine.adt.Board;

/**
 * UselessBoard represents a Board with all of its
 * cells unusable. Therefore, no cell can ever be
 * added to this board. 
 * 
 * 
 */
class UselessBoard extends Board {
 protected Cell getCell(int[] coord) {
  return null;
 }

@Override
	protected void safeCopy() {
		return;
	}  
}