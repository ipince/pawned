package controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.adt.RuleSet;
import engine.game.Game;
import engine.game.GameTermination;
import engine.player.GameObserver;
import engine.player.Player;

/**
 * The controller serves as a link between the Players and the Game. It also
 * contains information about the Timers (time) and is able to save a Game to
 * an XML file.
 * 
 * @specfield game  : Game ADT // The game currently being 
 * 								  executed
 * @specfield times : sequence // The sequence of times at which
 * 								  each player has executed moves
 * @specfield timers : StopWatch // The timers for each player
 * 
 */
public final class Controller { //Cannot be instantiated
	//Spawns threads in Constructor! 

	//AF(x) 
	// Abstraction Function:
	//    AF(c) = controller C such that
	//
	//      C.game = c.game
	//      C.times = times 
	//      C.timer(white) = timeWhite
	//      C.timer(black) = timeBlack

	// Rep Invariant
	// * game, rs != null 
	// * whitePlayer, blackPlayer != null
	// * (timeStatus == null) <=> (timeWhite.remainingTime() != 0) and
	//                            (timeBlack.remainingTime() != 0)

	private void checkRep() {
		if (debug.DebugInfo.DEBUG_CONTROLLER) {
			boolean rsgame =  (game == null) || (rs == null); 
			boolean players = (whitePlayer == null) || (blackPlayer == null); 
			boolean timestatLR = true; 
			boolean timestatRL = true; 

			if (timeStatus != null) //game has time depleted 
				timestatLR = (timeWhite != null && timeWhite.remaining() == 0) ||
				(timeBlack != null && timeBlack.remaining() == 0);
			//both timers were null or one has remaining of 0 

			if ((timeWhite != null && timeWhite.remaining() == 0) &&
					(timeBlack != null && timeBlack.remaining() == 0))
				timestatRL = timeStatus == null; 

			if (rsgame || players || !(timestatLR && timestatRL))
				throw new RuntimeException("Invariant violated for Controller");
		}
	}

	static final String INIT_BOARD_TAG = "init-board";
	static final String MOVE_HISTORY  = "moveHistory";

	public static final int UNTIMED = -1;

	private boolean terminated = false; 
	private int initialTimeWhite;
	private int initialTimeBlack; 

	private StopWatch timeWhite = null;
	private StopWatch timeBlack = null; 

	private TurnCycle cycle = null;
	// Fields


	/**
	 * The white Player
	 */
	private Player whitePlayer;

	/**
	 * The black Player
	 */
	private Player blackPlayer;


	private RuleSet rs; 
	private Set<GameObserver> observers;
	private List<Integer> times; 

	private GameTermination timeStatus = null;
	private Game game; 


	/**
	 * Constrcutrs a new Controller from the specified file 
	 * @param file The file the game is loaded from 
	 * @param ruleSet The ruleset that controls this game
	 * @param whitePlayer A reference to the white player
	 * @param blackPlayer A reference to the black player
	 * @param whiteTime The amount of time available for white
	 * @param blackTime The amount of time available for black
	 * @param observers <i> Optional </i> 
	 */
	public Controller(File file, RuleSet ruleSet, Player whitePlayer, Player blackPlayer,
			int whiteTime, int blackTime, GameObserver ... observers) 
	{

		initVars(ruleSet, whitePlayer, blackPlayer, whiteTime, blackTime, observers); 
		//TODO check what happens with malformed XMLs. It fails, but I
		//have to throw an IOException as opposed to just randomly 
		//dying 

		//Load the board from XML file initial conditions 		
		String boardDescription = XmlFactory.getTag(INIT_BOARD_TAG, file); 

		//Load game with initial board
		if (boardDescription.equals("")) 
			game = new Game(rs);
		else 
			game = new Game(rs, rs.boardFactory().getBoard(boardDescription));


		//assemble plies to send to Game 
		List<String[]> history = XmlFactory.xmlToHistory(
				XmlFactory.getTag(MOVE_HISTORY, file));

		List<String> plies = new LinkedList<String>();
		List<Boolean> turns = new LinkedList<Boolean>();
		for (String[] item : history) {
			//white c2-c3 299000
			turns.add(item[0].equals("white")); 
			plies.add(item[1]); 
			times.add(Integer.parseInt(item[2])); 
		}
		
		//TODO decide whether people will get informed of the
		//last move or not 
		try {
			if (! plies.isEmpty())
				game.executePlies(plies, turns); 

			//if (getGameHistory().size() > 0) 
			//	inform(getGameHistory().get(getGameHistory().size() - 1)); 

			start(); 

		}
		catch (GameTermination re) {
			//if (getGameHistory().size() > 0)
			//	inform(getGameHistory().get(getGameHistory().size() - 1)); 
			inform(re); 
		}


	}

