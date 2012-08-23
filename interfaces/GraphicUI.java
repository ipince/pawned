package interfaces;

import java.io.*;
import java.util.*;
import java.util.List;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.browser.*;

import player.AIPlayer;

import java.text.DecimalFormat;
import ruleset.antichess.*;
import ruleset.board.CoordinateParser;
import controller.*;
import engine.adt.*;
import engine.game.GameTermination;
import engine.player.*;
import ruleset.connectn.*;
import ruleset.eval.*;


public class GraphicUI implements Player {

	public final int SHELL_WIDTH = 900;
	public final int SHELL_HEIGHT = 725;
	public final int AC_BOARD_SIZE = 520; // size without numbers and letters
	public final int C4_BOARD_SIZE = 520; // size without numbers and letters
	public final int AC_SQUARE_SIZE = 65;  
	public final int C4_SQUARE_SIZE = 74;  
	public final int AC_NUMBER_OF_ROWS = 8;
	public final int AC_NUMBER_OF_COLUMNS = 8;
	public final int C4_NUMBER_OF_ROWS = 6;
	public final int C4_NUMBER_OF_COLUMNS = 7;
	public final int ROWLAYOUT_MARGINS = 10;
	public final String ANTICHESS = "Standard Antichess";
	public final String ENCASTLE_ANTICHESS = "EnCastle Antichess";
	public final String CONNECT_FOUR = "Connect N";
	public final int ANTICHESS_AI_DEPTH = 2;
	public final int CONNECTN_AI_DEPTH = 4;
	
	
	public Image C4_EMPTY;
	public Image C4_RED;
	public Image C4_BLACK;

	public Color DARK_BROWN;
	public Color LIGHT_BROWN;
	public Color DARK_BLUE;
	public Color LIGHT_BLUE;
	public Color DARK_PURPLE;
	public Color LIGHT_PURPLE;
	public Color DEFAULT;

	private Display display;
	private Shell shell;
	private Image pawnedLogo;

	// Menu 
	private Menu menu;	
	private MenuItem file;
	private MenuItem game;
	private MenuItem help;
	private Menu fileMenu;
	private MenuItem newGame;
	private MenuItem saveGame;
	private MenuItem loadGame;
	private MenuItem fileSep;
	private MenuItem quit;
	private Menu gameMenu;
	private MenuItem endGame;
	private MenuItem displayOpts;
	private Menu displayOptsSubMenu;
	private MenuItem hlMovablePieces;
	private MenuItem hlPossibleMoves;
	private MenuItem hlLastMove;
	private Menu helpMenu;
	private MenuItem manual;
	private MenuItem helpSep;
	private MenuItem about;

	// Toolbar
	private CoolBar bar;
	private CoolItem buttons1;
	private CoolItem buttons2;
	private Button newGameButton;
	private ToolBar buttonsBar;
	private ToolItem loadGameButton;
	private ToolItem saveGameButton;
	private ToolItem endGameButton;

	// Main display
	private Composite main;

	// Left Panel - Board
	private Composite leftPanel;
	private Composite board;
	// maps a String representation of a int[] to the corresponding Label in the board
	private HashMap<String, Label> squares; 

	//private Listener pieceMoverListener;

	// Right Panel 
	private Composite rightPanel;  
	private Group white;
	private Label whiteNameLabel; 
	private Label whiteTimer;
	private Group black;
	private Label blackNameLabel;
	private Label blackTimer;
	private Group capturedPiecesGroup;
	private Composite whiteCapturedPieces;
	private Composite blackCapturedPieces;
	private HashMap<Integer, Label> whiteCapturedPiecesMap;
	private HashMap<Integer, Label> blackCapturedPiecesMap;
	private Group moveHistoryGroup;
	private Table moveHistory;

	// New Game window fields
	private Shell newGameShell;
	private Group playersInfo;
	private Group options;
	private Label selectGameType;
	private Combo gameType;
	private Composite bottomPanel;
	private Button newGameOk;
	private Button newGameCancel;
	private Label pLabel;  // "Name: "
	// White Player
	private Composite wPlayerSubPanel;
	private Label whiteImage;
	private Text whitePlayerName;
	private Button whiteHuman; 
	private Button whiteComputer;
	private Combo whiteComputerType;
	private Button whiteTimed;
	private Composite whiteTime;
	private Label whiteTimeLabel; // "Time"
	private Spinner minWhite;
	private Label whiteColonLabel; // ":"
	private Spinner secWhite;
	// Black Player
	private Composite bPlayerSubPanel;
	private Label blackImage; 
	private Text blackPlayerName;
	private Button blackHuman; 
	private Button blackComputer;
	private Combo blackComputerType;
	private Button blackTimed;
	private Composite blackTime;
	private Label blackTimeLabel;   // "Time"
	private Spinner minBlack;
	private Label blackColonLabel;  // ":"
	private Spinner secBlack;	

	private Label NLabel;
	private Spinner connectN;
	private HashMap<Integer, Label> upperBoardLabelsMap;
	private Composite upperBoard;

	// Load Game window
	private Group loadOptions;
	private Label selectFile;
	private Text gameFilePath;
	private Button browse;


	// Maps a String representation of an int[] representing a cell to a 
	// List to all possible cells where the Piece in this cell can move
	private HashMap<String, List<String>> validMovesMap;   																 
	private boolean moving = false;  // indicates if the user is moving a piece in te GUI
	private int[] movingFrom;
	private int[] movingTo;

	// During a game
	private String move;
	private Controller controller; 
	private GameTermination gameTermination;
	private MouseAdapter pieceMoverAdapter;
	private MouseTrackAdapter moveIndicatorAdapter;
	private String lastMoveExecuted;
	private String lastMoveStart;
	private String lastMoveEnd;
	private Thread whiteTimerThread;
	private Thread blackTimerThread;

	// Display options
	private boolean highlightMovablePiecesOpt;
	private boolean highlightPossibleMovesOpt;
	private boolean highlightLastMoveOpt;

	private Browser browser;
	private Shell helpShell;
	private Shell aboutShell;

	private boolean antichessInPlay = false;
	private boolean connectNInPlay = false;
	private int aidepth;
	
	private Object submitPlyLock = new Object();


	/**
	 * Creates a new GraphicUI and initilizes the interface.
	 * @effects GUI is initialized
	 */
	public static void main(String[] args) {

		GraphicUI gui = new GraphicUI();
		gui.initializeGUI();

	} 

