package engine.adt;

import java.util.Collection;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;


import debug.*;
import engine.exception.InvalidBoardException;
import engine.exception.InvalidPositionException;
import engine.exception.UnusableCellException;

/**
 * A <code>Board</code> represents an N-dimensional game board, i.e. a set of cells
 * arranged in some way.
 * 
 * Each cell in a <code>Board</code> may contain at most one <code>Piece</code>. 
 * To refer to a cell within the <code>Board</code>, its position must be specified. 
 * A cell's position is determined by N integers, which specify the coordinates of the 
 * cell in the N-dimensional board.  
 * 
 * It should be noted that not all possible combinations of N integers are 
 * necessarily associated to a cell. If a sequence of N integers 
 * (n_1, n_2, ..., n_N) is not associated to a cell, the we say that the 
 * position (n_1, n_2, ..., n_N) is unusable. 
 * 
 * A <code>Board</code> contains a set of Pieces, each inside of some cell. 
 * A usable cell might be empty or occupied by a piece. If the cell is usable
 * and empty, a piece can be added to it. If the cell is occupied by a piece,
 * the piece can be removed from the board. Each piece can occupy at most 
 * one cell in the board. 
 * 
 * A <code>Board</code> can execute valid <code>Plies</code>. A <code>Ply</code> 
 * is valid if the instruction it refers to is executable in the board. 
 * For example, a <code>Ply</code> containing a piece addition to an unusable cell is
 * invalid. In the same way, a <code>Ply</code> that contains an <code>Action</code>
 * to add a piece previously removed in the ply, where no previous ply has been removed, 
 * is also invalid. 
 * 
 * A <code>Board</code> is synchronized, so it can be safely used in multi-threaded
 * environments.
 * 
 * @specfield cells : Set //The set of all usable cells in this board 
 * @specfield pieces : Set //The set of all pieces contained by 
 * 					   this board
 * @specfield map : a map from cells -> pieces, where pieces is the 
 * 			  set of all pieces contained by this board. 
 */
public abstract class Board implements Cloneable {

	// AF(x) : A Board B such that 
	// 
	//  B.cells = { x in R^k | x.isUsable() }
  	//  B.pieces = { x.piecesWhite U
	//               x.piecesBlack } 
	//               
	//  B.map: cells -> pieces 
	//  	 map(c) = x.getPiece ( c ), c in cells  
	// 
	
	
	 // Rep Invariant
     //
	 // * piecesWhite, piecesBlack != null
	 //
	
	/**
	 * Checks that the representation invariant holds. 
	 */
	private void checkRep() {
		if (DebugInfo.DEBUG_ADT) {
			if (piecesWhite == null ||
				piecesBlack == null)
				throw new RuntimeException("The piece list is null"); 
			
		}	
	}

	
	/** 
	 * A Cell represents a container for a piece within a board. 
	 * A position [x1,x2,..] is usable if and only if there is a cell 
	 * placed in x that position.
	 * 
	 * A Cell is mutable.
	 * 
	 * @specfield piece
	 */
	public class Cell implements Cloneable { 
		private Piece piece;
		
		//AF(x) = x
		//RI: piece is null or non-null
		
		/**
		 * Construct a new Cell with piece attached to it. 
		 * If piece is null, then the cell is empty.
		 * 
		 * The constructor does not make a copy of the piece it receives
		 * as a parameter
		 * @param piece The piece to be inserted on this cell. If the value
		 * 				is <code>null</code>, then the cell is empty.
		 * 
		 */
		public Cell(Piece piece) {
			this.piece = piece;
		}		
		
		/**
		 * Makes a clone of this cell. The clone of this cell is shallow, 
		 * so if cell c1 is a clone of c0, then both c1 and c0 will 
		 * reference the same piece. 
		 * 
		 * @return A shallow copy of <tt>this</tt> 
		 */
		public Cell clone() {
			try {
				return (Cell) (super.clone());
			} catch (Exception e) {
				//should never happen
			}
			return null;
		
		}
	}
	
	
	//Fields 
	private Map<Piece, int[]> piecesWhite; 
	private Map<Piece, int[]> piecesBlack;
		
	/**
	 * Constructs an n-dimensional Board. This abstract 
	 * constructor initializes the internal variables and 
	 * and ensures proper functionality of the partial
	 * representation provided by <tt>this</tt>  
	 * 
	 */
	public Board() {
		piecesWhite = new HashMap<Piece, int[]>();
		piecesBlack = new HashMap<Piece, int[]>(); 
	}
	
