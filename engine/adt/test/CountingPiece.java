package engine.adt.test;

import java.util.List;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * CountingPiece is a piece that keeps the record of 
 * the number of times it has been added, removed 
 * and how many plies have been executed since the 
 * piece entered a particular board. 
 *
 */
class CountingPiece extends Piece {

	int timesAdded = 0; 
	int timesRemoved = 0;
	int timesPly = 0; 
	
	public CountingPiece(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public void added() {
		timesAdded++;
	}

	@Override
	public List<Ply> getPlies() {
		return null;
	}

	@Override
	protected int[] initialPos() {
		return null;
	}

	@Override
	public void removed() {
		timesRemoved++;
	}

	@Override
	public String getType() {
		return "Counting Piece";
	}

	@Override
	public void updateInfo(Ply ply) {		
		timesPly++;
	} 
	
}