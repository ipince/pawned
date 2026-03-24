package player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.antichess.ai.AiPlayer;
import net.antichess.ai.ChatProxy;
import controller.Controller;
import engine.adt.RuleSet;
import engine.game.GameTermination;
import engine.player.Player;

/**
 * TODO write specs
 * A MachinePlayer is a player of Antichess that uses artificial intelligence
 * to decide on its moves.
 * Serves as a wrapper around AIPlayer to play on the internets.
 */
public class MachinePlayer implements Player, AiPlayer {

	/**
	 * Object that is used to hold a lock on the submitPly() method, while it
	 * waits for the new move to arrive.
	 */
	private final Object opponentMoveLock = new Object();
	
	/**
	 * Object that is used to hold a lock on the getMove() method, while it
	 * waits for the automatic player's move to arrive.
	 */
	private final Object aiMoveLock = new Object();
	
	/**
	 * The <code>Controller</code> that operates the game being played.
	 */
	private final Controller controller;
	
	/**
	 * Holds the color this <code>AiPlayer</code> plays as. This is needed because
	 * of the stalemate specification and the empty Strings. The MachinePlayer
	 * needs to know what his color is.
	 */
	private final boolean isWhite;
	
	/**
	 * Holds the next move sent by controller, to be accessed when the 
	 * makeMove is called.
	 */
	private String nextOpponentMove = "";
	
	/**
	 * Determines whether the opponent's move is ready or not.
	 */
	private boolean nextOpponentMoveReady = false;
	
	/**
	 * A queue holding the moves of the AIPlayer, which we will return.
	 */
	private List<String> aiMoveQueue = new LinkedList<String>();
	
	/**
	 * Determines whether the game is just starting or not.
	 */
	private boolean firstMove = true;
	
	/**
	 * A ChatProxy to chat with other AiPlayers.
	 */
	private ChatProxy chatProxy;
	
	/**
	 * Some messages to send the AiPlayers.
	 */
	private List<String> messages = Arrays.asList(" Perhaps we should " +
			"play Spaghetti Chess instead...", " You're getting pwnd tonight...",
			" Gummibears are yummy", " I want to sleeeeep");
	
	
	/**
	 * @effects Creates a new MachinePlayer with the normal starting board (as
	 * described in the assignment). If <code>isWhite</code> is
	 * <code>true</code>, the player plays as white, else as black.
	 * <code>level</code> is an optional, arbitrary integer which can be used to
	 * affect the algorithm the machine player uses. The tournament referee will
	 * always request a level 0 MachinePlayer.
	 */
	public MachinePlayer(boolean isWhite, RuleSet rs, boolean timed,
					long whiteTime, long blackTime, Player ai,
					ChatProxy chatProxy) {
		this.chatProxy = chatProxy;
		this.isWhite = isWhite;
		if (this.isWhite) {
			synchronized (ai) {
				controller = new Controller(rs, ai, this, Controller.UNTIMED, Controller.UNTIMED);
				ai.setController(controller);
			}
		} else {
			synchronized (ai) {
				controller = new Controller(rs, this, ai, Controller.UNTIMED, Controller.UNTIMED);
				ai.setController(controller);
			}
		}
	}
	
	/**
	 * @requires <code>opponentMove</code> be either a String representing a
	 * valid move by the opponent on the board stored in <code>this</code> (in
	 * the "standard string format" defined by the assignment), or the empty
	 * String. Also, <code>timeLeft > 0 && opponentTimeLeft > 0</code>. 
	 * @effects Returns a valid next move for this player in a String of the
	 * appropriate format, given the opponent's move, the time left for this
	 * player, and the time left for the opponent. (Both times are in
	 * milliseconds.) If <code>opponentMove</code> is the empty String, then the
	 * board for this player should be considered up to date (as would be the
	 * case if this player is asked to make the first move of the game). NOTE:
	 * This procedure may run greater than <code>timeLeft</code>, but this would
	 * mean losing the game.
	 * @modifies <code>this</code>
	 * 	 * Assumes that it wont be called when the game is not in play.
	 */
	public String getMove(String opponentMove, long timeLeft, long opponentTimeLeft) {
		
		// Add opponent's move and let submitPly() know that move is ready
		if (!opponentMove.equals("")) {
			synchronized (opponentMoveLock) {
				nextOpponentMove = opponentMove;
				nextOpponentMoveReady = true;
				opponentMoveLock.notifyAll();
			}
		}
		
		synchronized (aiMoveLock) {
			while(aiMoveQueue.isEmpty()) {
				try {
					aiMoveLock.wait();
				} catch (InterruptedException ie) {
					throw new RuntimeException("Thread was interrupted");
				}
			}
			String nextMove = aiMoveQueue.get(0);
			aiMoveQueue.remove(0);

			if (chatProxy != null) {
				chatProxy.sendChat(messages.get(new Random().nextInt(messages.size())));
			}
			return nextMove;
		}
	}

	public synchronized String  submitPly() {
		
		// Only gets called on opponents turn
		assert controller.isNextWhite() == !isWhite;

		synchronized (aiMoveLock) {
			if (aiMoveQueue.isEmpty()) // Meaning the AiPlayer is in stalemate
				if (!firstMove)
					aiMoveQueue.add("");
			firstMove = false;
			aiMoveLock.notifyAll();
		}
		
		// Wait for getMove() to get called by referee
		synchronized (opponentMoveLock) {
			// Wait for opponent move
			while (!nextOpponentMoveReady) {
				try {
					opponentMoveLock.wait();
				} catch (InterruptedException ie) {
					throw new RuntimeException("Thread has been interrupted");
				}
			}
		}
		// Reset for next turn
		nextOpponentMoveReady = false;
		
		return nextOpponentMove;
	}

	public synchronized void inform(String ply) {
		List<Boolean> turns = controller.getTurnHistory();
		if (turns.get(turns.size()-1) == isWhite) // AIPlayer's turn
			aiMoveQueue.add(ply);
	}

	public synchronized void inform(GameTermination termination) {
		List<Boolean> turns = controller.getTurnHistory();
		if (turns.get(turns.size()-1) == isWhite) {
			List<String> history = controller.getGameHistory();
			aiMoveQueue.add(history.get(history.size()-1));
		} else {
			aiMoveQueue.add("");
		}
		synchronized (aiMoveLock) {
			aiMoveLock.notifyAll();
		}
	}

	public void setController(Controller controller) {
		throw new RuntimeException("Cannot set Controller of MachinePlayer");	
	}

	public byte[] getPersistentState() {
		return new byte[] {0};
	}
	
}
