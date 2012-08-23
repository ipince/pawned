package ruleset.antichess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import ruleset.board.CoordinateParser;
import ruleset.piece.*;
import ruleset.ply.Coronation;
import ruleset.ply.Move;

/**
 * A <code>RuleSet</code> that encodes the rules of Standard Antichess (aka
 * 6.170-spring-2007 antichess).
 */
public class StandardAC extends AntichessRuleSet {

	/**
	 * A <code>PieceFactory</code> that can create any of the supported
	 * <code>Piece</code>s, given its type and a <code>Board</code> to associate
	 * them to. The <code>Piece</code>s supported by <code>StandardAC</code> are
	 * the regular <code>Piece</code>s <code>AntichessRuleSet</code> supports,
	 * with the constraints that <code>Pawn</code>s are not allowed to capture
	 * en passant and <code>King</code>s are not allowed to castle.
	 * 
	 * @see engine.adt.RuleSet, ruleset.AntichessRuleSet
	 */
	private static PieceFactory pieceFactory = new PieceFactory() {
		
		/**
		 * Returns a new <code>Piece</code> of the specified type and color,
		 * which is associated to the provided <code>Board</code>.
		 * 
		 * @requires <code>name</code> to represent the type of a
		 * <code>Piece</code> supported by this <code>RuleSet</code>.
		 */
		public Piece getPiece(String name, Board board, boolean isWhite) {
			if (name == null)
				throw new IllegalArgumentException("Illegal argument for name");
			PieceName pieceName = PieceName.valueOf(name);
			if (pieceName == PieceName.king) 
				return new King(isWhite, board);
			else if (pieceName == PieceName.queen)
				return new Queen(isWhite, board);
			else if (pieceName == PieceName.rook)
				return new Rook(isWhite, board); 
			else if (pieceName == PieceName.bishop)
				return new Bishop(isWhite, board); 
			else if (pieceName == PieceName.knight)
				return new Knight(isWhite, board); 
			else if (pieceName == PieceName.pawn)
				return new Pawn(isWhite, board);
			else
				throw new IllegalArgumentException("Illegal argument " + name); 
		}

		/**
		 * Returns the set of <code>Piece</code>s that this <code>RuleSet</code>
		 * supports. The <code>Piece</code>s are associated to the given
		 * <code>Board</code> and are of the specified color.
		 */ 
		public Set<Piece> getSupportedPieces(Board board, boolean isWhite) {
			Set<Piece> pieces = new HashSet<Piece>(); 
			for (PieceName name : PieceName.values()) {
				pieces.add(getPiece(name.toString(), board, isWhite));
			}
			return pieces;
		}
		
		/**
		 * Returns a <code>List</code> with the types of <code>Piece</code>s
		 * that this <code>RuleSet</code> supports in some order determined
		 * by the <code>RuleSet</code>.
		 */
		public List<String> getOrderedPieces() {
			List<String> orderedPieces = new ArrayList<String>(PieceName.values().length);
			for (PieceName name : PieceName.values())
				orderedPieces.add(name.toString());
			return orderedPieces;
		}
		
	};
	
	/**
	 * A <code>PlyFactory</code> that can create any type of <code>Plies</code>
	 * supported by this <code>RuleSet</code>. For <code>StandardAC</code>,
	 * these include only <code>Move</code> and <code>Coronation</code>.
	 * 
	 * @see ruleset.ply.Move, ruleset.ply.Coronation
	 */
	private static PlyFactory plyFactory = new PlyFactory() {

		/**
		 * Returns a <code>Ply</code> whose <code>String</code> representation
		 * matches the provided one, <code>name</code>. Any <code>Piece</code>s
		 * that form part of the returned <code>Ply</code> are associated to the
		 * given <code>Board</code> (e.g. a <code>Queen</code> within a
		 * <code>Coronation</code>). Notice that the returned <code>Ply</code>
		 * is not checked for validity in the given <code>Board</code>. This
		 * method only guarantees that the returned <code>Ply</code> is
		 * supported within <code>StandardAC</code>.
		 * 
		 * @see ruleset.board.RectangularParser
		 * 
		 * @requires <code>name</code> to be in the format supported by this
		 * <code>RuleSet</code>. This format is specified in the
		 * <code>Parser</code> for this <code>RuleSet</code>.
		 */
		public Ply getPly(String name, Board board) {
			// this method is bad because of coronation not having a diff
			// representation. Using extended Ply notation would be better
			int[] starting = CoordinateParser.getCell(name, true);
			int[] ending = CoordinateParser.getCell(name, false);
			Piece effector = board.getPiece(starting);
			if (effector != null &&
					PieceName.valueOf(effector.getType()) == PieceName.pawn) {
				if (isAtEnd(ending, effector.isWhite())) {
					return new Coronation(starting, ending, 
					pieceFactory.getPiece(PieceName.queen.toString(), 
							              board, effector.isWhite()));
				}
			}
			return new Move(starting, ending);
		}
	};
	
	/**
	 * Constructs a new <code>StandardAC</code> with a default antichess
	 * initial <code>Board</code> as its initial <code>Board</code>.
	 */
	public StandardAC() {
		super();
	}
	
	/**
	 * Constructs a new <code>StandardAC</code> with the <code>Board</code>
	 * that corresponds to <code>settings</code> as its initial
	 * <code>Board</code>.
	 * 
	 * @see controller.XmlFactory
	 * @param settings <code>String</code> that represents a <code>Board</code>
	 * in XML format. The format used is described in <code>XmlFactory</code>.
	 */
	public StandardAC(String settings) {
		super(settings);
	}
	
	@Override
	/**
	 * Specified by AntichessRuleSet.
	 */
	public PieceFactory pieceFactory() {
		return pieceFactory;
	}

	@Override
	/**
	 * Specified by AntichessRuleSet.
	 */
	public PlyFactory plyFactory() {
		return plyFactory;
	}
	
	@Override
	/**
	 * Specified by AntichessRuleSet.
	 */
	public String toString() {
		return "6170-spring-2007";
	}
	
	/**
	 * Given a <code>Board</code>, a list of <code>Plies</code>, and the color
	 * of the next player, filters out the invalid <code>Plies</code> according
	 * to the rules of standard antichess. These include plies that would
	 * put the player in check; also, if there are any <code>Plies</code> that
	 * capture a <code>Piece</code> of the opposite player, then those
	 * <code>Plies</code> that do not are filtered out too.
	 */
	protected List<Ply> filterPlies(Board board, List<Ply> toBeFiltered, boolean isWhite) {
		List<Ply> filteredByCheck = new LinkedList<Ply>(); // ONLY by check
		List<Ply> filteredByCapture = new LinkedList<Ply>(); // by check AND capture
		int piecesBefore = board.getPieces(!isWhite).size();
		int piecesAfter;
		for (int i = 0; i < toBeFiltered.size(); i++) {
			Ply ply = toBeFiltered.get(i);
			Board clonedBoard = executeFakePly(board, ply);
			piecesAfter = clonedBoard.getPieces(!isWhite).size();
			if (!isInCheck(clonedBoard, isWhite)) {
				filteredByCheck.add(ply); // filter in by check only
				if (piecesAfter < piecesBefore)
					filteredByCapture.add(ply); // filter in by check and capture
			}
		}
		return filteredByCapture.isEmpty() ? filteredByCheck : filteredByCapture;
	}
}