	/**
	 * Returns the cell located the position specified by <tt>coord</tt>.
     * 
	 * @param coord the coordinate where the desired cell is located.
	 * 
	 * @return the Cell associated with the specified coordinate, 
	 * 		   or <tt>null</tt> if the
	 * 		   space at <tt>coord</tt> is unusable.
	 */
	protected abstract Cell getCell(int[] coord); 
	
	
	/**
	 * Asks the current instance to disassociate itself from 
	 * its current state. In other words, this method should ensure
	 * that the representation of <tt>this</tt> is replaced by a cloned version 
	 * of itself, therefore disassociating itself from the previous references.  
	 * 
	 * @modifies <tt>this</tt>
	 * @effects After this method is executed, the representation of <tt>this</tt>
	 *          is should be a deeply copied version of <tt>this</tt> prior to the
	 *          method execution. 
	 */
	protected abstract void safeCopy() ;
	// Concrete Methods
	
	/**
	 * Returns the current pieces of a player.
	 * 
	 *  @param isWhite The color of the pieces. If the pieces required are white, 
	 *  				then <tt>isWhite == true</tt>. Otherwise, <tt>isWhite == false</tt>
	 *  
	 *  @return If <tt>true</tt> returns the set of all pieces p in <tt>this</tt> such that
	 *          p.isWhite(). Otherwise, returns the set of all pieces p in <tt>this</tt>
	 *          such that !p.isWhite(). If <tt>this</tt> is modified, the returned
	 *          set is unaffected. 
	 *          
	 */
	public synchronized Collection<Piece> getPieces(boolean isWhite) {
		checkRep();
		if (isWhite) {
			return Collections.unmodifiableSet(
					new HashSet<Piece>(piecesWhite.keySet()));
		}
		else
			return Collections.unmodifiableSet(
					new HashSet<Piece>(piecesBlack.keySet()));
	}
	
	/**
	 * Returns the Piece located at the specified cell, if any.
	 * 
	 * @param cell the coordinate for addressing the piece 
	 * @return  <tt>null</tt> if there is no Piece in the given cell or 
	 * 			if the specified cell is unusable. If there is a piece
	 * 			in the specified cell, then returns that cell. 
	 *             
	 */
	public synchronized Piece getPiece(int[] cell) {
		checkRep();
		if (getCell(cell) == null)
			return null; 
		
		return getCell(cell).piece;
	}
	
	/**
	 * Returns the position in the board of the specified piece.
	 *
	 * @requires this.contains(piece).
	 * 
	 * @param piece The piece to be searched on <tt>this</tt> 
	 * 
	 * @return the position of the given piece. If <tt>this.contains(piece)</tt> is
	 * 		   <tt>false</tt>, returns <tt>null</tt>. 
	 */
	public synchronized int[] getPosition(Piece piece) {
		checkRep();
		if (piece == null)
			return null;
		// HashMap returns null if piece has no position!
		else if (piece.isWhite())
			return piecesWhite.get(piece);
		else
			return piecesBlack.get(piece);
	}
	
	/**
	 * Determines whether the given <tt>Piece</tt> is contained 
	 * in this board or not. 
	 * 
	 * @param piece the piece to be searched on <tt>this</tt>.
	 * 
	 * @return <tt>true</tt> if <code>piece</code> is contained in <tt>this</tt>, 
	 * 			and <tt>false</tt> otherwise.
	 */
	public synchronized boolean contains(Piece piece) {
		checkRep();
		// Funny code... does the job though
		return getPosition(piece) != null;
	}
	
	/**
	 * Adds a Piece to the Board into the specified cell. The cell must be empty.
	 * 
	 * @param piece the piece to be added.
	 * @param cell the coordinates specifying the location in which the cell is
	 * 				to be added. 
	 * 
	 * @modifies this, piece
	 * @effects Adds a piece to the board. Notifies the
	 * 			piece that it has been added by calling the
	 * 			</tt>add()</tt> method. 
	 * 
	 * @throws RuntimeException if piece == null.
	 *         Otherwise, throws a RuntimeException if the piece is not associated to 
	 *         <tt>this</tt>. Then, if the cell is not usable, throws an 
	 *         <tt>UnusableCellException</tt>. Finally, throws a 
	 *         <tt>RuntimeException</tt> if the cell 
	 *         is already occupied. 
	 */
	public synchronized void addPiece(Piece piece, int[] cell) {
		addPieceSilent(piece, cell);
		piece.added();

		checkRep();

	}
	