	// TODO document on why we chose ints over longs for the time... MachinePlayer
	// specs used int, so we got pwnd.. ints only allow for a maximum of ~35 mins
	public Controller(RuleSet ruleSet, Player whitePlayer, Player blackPlayer,
			int whiteTime, int blackTime, GameObserver ... observers) {

		
		initVars(ruleSet, whitePlayer, blackPlayer, whiteTime, blackTime, observers); 
		//Game
		game = new Game(rs);

		start(); 
	}

	// Accessors

	/**
	 * Returns the RuleSet being used
	 */
	public synchronized  RuleSet getRuleSet() {
		checkRep(); 
		return rs; //repexp
	}

	/**
	 * Returns the collected pieces
	 */
	public synchronized Set<Piece> getCapturedPieces(boolean isWhite) {
		return game.getCapturedPieces(isWhite);
	}
	/**
	 * Returns the Board in which the Game is being played.
	 */
	public synchronized Board getBoard() {

		checkRep(); 
		return game.getBoard(); //repexp
	}

	/**
	 * Returns the <code>String</code> representation of the <code>Ply</code>
	 * history of the game.
	 * 
	 * If the game is new (i.e. there is no game history), then an empty list
	 * is returned. 
	 * 
	 * @return An list representing the sequence of ply representations that
	 * 		   have been executed on the board. If no ply has been executed,
	 * 		   returns the empty list. This list
	 * 		   is immutable. That is, removal and addition of plies is expected
	 * 		   to throw an <code>UnsupportedOperationException</code>.
	 * 
	 */
	public synchronized List<String> getGameHistory() {
		List<String> list = new LinkedList<String>(); 

		//Extract the string representation of each ply
		//in game
		for (Ply ply : game.getGameHistory() ) {
			list.add(ply.toString());
		}

		checkRep(); 

		return Collections.unmodifiableList(list);
	}


	/**
	 * Returns a list of <code>Booleans</code> representing the turns 
	 * that have been executed in this game in ascending chronological order.
	 * 
	 * For a given turn, the <code>Boolean</code> is <code>true</code>, then 
	 * the ply was executed by the white player, and otherwise it was executed
	 * by the black player. 
	 * 
	 *  
	 * @return A list s.t. if i < j, getTurnHistory().get(i) happened before
	 * 			getTurnHistory().get(j).  This list
	 * 		    is immutable. That is, removal and addition of plies is expected
	 * 		    to throw an <code>UnsupportedOperationException</code>. 
	 */	
	public synchronized List<Boolean> getTurnHistory() {
		checkRep(); 
		return Collections.unmodifiableList(game.getTurnHistory());
	}	
	/**
	 * Returns a list of valid plies. That is, returns a list of plies that
	 * are allowed in the game in its current state. If there are no plies
	 * allowed in the current state, returns the empty list. 
	 * 
	 * @return empty list if no valid plies, otherwise a list of 
	 * 		 the <code>String</code> representation of the valid plies 
	 * 		 for the current state. The returned list is immutable. That is,
	 * 		 removal and addition of plies is expected to throw an 
	 * 		 <code>Exception</code>. 
	 */
	public synchronized List<String> getValidPlies() {
		List<String> list = new LinkedList<String>(); 

		//Check if the game is terminated 
		//or the time is up. 
		if (terminated || timeStatus != null)
			return list; 

		//Extract the string representation of each ply
		//in game
		for (Ply ply : game.getValidPlies() ) {
			list.add(ply.toString());
		}

		checkRep(); 

		return Collections.unmodifiableList(list);

	}

