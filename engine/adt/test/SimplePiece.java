package engine.adt.test;

import java.util.List;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;

/**
 * SimplePiece represents a piece that
 * does not expect plies to be executed on them, to be
 * placeable on any type of board. 
 * 
 * A SimplePiece stores no information and throws an
 * exception whenever an action is signaled to it, 
 * such as a call to removed(), added() or
 * updateInfo(Ply) 
 *
 */
class SimplePiece extends Piece {

	public SimplePiece(boolean isWhite, Board board) {
		super(isWhite, board);
	}

	@Override
	public void added() {
		throw new RuntimeException("Should not happen"); 
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
		throw new RuntimeException("Should not happen"); 
	}

	@Override
	public String getType() {
		return "Silly piece";
	}

	@Override
	public void updateInfo(Ply ply) {		
		throw new RuntimeException("Should not happen"); 
	} 
	
}
