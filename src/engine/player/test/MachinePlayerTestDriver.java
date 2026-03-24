package engine.player.test;

import ruleset.antichess.EnCastleAC;
import ruleset.antichess.StandardAC;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import player.MachinePlayer;

import engine.adt.RuleSet;

import net.antichess.ai.AiPlayer;

/**
 * Used to simulate a referee so that MachinePlayer can be tested.
 */
public class MachinePlayerTestDriver {

	public static void main(String[] args) {
		try {
			MachinePlayerTestDriver td;

			if (args.length == 0) {
				td = new MachinePlayerTestDriver(new InputStreamReader(System.in), 
						new OutputStreamWriter(System.out));
			} else {
				System.err.println("Invalid arguments");
				return;
			}

			td.runTest();

		}  catch (IOException e) {
			System.err.println(e.toString());
			e.printStackTrace(System.err);
		}
	}

	private static final String INPUT_ERROR = "Input error";
//	private static final String differentBoard = "<pieces>" +
//	"<square id=\"h3\" piece=\"king\" side=\"white\"/>\n" +
//	"<square id=\"a4\" piece=\"pawn\" side=\"white\"/>\n" +
//	"<square id=\"f1\" piece=\"king\" side=\"black\"/>\n" +
//	"<square id=\"a5\" piece=\"pawn\" side=\"black\"/>\n" +
//	"</pieces>";
//	private static final String staleMateBoard = "<pieces>" +
//	"<square id=\"h3\" piece=\"king\" side=\"white\"/>\n" +
//	"<square id=\"e3\" piece=\"queen\" side=\"white\"/>\n" +
//	"<square id=\"a4\" piece=\"pawn\" side=\"white\"/>\n" +
//	"<square id=\"f1\" piece=\"king\" side=\"black\"/>\n" +
//	"<square id=\"a5\" piece=\"pawn\" side=\"black\"/>\n" +
//	"</pieces>";


	private final PrintWriter output;
	private final BufferedReader input;
	private AiPlayer player = null;
	private RuleSet rs;

	/**
	 * @requires r != null && w != null
	 *
	 * @effects Creates a new TextUI which reads command from
	 * <tt>r</tt> and writes results to <tt>w</tt>.
	 **/
	public MachinePlayerTestDriver(Reader r, Writer w) {
		input = new BufferedReader(r);
		output = new PrintWriter(w);	    	
	}


	/**
	 * Starts the interactive loop with the user that reads commands from the command line
	 * 
	 * @effects Executes the commands read from the input and writes results to the output
	 * @throws IOException if the input or output sources encounter an IOException
	 * 
	 */
	public void runTest() throws IOException {
		String inputLine;
		while ((inputLine = input.readLine()) != null) {

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
		try {
			if (command.equals("CreateMachinePlayer")) {
				createMachinePlayer(arguments);
			} else if (command.equals("KillMachinePlayer")) {
				killMachinePlayer(arguments);
			} else if (command.equals("MakeMove")) {
				getMove(arguments);
			} else {
				throw new CommandException();
			}
		} catch (CommandException e) {
			output.println(INPUT_ERROR);
			output.flush(); 
		}
	}


	/**
	 * 
	 * @param arguments
	 */
	private void createMachinePlayer(List<String> arguments) {

		// Error handling
		if (arguments.size() != 2)
			throw new CommandException();

		boolean isWhite;
		String color = arguments.get(0);
		if (color.equals("white"))
			isWhite = true;
		else if (color.equals("black"))
			isWhite = false;
		else
			throw new CommandException();

		String ruleSet = arguments.get(1);
		if (ruleSet.equals("Standard"))
			rs = new StandardAC();
		else if (ruleSet.equals("EnCastle"))
			rs = new EnCastleAC();
		else
			throw new CommandException();
		
		if (player == null) {
			player = new MachinePlayer(isWhite, rs, false, 0, 0, new DeterministicAiPlayer(), null);
			output.println("Created " + color + " MachinePlayer. You (opponent) " +
					"play as " + (isWhite ? "black" : "white"));
		} else {
			output.println("A MachinePlayer already exists. Please kill it first.");
		}
		output.flush();
	}

	private void killMachinePlayer(List<String> arguments) {

		// Error handling
		if (arguments.size() != 0)
			throw new CommandException();

		if (player==null) {
			output.println("There is no MachinePlayer to kill.");
		} else {
			output.println("MachinePlayer was killed.");
			player = null;
		}
		output.flush();
	}

	/**
	 * 
	 * @param arguments
	 */
	private void getMove(List<String> arguments) {

		if (arguments.size() != 1){
			throw new CommandException();
		}
		
		String move = arguments.get(0);
		if (move.equals("empty"))
			move = "";

		if (player == null) {
			output.println("There is not MachinePlayer to ask new move to.");
		} else {
			String playersMove = player.getMove(move, 0, 0);
			output.println(playersMove);
		}
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

}
