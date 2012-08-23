package ruleset.board.test;

import ruleset.antichess.StandardAC;
import ruleset.board.CoordinateParser;
import ruleset.board.RectangularBoard;

import java.io.*;
import java.util.*;

import engine.adt.Piece;
import engine.adt.RuleSet;
import engine.adt.RuleSet.PieceFactory;
import engine.adt.RuleSet.PlyFactory;


/**
 * This class implements a testing driver which reads test scripts
 * from files for testing CoordinateParser.
 **/

public class RectangularBoardTester {

    public static void main(String args[]) {
        try {
            if (args.length > 1) {
                printUsage();
                return;
            }
      
            RectangularBoardTester td;
      
            if (args.length == 0) {
                td = new RectangularBoardTester(new InputStreamReader(System.in),
                                              new OutputStreamWriter(System.out));
            } else {
  
                String fileName = args[0];
                File tests = new File (fileName);
  
                if (tests.exists() || tests.canRead()) {
                    td = new RectangularBoardTester(new FileReader(tests),
                                           new OutputStreamWriter(System.out));
                } else {
                    System.err.println("Cannot read from " + tests.toString());
                    printUsage();
                    return;
                }
            }

            td.runTests();

        } catch (IOException e) {
            System.err.println(e.toString());
            e.printStackTrace(System.err);
        }
    }

    private static void printUsage() {
        System.err.println("Usage:");
        System.err.println("to read from a file: java ruleset.test.RectangularBoardTester" +
        		"<name of input script>");
        System.err.println("to read from standard in: java ruleset.test.RectangularBoardTester");
    }


    private final Map<String, RectangularBoard> boards = new HashMap<String, RectangularBoard>();
    private final Map<Piece, String> pieceNames = new HashMap<Piece, String>();
 
    private final PrintWriter output;
    private final BufferedReader input;

    /**
     * @requires r != null && w != null
     *
     * @effects Creates a new RectangularBoardTester which reads command from
     * <tt>r</tt> and writes results to <tt>w</tt>.
     **/
    public RectangularBoardTester(Reader r, Writer w) {
        input = new BufferedReader(r);
        output = new PrintWriter(w);
    }