	/**
	 * Determines whether a Ply is valid or not.
	 * 
	 * @return <code>true</code> if ply is in <code>getValidPlies()</code>
	 *         <code>false</code> otherwise 
	 */
	public synchronized boolean isValid(String ply) {
		checkRep();
		if (ply == null || ply.equals(""))
			return false; 
		
		//Make ply with current board and check if valid
		return (game.isValid(rs.plyFactory().getPly(ply, getBoard()))) != null;
	}

	/**
	 * Become a listener of this.
	 */
	public synchronized void subscribe(GameObserver observer) {
		checkRep(); 

		observers.add(observer);
	}

	/**
	 * Saves the current Game.
	 */
	public synchronized void saveGame(File file) throws IOException {
		if (file == null )
			throw new IOException("Cannot write to the specified file: " + file); 


		if (file.exists()) {
			file.delete();
		}

		file.createNewFile();


		String xmlString = ""; 
		try {


			GameTermination curStatus; 
			if (game.getStatus() == null)
				if (timeStatus == null)
					curStatus = null;
				else //time depleted 
					curStatus = timeStatus;
			else //game has terminated due to a non-time reason
				curStatus = game.getStatus(); 

			xmlString = XmlFactory.gameToXml(rs.toString(), 
					!(initialTimeWhite == UNTIMED && initialTimeBlack == UNTIMED), 
					initialTimeWhite, initialTimeBlack, 
					remainingTime(true), remainingTime(false), 
					times, game.getTurnHistory(), game.getGameHistory(), 
					game.getBoard(), 
					(curStatus == null) ? null : curStatus.winnerIsWhite(),
							(curStatus == null) ? null : curStatus.toString() , 
									rs.getParser());
		} catch (ParserConfigurationException e) {
			throw new IOException("Cannot create XML file"); 
		}


		FileWriter writer = new FileWriter(file);
		if (xmlString == null)
			throw new IOException("Cannot save file for unspecified reasons"); 
		
		writer.write(xmlString); 
		writer.close(); 

		//checkRep(); 
	}


	/**
	 * Describes the next player that should execute a move. That is,
	 * returns the name of the player that is being waited for. 
	 * 
	 * @return <code>null</code> if the game is finished or otherwise
	 * 		   terminated. Returns <code>true</code> if the next player
	 * 		   is white, or <code>false</code> otherwise. 
	 *          
	 */
	public synchronized Boolean isNextWhite() { 
		checkRep(); 
		if (terminated || timeStatus != null) 
			return null; 
		return game.isNextWhite(); 
	}

	/**
	 * Terminates a current <code>Controller</code>. When a Controller 
	 * is terminated, it no longer accepts plys and no longer
	 * notifies players or observers of any event. 
	 * 
	 * @modifies this
	 * @effects Ends the game, not allowing further ply execution 
	 * 			and finalizing the <code>TurnCycle</code>
	 * 
	 *
	 */
	public synchronized void terminate() {
		checkRep(); 

		terminated = true;

		if (cycle != null)
			cycle.interrupt();

		if (timeWhite != null && timeWhite.isAlive())
			timeWhite.interrupt();
		if (timeBlack != null && timeBlack.isAlive())
			timeBlack.interrupt(); 

	}


	/**
	 * Returns the remaining amount of time (in ms) for a
	 * given player. If the player has no time limit,
	 * returns UNTIMED; 
	 * 
	 * @param isWhite <code>true</code> if the player is white, or
	 * 				  <code>false</code> otherwise
	 * 
	 * @return an <code>int</code> representation of the remaining
	 * 		   time in milliseconds for the given player, or UNTIMED
	 * 		   in case the player is playing in unlimited time mode.
	 */
	public synchronized int remainingTime(boolean isWhite) {
		if ((isWhite && (timeWhite == null)) ||
				(!isWhite && (timeBlack == null)))
			return UNTIMED;
		return (isWhite ? timeWhite.remaining() : 
			timeBlack.remaining());
	}


	/**
	 * Initializes the variables when an object has just been constructed.
	 * 
	 * @param ruleSet The <code>RuleSet</code>
	 * @param whitePlayer The <code>Player</code> for the white position. 
	 * @param blackPlayer The <code>Player</code> for the black position.
	 * @param whiteTime The initial time for white player (in ms) 
	 * @param blackTime The initial time for black player (in ms) 
	 * @param observers A list of the observers additional to the GUI 
	 * 
	 * @modifies this
	 * @effects initializes the object for the configurations established
	 * 			in the parameters. 
	 */

