package ruleset.antichess.test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ruleset.antichess.AntichessRuleSet;
import ruleset.antichess.StandardAC;
import ruleset.board.RectangularBoard;
import ruleset.piece.Bishop;
import ruleset.piece.King;
import ruleset.piece.Pawn;
import ruleset.piece.Queen;
import ruleset.piece.Rook;
import ruleset.ply.Move;
import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.adt.RuleSet;
import engine.game.GameInfo;
import engine.game.GameMessage;
import engine.game.GameTermination;
import junit.framework.TestCase;

/**
 * Unit tests for the Standard Antichess RuleSet
 */
public class StandardACTest extends TestCase {
	
	// As of now, these tests provide validation for a few hard-coded cases.
	// In the future, a test driver will be made so that many more tests can
	// be run much faster. These tests show that StdACRuleSet is working
	// properly in simple cases. More thorough testing and border line cases
	// will be treated in the future.
	
	private RuleSet rs;
	private List<Boolean> turnHistory;
	private List<GameMessage> messages;
	
	private final Set<Ply> initialWhitePlies;
	private final Set<Ply> initialBlackPlies;
	
	public StandardACTest() {
		initialWhitePlies = new HashSet<Ply>(initialPlies(true));
		initialBlackPlies = new HashSet<Ply>(initialPlies(false));
	}
	
	protected void setUp() {
		rs = new StandardAC();
		turnHistory = new ArrayList<Boolean>(1);
		messages = new ArrayList<GameMessage>();
	}
	
	/**
	 * Test that the default initial Board is correct.
	 */
	public void testInitialBoardDefault() {
		// This was also tested in the GUI (when starting a Game)
		Board board = rs.boardFactory().getInitialBoard();
		assertEquals("The RuleSet did not produce the correct default Board. " +
				"It did not add the correct amount of white Pieces",
				16, board.getPieces(true).size());
		assertEquals("The RuleSet did not produce the correct default Board. " +
				"It did not add the correct amount of black Pieces",
				16, board.getPieces(false).size());
	}
	