	/**
	 * Ensures that a piece is not contained by <tt>this</tt> If a piece
	 * is removed, such a piece is notified of its removal by invoking the
	 * <tt>remove()</tt> method in Piece.
	 * 
	 * @param piece the piece to be removed 
	 * 
	 * @modifies this, piece
	 * 
	 * @effects ensures the piece is not contained in the current board,
	 *          and notifies the piece that it has been removed. 
	 */
	public synchronized void removePiece(Piece piece) {
		if (contains(piece)) {
			removePieceSilent(piece);
			piece.removed();
		}
		checkRep();
	}
	
	/**
	 * Determines if the cell specified by <code>cell</code> is usable or not.
	 * The null cell is unusable. 
	 * 
	 * @param cell the coordinates of the cell to be analyzed. 
	 * 
	 * @return <tt>true</tt> if a cell is usable or <tt>false</tt> otherwise. 
	 * 
	 */
	public synchronized boolean isUsable(int[] cell) {
		return getCell(cell) != null;
	}
	
	/**
	 * Determines whether the cell specified by <code>cell</code> is empty or
	 * not. As specified abovee, a cell is empty if it does not contain a Piece
	 * but is usable. 
	 * 
	 * @return <code>true</code> if the cell is empty, false if the
	 * 			cell is not empty (occupied or unusable). 
	 * @requires <code>cell</code> to specify a cell in the Board.
	 */
	public synchronized boolean isEmpty(int[] cell) {
		return getCell(cell) != null && getCell(cell).piece == null;
	}

	/**
	 * Performs the actions contained in <tt>Ply</tt> in the order specified. 
	 * When a ply is executed, the pieces in the board get notified of the 
	 * ply to be executed. Additionally, pieces that are added or removed
	 * by the end of the ply execution are notified of their respective
	 * addition or removal. 
	 * 
	 * 
	 * @requires <code>ply</code> to be a valid ply
	 * @modifies this, this.pieces, pieces in ply. 
	 * @effects notifies all the pieces in <tt>this</tt> that were
	 * 			present in the board prior to the execution of 
	 * 			the ply by calling their updatePly(Ply) method with 
	 * 			the current ply. 
	 * 
	 * 			The pieces that are removed from the 
	 * 			board, along with the pieces that remain in the
	 * 			board that weren't in the board before the ply 
	 * 			was executed are notified via their removed()
	 * 			and added() methods respectively. 
	 * 
	 * @param ply the ply to be executed by <tt>this</tt> 
	 * 
	 * @return A collection containing the collected pieces during the ply.
	 * 			If no piece was collected, then the collection is empty. 
	 * @throws RuntimeException if ply is invalid. 
	 */
	public synchronized Collection<Piece> executePly(Ply ply) {
		Stack<Piece> collectedPieces = new Stack<Piece>();
		if (ply == null) 
			return collectedPieces;

		
		//Keep a record of pieces to be added
		Stack<Piece> potentiallyAddedPieces = new Stack<Piece>(); 
		
		for (Action ac : ply) { 
			if (ac.getType() == Action.ADD) {
				Piece pc = ac.getPiece();
				if (pc != null) { 
					if (! contains(pc))
						potentiallyAddedPieces.add(pc); 
				}
			}
		}
		
		tellAll(ply); 
		for (Action ac : ply) {
			if (ac.getType() == Action.ADD) {
				if (ac.getPiece() == null) {
					try { 
						Piece piece = collectedPieces.pop(); 
						addPieceSilent(piece, ac.getCell());				
					}
					catch (EmptyStackException ese) {
						throw new IllegalArgumentException("Malformed ply");
					}
					catch (UnusableCellException uce) {
						throw new IllegalArgumentException("Malformed ply");						
					}
				}
				else {
					try { 
					addPieceSilent(ac.getPiece(), ac.getCell()); 
					}
					catch (UnusableCellException uce) {
						throw new IllegalArgumentException("Malformed ply");						
					}
				}
			}
			else if (ac.getType() == Action.REMOVE ) {
				Piece toRemove = getPiece(ac.getCell()); 
				if (toRemove != null) {
					collectedPieces.push(toRemove); 
					try { 
						removePieceSilent(toRemove); 
					}
					catch (RuntimeException re) {
						throw new IllegalArgumentException("Invalid ply"); 
					}
				}
			}
		}

		for (Piece pc : collectedPieces) {
			pc.removed();
		}
		for (Piece pc : potentiallyAddedPieces) {
			if (contains(pc))
				pc.added();
		}
		
		return collectedPieces; 
	}
	