	private void initVars(RuleSet ruleSet, Player whitePlayer, Player blackPlayer,
			int whiteTime, int blackTime, GameObserver ... observers) {
		if (ruleSet == null || whitePlayer == null || blackPlayer == null)
			throw new IllegalArgumentException("Null arguments not allowed for Controller"); 

		this.rs = ruleSet;
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
		this.observers = new HashSet<GameObserver>(Arrays.asList(observers));

		this.initialTimeBlack = blackTime;
		this.initialTimeWhite = whiteTime;

		//Add players to the observer list
		this.observers.add(whitePlayer);
		this.observers.add(blackPlayer);


		this.times = new LinkedList<Integer>(); 		

	}



	/**
	 * Executes the received ply in the current <code>Game</code>, 
	 * and updates the objects of type <code>StopWatch</code> associated
	 * with each player. 
	 * 
	 * @param ply The <code>String</code> representation of the ply to be
	 * 			  executed. 
	 * 
	 * @modifies this
	 * @effects Continues the game with the execution of <code>ply</code>.
	 *  		If <code>ply</code> is invalid, or the game has ended 
	 *          (either by time depletion or any other reason), 
	 *          this method does nothing. 
	 * 			
	 */
	private void executePly(String ply) {
		//checkRep(); 
		GameTermination termination = null; 

		//we need to synchronize on this, since this method could 
		//be changing the game (e.g. executing plies in the board, 
		//so if someone called a controller method like "getBoard"
		//we could have a problem. 
		//however we don't synchronize over the whole method, since
		//the inform() bodies could have a call to controller, which
		//would effectiveely deadlock our application. 
		
		synchronized(this) {
			if (terminated) {
				//Game has been terminated by creator 
				return; 
			}

			pauseClocks(); 

			Boolean turn = game.isNextWhite(); 
			if (turn == null || timeStatus != null)  {
				//Game has been ended, either via time depletion or 
				//regular end
				return;
			}


			try {
				game.executePly(rs.plyFactory().getPly(ply, getBoard()));	
			}
			catch (GameTermination term) {
				termination = term;  
			}
			finally {
				times.add(remainingTime(turn));
			}

		}
		
		//OK to run after lock release since 
		//the ply has run. 
		if (termination == null) { //ply success
			inform(ply);
			unpauseClocks(); 	
		}
		else { 
			inform(ply); 
			inform(termination); 
			cycle.interrupt(); 				
		}

		return; 
	}


	/**
	 * Starts the game. In other words, initializes the <code>StopWatch</code>
	 * objects for each player, starts the <code>TimeCycle</code> object 
	 * and starts the timer for the corresponding player. 
	 * 
	 * @modifies this
	 * @effects Initializes a match. 
	 */
	private synchronized void start() {
		cycle = new TurnCycle();

		if (initialTimeWhite != UNTIMED) {
			//start watches
			timeWhite = new StopWatch(initialTimeWhite, 
					new Runnable() {
				public void run() {
					GameTermination term = new GameTermination(false, "time depletion");
					inform(term);
					Controller.this.timeStatus = term;
					Controller.this.cycle.interrupt();
				}});
			timeWhite.start();
			timeWhite.pause(); 
		}
		if (initialTimeBlack != UNTIMED) {
			timeBlack = new StopWatch(initialTimeBlack, 
					new Runnable() {
				public void run() {
					GameTermination term = 
						new GameTermination(true, "time depletion");
					inform(term);
					Controller.this.timeStatus = term;
					Controller.this.cycle.interrupt();

				}});
			timeBlack.start();
			timeBlack.pause(); 
		}

		unpauseClocks(); 
		cycle.start(); 
	}