    /**
     * @effects Executes the commands read from the input and writes results to the output
     * @throws IOException if the input or output sources encounter an IOException
     **/
    public void runTests()
        throws IOException
    {
        String inputLine;
        while ((inputLine = input.readLine()) != null) {
            if (inputLine.trim().length() == 0 ||
                inputLine.charAt(0) == '#') {
                // echo blank and comment lines
                output.println(inputLine);
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

    private void executeCommand(String command, List<String> arguments) {
        try {
            if (command.equals("CreateBoard")) {
                createBoard(arguments);
            } else if (command.equals("CreatePiece")) {
                createPiece(arguments);
            } else if (command.equals("PrintBoard")) {
                printBoard(arguments);
            } else if (command.equals("MakePly")) {
            	makePly(arguments); 
            } else if (command.equals("Info")) {
            	info(arguments);
            } else {
                output.println("Unrecognized command: " + command);
            }
        } catch (Exception e) {
            output.println("Exception: " + e.toString());
            output.flush(); 
        }
    }

    
    //Command of the form 
    //CreateBoard name height length blocked1 blocked2 ...
    private void createBoard(List<String> arguments) {
        if (arguments.size() < 3) {
            throw new CommandException("Bad arguments to CreateBoard: " + arguments);
        }

        String boardName = arguments.get(0);
        String length = arguments.get(1); 
        String height = arguments.get(2);
        
        
        List<String> blocked = new ArrayList<String>(arguments);
        for (int i = 0 ; i < 3; i++) 
        	blocked.remove(0);
        
        
        try {
        	createBoard(boardName, Integer.parseInt(length), 
        			Integer.parseInt(height), blocked);
        }
        catch (NumberFormatException nfe) {
        	throw new CommandException("Bad arguments to " +
        			"CreateBoard: " + arguments);
        }
    }

    private void createBoard(String boardName, 
    		int length, int height, List<String> blocked) {

    	List<int[]> blockedInt = new ArrayList<int[]>(); 
    	
    	for (String string : blocked ) 
    		blockedInt.add(CoordinateParser.parseString(string));
    	
    	boards.put(boardName,  
    		new RectangularBoard(length, height, blockedInt));
    	
    	output.println("created board " + boardName);
    	output.flush(); 
    }

    //Command of the form 
    //CreatePiece name type board black/white position
    private void createPiece(List<String> arguments) {
        if (arguments.size() != 5) {
            throw new CommandException("Bad arguments to " +
            		"createPiece: " + arguments);
        }
      
        String pieceName = arguments.get(1);
        String typeName = arguments.get(0);
        String boardName = arguments.get(2);
        boolean isWhite = arguments.get(3).equals("white") ?
        			true : false;
        
        String coords = arguments.get(4);
        
        createPiece(pieceName, typeName, boardName, isWhite, coords);
    }

    private void createPiece(String pieceName, String typeName, String boardName, 
    		boolean isWhite, String coords) {

    	RuleSet rs = new StandardAC() ;
    	PieceFactory factory = rs.pieceFactory(); 
    	
    	Piece piece = 
    		factory.getPiece(typeName, boards.get(boardName), isWhite);
   	
    	try {
    		boards.get(boardName).addPiece(piece, CoordinateParser.parseString(coords));
    	}
    	catch (Exception e) {
    		throw new CommandException("Incorrect piece command");
    	}
    	
    	pieceNames.put(piece, pieceName); 
    	
    	output.println("created piece " + pieceName); 
    	output.flush(); 
    }


    private void printBoard(List<String> arguments) {
        if (arguments.size() != 1) {
            throw new CommandException("Bad arguments to printBoard: " + arguments);
        }
    
        String boardName = arguments.get(0);

        printBoard(boardName);
    }

    private void printBoard(String boardName) {
    	
    	RectangularBoard board = boards.get(boardName);
    	    	
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
    				string = pieceNames.get(pc);
    				if (!pc.isWhite())
    					string += "*";
    			}
    			System.out.printf("%5s", string); 
    		}
    		System.out.println("");
    		output.flush(); 
    	}
    }
    
    //MakePly ply board
    private void makePly(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to MakePly: " + arguments);
        }
    
        String move = arguments.get(0);	
        String board = arguments.get(1);

        makePly(move, board);
    }

    private void makePly(String move, String board) {
    	RuleSet rs = new StandardAC(); 
    	PlyFactory fact = rs.plyFactory(); 
    	
    	boards.get(board).executePly(fact.getPly(move, boards.get(board)));
    
    	output.println("Ply " + move + " executed on " + board); 
    	output.flush(); 
    }
    
    //Info c3 board
    private void info(List<String> arguments) {
        if (arguments.size() != 2) {
            throw new CommandException("Bad arguments to info: " + arguments);
        }
    
        String position = arguments.get(0);	
        String board = arguments.get(1);
        info(position, board);
    }
    
    private void info(String position, String board) {
    	RectangularBoard bd = boards.get( board );
    	
    	int[] coord = CoordinateParser.parseString(position);
    	
    	if (! bd.isUsable(coord))
			output.println("Unusable cell");
		else if (bd.isEmpty(coord)) 
			output.println("Empty cell");
		else {
	    	Piece piece = bd.getPiece(coord); 
			Class<? extends Piece> pieceClass = piece.getClass(); 
			output.println("Piece type: " + piece.getType());
			output.println("Class: " + pieceClass);
			
			for (java.lang.reflect.Field field : pieceClass.getDeclaredFields()) {
				output.print(field.toString() + " "); 

				try {
				field.setAccessible(true);
				Object type = field.get(piece); 
				
				if (type instanceof int[])
					output.print(Arrays.toString((int[]) type));
				else 
					output.print(type.toString());
				}
				catch (Exception e) {
					output.print("Private");
				}
				output.println("");
			}
			
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
	