	/**
	 * Returns a deep copy of <tt>this</tt>. 
	 * 
	 * @return A board such that, for all possible cell positions, each 
	 * 		   position on both boards is either unusable on both, empty 
	 * 		   on both, or the original board contains a piece p1 and the
	 * 		   cloned board contains a piece p2 such that p1 is a reproduced
	 * 		   version of p2. 
	 */
	public synchronized Board clone() {
		// deep copy of this with pieces
		try {
			// Get a new Board
			Board copy = (Board) super.clone();

			copy.safeCopy();
			
			copy.piecesWhite = new HashMap<Piece, int[]>(piecesWhite);
			copy.piecesBlack = new HashMap<Piece, int[]>(piecesBlack);
			// Reproduce white Pieces (with the copied Board) and add them to it
			for (Piece piece : getPieces(true)) {
				int[] location = getPosition(piece);
				copy.removePieceSilent(piece);
				copy.addPieceSilent(piece.reproduce(copy), location);
			}
			// Reproduce black Pieces (with the copied Board) and add them to it
			for (Piece piece : getPieces(false)) {
				int[] location = getPosition(piece);
				copy.removePieceSilent(piece);
				copy.addPieceSilent(piece.reproduce(copy), location);
			}
			return copy;
		} catch (CloneNotSupportedException e) {
			// Should never get here because Object supports clone()
			throw new RuntimeException("This should never happen");
		}
	}
	

	/**
	 * Notifies all the <code>Piece</code>s currently contained in
	 * <code>this</code> about the ply executed. 
	 * 
	 * @requires ply != null
	 * 
	 * @modifies this.getPieces(true) U this.getPieces(false)
	 * @effects Each piece in <tt>this</tt> receives a notification 
	 * 			and updates its status accordingly. 
	 * 
	 * @param ply the ply to be informed 
	 * 
	 */
	private synchronized void tellAll(Ply ply) { 
		// Tell the white Pieces

		for (Piece piece : getPieces(true)) {
			piece.updateInfo(ply); 
		}
		// Tell the black Pieces		

		for (Piece piece : getPieces(false)) {
			piece.updateInfo(ply); 
		}
	}
	
	/**
	 * Adds a piece without notifying it that it has been added.
	 * 
	 * @param piece The piece to be added
	 * @param cell The position at which the piece is to be added 
	 * 
	 * @modifies <tt>this</tt>
	 * @effects adds piece to <tt>this</tt> 
	 * 
	 * @throws IllegalArgumentException if piece or cell equal null,
	 * @throws UnusableCellException if the specified cell is not usable
	 * @throws InvalidPositionException if the cell is not empty
	 * @throws RuntimeException if the piece is already contained in <tt>this</tt> 
	 * @throws InvalidBoardException in case <tt>piece</tt> is not associated 
	 * 			with <tt>this</tt> 
	 * 		   
	 */
	private void addPieceSilent(Piece piece, int[] cell) {
		if (piece == null) {
			throw new IllegalArgumentException("Null arguments not allowed" +
					" for addPiece");
		}
		if (cell == null) { 
			throw new UnusableCellException("Null arguments not allowed"); 			
		}
		if (piece.associatedTo(this)) {
			if (!contains(piece)) {
				Cell container  = getCell(cell);
				if (container  == null) {
					throw new UnusableCellException("This cell is not usable"); 
				}
				if (container.piece != null ){ 
					throw new InvalidPositionException("This cell is not empty");
				}
				else { 
					container.piece = piece;
					if (piece.isWhite())
						piecesWhite.put(piece, cell);
					else
						piecesBlack.put(piece, cell);
				}
			}
			else {
				throw new RuntimeException("Cannot add " + piece.toString() + 
						" to this Board, because the piece is already there");
			}
		} else {
			throw new InvalidBoardException("Cannot add " + piece.toString() +
					" to this Board, because the Piece is not associated " +
					"to it");
		}
	}
	
	/** 
	 * Ensures that a piece is not contained in <tt>this</tt>. 
	 * 
	 * @param piece the piece to be removed from <tt>this</tt>
	 * 
	 * @modifies <tt>this</tt>
	 * @effects Ensures piece is not contained in <tt>this</tt> 
	 * 
	 */
	private void removePieceSilent(Piece piece) {
		if (contains(piece)) {
			int[] position = getPosition(piece);
			getCell(position).piece = null;
			if (piece.isWhite())
				piecesWhite.remove(piece);
			else
				piecesBlack.remove(piece);
		}

		
	}
	
}