	/**
	 * Test that the initial Board is set correctly, given an arbitrary setting.
	 */
	public void testInitialBoardArbitrary() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test the PlyFactory.
	 */
	public void testPlyFactory() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test the PieceFactory.
	 */
	public void testPieceFactory() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test that the turn determination mechanism works correctly.
	 */
	public void testNextTurn() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test that the valid Plies when a Player can capture a Piece are the
	 * correct ones.
	 */
	public void testValidPliesCapture() {
		// For white player moving
		Board board = rs.boardFactory().getInitialBoard();
		Piece whiteBishop = new Bishop(true,board);
		board.addPiece(whiteBishop,new int[] {4,3});
		GameInfo info = null;
		try {
			info = rs.continueGame(board, turnHistory, messages);
			Set<Ply> whitePlies = new HashSet<Ply>(info.getPlies());
			Set<Ply> correctPlies = new HashSet<Ply>();
			correctPlies.add(new Move(new int[] {4,3},new int[] {1,6}));
			correctPlies.add(new Move(new int[] {4,3},new int[] {7,6}));
			assertEquals("The valid Plies for the white Player are incorrect",
					correctPlies, whitePlies);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getClass());
		}
		// For black player moving
		turnHistory.add(info.getTurn());
		messages = info.getMessages();
		board.removePiece(whiteBishop);
		Piece blackBishop = new Bishop(false,board);
		board.addPiece(blackBishop, new int[] {4,3});
		try {
			info = rs.continueGame(board, turnHistory, messages);
			Set<Ply> blackPlies = new HashSet<Ply>(info.getPlies());
			Set<Ply> correctPlies = new HashSet<Ply>();
			correctPlies.add(new Move(new int[] {4,3},new int[] {2,1}));
			correctPlies.add(new Move(new int[] {4,3},new int[] {6,1}));
			assertEquals("The valid Plies for the black Player are incorrect",
					correctPlies, blackPlies);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getMessage());
		}
	}
	
	/**
	 * Test that the valid Plies when a Player cannot capture a Piece are the
	 * correct ones.
	 */
	public void testValidPliesNoCapture() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test that the valid Plies never put the Player in check.
	 */
	public void testValidPliesCheck() {
		// Scenario: black King able to eat white Pawn, but if so, white
		// Bishop would put it in check. A white Rook also prevents it
		// to move to a side. Moreover, a black Queen is in the path of
		// a white Rook and can unblock the Rook by eating a white Pawn.
		// Thus, the Queen can't move to eat the white Pawn, because
		// otherwise the white Rook would put the black King in check.
		Board board = new RectangularBoard();
		Piece whiteRook1 = new Rook(true,board);
		Piece whiteRook2 = new Rook(true,board);
		Piece whiteBishop = new Bishop(true,board);
		Piece whitePawn1 = new Pawn(true,board);
		Piece whitePawn2 = new Pawn(true,board);
		Piece blackQueen = new Queen(false,board);
		Piece blackKing = new King(false,board);
		board.addPiece(whiteRook1, new int[] {1,7});
		board.addPiece(whiteRook2, new int[] {5,3});
		board.addPiece(whiteBishop, new int[] {3,3});
		board.addPiece(whitePawn1, new int[] {6,6});
		board.addPiece(whitePawn2, new int[] {4,5});
		board.addPiece(blackQueen, new int[] {4,7});
		board.addPiece(blackKing, new int[] {6,7});
		GameInfo info = null;
		try {
			turnHistory.add(true); // make next Player be black
			info = rs.continueGame(board, turnHistory, messages);
			Set<Ply> blackPlies = new HashSet<Ply>(info.getPlies());
			Set<Ply> correctPlies = new HashSet<Ply>();
			correctPlies.add(new Move(new int[] {4,7}, new int[] {1,7}));
			assertEquals("The initial valid Plies for the white Player are incorrect",
					correctPlies, blackPlies);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getClass());
		}
		// check next turn (save for later)
	}
	
	/**
	 * Test that the initial valid Plies are the correct ones.
	 */
	public void testValidPliesInitial() {
		Board board = rs.boardFactory().getInitialBoard();
		GameInfo info = null;
		// For white player moving
		try {
			info = rs.continueGame(board, turnHistory, messages);
			Set<Ply> whitePlies = new HashSet<Ply>(info.getPlies());
			assertEquals("The initial valid Plies for the white Player are incorrect",
					initialWhitePlies, whitePlies);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getClass());
		}
		// For black player moving
		turnHistory.add(info.getTurn());
		messages = info.getMessages();
		try {
			info = rs.continueGame(board, turnHistory, messages);
			Set<Ply> blackPlies = new HashSet<Ply>(info.getPlies());
			assertEquals("The initial valid Plies for the black Player are incorrect",
					initialBlackPlies, blackPlies);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getMessage());
		}
	}
	
	/**
	 * Test that the valid Plies at some arbitrary Board configuration are the
	 * correct ones.
	 */
	public void testValidPliesArbitrary() {
		fail("Test not yet implemented");
	}
	
	/**
	 * Test that a checkmate is correctly determined.
	 */
	public void testCheckmate() {
		// A white King and two black Rooks check-mating it. Additionally,
		// white has no other Piece available. This checks the termination
		// condition hierarchy, as a check-mate overpowers a piece depletion
		// victory.
		Board board = new RectangularBoard();
		Piece king = new King(true,board);
		Piece rook1 = new Rook(false,board);
		Piece rook2 = new Rook(false,board);
		board.addPiece(king, new int[] {0,0});
		board.addPiece(rook1, new int[] {4,0});
		board.addPiece(rook2, new int[] {5,1});
		try {
			rs.continueGame(board, turnHistory, messages);
		} catch (GameTermination gt) {
			assertTrue("Game was terminated, but for the wrong conditions",
					gt.getType().equals(AntichessRuleSet.CHECKMATE));
			assertFalse("The wrong Player won the Game",gt.winnerIsWhite());
			return;
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getClass());
		}
		fail("RuleSet did not terminate the game as it should have.");
	}
	
	/**
	 * Test that a stalemate is correctly determined.
	 */
	public void testStalemate() {
		// A white King and two black Rooks that don't allow it to move.
		// Additionally, the Rooks attack a white Pawn that can't move
		// either because a black Pawn is in its way. Therefore, white cannot
		// move and black should move; it only has two possible Plies, namely,
		// to capture the white Pawn. There is a black King in the Board because
		// otherwise the game cannot continue, but it cannot capture any Piece.
		Board board = new RectangularBoard();
		Piece king1 = new King(true,board);
		Piece king2 = new King(false,board);
		Piece whitePawn = new Pawn(true,board);
		Piece blackPawn = new Pawn(false,board);
		Piece rook1 = new Rook(false,board);
		Piece rook2 = new Rook(false,board);
		board.addPiece(king1, new int[] {0,0});
		board.addPiece(king2, new int[] {7,7});
		board.addPiece(whitePawn, new int[] {4,3});
		board.addPiece(blackPawn, new int[] {4,4});
		board.addPiece(rook1, new int[] {4,1});
		board.addPiece(rook2, new int[] {1,3});
		try {
			GameInfo info = rs.continueGame(board, turnHistory, messages);
			Set<Ply> plies = new HashSet<Ply>(info.getPlies());
			Set<Ply> correctPlies = new HashSet<Ply>();
			correctPlies.add(new Move(new int[] {4,1}, new int[] {4,3}));
			correctPlies.add(new Move(new int[] {1,3}, new int[] {4,3}));
			assertFalse("The next player should be black, not white",
					info.getTurn());
			assertEquals("The valid Plies for the black Player are incorrect",
					correctPlies, plies);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getMessage());
		}
	}
	
	/**
	 * Test that a victory by Piece depletion is correctly determined.
	 */
	public void testPieceDepletion() {
		// A white King and two black Rooks around the Board (not checking it)
		Board board = new RectangularBoard();
		Piece king = new King(true,board);
		Piece rook1 = new Rook(false,board);
		Piece rook2 = new Rook(false,board);
		board.addPiece(king, new int[] {0,0});
		board.addPiece(rook1, new int[] {4,3});
		board.addPiece(rook2, new int[] {5,7});
		try {
			rs.continueGame(board, turnHistory, messages);
		} catch (GameTermination gt) {
			assertTrue("Game was terminated, but for the wrong conditions",
					gt.getType().equals(AntichessRuleSet.PIECE_DEPLETION));
			assertTrue("The wrong Player won the Game",gt.winnerIsWhite());
			return;
		} catch (Exception e) {
			fail("RuleSet could not continue Game successfully: " + e.getClass());
		}
		fail("RuleSet did not terminate the game as it should have.");
	}
	
	/**
	 * Test that Coronations are handled correctly.
	 */
	public void testCoronation() {
		Board board = new RectangularBoard();
		Piece pawn = new Pawn(true,board);
		Piece king = new King(true,board);
		Piece bishop = new Bishop(false,board);
		board.addPiece(pawn, new int[] {6,6});
		board.addPiece(king, new int[] {0,0});
		board.addPiece(bishop, new int[] {1,3});
		try {
			rs.continueGame(board, turnHistory, messages);
		} catch (GameTermination gt) {
			fail("RuleSet wrongly terminated the Game.");
		}
		
	}
	
	/**
	 * Returns the a Collection of the valid initial Plies
	 */
	private Collection<Ply> initialPlies(boolean isWhite) {
		Collection<Ply> plies = new ArrayList<Ply>(20);
		if (isWhite) {
			for (int i=0; i<8; i++) {
				plies.add(new Move(new int[] {i,1}, new int[] {i,2}));
				plies.add(new Move(new int[] {i,1}, new int[] {i,3}));
			}
			plies.add(new Move(new int[] {1,0}, new int[] {0,2}));
			plies.add(new Move(new int[] {1,0}, new int[] {2,2}));
			plies.add(new Move(new int[] {6,0}, new int[] {5,2}));
			plies.add(new Move(new int[] {6,0}, new int[] {7,2}));
		} else {
			for (int i=0; i<8; i++) {
				plies.add(new Move(new int[] {i,6}, new int[] {i,5}));
				plies.add(new Move(new int[] {i,6}, new int[] {i,4}));
			}
			plies.add(new Move(new int[] {1,7}, new int[] {0,5}));
			plies.add(new Move(new int[] {1,7}, new int[] {2,5}));
			plies.add(new Move(new int[] {6,7}, new int[] {5,5}));
			plies.add(new Move(new int[] {6,7}, new int[] {7,5}));
			}
		return plies;
	}

}