	/**
	 * Restarts the <code>StopWatch</code> corresponding to the player who is in 
	 * turn of executing the next move. 
	 * If the time for that particular player is unlimited, then
	 * the method does not do anything. 
	 * 
	 * @modifies this
	 * @effects resumes the game timing. 
	 * 
	 */
	private synchronized void unpauseClocks(){
		if (game.isNextWhite()) {
			if (timeWhite != null)
				timeWhite.proceed();
		}
		else { // next player is black 
			if (timeBlack != null)
				timeBlack.proceed(); 
		}

	}

	/**
	 * Pauses the <code>StopWatch</code> corresponding to the player who is in 
	 * turn of executing the next move. 
	 * If the time for that particular player is unlimited, then
	 * the method does not do anything. 
	 * 
	 * @modifies this
	 * @effects pauses the game timing. 
	 * 
	 */
	private synchronized void pauseClocks() {
		if (game.isNextWhite()) {
			if (timeWhite != null)
				timeWhite.pause();
		}
		else { // next player is black 
			if (timeBlack != null)
				timeBlack.pause(); 
		}


	}

	/**
	 * Informs all the <code>GameObservers</code> in the observer queue 
	 * that a <code>Ply</code> has occured. 
	 * 
	 * A new Thread is not spawned for each object informed of the message. 
	 * For that reason, a <code>GameObserver</code> will be responsible 
	 * for finishing the thread early, or spawning a new thread
	 * if it needs lengthy processing. In other words, once the 
	 * <code>GameObserver</code>'s <code>inform(String msg)</code>
	 * finishes, the queue will proceed the notification cycle. 
	 * 
	 * @param message the <code>String</code> to be broadcast. This 
	 * 					<code>String</code> corresponds to a particular
	 * 					<code>Ply</code> representation. 
	 * 
	 * @modifies this
	 * @effects Each observer is informed of the ply, so that 
	 * 			they can update their states accordingly. 
	 */
	private void inform(final String message) {
		for (final GameObserver observer : observers) {
			observer.inform(message);	
		}
	}

	/**
	 * Informs all the <code>GameObservers</code> in the observer queue 
	 * that a <code>GameTermination</code> has occured. 
	 * 
	 * A new Thread is not spawned for each object informed of the message. 
	 * For that reason, a <code>GameObserver</code> will be responsible 
	 * for finishing the thread early, or spawning a new thread
	 * if it needs lengthy processing. In other words, once the 
	 * <code>GameObserver</code>'s <code>inform(String msg)</code>
	 * finishes, the queue will proceed the notification cycle. 
	 * 
	 * 
	 * @param message the <code>GameTermination</code> to be broadcast.
	 * 
	 * @modifies this
	 * @effects Each observer is informed of the game termination, so that 
	 * 			they can update their states accordingly. 

	 */
	private void inform(final GameTermination message) {
		for (final GameObserver observer : observers)
//			new Thread("Termination-informer") {
//			public void run() {
			observer.inform(message);
//		}
//		}.start(); 
	}


	/**
	 * <p><code>TurnCycle</code> represents an iterating turn directing facility. 
	 * That is, <code>TurnCycle</code> is a <code>Thread</code> that runs 
	 * constantly during the duration of a game, delegating turns to the appropriate 
	 * player and waiting for its answer at all times. </p>
	 * 
	 * <p>A <code>TurnCycle</code> will stop its loop as soon as it is 
	 * interrupted</p>.
	 * 
	 *
	 */
	class TurnCycle extends Thread {
		/**
		 * Constantly queries the next player (as specified by 
		 * Controller.nextPlayerWhite()) for the next turn. 
		 * 
		 * Upon interruption, this thread stops. 
		 */
		public void run() {
			while (! isInterrupted()) {

				Boolean nextTurnWhite = Controller.this.isNextWhite(); 

				if (nextTurnWhite == null) {
					//No one's next
					return;
				} else if (nextTurnWhite) {
					//white player's turn
					String response;
					try {
						response = Controller.this.whitePlayer.submitPly();

						if (Controller.this.isValid(response))
							Controller.this.executePly(response);

					} catch (InterruptedException e) {
						return; 
					} 
				} else {
					//black player's turn
					String response = null; 
					try {
						response = Controller.this.blackPlayer.submitPly(); 
						if (Controller.this.isValid(response))
							Controller.this.executePly(response);

					} catch (InterruptedException ie) {
						return; 
					}
				}				
			}
		}
	}
}


