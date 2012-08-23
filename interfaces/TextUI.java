package interfaces;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.xml.parsers.ParserConfigurationException;

import player.TextAIPlayer;
import controller.Controller;
import controller.XmlFactory;
import engine.adt.*;
import engine.game.Game;
import engine.game.GameTermination;
import ruleset.antichess.EnCastleAC;
import ruleset.antichess.StandardAC;
import ruleset.board.RectangularBoard;
import ruleset.connectn.ConnectNRuleSet;

/**
 * A TextUI is an interactive text interface to play AntiChess.
 *
 */
public class TextUI {

	/**
	 * Main is an interactive loop between a human and a computer. The TextUI 
	 * should wait for user input. Valid user input is in the form of the 
	 * commands shown in the requires clause. Parameters to commands are 
	 * enclosed in square brackets. Outputs as a result of a command should be 
	 * terminated with a newline. If multiple lines are output, the last line 
	 * should also be terminated with a new line.
	 * 
	 * <p> The TextUI can also be run with an additional argument, "debug", that
	 * will make it run in debug mode. In debug mode, it will echo any inputs
	 * that start with '#' so that test descriptions can be added. Also, a more
	 * comprehensive representation of the board is printed when the command
	 * PrintBoard is called, in order to make it easier to visualize.
	 * <p>
	 * When main is executed with no command-line arguments, supported commands
	 * are:
	 * <p>
	 * StartNewGame <code>[player] [time] [player] [time]</code> :
	 * Each player is denoted as "human" or "computer." The times specified are
	 * the initial "time remaining" for the player, in milliseconds. If a time 
	 * argument is zero, then the playing time is unlimited. The first player is
	 * white and the second player is black. For example, StartNewGame computer 
	 * 60000 human 180000 should start a new game with the computer playing as 
	 * white and the human playing as black. The computer will have 1 minute to 
	 * make all of its moves, and the human will have 3 minutes. The system 
	 * should output New game started on its own line. TODO mention optional argument
	 * <p>
	 * SaveGame <code>[filename]</code> :
	 * The system should save the game to the given filename and report Game 
	 * saved on its own line. TODO what happens after its ended? Per specification,
	 * only need to support a subset of commands; meaning game cannot be saved after
	 * it has ended.
	 * <p>
	 * LoadGame <code>[filename]</code> :
	 * The system should load the game from the given filename. Once the files 
	 * is loaded, print Game loaded on its own line. You should report Corrupt 
	 * file if the file does not have a correct format. We will not require you 
	 * to determine if the board is legal. If no game is currently in progress 
	 * from a previously executed StartNewGame or LoadGame command, then assume 
	 * a human-human game.
	 * <p>
	 * GetNextMove :
	 * If this command is called during a human player's turn, the command 
	 * prints Human turn on its own line. If this command is called during a 
	 * machine player's turn, print on its own line the next move it believes to 
	 * be the best. The printed move should be in the "standard string format" 
	 * described in the assignment. The time taken to compute the move should be 
	 * subtracted from the computer player's game clock. If called repeatedly, 
	 * this should return the same move over and over without further 
	 * decrementing the computer's time remaining.
	 * <p>
	 * MakeNextMove :
	 * If it is a human player's turn, the system should print Please specify 
	 * human move on its own line. If it is the computer player's turn, and 
	 * GetNextMove has not yet been called on this turn, then the system 
	 * should print First GetNextMove. Otherwise, the system performs the 
	 * move that GetNextMove would return. 
	 * <p>
	 * MakeMove <code>[move] [time]</code> :
	 * Perform the move specified by the move string, in the "standard string 
	 * format" described in the assignment. The time parameter is specified in 
	 * milliseconds. This command should only be used by a Human Player. If it 
	 * is used during a computer player's turn, nothing will happen to the game 
	 * state and no response should be printed. If the move is not legal, the 
	 * system should print, on its own line, Illegal move and not perform the 
	 * move. If the move is legal, the system should perform the move, decrement
	 * the player's time by the amount given, and print the move performed, in 
	 * proper format, on its own line. If the player's time is unlimited, then 
	 * the time argument is ignored (but must still be present).
	 * <p>
	 * PrintBoard :
	 * System should print the current "state" of the game to the screen using 
	 * the same format as if it were being saved to a file. The output should 
	 * end with (at least one) a newline. // TODO other printing
	 * <p>
	 * IsLegalMove <code>[move]</code> :
	 * System should print, on its own line, either "legal" or "illegal" to 
	 * specify if the move is a legal next move.
	 * <p>
	 * PrintAllMoves :
	 * System should, in alphanumeric order, print all legal moves for the next 
	 * player. Each move should each appear on its own line.
	 * <p>
	 * GetTime <code>[player]</code> :
	 * On its own line, the system should print the time remaining in 
	 * milliseconds for the player specified. For example GetTime white, should 
	 * print 3000 to indicate 3 seconds left for the white player. If the time 
	 * for the player is unlimited, the system should print "unlimited".
	 * <p>
	 * QuitGame :
	 * Prints (on its own line) Exiting game and terminates the present game 
	 * and application. QuitGame cannot be the first command.
	 * <p>
	 * For each command other than GetNextMove the specified behavior completes 
	 * within 10 seconds. GetNextMove may take no more than ten seconds more 
	 * than the player's time remaining to complete; if it exceeds the player's 
	 * time remaining it must report a human victory in the format described 
	 * below.
	 * <p>
	 * If the user input does not match one of these commands, output Input 
	 * error alone on one line. Also, the first valid command entered must be 
	 * either StartNewGame or LoadGame, or else Input error is printed.
	 * <p>
	 * TODO say when these messages are thrown.
	 * When a player has won the game, output on its own line: [Player color] 
	 * Player has won. For example, if the black player has won, output: Black 
	 * Player has won. At this point, you can assume that the Antichess program 
	 * has just been started and therefore, you only need to support the 
	 * subset of commands.
	 * <p>
	 * The behavior of the TextUI is unspecified when main is run with one or 
	 * more command-line arguments.
	 */
	public static void main(String[] args) {

		try {	
			if (args.length > 1) {
				//printUsage();
				return;
			}

			TextUI tui;

			if (args.length == 0) {
				tui = new TextUI(new InputStreamReader(System.in), 
						new OutputStreamWriter(System.out));
			} else {
				if (args.length!=1) {
					System.err.println("Invalid arguments");
					return;
				} else {
					String arg = args[0];
					if (arg.equals("debug"))
						tui = new TextUI(new InputStreamReader(System.in),
								new OutputStreamWriter(System.out), true);
					else {
						System.err.println("Invalid arguments");
						return;
					}
				}
			}

			tui.interactiveLoop();

		}  catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		}
	}

	private static final String INPUT_ERROR = "Input error";
	private static final String HUMAN = "human";
	private static final String COMPUTER = "computer";
	private static final String STANDARD = "6170-spring-2007";
	private static final String ENCASTLE = "6170-spring-2007-encastle";
	private static final List<String> RULESETS = Arrays.asList(STANDARD, ENCASTLE);
	private static final String TIME_DEPLETION = "time depletion";
	private static final String INIT_BOARD_TAG = "init-board";
	private static final String MOVE_HISTORY  = "moveHistory";

	/**
	 * If debug is true, echoes all lines starting with '#'. Also, the AiPlayer
	 * the TextUI uses is a <code>DeterministicAiPlayer</code> that allows
	 * easy testing.
	 */
	private final boolean debug;

	private final PrintWriter output;
	private final BufferedReader input;

	/**
	 * The RuleSet being used. For this TextUI, we assume we play Standard
	 * Antichess.
	 */
	private RuleSet rs;

	/**
	 * Determines whether a Game is currently in progress.
	 */
	private boolean inGame;

	/**
	 * Game being played. If not currently playing a game, null.
	 */
	private Game game;

	/**
	 * The white player.
	 */
	private String whitePlayer;

	/**
	 * Whether white player is being timed or not.
	 */
	private boolean whiteTimed;

	/**
	 * Initial time for the white player, if its being timed.
	 */
	private int whiteInitialTime;

	/**
	 * White's remaining time. If white is not being timed, this is -1.
	 */
	private int whiteTime;

	/**
	 * The black player.
	 */
	private String blackPlayer;

	/**
	 * Whether black player is being timed or not.
	 */
	private boolean blackTimed;

	/**
	 * Initial time for the black player, if its being timed.
	 */
	private int blackInitialTime;

	/**
	 * Black's remaining time. If black is not being timed, this is -1.
	 */
	private int blackTime;

	/**
	 * Holds the time history for the game being played.
	 */
	private List<Integer> timeHistory;

	/**
	 * Determines whether the next AI player move has been set or not.
	 */
	private boolean moveAlreadySet;

	/**
	 * Holds the next move of the AI player.
	 */
	private String nextMove;
