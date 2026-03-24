package player;


import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import ruleset.antichess.EnCastleAC;
import ruleset.antichess.StandardAC;
import ruleset.eval.EvaluatorFactory;

import controller.Controller;
import engine.adt.RuleSet;
import engine.player.Player;
import engine.player.test.DeterministicAiPlayer;

import net.antichess.ai.AiPlayer;
import net.antichess.ai.AiPlayerFactory;
import net.antichess.ai.ChatHistoryViewer;
import net.antichess.ai.ChatProxy;

/**
 * 
 * @author reipince
 *
 */
public class PlayerFactory implements AiPlayerFactory {
	
	/**
	 * The <code>String</code> representation of the default
	 * <code>Player</code> this factory creates.
	 */
	private static final String defaultPlayer = "AIPlayer";
	
	/**
	 * The <code>String</code> representation of the <code>Player</code> that
	 * this factory will actually create.
	 */
	private final String playerToCreate;
	
	/**
	 * <code>String</code> representation of the standard antichess ruleset.
	 */
	private final String STANDARD = "6170-spring-2007";
	
	/**
	 * <code>String</code> representation of the encastle ruleset.
	 */
	private final String ENCASTLE = "6170-spring-2007-encastle";
	
	/**
	 * <code>Collection</code> of supported <code>RuleSet</code>s.
	 */
	private final Collection<String> SUPPORTED_RULESETS =
				Arrays.asList(STANDARD, ENCASTLE);
	
	/**
	 * The depth the <code>AiPlayer</code> will use to calculate its moves.
	 */
	private final int DEPTH = 2;
	
	/**
	 * The persistent state.
	 */
	private byte[] persistentState;
	
	
	/**
	 * Constructs a new <code>PlayerFactory</code> that will be able to create
	 * the default <code>Player</code> and allow it to play in the
	 * antichess.mit.edu server.
	 */
	public PlayerFactory() {
		this(defaultPlayer);
	}
	
	/**
	 * Constructs a new <code>PlayerFactory</code> that will be able to create
	 * the given <code>Player</code> and allow it to play in the
	 * antichess.mit.edu server. This factory only supports a few possible
	 * <code>AiPlayer</code>s. Among these are "Deterministic" and "AIPlayer".
	 * If <code>player</code> is neither of these values, the factory will be
	 * able to create "AIPlayer"s.
	 */
	public PlayerFactory(String player) {
		playerToCreate = player;
	}

	/**
	 * This method should return an <code>AiPlayer</code> who is expected to
	 * play one game of antichess under the conditions specified by the
	 * parameters.
	 * 
	 * @requires <code>gameProperties</code> to contain valid properties for a
	 * game of antichess. In particular, the ruleset it provides must be
	 * supported by <code>this</code>.
	 * @param isWhite indicates if the player is white or black
	 * @param gameProperties a properties object with information about the game
	 * to be played. General properties to antichess have the prefix
	 * "antichess.", whereas properties specific to a particular rule set use
	 * the rule set's name as a prefix.
	 * <ul>
	 * 	<li> antichess.timedGame - a boolean indicating if the game is timed
	 * (referred to by <code>PROPERTY_TIMED_GAME</code>).</li>
	 * 	<li> antichess.initTime.white - if the game is timed, the amount of time
	 * the white side will start with, in milliseconds (referred to by
	 * <code>PROPERTY_INIT_TIME_WHITE</code>).</li>
	 * 	<li> antichess.initTime.black - if the game is timed, the amount of time
	 * the black side will start with, in milliseconds (referred to by
	 * <code>PROPERTY_INIT_TIME_BLACK</code>).</li>
	 * 	<li> antichess.ruleSet - the name of the set of rules under which the
	 * game will be played, see antichess.csail.mit.edu for the list of possible
	 * rule sets (referred to by <code>PROPERTY_RULE_SET</code>).</li>
	 * 	<li> antichess.whitePlayerName - the name of the white player (referred
	 * to by <code>PROPERTY_WHITE_NAME</code>.</li>
	 * 	<li> antichess.blackPlayerName - the name of the black player (referred
	 * to by <code>PROPERTY_BLACK_NAME</code>.</li>
	 * @param chatProxy - the player may use the sendChat method on the chatProxy
	 * to send messages.
	 * @param chatHistoryViewer - a view of the ChatHistory for the game.
	 * @param persistentState - a byte array storing some player-specific
	 * information. The first time createPlayer is invoked, persistentState is a
	 * zero-length array. Every subsequent invocation will pass in the
	 * persistentState returned by <code>AiPlayer.getPersistentState()</code>
	 * after the completion of the previous game. This provides a mechanism to
	 * store information across runs of the AI.
	 * @param persistentStateSizeLimit - the maximum length array which
	 * <code>AiPlayer.getPersistentState()</code> will be allowed to return.
	 * Your persistent state will not be updated if AiPlayer.getPersistentState()
	 * returns an array which is too large.
	 * @return an <code>AiPlayer</code> who is expected to play one game of
	 * antichess under the conditions specified by the parameters.\
	 * @see antichess-ai.AiPlayer, antichess-ai.ChatHistory
	 */
	public AiPlayer createPlayer(boolean isWhite, Properties gameProperties, 
				ChatProxy chatProxy, ChatHistoryViewer chatHistoryViewer,
				byte[] persistentState, int persistentStateSizeLimit) {
		
		Player ai = null;
		EvaluatorFactory efact = new EvaluatorFactory();
		if (playerToCreate.equals("Deterministic"))
			ai = new DeterministicAiPlayer();
		else
			ai = new AIPlayer(true, efact.createEvaluator(1), DEPTH);
		return new MachinePlayer(isWhite, parseRuleSet(gameProperties),
						false, 0, 0, ai, chatProxy);
	}

	/**
	 * Lists the rulesets for which this factory can create players. If the
	 * factory cannot create players for any ruleset, it should return an empty
	 * array instead of <code>null</code>. See <a href="antichess.mit.edu">
	 * antichess.mit.edu</a> for a list of available rulesets.
	 */
	public Collection<String> getSupportedRulesets() {
		return SUPPORTED_RULESETS;
	}
	
	/**
	 * Given some <code>Properties</code>, gets the
	 * 
	 * @return a <code>RuleSet</code> 
	 */
	private RuleSet parseRuleSet(Properties gameProperties) {
		String ruleSet = gameProperties.getProperty(AiPlayerFactory.PROPERTY_RULE_SET);
		if (ruleSet.equals(STANDARD))
			return new StandardAC();
		else if (ruleSet.equals(ENCASTLE))
			return new EnCastleAC();
		else
			throw new RuntimeException("Unsupported ruleset: " + ruleSet);
	}
	
	private long parseTime(boolean isWhite, Properties gameProperties) {
		String timed = gameProperties.getProperty(AiPlayerFactory.PROPERTY_TIMED_GAME);
		long time = Long.valueOf(Controller.UNTIMED);
		if (timed.equals("true"))
			if (isWhite)
				time = Long.valueOf(gameProperties.getProperty(AiPlayerFactory.PROPERTY_INIT_TIME_WHITE));
			else
				time = Long.valueOf(gameProperties.getProperty(AiPlayerFactory.PROPERTY_INIT_TIME_BLACK));
		return time;
	}

}