	/**
	 * @effects initializes the necessary fields to run the GUI.
	 */
	private void initializeGUI(){

		display = new Display();	
		shell = new Shell(display, SWT.CLOSE | SWT.MIN);
		shell.addListener(SWT.Close, new Listener() {
			public void handleEvent(Event event) {

				if (whiteTimerThread != null && whiteTimerThread.isAlive())
					whiteTimerThread.interrupt(); 

				if (blackTimerThread != null && blackTimerThread.isAlive())
					blackTimerThread.interrupt(); 

				if (controller != null)
					controller.terminate();

				System.exit(0);
			}
		});

		shell.setMinimumSize(SHELL_WIDTH, SHELL_HEIGHT);
		shell.setBounds(new Rectangle(20, 20, SHELL_WIDTH, SHELL_HEIGHT));
		shell.setText("Pawned");

		pawnedLogo = new Image(display, getImage("logo.png"));
		shell.setImage(pawnedLogo);
		RowLayout rowLayout = new RowLayout();
		rowLayout.type = SWT.VERTICAL;
		shell.setLayout(rowLayout);
		menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		file = new MenuItem(menu, SWT.CASCADE);
		game = new MenuItem(menu, SWT.CASCADE);
		help = new MenuItem(menu, SWT.CASCADE);	
		file.setText("&File");		
		game.setText("&Game");
		help.setText("&Help");

		DARK_BROWN = new Color(display, 143, 96, 79);
		LIGHT_BROWN = new Color(display, 255, 207, 144);
		DARK_BLUE = new Color(display, 73, 96, 129);
		LIGHT_BLUE = new Color(display, 185, 207, 250);
		DARK_PURPLE =  new Color(display, 143, 96, 129); 
		LIGHT_PURPLE = new Color(display, 255, 207, 250);

		// File Menu
		fileMenu = new Menu(shell, SWT.DROP_DOWN);
		file.setMenu(fileMenu);
		newGame = new MenuItem(fileMenu, SWT.PUSH);
		newGame.setText("&New Game\tCtrl+N");
		newGame.setAccelerator(SWT.CTRL+'N');
		newGame.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				newGame();
			}
		});
		saveGame = new MenuItem(fileMenu, SWT.PUSH);
		saveGame.setText("&Save Game\tCtrl+S");
		saveGame.setAccelerator(SWT.CTRL+'S');
		saveGame.setEnabled(false);
		saveGame.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				save();
			}
		});
		loadGame = new MenuItem(fileMenu, SWT.PUSH);
		loadGame.setText("&Load Game\tCtrl+O");
		loadGame.setAccelerator(SWT.CTRL+'O');
		loadGame.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				load();
			}
		});
		fileSep = new MenuItem(fileMenu, SWT.SEPARATOR);
		fileSep.setEnabled(false);
		quit = new MenuItem(fileMenu, SWT.PUSH);
		quit.setText("&Quit\tCtrl+Q");
		quit.setAccelerator(SWT.CTRL+'Q');
		quit.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				quit();
			}
		});

		// Game Menu
		gameMenu = new Menu(shell, SWT.DROP_DOWN);
		game.setMenu(gameMenu);
		endGame = new MenuItem(gameMenu, SWT.PUSH);
		endGame.setText("&End Game\tCtrl+E");
		endGame.setAccelerator(SWT.CTRL+'E');
		endGame.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				endGame();
			}
		});	
		endGame.setEnabled(false);
		displayOpts = new MenuItem(gameMenu, SWT.CASCADE);
		displayOpts.setText("&Display Options");
		displayOptsSubMenu = new Menu(shell, SWT.DROP_DOWN);
		displayOpts.setMenu(displayOptsSubMenu);
		hlMovablePieces = new MenuItem(displayOptsSubMenu, SWT.CHECK);
		hlMovablePieces.setText("Highlight Movable Pieces");
		hlPossibleMoves = new MenuItem(displayOptsSubMenu, SWT.CHECK);
		hlPossibleMoves.setText("Highlight Possible Moves");
		hlLastMove = new MenuItem(displayOptsSubMenu, SWT.CHECK);
		hlLastMove.setText("Highlight Last Move");
		hlMovablePieces.setSelection(true);
		hlPossibleMoves.setSelection(true);
		hlLastMove.setSelection(true);
		hlMovablePieces.setEnabled(false);
		hlPossibleMoves.setEnabled(false);
		hlLastMove.setEnabled(false);
		hlMovablePieces.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				highlightMovablePiecesOpt = hlMovablePieces.getSelection();
			}
		});
		hlPossibleMoves.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				highlightPossibleMovesOpt = hlPossibleMoves.getSelection();
			}
		});
		hlLastMove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				highlightLastMoveOpt = hlLastMove.getSelection();
			}
		});

		// Help Menu
		helpMenu = new Menu(shell, SWT.DROP_DOWN);
		help.setMenu(helpMenu);
		manual = new MenuItem(helpMenu, SWT.PUSH);
		manual.setText("&User manual\tCtrl+U");
		manual.setAccelerator(SWT.CTRL+'U');
		manual.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				helpShell = new Shell(display);
				helpShell.setLayout(new FillLayout());
				helpShell.setText("User Manual");
				helpShell.setImage(pawnedLogo);
				try {
					browser = new Browser(helpShell, SWT.NONE);
				} catch (SWTError err) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
					messageBox.setText("Error");
					messageBox.setMessage("Could not instantiate Browser: " + err.getMessage());
					messageBox.open();
					return;
				}
				helpShell.open();
				browser.setUrl("http://web.mit.edu/reipince/www/pawned/manual/userManual.pdf");
			}
		});
		helpSep = new MenuItem(helpMenu, SWT.SEPARATOR);
		helpSep.setEnabled(false);
		about = new MenuItem(helpMenu, SWT.PUSH);
		about.setText("&About Pawned");
		about.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				shell.setEnabled(false);
				aboutShell = new Shell(shell);
				aboutShell.addListener(SWT.Close, new Listener() {
					public void handleEvent(Event e) {
						shell.setEnabled(true);
					}
				});
				aboutShell.setBounds(aboutShell.getParent().getSize().x/4,
						aboutShell.getParent().getSize().y/4, 192, 287);
				aboutShell.setText("About Pawned");
				aboutShell.setImage(pawnedLogo);
				aboutShell.setBackgroundImage(new Image(display, getImage("aboutPawned.png")));
				aboutShell.open();
			}
		});

		//Toolbar
		bar = new CoolBar(shell, SWT.NONE);
		bar.setSize(400, 100);
		buttons1 = new CoolItem(bar, SWT.NONE);
		buttons2 = new CoolItem(bar, SWT.NONE);

		newGameButton = new Button(bar, SWT.PUSH);
		newGameButton.setToolTipText("New Game");
		newGameButton.setImage(new Image(display, getImage("New.png")));
		newGameButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				newGame();
			}
		});
		newGameButton.pack();

		buttonsBar = new ToolBar(bar, SWT.NONE);
		loadGameButton = new ToolItem(buttonsBar, SWT.PUSH);
		loadGameButton.setToolTipText("Load Game");
		loadGameButton.setImage( new Image(display, getImage("Open.png")));
		loadGameButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				load();
			}
		});
		saveGameButton = new ToolItem(buttonsBar, SWT.PUSH);
		saveGameButton.setToolTipText("Save Game");
		saveGameButton.setImage(new Image(display, getImage("Save.png")));
		saveGameButton.setEnabled(false);
		saveGameButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				save();
			}
		});
		endGameButton = new ToolItem(buttonsBar, SWT.PUSH);
		endGameButton.setToolTipText("End Game");
		endGameButton.setImage(new Image(display, getImage("Delete.png")));
		endGameButton.setEnabled(false);
		endGameButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				endGame();
			}
		});
		buttonsBar.pack();

		Point size = newGameButton.getSize();
		buttons1.setControl(newGameButton);
		buttons1.setSize(buttons1.computeSize(size.x, size.y));

		size = buttonsBar.getSize();
		buttons2.setControl(buttonsBar);
		buttons2.setSize(buttons2.computeSize(size.x, size.y));
		buttons2.setMinimumSize(size);

		// Main display
		main = new Composite(shell, SWT.NONE);
		main.setSize(900, 600);
		rowLayout = new RowLayout();
		rowLayout.type = SWT.HORIZONTAL;
		main.setLayout(rowLayout);

		moveIndicatorAdapter = new MouseTrackAdapter() {

			/**
			 * @requires a mouse event (e != null)
			 * @effects unhighlightlight the cells that indicate valid moves, and highlights the 
			 * possible cells to where you can move from the cell that created the event 
			 */
			public synchronized void mouseEnter(MouseEvent e){
				if (antichessInPlay) {
					if (highlightPossibleMovesOpt) {
						Label l = (Label) e.widget ;				
						int[] cell = convertPositionToCell(l.getLocation());
						List<String> possibleEndCells = validMovesMap.get(Arrays.toString(cell));
						if (possibleEndCells != null){
							highlightCells(possibleEndCells, DARK_BLUE, LIGHT_BLUE);
							List<String> enabledCells = new ArrayList<String>(validMovesMap.keySet());
							enabledCells.remove(Arrays.toString(convertPositionToCell(l.getLocation())));
							undohighlightCells(enabledCells);
						}
					}
				} else if (connectNInPlay) {
					Label l = (Label) e.widget ;				
					int[] cell = convertPositionToCell(l.getLocation());
					Image piece;
					if (controller.isNextWhite() != null && controller.isNextWhite()) {
						piece = new Image(display, getImage("c4RedPiece.png"));
						upperBoardLabelsMap.get(cell[0]).setImage(piece);
					}
					else if (controller.isNextWhite() != null) {
						piece = new Image(display, getImage("c4BlackPiece.png"));
						upperBoardLabelsMap.get(cell[0]).setImage(piece);
					}



				}
			}

			/**
			 * @requires a mouse event (e != null)
			 * @effects highlights the cells that indicate valid moves, and unhighlights the 
			 * possible cells to where you can move from the cell that created the event 
			 */
			public synchronized void mouseExit(MouseEvent e){
				if (antichessInPlay) {
					if (highlightPossibleMovesOpt) {
						Label l = (Label) e.widget ;				
						int[] cell = convertPositionToCell(l.getLocation());
						List<String> possibleEndCells = validMovesMap.get(Arrays.toString(cell));
						if (possibleEndCells != null){
							undohighlightCells(possibleEndCells);
							if (possibleEndCells.contains(lastMoveStart) || possibleEndCells.contains(lastMoveEnd)){
								if (highlightLastMoveOpt) {
									highlightLastMove(lastMoveStart, lastMoveEnd);
								}
							}
							List<String> enabledCells = new ArrayList<String>(validMovesMap.keySet());
							enabledCells.remove(Arrays.toString(convertPositionToCell(l.getLocation())));
							if (highlightMovablePiecesOpt) {
								highlightCells(enabledCells, DARK_BLUE, LIGHT_BLUE);
							}
						}
					}
				} else if (connectNInPlay){
					Label l = (Label) e.widget ;				
					int[] cell = convertPositionToCell(l.getLocation());
					if (upperBoardLabelsMap.get(cell[0]).getImage() != null) {
						upperBoardLabelsMap.get(cell[0]).getImage().dispose();
						upperBoardLabelsMap.get(cell[0]).setImage(null);
					}
				}
			}
		};

		pieceMoverAdapter = new MouseAdapter() {

			/**
			 * @requires a mouse event (e != null)
			 * @effects identifies the user intention to move something in the board. If the
			 * movement is valid, then the variable <code>move</code> is set to a String 
			 * representing the move
			 */
			public void mouseDown(MouseEvent e){
				synchronized(submitPlyLock) {
					if (antichessInPlay) {
						if (moving) {			
							Label l = (Label) e.widget;
							movingTo = convertPositionToCell(l.getLocation());
							String movingToString = Arrays.toString(movingTo);
							if (validMovesMap.get(Arrays.toString(movingFrom)).contains(movingToString)) {  
								String ply = writeACPly(movingFrom, movingTo);
								cleanAllMoveVariables();
								move = ply;
								submitPlyLock.notifyAll();
							} else {
								cleanAllMoveVariables();
								if (highlightMovablePiecesOpt) {
									highlightCells(new ArrayList<String>(validMovesMap.keySet()), DARK_BLUE, LIGHT_BLUE);
								}
								enableMoveIndicator(new ArrayList<String>(validMovesMap.keySet()));
							}

						} else {

							Label l = (Label) e.widget ;
							movingFrom = convertPositionToCell(l.getLocation());
							String movingFromString = Arrays.toString(movingFrom);
							if (validMovesMap.containsKey(movingFromString)){
								ArrayList<String> possibleStartCells = new ArrayList<String>(validMovesMap.keySet());
								disableMoveIndicator(possibleStartCells);
								possibleStartCells.remove(movingFromString);
								undohighlightCells(possibleStartCells);
								if (highlightPossibleMovesOpt) {
									List<String> possibleEndCells = validMovesMap.get(movingFromString);
									if (possibleEndCells != null){
										highlightCells(possibleEndCells, DARK_BLUE, LIGHT_BLUE);						
									}
								}
								moving = true;
							}
						}
					} else if (connectNInPlay) {
						Label l = (Label) e.widget;
						movingTo = convertPositionToCell(l.getLocation());
						String movingToString = Arrays.toString(movingTo);
						if (validMovesMap.containsKey(movingToString)){
							String ply = writeC4Ply(movingTo);
							cleanAllMoveVariables();
							move = ply;
							submitPlyLock.notifyAll();
						}
					}

				}}
		};

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	} ////////////////  end of initializeGUI()  /////////////////////


	private void initializeAntichess() {

		antichessInPlay = true;
		connectNInPlay = false;

		highlightMovablePiecesOpt = true;
		highlightPossibleMovesOpt = true;
		highlightLastMoveOpt = true;
		hlMovablePieces.setSelection(true);
		hlPossibleMoves.setSelection(true);
		hlLastMove.setSelection(true);
		hlMovablePieces.setEnabled(true);
		hlPossibleMoves.setEnabled(true);
		hlLastMove.setEnabled(true);

		// Left Panel - Board
		squares = new HashMap<String, Label>();
		leftPanel = new Composite(main, SWT.NONE);
		leftPanel.setSize(600,600);
		Label boardNumbers = new Label(leftPanel, SWT.NONE);
		boardNumbers.setImage(new Image(display, getImage("numbers.png")));
		boardNumbers.setBounds(10,40,27,520);
		Label boardLetters = new Label(leftPanel, SWT.NONE);
		boardLetters.setImage(new Image(display, getImage("letters.png")));
		boardLetters.setBounds(10, 560, 547, 27);
		board = new Composite(leftPanel, SWT.NONE);
		board.setBounds(37, 40, AC_BOARD_SIZE, AC_BOARD_SIZE);
		createSquares(ANTICHESS);

		// Right Panel
		GridLayout gridLayout = new GridLayout();
		GridData gridData;

		rightPanel = new Composite(main, SWT.NONE);
		rightPanel.setSize(300,600);
		gridLayout.numColumns = 2;
		gridLayout.marginLeft = 20;
		gridLayout.verticalSpacing = 20;
		rightPanel.setLayout(gridLayout);

		white = new Group(rightPanel, SWT.NONE);
		white.setText("White");
		white.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL)); 
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		white.setLayout(gridLayout);
		whiteNameLabel = new Label(white, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		whiteNameLabel.setLayoutData(gridData);
		whiteNameLabel.setFont(new Font(display, "arial", 13, SWT.NORMAL));
		whiteTimer = new Label(white, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 30;
		whiteTimer.setLayoutData(gridData);
		whiteTimer.setFont(new Font(display, "arial", 18, SWT.BOLD));


		black = new Group(rightPanel, SWT.NONE);
		black.setText("Black");
		black.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL)); 
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		black.setLayout(gridLayout);
		blackNameLabel = new Label(black, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		blackNameLabel.setLayoutData(gridData);
		blackNameLabel.setFont(new Font(display, "arial", 13, SWT.NORMAL));
		blackTimer = new Label(black, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 30;
		blackTimer.setLayoutData(gridData);
		blackTimer.setFont(new Font(display, "arial", 18, SWT.BOLD));

		whiteCapturedPiecesMap = new HashMap<Integer, Label>();
		blackCapturedPiecesMap = new HashMap<Integer, Label>();
		capturedPiecesGroup = new Group(rightPanel, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		capturedPiecesGroup.setText("Captured Pieces");
		capturedPiecesGroup.setLayoutData(gridData);
		whiteCapturedPieces = new Composite(capturedPiecesGroup, SWT.NONE);
		whiteCapturedPieces.setBounds(5, 25, 240, 60);
		blackCapturedPieces = new Composite(capturedPiecesGroup, SWT.NONE);
		blackCapturedPieces.setBounds(5, 90, 240, 60);
		for (int i= 0 ; i<16 ; i++) {
			Label l1 = new Label(whiteCapturedPieces, SWT.CENTER);
			Label l2 = new Label(blackCapturedPieces, SWT.CENTER);
			if (i < 8) {
				l1.setBounds(30*i, 0, 30, 30);
				l2.setBounds(30*i, 0, 30, 30);
			} else {
				l1.setBounds(30*(i-8), 30, 30, 30);
				l2.setBounds(30*(i-8), 30, 30, 30);
			}

			whiteCapturedPiecesMap.put(i, l1);
			blackCapturedPiecesMap.put(i, l2);
		}


		moveHistoryGroup = new Group(rightPanel, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 2;
		moveHistoryGroup.setLayoutData(gridData);
		moveHistoryGroup.setText("Move History");
		Label wKing = new Label(moveHistoryGroup, SWT.NONE);
		wKing.setBounds(8,20,30,30);
		wKing.setImage(new Image(display, 
				getImage(ImageFileName.parse("king", "white", "small").getFileName())));
		Label bKing = new Label(moveHistoryGroup, SWT.NONE);
		bKing.setBounds(78,20,30,30);
		bKing.setImage(new Image(display, 
				getImage(ImageFileName.parse("king", "black", "small").getFileName())));
		moveHistory = new Table(moveHistoryGroup, SWT.V_SCROLL);
		moveHistory.setBounds(5,55,140,200);
		TableColumn whiteHistory = new TableColumn(moveHistory, SWT.CENTER);
		whiteHistory.setText("white");
		whiteHistory.setWidth(60);
		whiteHistory.setAlignment(SWT.CENTER);
		TableColumn blackHistory = new TableColumn(moveHistory, SWT.CENTER);
		blackHistory.setText("black");
		blackHistory.setWidth(60);
		blackHistory.setAlignment(SWT.CENTER);

		main.pack();
	}

	private void initializeConnectN() {

		connectNInPlay = true;
		antichessInPlay = false;

		highlightMovablePiecesOpt = false;
		highlightPossibleMovesOpt = false;
		highlightLastMoveOpt = false;
		hlMovablePieces.setSelection(false);
		hlPossibleMoves.setSelection(false);
		hlLastMove.setSelection(false);
		hlMovablePieces.setEnabled(false);
		hlPossibleMoves.setEnabled(false);
		hlLastMove.setEnabled(false);

		C4_EMPTY = new Image(display, getImage("c4Square.png"));
		C4_RED = new Image(display, getImage("c4SquareRed.png"));
		C4_BLACK = new Image(display, getImage("c4SquareBlack.png"));;

		// Left Panel - Board
		squares = new HashMap<String, Label>();
		leftPanel = new Composite(main, SWT.NONE);
		leftPanel.setSize(600,600);

		upperBoardLabelsMap = new HashMap<Integer, Label>();
		upperBoard = new Composite(leftPanel, SWT.NONE);
		upperBoard.setBounds(37, 40, C4_BOARD_SIZE, C4_SQUARE_SIZE);
		for (int i = 0 ; i < C4_NUMBER_OF_COLUMNS ; i++) {
			Label l = new Label(upperBoard, SWT.CENTER);
			l.setBounds(i*C4_SQUARE_SIZE, 0, C4_SQUARE_SIZE, C4_SQUARE_SIZE);
			upperBoardLabelsMap.put(i, l);
		}

		board = new Composite(leftPanel, SWT.NONE);
		board.setBounds(37, 114, C4_BOARD_SIZE, C4_BOARD_SIZE);
		createSquares(CONNECT_FOUR);

		// Right Panel
		GridLayout gridLayout = new GridLayout();
		GridData gridData;

		rightPanel = new Composite(main, SWT.NONE);
		rightPanel.setSize(300,600);
		gridLayout.numColumns = 2;
		gridLayout.marginTop = 70;
		gridLayout.marginLeft = 30;
		gridLayout.verticalSpacing = 20;
		rightPanel.setLayout(gridLayout);

		white = new Group(rightPanel, SWT.NONE);
		white.setText("Red");
		white.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL)); 
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		white.setLayout(gridLayout);
		whiteNameLabel = new Label(white, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		whiteNameLabel.setLayoutData(gridData);
		whiteNameLabel.setFont(new Font(display, "arial", 13, SWT.NORMAL));
		whiteTimer = new Label(white, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 30;
		whiteTimer.setLayoutData(gridData);
		whiteTimer.setFont(new Font(display, "arial", 18, SWT.BOLD));


		black = new Group(rightPanel, SWT.NONE);
		black.setText("Black");
		black.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL)); 
		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		black.setLayout(gridLayout);
		blackNameLabel = new Label(black, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		blackNameLabel.setLayoutData(gridData);
		blackNameLabel.setFont(new Font(display, "arial", 13, SWT.NORMAL));
		blackTimer = new Label(black, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		gridData.horizontalIndent = 30;
		blackTimer.setLayoutData(gridData);
		blackTimer.setFont(new Font(display, "arial", 18, SWT.BOLD));

		Composite invisibleComposite = new Composite(rightPanel, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		invisibleComposite.setLayoutData(gridData);
		invisibleComposite.setBounds(5, 90, 240, 60);
		for (int i= 0 ; i<8 ; i++) {
			Label l = new Label(invisibleComposite, SWT.CENTER);
			l.setBounds(30*i, 0, 30, 30);
		}


		moveHistoryGroup = new Group(rightPanel, SWT.NONE);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 2;
		moveHistoryGroup.setLayoutData(gridData);
		moveHistoryGroup.setText("Move History");
		Label redPiece = new Label(moveHistoryGroup, SWT.NONE);
		redPiece.setBounds(8,20,30,30);
		redPiece.setImage(new Image(display, 
				getImage("redPieceSmall.png")));
		Label blackPiece = new Label(moveHistoryGroup, SWT.NONE);
		blackPiece.setBounds(78,20,30,30);
		blackPiece.setImage(new Image(display, 
				getImage("blackPieceSmall.png")));
		moveHistory = new Table(moveHistoryGroup, SWT.V_SCROLL);
		moveHistory.setBounds(5,55,140,200);
		TableColumn whiteHistory = new TableColumn(moveHistory, SWT.CENTER);
		whiteHistory.setText("white");
		whiteHistory.setWidth(60);
		whiteHistory.setAlignment(SWT.CENTER);
		TableColumn blackHistory = new TableColumn(moveHistory, SWT.CENTER);
		blackHistory.setText("black");
		blackHistory.setWidth(60);
		blackHistory.setAlignment(SWT.CENTER);

		main.pack();
	}


	/**
	 * @effects opens a window in which the user selects the options of a new game.
	 * When 'OK' is clicked, it sends the options selected to initializeGame()
	 */
	private void newGame(){

		shell.setEnabled(false);
		newGameShell = new Shell(shell);
		newGameShell.setBounds(newGameShell.getParent().getSize().x/4,
				newGameShell.getParent().getSize().y/4, 380, 500);
		newGameShell.setText("New Game");

		createGameOptionsWindow();

		GridData gridData;
		GridLayout gridLayout;

		//OK button
		bottomPanel = new Composite(newGameShell, SWT.NONE);
		bottomPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		bottomPanel.setLayout(gridLayout);

		newGameOk = new Button(bottomPanel, SWT.PUSH);
		newGameOk.setText("OK");
		newGameOk.setSize(40,20);
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 40;
		newGameOk.setLayoutData(gridData);
		newGameOk.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int wTime, bTime;

				if (whiteTimed.getSelection()) 
					wTime = minWhite.getSelection()*60000 + secWhite.getSelection()*1000;
				else 
					wTime = -1;

				if (blackTimed.getSelection())
					bTime = minBlack.getSelection()*60000 + secBlack.getSelection()*1000;	
				else
					bTime = -1;

				int wLevel, bLevel;
				if (whiteHuman.getSelection()) {
					wLevel = -1;
				} else {
					wLevel = whiteComputerType.getSelectionIndex();
				}
				if (blackHuman.getSelection()) {
					bLevel = -1;
				} else {
					bLevel = blackComputerType.getSelectionIndex();
				}

				if (wTime == 0 || bTime == 0) {
					MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
					messageBox.setText("Invalid Time");
					messageBox.setMessage("Please enter a non-zero time");
					messageBox.open();
				} else {
					initializeGame(whitePlayerName.getText(), blackPlayerName.getText(), 
							wTime, bTime, whiteHuman.getSelection(), blackHuman.getSelection(), 
							wLevel, bLevel, gameType.getItems()[gameType.getSelectionIndex()], 
							connectN.getSelection());

					newGameShell.close();
				}
			}
		});
		newGameOk.pack();

		//Cancel button
		newGameCancel = new Button(bottomPanel, SWT.PUSH);
		newGameCancel.setText("Cancel");
		newGameCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				newGameShell.close();
			}
		});
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		newGameCancel.setLayoutData(gridData);

		newGameShell.pack();
		newGameShell.open();

	} ////////////////  end of newGame()  /////////////////////

	/**
	 * @requires 	whiteName != null
	 * 				blackName != null
	 * 				whiteTime != null
	 * 				blackName != null
	 * 				whiteHuman == true || whiteHuman == false
	 * 				blackHuman == true || blackHuman == false
	 * 				whiteLevel != null
	 * 				blackLevel != null
	 * 				gameType != null
	 * @effects creates a new game given the specified options. <code>controller</code> is 
	 * initialized in this method
	 */
	private synchronized void initializeGame(String whiteName, String blackName, int whiteTime, int blackTime, 
			boolean whiteHuman, boolean blackHuman, int whiteLevel, int blackLevel, String gameType, int n){

		if (whiteTimerThread != null && whiteTimerThread.isAlive()) {
			whiteTimerThread.interrupt();
		}
		if (blackTimerThread != null && blackTimerThread.isAlive()) {
			blackTimerThread.interrupt(); 
		}
		if (controller != null) {
			controller.terminate();
		}

		if (gameType.equals(ANTICHESS) || gameType.equals(ENCASTLE_ANTICHESS)) {
			if (antichessInPlay == false) {			
				if (leftPanel != null && rightPanel != null) {
					leftPanel.dispose();
					rightPanel.dispose();
				}
				aidepth = ANTICHESS_AI_DEPTH;
				initializeAntichess();
			} else {
				reset();
			}
		} else if (gameType.equals(CONNECT_FOUR)) {
			if (connectNInPlay == false) {
				if (leftPanel != null && rightPanel != null) {
					leftPanel.dispose();
					rightPanel.dispose();	
				}
				aidepth = CONNECTN_AI_DEPTH;
				initializeConnectN();
			} else {
				reset();
			}
		}

		RuleSet ruleSet;
		if (gameType.equals(ANTICHESS))
			ruleSet = new StandardAC();
		else if (gameType.equals(ENCASTLE_ANTICHESS))
			ruleSet = new EnCastleAC();
		else if (gameType.equals(CONNECT_FOUR)) {
			ruleSet = new ConnectNRuleSet(n);
		}
		else 
			throw new RuntimeException("There is no valid ruleset for the game type specified");


		EvaluatorFactory efact = new EvaluatorFactory();

		if (whiteHuman && blackHuman) {
			controller = new Controller(ruleSet, this, this, whiteTime, blackTime);
		} else if (whiteHuman && !blackHuman) {
			Player aiPlayer = new AIPlayer(false, efact.createEvaluator(blackLevel), aidepth);
			synchronized (aiPlayer) {
				controller = new Controller(ruleSet, this, aiPlayer, whiteTime, blackTime);
				aiPlayer.setController(controller);
			}
		} else if (!whiteHuman && blackHuman) {
			Player aiPlayer = new AIPlayer(false, efact.createEvaluator(whiteLevel), aidepth);
			synchronized (aiPlayer) {
				controller = new Controller(ruleSet, aiPlayer, this, whiteTime, blackTime);
				aiPlayer.setController(controller);
			}
		} else {
			Player aiPlayer1 = new AIPlayer(false, efact.createEvaluator(whiteLevel), aidepth);
			Player aiPlayer2 = new AIPlayer(false, efact.createEvaluator(blackLevel), aidepth);
			synchronized (aiPlayer1) {
				synchronized (aiPlayer2) {
					controller = new Controller(ruleSet, aiPlayer1, aiPlayer2, whiteTime, blackTime, this);
					aiPlayer1.setController(controller);
					aiPlayer2.setController(controller);
				}
			}
		}

		saveGame.setEnabled(true);
		saveGameButton.setEnabled(true);
		endGame.setEnabled(true);
		endGameButton.setEnabled(true);

		if (gameType.equals(ANTICHESS) || gameType.equals(ENCASTLE_ANTICHESS)) {
			Board board = controller.getBoard();		
			for (Piece piece : board.getPieces(true)) {
				drawPiece(board.getPosition(piece), piece.getType(), "white");
			}
			for (Piece piece : board.getPieces(false)) {
				drawPiece(board.getPosition(piece), piece.getType(), "black");
			}
		} else if (gameType.equals(CONNECT_FOUR)) {
			// TODO not sure
		}


		whiteNameLabel.setText(whiteName);
		blackNameLabel.setText(blackName);
		white.setBackground(LIGHT_BLUE); 
		whiteNameLabel.setBackground(LIGHT_BLUE);
		whiteTimer.setBackground(LIGHT_BLUE);


		if (whiteHuman || blackHuman)
			enablePieceMover(new ArrayList<String>(squares.keySet()));

		if (whiteTime == -1) {
			whiteTimer.setImage(new Image(display, getImage("infinity.png")));
		} else {
			whiteTimerThread = new Thread("White Timer"){
				public void run(){ 
					synchronized (this) {
						while (! isInterrupted()) {
							try {
								display.syncExec(new Runnable() {
									public void run() {
										whiteTimer.setText(timeDisplayConverter(controller.remainingTime(true)));
									}
								});
								sleep(1000);
							} catch (InterruptedException e) {
								return; 
							}	
						}
					}
				}
			};
			whiteTimerThread.start();
		}
		if (blackTime == -1) {
			blackTimer.setImage(new Image(display, getImage("infinity.png")));
		} else {
			blackTimerThread = new Thread("Black Timer"){
				public void run(){ 
					synchronized (this) {
						while (! isInterrupted()) {
							try {
								display.syncExec(new Runnable() {
									public void run() {
										blackTimer.setText(timeDisplayConverter(controller.remainingTime(false)));
									}
								});								
								sleep(1000);
							} catch (InterruptedException e) {
								return; 
							}	
						}
					}
				}
			};
			blackTimerThread.start();

		}


	} ////////////////  end of initializeGame()  /////////////////////


	/**
	 * @effects opens a window in which the user selects the options for laoding 
	 * a saved game. When 'OK' is clicked, it sends the options selected to initializeSavedGame()
	 */
	private void load(){

		shell.setEnabled(false);
		newGameShell = new Shell(shell);
		newGameShell.setBounds(newGameShell.getParent().getSize().x/4,
				newGameShell.getParent().getSize().y/4, 380, 500);
		newGameShell.setText("Load Game");

		GridData gridData;
		GridLayout gridLayout;

		// Load Options Panel
		loadOptions = new Group(newGameShell, SWT.NONE);  
		loadOptions.setText("Load Game");
		loadOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginLeft = ROWLAYOUT_MARGINS;
		gridLayout.marginRight = ROWLAYOUT_MARGINS;
		gridLayout.marginBottom = ROWLAYOUT_MARGINS;
		gridLayout.marginTop = ROWLAYOUT_MARGINS;
		loadOptions.setLayout(gridLayout);
		selectFile = new Label(loadOptions, SWT.NONE);
		selectFile.setText("Select File: ");
		gameFilePath = new Text (loadOptions, SWT.BORDER);
		gameFilePath.setEditable(false);
		gameFilePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		browse = new Button(loadOptions, SWT.PUSH);
		browse.setText("Browse");
		browse.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				FileDialog dialog = new FileDialog (shell, SWT.OPEN);
				dialog.setFilterNames (new String [] {"XML Files"});
				dialog.setFilterExtensions (new String [] {"*.xml"}); //Windows wild cards
				String path = dialog.open();

				if (path != null) {
					try {

						gameFilePath.setText(path);

						whiteTimed.setEnabled(true);
						blackTimed.setEnabled(true);

						File file = new File(path);
						String[] times = XmlFactory.xmlToTimes(file, false);
						if (times[0].equals(String.valueOf(Controller.UNTIMED))) {
							whiteTimed.setSelection(false);
						} else {
							int[] whiteTimeArray = timeConverter(Integer.valueOf(times[0]));
							whiteTimed.setSelection(true);
							minWhite.setEnabled(true);
							secWhite.setEnabled(true);
							minWhite.setSelection(whiteTimeArray[0]);
							secWhite.setSelection(whiteTimeArray[1]);
						}
						if (times[1].equals(String.valueOf(Controller.UNTIMED))) {
							blackTimed.setSelection(false);
						} else {
							int[] blackTimeArray = timeConverter(Integer.valueOf(times[1]));	
							blackTimed.setSelection(true);
							minBlack.setEnabled(true);
							secBlack.setEnabled(true);
							minBlack.setSelection(blackTimeArray[0]);
							secBlack.setSelection(blackTimeArray[1]);
						}

						String ruleSet = XmlFactory.xmlToRuleSet(file);
						if(ruleSet.equals("6170-spring-2007")) {
							gameType.select(0);
							whiteImage.getImage().dispose();
							whiteImage.setImage(new Image(display, getImage("pawnWhite.png")));
							blackImage.getImage().dispose();
							blackImage.setImage(new Image(display, getImage("pawnBlack.png")));
							NLabel.setVisible(false);
							connectN.setVisible(false);
						} else if (ruleSet.equals("6170-spring-2007-encastle")) {
							whiteImage.getImage().dispose();
							whiteImage.setImage(new Image(display, getImage("pawnWhite.png")));
							blackImage.getImage().dispose();
							blackImage.setImage(new Image(display, getImage("pawnBlack.png")));
							gameType.select(1);
							NLabel.setVisible(false);
							connectN.setVisible(false);
						} else if (ruleSet.startsWith("connect")) {
							gameType.select(2);
							String n = ruleSet.split("-")[1];
							connectN.setSelection(Integer.valueOf(n));
							NLabel.setVisible(true);
							connectN.setVisible(true);
							connectN.setEnabled(false);
							whiteImage.getImage().dispose();
							whiteImage.setImage(new Image(display, getImage("redPiece.png")));
							blackImage.getImage().dispose();
							blackImage.setImage(new Image(display, getImage("blackPiece.png")));
						}




					} catch (Exception exc) { 
						MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
						messageBox.setText("Error");
						messageBox.setMessage("Pawn cannot open the specified file; please check if it is a file of this application");
						messageBox.open();
					}

				}


			}
		});

		createGameOptionsWindow();

		//OK button
		bottomPanel = new Composite(newGameShell, SWT.NONE);
		bottomPanel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		bottomPanel.setLayout(gridLayout);

		newGameOk = new Button(bottomPanel, SWT.PUSH);
		newGameOk.setText("OK");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		newGameOk.setLayoutData(gridData);
		newGameOk.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (gameFilePath.getText() == null  || gameFilePath.getText().length() == 0 ) {
					MessageBox messageBox = new MessageBox(newGameShell, SWT.ICON_WARNING);
					messageBox.setText("Select File");
					messageBox.setMessage("Please select a file to load");
					messageBox.open();
				} else {

					int wTime, bTime;
					if (whiteTimed.getSelection()) {
						wTime = minWhite.getSelection()*60000 + secWhite.getSelection()*1000;
						if (wTime == 0)
							wTime = -1;
					} else {
						wTime = -1;
					}

					if (blackTimed.getSelection()) {
						bTime = minBlack.getSelection()*60000 + secBlack.getSelection()*1000;
						if (bTime == 0)
							bTime = -1;
					}
					else {
						bTime = -1;
					}

					int wLevel, bLevel;
					if (whiteHuman.getSelection()) {
						wLevel = -1;
					} else {
						wLevel = whiteComputerType.getSelectionIndex();
					}
					if (blackHuman.getSelection()) {
						bLevel = -1;
					} else {
						bLevel = blackComputerType.getSelectionIndex();
					}

					int n;
					File file = new File(gameFilePath.getText());
					String[] ruleSetInfo = XmlFactory.xmlToRuleSet(file).split("-"); 
					if (ruleSetInfo[0].equals("connect")) {
						n = Integer.valueOf(ruleSetInfo[1]);
					} else {
						n = 0;
					}



					initializeSavedGame(whitePlayerName.getText(), blackPlayerName.getText(), 
							whiteHuman.getSelection(), blackHuman.getSelection(), wTime, bTime,
							wLevel, bLevel, gameFilePath.getText(), 
							gameType.getItems()[gameType.getSelectionIndex()], n);

					newGameShell.close();	



				}			
			}
		});


		//Cancel button
		newGameCancel = new Button(bottomPanel, SWT.PUSH);
		newGameCancel.setText("Cancel");
		newGameCancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				newGameShell.close();
			}
		});
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		newGameCancel.setLayoutData(gridData);

		whiteTimed.setEnabled(false);
		blackTimed.setEnabled(false);
		gameType.setEnabled(false);

		newGameShell.pack();
		newGameShell.open();

	} ///////////////// end of load() /////////////////////////////////


	/**
	 * @requires 	whiteName != null
	 * 				blackName != null
	 * 				whiteTime != null
	 * 				blackName != null
	 * 				whiteHuman == true || whiteHuman == false
	 * 				blackHuman == true || blackHuman == false
	 * 				whiteLevel != null
	 * 				blackLevel != null
	 * 				path != null
	 * 				gameType != null
	 * @effects creates a game with the status described by a saved game file, and given 
	 * options specified by the user. <code>controller</code> is initialized in this method.
	 */
	private synchronized void initializeSavedGame(String whiteName, String blackName, 
			boolean whiteHuman, boolean blackHuman, int whiteTime, int blackTime,
			int whiteLevel, int blackLevel, String path, String gameType, int n){


		if (whiteTimerThread != null && whiteTimerThread.isAlive()) {
			whiteTimerThread.interrupt();
		}
		if (blackTimerThread != null && blackTimerThread.isAlive()) {
			blackTimerThread.interrupt(); 
		}
		if (controller != null) {
			controller.terminate();
		}

		//	synchronized(org.eclipse.swt.accessibility.ACC.class) {
		try {

			if (gameType.equals(ANTICHESS) || gameType.equals(ENCASTLE_ANTICHESS)) {
				if (antichessInPlay == false) {			
					if (leftPanel != null && rightPanel != null) {
						leftPanel.dispose();
						rightPanel.dispose();
					}
					aidepth = ANTICHESS_AI_DEPTH;
					initializeAntichess();
				} else {
					reset();
				}
			} else if (gameType.equals(CONNECT_FOUR)) {
				if (connectNInPlay == false) {
					if (leftPanel != null && rightPanel != null) {
						leftPanel.dispose();
						rightPanel.dispose();	
					}
					aidepth = CONNECTN_AI_DEPTH;
					initializeConnectN();
				} else {
					reset();
				}
			}

			RuleSet ruleSet;
			if (gameType.equals(ANTICHESS))
				ruleSet = new StandardAC();
			else if (gameType.equals(ENCASTLE_ANTICHESS))
				ruleSet = new EnCastleAC();
			else if (gameType.equals(CONNECT_FOUR))
				ruleSet = new ConnectNRuleSet(n);
			else 
				throw new RuntimeException("There is no valid ruleset for the game type specified");

			File filel = new File(path);
			EvaluatorFactory efact = new EvaluatorFactory();

			if (whiteHuman && blackHuman) {
				controller = new Controller(filel, ruleSet, this, this, whiteTime, blackTime);
			} else if (whiteHuman && !blackHuman) {
				Player aiPlayer = new AIPlayer(false, efact.createEvaluator(blackLevel), aidepth);
				synchronized (aiPlayer) {
					controller = new Controller(filel, ruleSet, this, aiPlayer, whiteTime, blackTime);
					aiPlayer.setController(controller);
				}
			} else if (!whiteHuman && blackHuman) {
				Player aiPlayer = new AIPlayer(false, efact.createEvaluator(whiteLevel), aidepth );
				synchronized (aiPlayer) {
					controller = new Controller(filel, ruleSet, aiPlayer, this, whiteTime, blackTime);
					aiPlayer.setController(controller);
				}
			} else {
				Player aiPlayer1 = new AIPlayer(false, efact.createEvaluator(whiteLevel), aidepth );
				Player aiPlayer2 = new AIPlayer(false, efact.createEvaluator(blackLevel), aidepth);
				synchronized (aiPlayer1) {
					synchronized (aiPlayer2) {
						controller = new Controller(filel, ruleSet, aiPlayer1, aiPlayer2, whiteTime, blackTime, this);
						aiPlayer1.setController(controller);
						aiPlayer2.setController(controller);
					}
				}
			}

			saveGame.setEnabled(true);
			saveGameButton.setEnabled(true);
			endGame.setEnabled(true);
			endGameButton.setEnabled(true);

			eraseBoard();
			Board board = controller.getBoard();		
			for (Piece piece : board.getPieces(true)) {
				drawPiece(board.getPosition(piece), piece.getType(), "white");
			}
			for (Piece piece : board.getPieces(false)) {
				drawPiece(board.getPosition(piece), piece.getType(), "black");
			}

			if (gameType.equals(ANTICHESS) || gameType.equals(ENCASTLE_ANTICHESS)) {
				updateCapturedPiecesDisplay();
			} 

			whiteNameLabel.setText(whiteName);
			blackNameLabel.setText(blackName);

			// display history
			List<String[]> history = XmlFactory.xmlToHistory(XmlFactory.getTag("moveHistory", filel));
			for (String[] ply : history) {
				String correctPly;
				if (gameType.equals(CONNECT_FOUR)) {
					correctPly = ply[1].split("\\+")[1];
				} else {
					correctPly = ply[1];
				}
				if (ply[0].equals("white")) {
					TableItem item = new TableItem(moveHistory, SWT.NONE);
					item.setText(0, correctPly);
				} else {
					if (moveHistory.getItem(moveHistory.getItemCount() - 1).getText(1).length() == 0 ) {
						moveHistory.getItem(moveHistory.getItemCount() - 1).setText(1, correctPly);
					} else {
						TableItem item = new TableItem(moveHistory, SWT.NONE);
						item.setText(1, correctPly);
					}

				}
			}

			updateTurnDisplay();

			if (whiteHuman || blackHuman)
				enablePieceMover(new ArrayList<String>(squares.keySet()));

			if (whiteTime == -1) {
				whiteTimer.setImage(new Image(display, getImage("infinity.png")));
			} else {
				whiteTimerThread = new Thread("White Timer"){
					public void run(){ 
						synchronized (this) {
							while (! isInterrupted()) {
								try {
									display.syncExec(new Runnable() {
										public void run() {
											whiteTimer.setText(timeDisplayConverter(controller.remainingTime(true)));
										}
									});
									sleep(1000);
								} catch (InterruptedException e) {
									return; 
								}	
							}
						}
					}
				};
				whiteTimerThread.start();
			}
			if (blackTime == -1) {
				blackTimer.setImage(new Image(display, getImage("infinity.png")));
			} else {
				blackTimerThread = new Thread("Black Timer"){
					public void run(){ 
						synchronized (this) {
							while (! isInterrupted()) {
								try {
									display.syncExec(new Runnable() {
										public void run() {
											blackTimer.setText(timeDisplayConverter(controller.remainingTime(false)));
										}
									});								
									sleep(1000);
								} catch (InterruptedException e) {
									return; 
								}	
							}
						}
					}
				};
				blackTimerThread.start();
			}

		} catch (Exception exp) {		
			exp.printStackTrace();
			MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
			messageBox.setText("Error");
			messageBox.setMessage("Pawn cannot open the specified file; please check if it is a file of this application");
			messageBox.open();
		}
		//}


	} ////////////////  end of initializeSavedGame()  /////////////////////


	/**
	 * Used by newGame() and loadGame() to display shared game options in a window
	 * @effects displays options in the new game and the load game options window
	 */
	private void createGameOptionsWindow(){

		GridLayout gridLayout;
		GridData gridData;
		RowLayout rowLayout;

		gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.marginBottom = 5;
		gridLayout.marginTop = 5;
		gridLayout.marginLeft = 5;
		gridLayout.marginRight = 5;
		gridLayout.verticalSpacing = 5;
		newGameShell.setLayout(gridLayout); 
		newGameShell.addListener (SWT.Close, new Listener () {
			public void handleEvent (Event event) {
				shell.setEnabled(true);
			}
		});

		// Players Info Panel ********************************************
		playersInfo = new Group(newGameShell, SWT.NONE);  
		playersInfo.setText("Players");
		playersInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		int playersSubPanelHorizontalIndent = 50;
		rowLayout = new RowLayout();
		rowLayout.spacing = ROWLAYOUT_MARGINS;
		rowLayout.marginLeft = ROWLAYOUT_MARGINS;
		rowLayout.marginRight = ROWLAYOUT_MARGINS;
		rowLayout.marginBottom = ROWLAYOUT_MARGINS;
		rowLayout.marginTop = ROWLAYOUT_MARGINS;
		playersInfo.setLayout(rowLayout);

		// White Panel ****************************************************
		wPlayerSubPanel = new Composite(playersInfo, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		wPlayerSubPanel.setLayout(gridLayout);
		pLabel = new Label(wPlayerSubPanel, SWT.NONE);
		pLabel.setText("Name: ");
		whitePlayerName = new Text(wPlayerSubPanel, SWT.BORDER);
		whitePlayerName.setText("Player 1");
		whitePlayerName.setTextLimit(8);
		whitePlayerName.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		whiteImage = new Label(wPlayerSubPanel, SWT.NONE);
		whiteImage.setImage(new Image(display, getImage("pawnWhite.png")));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 2;
		whiteImage.setLayoutData(gridData);
		whiteHuman = new Button(wPlayerSubPanel, SWT.RADIO);
		whiteHuman.setText("Human");
		whiteHuman.setSelection(true);
		whiteHuman.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				whiteComputerType.setEnabled(false); }
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		whiteHuman.setLayoutData(gridData);
		whiteComputer = new Button(wPlayerSubPanel, SWT.RADIO);
		whiteComputer.setText("Computer");
		whiteComputer.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				whiteComputerType.setEnabled(true); } 
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		whiteComputer.setLayoutData(gridData);
		whiteComputerType = new Combo (wPlayerSubPanel, SWT.READ_ONLY);
		whiteComputerType.setItems (new String [] {"Baby", "Kid", "Adolescent"});
		whiteComputerType.select(0);
		whiteComputerType.setEnabled(false);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent + 20;
		whiteComputerType.setLayoutData(gridData);
		whiteTimed = new Button(wPlayerSubPanel, SWT.CHECK);
		whiteTimed.setText("Timed");
		whiteTimed.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e){
				if (whiteTimed.getSelection()) {
					minWhite.setEnabled(true);
					secWhite.setEnabled(true);
				} else {
					minWhite.setEnabled(false);
					secWhite.setEnabled(false);
				}
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		whiteTimed.setLayoutData(gridData);
		whiteTime = new Composite(wPlayerSubPanel, SWT.NONE);
		rowLayout = new RowLayout();
		rowLayout.spacing = 3;
		whiteTime.setLayout(rowLayout);
		whiteTimeLabel = new Label(whiteTime, SWT.NONE);
		whiteTimeLabel.setText("Time: ");
		minWhite = new Spinner(whiteTime, SWT.NONE);
		minWhite.setMinimum(0);
		minWhite.setMaximum(60);
		minWhite.setSelection(5);
		minWhite.setIncrement(1);
		minWhite.setEnabled(false);
		whiteColonLabel = new Label(whiteTime, SWT.NONE);
		whiteColonLabel.setText(":");
		secWhite = new Spinner(whiteTime, SWT.NONE);
		secWhite.setMinimum(0);
		secWhite.setMaximum(59);
		secWhite.setSelection(0);
		secWhite.setIncrement(1);
		secWhite.setEnabled(false);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		whiteTime.setLayoutData(gridData);


		//Black Panel *******************************************************
		bPlayerSubPanel = new Composite(playersInfo, SWT.NONE);
		gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		bPlayerSubPanel.setLayout(gridLayout);
		pLabel = new Label(bPlayerSubPanel, SWT.NONE);
		pLabel.setText("Name: ");
		blackPlayerName = new Text(bPlayerSubPanel, SWT.BORDER);
		blackPlayerName.setText("Player 2");
		blackPlayerName.setTextLimit(8);
		blackPlayerName.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		blackImage = new Label(bPlayerSubPanel, SWT.NONE);
		blackImage.setImage(new Image(display, getImage("pawnBlack.png")));
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		gridData.horizontalSpan = 2;
		blackImage.setLayoutData(gridData);
		blackHuman = new Button(bPlayerSubPanel, SWT.RADIO);
		blackHuman.setText("Human");
		blackHuman.setSelection(true);
		blackHuman.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				blackComputerType.setEnabled(false); }
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		blackHuman.setLayoutData(gridData);
		blackComputer = new Button(bPlayerSubPanel, SWT.RADIO);
		blackComputer.setText("Computer");
		blackComputer.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				blackComputerType.setEnabled(true); } 
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		blackComputer.setLayoutData(gridData);
		blackComputerType = new Combo (bPlayerSubPanel, SWT.READ_ONLY);
		blackComputerType.setItems (new String [] {"Baby", "Kid", "Adolescent"});
		blackComputerType.select(0);
		blackComputerType.setEnabled(false);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent + 20;
		blackComputerType.setLayoutData(gridData);
		blackTimed = new Button(bPlayerSubPanel, SWT.CHECK);
		blackTimed.setText("Timed");
		blackTimed.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e){
				if (blackTimed.getSelection()) {
					minBlack.setEnabled(true);
					secBlack.setEnabled(true);
				} else {
					minBlack.setEnabled(false);
					secBlack.setEnabled(false);
				}
			}
		});
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		blackTimed.setLayoutData(gridData);
		blackTime = new Composite(bPlayerSubPanel, SWT.NONE);
		rowLayout = new RowLayout();
		rowLayout.spacing = 3;
		blackTime.setLayout(rowLayout);
		blackTimeLabel = new Label(blackTime, SWT.NONE);
		blackTimeLabel.setText("Time: ");
		minBlack = new Spinner(blackTime, SWT.NONE);
		minBlack.setMinimum(0);
		minBlack.setMaximum(60);
		minBlack.setSelection(5);
		minBlack.setIncrement(1);
		minBlack.setEnabled(false);
		blackColonLabel = new Label(blackTime, SWT.NONE);
		blackColonLabel.setText(":");
		secBlack = new Spinner(blackTime, SWT.NONE);
		secBlack.setMinimum(0);
		secBlack.setMaximum(59);
		secBlack.setSelection(0);
		secBlack.setIncrement(1);
		secBlack.setEnabled(false);
		gridData = new GridData();
		gridData.horizontalSpan = 2;
		gridData.horizontalIndent = playersSubPanelHorizontalIndent;
		blackTime.setLayoutData(gridData);


		// Options Panel
		options = new Group(newGameShell, SWT.NONE);  
		options.setText("Options");
		options.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		rowLayout = new RowLayout();
		rowLayout.spacing = 5;
		rowLayout.marginLeft = ROWLAYOUT_MARGINS;
		rowLayout.marginRight = ROWLAYOUT_MARGINS;
		rowLayout.marginBottom = ROWLAYOUT_MARGINS;
		rowLayout.marginTop = ROWLAYOUT_MARGINS;
		options.setLayout(rowLayout);
		selectGameType = new Label(options, SWT.NONE);
		selectGameType.setText("Select Game Type: ");
		gameType = new Combo (options, SWT.READ_ONLY);
		gameType.setItems (new String [] {ANTICHESS, ENCASTLE_ANTICHESS, CONNECT_FOUR});
		gameType.select(0);
		gameType.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (gameType.getSelectionIndex() == 2) {
					whiteImage.getImage().dispose();
					whiteImage.setImage(new Image(display, getImage("redPiece.png")));
					blackImage.getImage().dispose();
					blackImage.setImage(new Image(display, getImage("blackPiece.png")));
					NLabel.setVisible(true);
					connectN.setVisible(true);
				} else {
					whiteImage.getImage().dispose();
					whiteImage.setImage(new Image(display, getImage("pawnWhite.png")));
					blackImage.getImage().dispose();
					blackImage.setImage(new Image(display, getImage("pawnBlack.png")));
					NLabel.setVisible(false);
					connectN.setVisible(false);
				}
			}
		});
		NLabel = new Label(options, SWT.NONE);
		NLabel.setText("N =");
		connectN = new Spinner(options, SWT.NONE);
		connectN.setMinimum(2);
		connectN.setMaximum(7);
		connectN.setIncrement(1);
		connectN.setSelection(4);
		NLabel.setVisible(false);
		connectN.setVisible(false);


	} ////////////////  end of createGameOptionsWindow()  /////////////////////


	/**
	 * @effects opens a file dialog to allow the user save a game. This methods
	 * calls <code>controller.saveGame(file)</code>
	 */
	private void save(){
		FileDialog dialog = new FileDialog (shell, SWT.SAVE);
		dialog.setFilterNames (new String [] {"XML Files"});
		dialog.setFilterExtensions (new String [] {"*.xml"}); //Windows wild cards
		dialog.setFileName ("game.xml");
		String path = dialog.open();

		if (path != null) {
			File file = new File(path);
			try {
				controller.saveGame(file);
			} catch (Exception e) {
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_WARNING);
				messageBox.setText("Error");
				messageBox.setMessage("Failed to save game");
				messageBox.open();
			}
		}

	} ////////////////  end of save()  /////////////////////


	/**
	 * To allow the user end a game in process
	 * @effects terminates the current game in process
	 */
	private void endGame(){
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION |
				SWT.YES | SWT.NO);
		messageBox.setText("End Game");
		messageBox.setMessage("Are you sure you want to end this game");
		int response = messageBox.open();
		switch(response){
		case SWT.YES:

			if (whiteTimerThread != null && whiteTimerThread.isAlive())
				whiteTimerThread.interrupt(); 

			if (blackTimerThread != null && blackTimerThread.isAlive())
				blackTimerThread.interrupt(); 

			if (controller != null)
				controller.terminate();

			saveGame.setEnabled(false);
			saveGameButton.setEnabled(false);
			endGame.setEnabled(false);
			endGameButton.setEnabled(false);
			disablePieceMover(new ArrayList<String>(squares.keySet()));
			cleanAllMoveVariables();

		case SWT.NO:
			break;
		}
	}  ////////////////  end of endGame()  /////////////////////

	/**
	 * @effects quits the application
	 */
	private void quit(){
		MessageBox messageBox = new MessageBox(shell, SWT.ICON_QUESTION |
				SWT.YES | SWT.NO);
		messageBox.setText("Quit");
		messageBox.setMessage("Are you sure you want to quit Pawned?");
		int response = messageBox.open();
		switch(response){
		case SWT.YES:
			if (whiteTimerThread != null && whiteTimerThread.isAlive())
				whiteTimerThread.interrupt(); 

			if (blackTimerThread != null && blackTimerThread.isAlive())
				blackTimerThread.interrupt(); 

			if (controller != null)
				controller.terminate();

			System.exit(0);

		case SWT.NO:
			break;
		}
	}  ////////////////  end of quit()  /////////////////////


	/**
	 *@effects helper function that creates each individual square of the board
	 */
	private void createSquares(String game) {
		int rows;
		int columns;
		int squareSize;
		if (game.equals(ANTICHESS)) {
			rows = AC_NUMBER_OF_ROWS;
			columns = AC_NUMBER_OF_COLUMNS;
			squareSize = AC_SQUARE_SIZE;
		} else if (game.equals(CONNECT_FOUR)){
			rows = C4_NUMBER_OF_ROWS;
			columns = C4_NUMBER_OF_COLUMNS;
			squareSize = C4_SQUARE_SIZE;
		} else {
			throw new RuntimeException("There is no createSquare setting for the game type specified");
		}
		for(int i=rows; i>0; i--) {
			for(int j=1; j<=columns; j++) {
				Label square = new Label(board, SWT.CENTER);
				square.setBounds((j-1)*squareSize,(rows-i)*squareSize, squareSize, squareSize);
				if (game.equals(ANTICHESS)) {
					square.setBackground(getColor(i, j));
				} else {
					square.setImage(C4_EMPTY);
				}

				int[] cell = {j-1,i-1};
				squares.put(Arrays.toString(cell), square);
			}	
		}
	}  ////////////////  end of createSquares()  /////////////////////	

	/**
	 * Determines what color to return to paint the board squares given the position 
	 * of the square
	 * @return a Color object
	 */
	private Color getColor(int x, int y) {
		if((x+y) % 2 != 0)
			return LIGHT_BROWN;
		else
			return DARK_BROWN;
	}  ////////////////  end of getColor()  /////////////////////

	/**
	 * @requires	cell != null, and cell represents a existing square in the board
	 * 				piece != null, and piece is the valid name of a piece
	 * 				color != null, and color is a valid color for the given piece
	 * @effects sets the image of the specified piece in the specified square of the board
	 * @throws RuntimeException if the given cell represents a square that does not exist in 
	 * the board
	 */
	private void drawPiece(int[] cell, String piece, String color){
		String file = ImageFileName.parse(piece, color, "normal").getFileName();
		Image pieceImg = new Image(display, getImage(file));
		String key = Arrays.toString(cell);
		if (squares.get(key)!= null) {
			if (squares.get(key).getImage() != null) {
				squares.get(key).getImage().dispose();
			}	
			squares.get(key).setImage(pieceImg);
			squares.get(key).update();
		} else {
			throw new RuntimeException("No square - GraphicUI.drawPiece()");
		}
	}  ////////////////  end of drawPiece()  /////////////////////

	private void erasePiece(int[] cell){
		Image emptyImg = new Image(display, getImage("c4Square.png"));
		String key = Arrays.toString(cell);
		if (squares.get(key)!= null) {
			if (squares.get(key).getImage() != null) {
				squares.get(key).getImage().dispose();
			}	
			squares.get(key).setImage(emptyImg);
			squares.get(key).update();
		} else {
			throw new RuntimeException("No square - GraphicUI.drawPiece()");
		}
	}  ////////////////  end of erasePiece()  /////////////////////

	/**
	 * @effects removes all the piece images from all the square in the board
	 */
	private void eraseBoard(){
		if (antichessInPlay) {
			for (Label l : squares.values()) {
				if (l.getImage() != null)
					l.getImage().dispose(); 
				l.setImage(null);
			}
		} else if (connectNInPlay) {
			for (Label l : squares.values()) {
				if (l.getImage() != null)
					l.getImage().dispose(); 
				l.setImage(null);
				l.setImage(new Image(display, getImage("c4Square.png")));
			}
		}

	}  ////////////////  end of eraseBoard()  /////////////////////

	/**
	 * @requires validMoves != null
	 * @effects sets up the board to allow the player make a move.
	 */
	private void setUpBoardForMove(List<String> validMoves){
		setValidMoves(validMoves);
		List<String> validMovesList = new ArrayList<String>(validMovesMap.keySet());
		if (highlightMovablePiecesOpt) {
			highlightCells(validMovesList, DARK_BLUE, LIGHT_BLUE);
		}
		if (antichessInPlay) {
			enableMoveIndicator(validMovesList);
		} else if (connectNInPlay) {
			List<String> allCellsInColumn = new ArrayList<String>();
			for (String move : validMovesList) {
				char c = move.charAt(1);
				for (String cell : squares.keySet()){
					if (cell.charAt(1) == c) 
						allCellsInColumn.add(cell);
				}
			}
			enableMoveIndicator(allCellsInColumn);
		}
	}  ////////////////  end of setUpBoardForMove()  /////////////////////

	/**
	 * @requires validMoves != null
	 * @effects organizes the valid moves given in <code>validMoves</code> in a HashMap that
	 * maps from a starting cell to a list of all its possible end cells 
	 */
	private void setValidMoves(List<String> validMoves){
		validMovesMap = new HashMap<String, List<String>>(); 
		if (antichessInPlay) {
			for(String move : validMoves) {
				String start = Arrays.toString(CoordinateParser.getCell(move, true)); 
				String end = Arrays.toString(CoordinateParser.getCell(move, false));
				if(validMovesMap.containsKey(start)){
					List<String> endCells = new ArrayList<String>(validMovesMap.get(start));
					endCells.add(end);
					validMovesMap.put(start, endCells);
				} else {
					List<String> endCells = new ArrayList<String>();
					endCells.add(end);
					validMovesMap.put(start, endCells);
				}
			}
		} else if (connectNInPlay) {
			for (String move : validMoves) {
				String[] m = move.split("\\+");
				String validSquare = Arrays.toString(CoordinateParser.parseString(m[1]));
				validMovesMap.put(validSquare, new ArrayList<String>());	
			}
		}

	}  ////////////////  end of setValidMoves()  /////////////////////

	/**
	 * @requires cells != null, and each cell in the list represents an existing square
	 * @effects adds the MouseTrackListener <code>moveIndicatorAdapter</code> to the
	 * cells in <code>cells</code>
	 */
	private void enableMoveIndicator(List<String> cells){
		for(String cell : cells) {
			squares.get(cell).addMouseTrackListener(moveIndicatorAdapter);
		}
	} ////////////////  end of enableMoveIndicator()  /////////////////////

	/**
	 * @requires cells != null, and each cell in the list represents an existing square
	 * @effects removes the MouseTrackListener <code>moveIndicatorAdapter</code> from the
	 * cells in <code>cells</code>
	 */
	private void disableMoveIndicator(List<String> cells){
		for(String cell : cells) {
			squares.get(cell).removeMouseTrackListener(moveIndicatorAdapter);
		}
	} ////////////////  end of disableMoveIndicator()  /////////////////////

	/**
	 * @requires cells != null, and each cell in the list represents an existing square
	 * @effects adds the MouseListener <code>pieceMoverAdapter</code> to the
	 * cells in <code>cells</code>
	 */
	private void enablePieceMover(List<String> cells){
		for(String cell : cells) {
			squares.get(cell).addMouseListener(pieceMoverAdapter);
		}
	} ////////////////  end of enablePieceMover()  /////////////////////

	/**
	 * @requires cells != null, and each cell in the list represents an existing square
	 * @effects removes the MouseListener <code>pieceMoverAdapter</code> from the
	 * cells in <code>cells</code>
	 */
	private void disablePieceMover(List<String> cells){
		for(String cell : cells) {
			squares.get(cell).removeMouseListener(pieceMoverAdapter);
		}
	} ////////////////  end of disablePieceMover()  /////////////////////

	/**
	 * @requires cells != null && darkColor != null && lightColor != null, 
	 * and each cell in the list represents an existing square
	 * @effects highlights the specified cells with the given colors (darkColor for 
	 * the dark squares, and lightColor for the light ones)  
	 */
	private void highlightCells(List<String> cells, Color darkColor, Color lightColor){
		for (String cell : cells){
			Label sq = squares.get(cell);
			int[] c = convertPositionToCell(sq.getLocation());
			if((c[0] + c[1]) % 2 != 0)
				sq.setBackground(lightColor); 
			else 
				sq.setBackground(darkColor);
		}
	} ////////////////  end of highlightCells()  /////////////////////

	/**
	 * @requires cells != null, and each cell in the list represents an existing square
	 * @effects unhighlights the specified cells
	 */
	private void undohighlightCells(List<String> cells){
		for (String cell : cells){
			Label sq = squares.get(cell);
			int[] c = convertPositionToCell(sq.getLocation());
			if((c[0] + c[1]) % 2 != 0)
				sq.setBackground(LIGHT_BROWN); 
			else
				sq.setBackground(DARK_BROWN); 
		} 
	} ////////////////  end of undohighlightCells()  /////////////////////

	/**
	 * @requires start != null && end != null, && start, end represent existing squares
	 * @effects highlights the two given cells
	 */
	private void highlightLastMove(String start, String end){
		List<String> cells = Arrays.asList(start, end);
		for (String cell : cells){
			Label sq = squares.get(cell);
			int[] c = convertPositionToCell(sq.getLocation());
			if((c[0] + c[1]) % 2 != 0)
				sq.setBackground(LIGHT_PURPLE); 
			else 
				sq.setBackground(DARK_PURPLE);
		}
	} ////////////////  end of highlightLastMove()  /////////////////////

	/**
	 * @requires start != null && end != null, && start, end represent existing squares
	 * @effects unhighlights the two given cells
	 */
	private void undohighlightLastMove(String start, String end){
		List<String> cells = Arrays.asList(start, end);
		for (String cell : cells){
			Label sq = squares.get(cell);
			int[] c = convertPositionToCell(sq.getLocation());
			if((c[0] + c[1]) % 2 != 0)
				sq.setBackground(LIGHT_BROWN);
			else
				sq.setBackground(DARK_BROWN);
		} 
	}  ////////////////  end of undohighlightLastMove()  /////////////////////


	/**
	 * @effects resets all variables used for piece movement in the board
	 * to allow a next move
	 */
	private void cleanAllMoveVariables(){
		movingFrom = null;
		movingTo = null;
		moving = false;
		undohighlightCells(new ArrayList<String>(squares.keySet()));
		disableMoveIndicator(new ArrayList<String>(squares.keySet()));
	}  ////////////////  end of cleanAllMoveVariables()  /////////////////////

	/**
	 * @effects resets all fields to give a fresh start for a new game
	 */
	private synchronized void reset(){

		move = null;
		eraseBoard();
		cleanAllMoveVariables();
		disablePieceMover(new ArrayList<String>(squares.keySet()));
		moveHistory.removeAll();

		if (antichessInPlay) {
			if (lastMoveStart != null && lastMoveEnd != null){
				undohighlightLastMove(lastMoveStart, lastMoveEnd);
			}
			lastMoveStart = null;
			lastMoveEnd = null;
		}

		white.setBackground(DEFAULT); 
		whiteNameLabel.setBackground(DEFAULT);
		whiteTimer.setBackground(DEFAULT);
		black.setBackground(DEFAULT);  
		blackNameLabel.setBackground(DEFAULT);
		blackTimer.setBackground(DEFAULT);


		if (whiteTimer.getImage() != null) {
			whiteTimer.getImage().dispose();
			whiteTimer.setImage(null);
		}

		if (blackTimer.getImage() != null) {
			blackTimer.getImage().dispose();
			blackTimer.setImage(null);
		}
		whiteTimer.setText("");
		blackTimer.setText("");
		whiteNameLabel.setText("");
		blackNameLabel.setText("");

		if (antichessInPlay) {
			for (Label l : whiteCapturedPiecesMap.values()) {
				if (l.getImage() != null)
					l.getImage().dispose(); 
				l.setImage(null);
			}


			for (Label l : blackCapturedPiecesMap.values()) {
				if (l.getImage() != null)
					l.getImage().dispose(); 
				l.setImage(null);
			}

		}

	}  ////////////////  end of reset()  /////////////////////

	/**
	 * @requires p != null
	 * @return given the position p of a square in the board, returns the 
	 * cell representation of the square
	 */
	private int[] convertPositionToCell(Point p){
		int squareSize;
		int rows;
		if (antichessInPlay) {
			squareSize = AC_SQUARE_SIZE;
			rows = AC_NUMBER_OF_ROWS;
		} else if (connectNInPlay) {
			squareSize = C4_SQUARE_SIZE;
			rows = C4_NUMBER_OF_ROWS;
		} else {
			throw new RuntimeException("Game type in play error");
		}


		int[] cell = {(p.x /squareSize),  ((rows-1) - (p.y/squareSize))};
		return cell;	
	}  ////////////////  end of convertPositionToCell()  /////////////////////

	/**
	 * @requires from != null && to != null
	 * @return a String representation for Antichess of the ply that starts in the cell <code>from</code>, 
	 * and ends in the cell <code>to</code>
	 */
	private String writeACPly(int[] from, int[] to){
		String ply = CoordinateParser.parseCoord(from) + "-" + CoordinateParser.parseCoord(to);
		return ply;

	}  ////////////////  end of writePly()  /////////////////////

	/**
	 * @requires from != null && to != null
	 * @return a String representation of the ply for Connect Four that starts in the cell <code>from</code>, 
	 * and ends in the cell <code>to</code>
	 */
	private String writeC4Ply(int[] to){
		String ply = CoordinateParser.parseCoord(to);
		if (controller.isNextWhite() != null && controller.isNextWhite()) {
			return "t+" + ply;
		} else if (controller.isNextWhite() != null){
			return "f+" + ply;
		}
		return "";

	}  ////////////////  end of writePly()  /////////////////////

	/**
	 * @requires timeInMilliSec != null
	 * @return a String representation of the time in the format mm:ss
	 */
	private String timeDisplayConverter(int timeInMilliSec){
		DecimalFormat df = new DecimalFormat("00");
		int[] time = timeConverter(timeInMilliSec);
		String timeString = String.valueOf(time[0]) + ":" + df.format(Integer.valueOf(time[1]).doubleValue());
		return timeString;

	}  ////////////////  end of timeDisplayConverter()  /////////////////////

	/**
	 * @requires timeInMilliSec != null
	 * @return an int array of the form [minutes, seconds]
	 */
	private int[] timeConverter(int timeInMilliSec){
		int min = timeInMilliSec/60000;
		int sec = (timeInMilliSec % 60000)/1000;
		return new int[]{min, sec};

	}  ////////////////  end of timeConverter()  /////////////////////


	/**
	 * @requires name != null
	 * @return an InputStream
	 */
	private InputStream getImage(String name){
		String path = "img" + System.getProperty("file.separator") + name;
		InputStream is = GraphicUI.class.getResourceAsStream(path);
		return is;

	}  ////////////////  end of getImage()  /////////////////////

	private void updateTurnDisplay(){

		if (controller.isNextWhite() != null && controller.isNextWhite()) {		
			white.setBackground(LIGHT_BLUE); 
			whiteNameLabel.setBackground(LIGHT_BLUE);
			whiteTimer.setBackground(LIGHT_BLUE);
			black.setBackground(DEFAULT);  
			blackNameLabel.setBackground(DEFAULT);
			blackTimer.setBackground(DEFAULT);

		} else if (controller.isNextWhite() != null){
			white.setBackground(DEFAULT); 
			whiteNameLabel.setBackground(DEFAULT);
			whiteTimer.setBackground(DEFAULT);
			black.setBackground(LIGHT_BLUE);  
			blackNameLabel.setBackground(LIGHT_BLUE);
			blackTimer.setBackground(LIGHT_BLUE);

		}
	} ////////////////  end of updateTurnDisplay()  /////////////////////

	/**
	 * @effects updates the captured pieces display in the GUI
	 */
	private void updateCapturedPiecesDisplay() {

		List<Piece> whiteCapturedList = new ArrayList<Piece>(controller.getCapturedPieces(true));
		List<Piece> whiteCapturedSortedList = new ArrayList<Piece>();
		for (String type : controller.getRuleSet().pieceFactory().getOrderedPieces()) {
			for (int i = whiteCapturedList.size()-1 ; i >= 0 ; i--) {
				Piece p = whiteCapturedList.get(i);
				if (p.getType().equals(type)) {
					whiteCapturedSortedList.add(p);
					whiteCapturedList.remove(i);
				}
			}
		}

		int wcount = 0;
		for (Piece p : whiteCapturedSortedList) {
			if (wcount > 14) {
				whiteCapturedPiecesMap.get(15).setText("...");
			} else {
				String file = ImageFileName.parse(p.getType(), "white", "small").getFileName();
				if (whiteCapturedPiecesMap.get(wcount).getImage() != null) 
					whiteCapturedPiecesMap.get(wcount).getImage().dispose();

				whiteCapturedPiecesMap.get(wcount).setImage(new Image(display, getImage(file)));
				wcount++;
			}
		}

		List<Piece> blackCapturedList = new ArrayList<Piece>(
				controller.getCapturedPieces(false));
		List<Piece> blackCapturedSortedList = new ArrayList<Piece>();
		for (String type : controller.getRuleSet().pieceFactory().getOrderedPieces()) {
			for (int i = blackCapturedList.size()-1 ; i >= 0 ; i--) {
				Piece p = blackCapturedList.get(i);
				if (p.getType().equals(type)) {
					blackCapturedSortedList.add(p);
					blackCapturedList.remove(i);
				}
			}
		}

		int bcount = 0;
		for (Piece p : blackCapturedSortedList) {
			if (bcount > 14) {
				blackCapturedPiecesMap.get(15).setText("...");
			} else {
				String file = ImageFileName.parse(p.getType(), "black", "small").getFileName();
				if (blackCapturedPiecesMap.get(bcount).getImage() != null)
					blackCapturedPiecesMap.get(bcount).getImage().dispose(); 
				blackCapturedPiecesMap.get(bcount).setImage(new Image(display, getImage(file)));
				bcount++;
			}
		}
	} ////////////////  end of updateCapturedPiecesDisplay()  /////////////////////


	private void animateFall(String color, String ply) {
		int[] cell = CoordinateParser.parseString(ply);
		String cellString = Arrays.toString(cell);
		List<String> allCellsInColumn = new ArrayList<String>();
		char c = cellString.charAt(1);
		for (String square : squares.keySet()) {
			if (square.charAt(1) == c) 
				allCellsInColumn.add(square);
		}
		Collections.sort(allCellsInColumn);
		Collections.reverse(allCellsInColumn);
		for (int i = C4_NUMBER_OF_ROWS-1 ; i > cell[1] ; i--) {
			int[] f = new int[]{cell[0], i};
			if (color.equals("t")) 
				drawPiece(f, "chip", "white" );
			else 
				drawPiece(f, "chip", "black" );
			try { Thread.sleep(50); } catch (Exception e) {}
			erasePiece(f);
		}
	}


	/**
	 * @return a string representing a move in the game
	 * @effects allows the GUI to wait for a movement action from the user
	 */
	public String submitPly() throws InterruptedException {

		synchronized(submitPlyLock) {

			display.asyncExec(new Runnable() {
				public void run() {

					setUpBoardForMove(controller.getValidPlies());
				}
			});
			try {
				while (move == null) {
					submitPlyLock.wait();
				}
				return move;
			} finally {
				move = null;
			}
		}

	} ////////////////  end of submitPly()  /////////////////////


	/**
	 * @requires ply != null
	 * @effects updates the GUI in order to reflect the status of the game as 
	 * informed by the controller
	 */
	public void inform(String ply){

		//synchronization over this ensures that no two methods update the 
		//GUI at the same time. For example, initialize game wants to ensure 
		//that he has priviledges to write on the GUI when no one else will, 
		//so he can synchronize over this.

		//synchronizatino over ACC.class ensures that these methods are not 
		//called at the same time. methods infom, submitPly are all 
		//synchronized over this object since in that way we ensure that only
		//one of them is running at a time. 

//		synchronized(org.eclipse.swt.accessibility.ACC.class) {


		lastMoveExecuted = ply;
		display.syncExec(new Runnable() {
			public void run() {

				if (connectNInPlay) {

					String[] plyArray = lastMoveExecuted.split("\\+");
					lastMoveExecuted = plyArray[1];

					for (Label l : upperBoardLabelsMap.values()) {
						if (l.getImage() != null) {
							l.getImage().dispose();
							l.setImage(null);
							l.update();
						}
					}

					animateFall(plyArray[0], plyArray[1]);

				}



				updateTurnDisplay();
				List<Boolean> turnHistory = controller.getTurnHistory();
				int turnHistorySize = turnHistory.size();
				if (turnHistorySize == 0) {
					TableItem item = new TableItem(moveHistory, SWT.NONE);
					item.setText(0, lastMoveExecuted);
					moveHistory.showItem(item);
				} else if (turnHistory.get(turnHistorySize -1) == true) {
					TableItem item = new TableItem(moveHistory, SWT.NONE);
					item.setText(0, lastMoveExecuted);
					moveHistory.showItem(item);
				} else if (turnHistorySize > 1 && turnHistory.get(turnHistorySize-1) == false
						&& turnHistory.get(turnHistorySize-2) == false) {
					TableItem item = new TableItem(moveHistory, SWT.NONE);
					item.setText(1, lastMoveExecuted); 
					moveHistory.showItem(item);
				} else if (turnHistorySize > 0){
					TableItem lastItem = moveHistory.getItem(moveHistory.getItemCount() - 1);
					lastItem.setText(1, lastMoveExecuted);
					moveHistory.showItem(lastItem);
				}

				eraseBoard();
				Board board = controller.getBoard();		
				for (Piece piece : board.getPieces(true)) {
					drawPiece(board.getPosition(piece), piece.getType(), "white");

				}
				for (Piece piece : board.getPieces(false)) {
					drawPiece(board.getPosition(piece), piece.getType(), "black");
				}
				if (antichessInPlay) {						
					updateCapturedPiecesDisplay();
					if (lastMoveStart != null && lastMoveEnd != null) {
						undohighlightLastMove(lastMoveStart, lastMoveEnd);	
					}
					int[] start = CoordinateParser.parseString(lastMoveExecuted.substring(0,2));
					int[] end = CoordinateParser.parseString(lastMoveExecuted.substring(3));
					lastMoveStart = Arrays.toString(start); 
					lastMoveEnd = Arrays.toString(end);
					if (highlightLastMoveOpt) {
						highlightLastMove(lastMoveStart, lastMoveEnd);
					}
				}
			}
		});	



		//	}
	}  ////////////////  end of inform()  /////////////////////



	/**
	 * @requires termination != null
	 * @effects the GUI displays information about the termination of the game
	 */
	public void inform(GameTermination termination) {

//		synchronized(org.eclipse.swt.accessibility.ACC.class) {
		gameTermination = termination;		
		display.asyncExec(new Runnable() {
			public void run() {
				disablePieceMover(new ArrayList<String>(squares.keySet()));
				MessageBox messageBox = new MessageBox(shell, SWT.ICON_INFORMATION);
				Boolean winnerIsWhite = gameTermination.winnerIsWhite();
				if (winnerIsWhite == null) {
					messageBox.setText("Draw");
				} else {
					String player;
					if (antichessInPlay){
						if (winnerIsWhite) {
							player = "Black";
						} else {
							player = "White";
						}
					} else if (connectNInPlay) {
						if (winnerIsWhite) {
							player = "Black";
						} else {
							player = "Red";
						}
					} else {
						throw new RuntimeException("No other game possible");
					}

					messageBox.setText(player + " player got pawned");
				}
				messageBox.setMessage(gameTermination.toString());
				if (whiteTimerThread != null && whiteTimerThread.isAlive())
					whiteTimerThread.interrupt(); 

				if (blackTimerThread != null && blackTimerThread.isAlive())
					blackTimerThread.interrupt(); 

				endGame.setEnabled(false);
				endGameButton.setEnabled(false);
				disablePieceMover(new ArrayList<String>(squares.keySet()));
//				cleanAllMoveVariables(); TODO should this be here?
				messageBox.open();
			}});
//		}
	}  ////////////////  end of inform()  /////////////////////

	/**
	 * The client is not supposed to set the controller of the GUI
	 * @throws RunTimeException if the client tries to set the controller of
	 * the GUI
	 */
	public void setController(Controller controller) {
		throw new RuntimeException("Cannot set Controller of GraphicUI");	
	}  ////////////////  end of setController()  /////////////////////


}  ////////////////  end of GraphiUI  /////////////////////