//	private int gameMode;

	/**
	 * @requires r != null && w != null
	 *
	 * @effects Creates a new TextUI which reads command from
	 * <tt>r</tt> and writes results to <tt>w</tt>.
	 **/
	public TextUI(Reader r, Writer w, boolean ... debug) {
		input = new BufferedReader(r);
		output = new PrintWriter(w);
		if (debug.length!=0) {
			this.debug = debug[0];
		} else
			this.debug = false;
		cleanUp();
	}


	/**
	 * Starts the interactive loop with the user that reads commands from the command line
	 * 
	 * @effects Executes the commands read from the input and writes results to the output
	 * @throws IOException if the input or output sources encounter an IOException
	 * 
	 */
	public void interactiveLoop() throws IOException {
		String inputLine;
		while ((inputLine = input.readLine()) != null) {
			if (debug && (inputLine.trim().length() == 0 ||
					inputLine.charAt(0) == '#')) {
				// echo blank and comment lines
				output.println(inputLine);
				output.flush();
				continue;
			}
			// separate the input line on white space
			StringTokenizer st = new StringTokenizer(inputLine);
			if (st.hasMoreTokens()) {
				String command = st.nextToken();

				List<String> arguments = new ArrayList<String>();
				while (st.hasMoreTokens()) {
					arguments.add(st.nextToken());
				}
				executeCommand(command, arguments);
			}	            
		}
		output.flush();
	}

	/**
	 * 
	 * @param command
	 * @param arguments
	 */
	private void executeCommand(String command, List<String> arguments) {
		if (inGame) {
			try {
				if (command.equals("StartNewGame")) {
					startNewGame(arguments);
				} else if (command.equals("SaveGame")) {
					saveGame(arguments);
				} else if (command.equals("LoadGame")) {
					loadGame(arguments);
				} else if (command.equals("GetNextMove")) {
					getNextMove(arguments); 
				} else if (command.equals("MakeNextMove")) {
					makeNextMove(arguments);
				} else if (command.equals("MakeMove")) {
					makeMove(arguments);
				} else if (command.equals("PrintBoard")) {
					printBoard(arguments);
				} else if (command.equals("IsLegalMove")) {
					isLegalMove(arguments);
				} else if (command.equals("PrintAllMoves")) {
					printAllMoves(arguments);
				} else if (command.equals("GetTime")) {
					getTime(arguments);
				} else if (command.equals("QuitGame")) {
					quitGame(arguments);
				} else {
					throw new CommandException();
				}
			} catch (CommandException e) {
				output.println(INPUT_ERROR);
				output.flush(); 
			}
		} else {
			try {
				if (command.equals("StartNewGame")) {
					startNewGame(arguments);
				} else if (command.equals("LoadGame")) {
					loadGame(arguments);
				} else {
					throw new CommandException();
				}
			} catch (CommandException e) {
				output.println(INPUT_ERROR);
				output.flush(); 
			}
		}
	}

	/**
	 * 
	 */
	private void startNewGame(List<String> arguments) {

		// Error handling begins
		if (!(arguments.size() == 4 || arguments.size() == 5)) {
			throw new CommandException();
		}
		int time1;
		int time2;
		try {
			time1 = Integer.valueOf(arguments.get(1));
			time2 = Integer.valueOf(arguments.get(3));
			if ( (time1 < 0)  || (time2 < 0)) {
				throw new CommandException();
			}
		} catch (NumberFormatException e) {
			throw new CommandException();
		}

		String white = arguments.get(0);
		String black = arguments.get(2);
		if (!(white.equals(HUMAN) || white.equals(COMPUTER)) ||
				!(black.equals(HUMAN) || black.equals(COMPUTER)))
			throw new CommandException();

		String ruleSet;
		if (arguments.size()==5)
			ruleSet = arguments.get(4);
		else
			ruleSet = STANDARD;
		if (!RULESETS.contains(ruleSet))
			throw new CommandException();
		// Error handling ends

		// Reset variables for new game
		cleanUp();

		// Set players
		whitePlayer = white;
		blackPlayer = black;

		// Set up timers and initial times
		timeHistory = new LinkedList<Integer>();
		whiteTime = time1;
		whiteInitialTime = time1;
		if (time1==0)
			whiteTimed = false;
		else
			whiteTimed = true;
		blackTime = time2;
		blackInitialTime = time2;
		if (time2==0)
			blackTimed = false;
		else
			blackTimed = true;

		// Set ruleset
		if (ruleSet.equals(STANDARD))
			rs = new StandardAC();
		else if (ruleSet.equals(ENCASTLE))
			rs = new EnCastleAC();

		// Create game
		game = new Game(rs);
		inGame = true;

		output.println("New game started");
		output.flush();
	}

	/**
	 * Sets up all the variables as if they were new.
	 */
	private void cleanUp() {
		rs = null;
		inGame = false;
		game = null;
		whitePlayer = null;
		whiteTimed = false;
		whiteInitialTime = -1;
		whiteTime = -1;
		blackPlayer = null;
		blackTimed = false;
		blackInitialTime = -1;
		blackTime = -1;
		timeHistory = null;
		moveAlreadySet = false;
		nextMove = null;
	}

	/**
	 * 
	 * @param arguments
	 */
	private void saveGame(List<String> arguments) {

		// Error handling
		if (arguments.size() != 1){
			throw new CommandException();
		}

		// Get filename and create File
		String fileName = arguments.get(0);
		File file = new File(fileName);

		try {
			if (file == null)
				throw new IOException("Cannot write to the specified file: " + file);

			// Create the new file
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			// Start building XML string
			String xmlString = ""; 
			try {
				xmlString = XmlFactory.gameToXml(rs.toString(), 
						(whiteTimed && blackTimed),
						whiteInitialTime, blackInitialTime,
						whiteTime, blackTime,
						timeHistory, game.getTurnHistory(), game.getGameHistory(), 
						game.getBoard(), null, null, rs.getParser());
			} catch (ParserConfigurationException e) {
				throw new IOException("Cannot create XML file");
			}

			FileWriter writer = new FileWriter(file);
			if (xmlString == null)
				throw new IOException("Cannot save file for unspecified reasons"); 

			writer.write(xmlString); 
			writer.close();
			output.println("Game saved");
		} catch (Exception e) {
			output.println("Failed to save game: " + e.getMessage());
		}
		output.flush();
	}

	private void loadGame(List<String> arguments) {

		// Error handling
		if (arguments.size() != 1){
			throw new CommandException();
		}
		String fileName = arguments.get(0);
		
		// Create URL to schema
		URL schema = null;
		try {
			schema = new URL(XmlFactory.URL_ADDRESS);
		} catch (MalformedURLException mue) {}
		
		// Validate file to schema
		if (XmlFactory.validate(fileName, schema)) {
			
			File toLoad = new File(fileName);

			String[] initialTimes = null;
			String[] currentTimes = null;
			String ruleSet = null;

			try {

				initialTimes = XmlFactory.xmlToTimes(toLoad, true);
				currentTimes = XmlFactory.xmlToTimes(toLoad, false);
				ruleSet = XmlFactory.xmlToRuleSet(toLoad);

			} catch (Exception e) {
				output.println("Corrupt file");
			}
			
			try {
				// Set all the variables
				if (initialTimes[0].equals(String.valueOf(Controller.UNTIMED)))
					whiteInitialTime = Integer.valueOf(initialTimes[0]);
				if (initialTimes[1].equals(String.valueOf(Controller.UNTIMED)))
					blackInitialTime = Integer.valueOf(initialTimes[1]);
				if (whiteInitialTime!=Controller.UNTIMED || blackInitialTime!=Controller.UNTIMED) {
					whiteTime = Integer.valueOf(currentTimes[0]);
					blackTime = Integer.valueOf(currentTimes[1]);
				}
				if (ruleSet.equals(STANDARD))
					rs = new StandardAC();
				else if (ruleSet.equals(ENCASTLE))
					rs = new EnCastleAC();
				else if (ruleSet.startsWith("connect")) {
					rs = new ConnectNRuleSet(Integer.valueOf(ruleSet.split("-")[1]));
				}
				if (whitePlayer == null || blackPlayer == null) {
					whitePlayer = HUMAN;
					blackPlayer = HUMAN;
				}
				inGame = true;
			} catch (Exception e) {
				output.println("Corrupt file");
			}

			// Load the board from XML file initial conditions 		
			String boardDescription = XmlFactory.getTag(INIT_BOARD_TAG, toLoad); 

			// Load game with initial board
			if (boardDescription.equals(""))
				game = new Game(rs);
			else 
				game = new Game(rs, rs.boardFactory().getBoard(boardDescription));

			// assemble plies to send to Game
			List<String[]> history = XmlFactory.xmlToHistory(
					XmlFactory.getTag(MOVE_HISTORY, toLoad));
			List<String> plies = new LinkedList<String>();
			List<Boolean> turns = new LinkedList<Boolean>();
			timeHistory = new LinkedList<Integer>();
			for (String[] item : history) {
				//white c2-c3 299000
				turns.add(item[0].equals("white")); 
				plies.add(item[1]); 
				timeHistory.add(Integer.parseInt(item[2]));
			}

			try {
				if (! plies.isEmpty())
					game.executePlies(plies, turns);
			}
			catch (GameTermination gt) {
				terminateGame(gt);
			}
			output.println("Game loaded");
		} else {
			output.println("Corrupt file");
		}
		output.flush();
	}

	/**
	 * 
	 *
	 */
	private void getNextMove(List<String> arguments) {

		// Error handling
		if (arguments.size() != 0) {
			throw new CommandException();
		}

		if (isHumanTurn()) {
			output.println("Human turn");
		} else {
			if (moveAlreadySet) {
				output.println(nextMove);
			} else {
				if (game.isNextWhite() ? whiteTimed : blackTimed) {
					long before = System.currentTimeMillis();
					nextMove = TextAIPlayer.getNextMove(game.getValidPlies());
					moveAlreadySet = true;
					long after = System.currentTimeMillis();
					if (game.isNextWhite()) {
						whiteTime -= (after - before);
						if (whiteTime <= 0) {
							terminateGame(new GameTermination(false, TIME_DEPLETION)); // black wins
							return;
						}
					} else {
						blackTime -= (after - before);
						if (blackTime <= 0) {
							terminateGame(new GameTermination(true, TIME_DEPLETION)); // white wins
							return;
						}
					}
					timeHistory.add(game.isNextWhite() ? whiteTime : blackTime);
					output.println(nextMove);
				} else {
					nextMove = TextAIPlayer.getNextMove(game.getValidPlies());
					moveAlreadySet = true;
					output.println(nextMove);
				}
			}
		}
		output.flush();
	}

	/**
	 * Determines whether the current turn is the human's or not.
	 * @return true if its the human's turn and false if it's the computer's
	 * turn.
	 */
	private boolean isHumanTurn() {
		return ((game.isNextWhite() && whitePlayer.equals(HUMAN)) ||
				(!game.isNextWhite() && blackPlayer.equals(HUMAN)));
	}

	/**
	 * 
	 *
	 */
	private void makeNextMove(List<String> arguments) {

		// Error handling
		if (arguments.size() != 0){
			throw new CommandException();
		}

		if (isHumanTurn()) {
			output.println("Please specify human move");
		} else {
			if (moveAlreadySet) {
				try {
					output.println(nextMove);
					game.executePly(rs.plyFactory().getPly(nextMove, game.getBoard()));
					moveAlreadySet = false;
					nextMove = null;
				} catch (GameTermination gt) {
					terminateGame(gt);
					return;
				}
			} else {
				output.println("First GetNextMove");
			}
		}
		output.flush();
	}

	/**
	 * 
	 * @param arguments
	 */
	private void makeMove(List<String> arguments) {

		// Error handling
		if (arguments.size() != 2){
			throw new CommandException();
		}
		int time;
		try {
			time = Integer.valueOf(arguments.get(1));
			if (time < 0) {
				throw new CommandException();
			}		            
		} catch (NumberFormatException e){
			throw new CommandException();
		}
		String move = arguments.get(0);

		if (isHumanTurn()) {
			if (game.isValid(rs.plyFactory().getPly(move, game.getBoard()))==null)
				output.println("Illegal move");
			else {
				// Check time depletion
				if (whiteTimed && game.isNextWhite()) {
					whiteTime -= time;
					if (whiteTime<=0) {
						terminateGame(new GameTermination(false, TIME_DEPLETION)); // black wins
						return;
					}
				} else if (blackTimed && !game.isNextWhite()) {
					blackTime -= time;
					if (blackTime <= 0) {
						terminateGame(new GameTermination(true, TIME_DEPLETION)); // white wins
						return;
					}
				}
				timeHistory.add((game.isNextWhite()? whiteTime : blackTime));
				try {
					output.println(move);
					game.executePly(rs.plyFactory().getPly(move, game.getBoard()));
				} catch (GameTermination gt) {
					terminateGame(gt);
					return;
				}
			}
		} // else do nothing
		output.flush();
	}

	/**
	 * 
	 *
	 */
	private void printBoard(List<String> arguments) {

		// Error handling
		if (arguments.size() != 0){
			throw new CommandException();
		}

		if (debug) {
			// Print a nice representation of a board, to make debuggin easier
			printBoardDebug();
		} else {
			output.print(XmlFactory.boardToXml(game.getBoard(), rs.getParser()));
		}
		output.flush();
	}

	/**
	 * 
	 * @param arguments
	 */
	private void isLegalMove(List<String> arguments) {

		// Error handling
		if (arguments.size() != 1){
			throw new CommandException();
		}
		String move = arguments.get(0);

		if (game.isValid(rs.plyFactory().getPly(move,game.getBoard()))!=null) {
			output.println("legal");
		} else {
			output.println("illegal");
		}
		output.flush();
	}

	/**
	 * 
	 *
	 */
	private void printAllMoves(List<String> arguments) {

		// Error handling
		if (arguments.size() != 0){
			throw new CommandException();
		}

		List<Ply> validPlies = game.getValidPlies();
		List<String> plies = new LinkedList<String>();
		for (Ply ply : validPlies)
			plies.add(ply.toString());
		Collections.sort(plies);
		for (String ply : plies) {
			output.println(ply);
		}
		output.flush();
	}

	/**
	 * 
	 * @param arguments
	 */
	private void getTime(List<String> arguments) {

		// Error handling
		if (arguments.size() != 1){
			throw new CommandException();
		}
		String player = arguments.get(0);


		if (player.equals("white")) {
			if (whiteTimed)
				output.println(whiteTime);
			else
				output.println("unlimited");
		} else if (player.equals("black")) {
			if (blackTimed)
				output.println(blackTime);
			else
				output.println("unlimited");
		} else {
			throw new CommandException();
		}
		output.flush();
	}

	/**
	 * 
	 *
	 */
	private void quitGame(List<String> arguments) {

		// Error handling
		if (arguments.size() != 0){
			throw new CommandException();
		}

		output.println("Exiting game");
		output.flush();
		System.exit(0);
	}

	/**
	 * Per specification, when the game ends, the user may only enter the
	 * StartNewGame and LoadGame commands. It is as if there was no game
	 * being played anymore.
	 * @param gt
	 */
	private void terminateGame(GameTermination gt) {
		cleanUp();
		output.println((gt.winnerIsWhite() ? "White" : "Black") + 
		" Player has won");
		output.flush();
	}


	/**
	 * This exception results when the input file cannot be parsed properly
	 **/
	static class CommandException extends RuntimeException {

		public CommandException() {
			super();
		}
		public CommandException(String s) {
			super(s);
		}

		public static final long serialVersionUID = 3495;
	}

	private void printBoardDebug() {
		RectangularBoard board = (RectangularBoard) game.getBoard();
		for (int height = board.getHeight() - 1; height >= 0; height--) {
			for (int length = 0; length < board.getLength(); length++) {

				int[] coord = new int[] {length, height};

				String string;
				if (! board.isUsable(coord))
					string = "x";
				else if (board.isEmpty(coord)) 
					string = "o";
				else {
					Piece pc = board.getPiece(coord);
					string = getName(pc.toString());
					if (!pc.isWhite())
						string += "*";
				}
				output.printf("%5s", string); 
			}
			output.println("");
			output.flush(); 
		}
	}

	private String getName(String pieceName) {
		String name = "";
		if (pieceName.equals("pawn"))
			name = "p";
		else if (pieceName.equals("rook")) 
			name = "r";
		else if (pieceName.equals("knight"))
			name = "n";
		else if (pieceName.equals("bishop"))
			name = "b";
		else if (pieceName.equals("queen"))
			name = "q";
		else if (pieceName.equals("king"))
			name = "k";
		return name;
	}

}
