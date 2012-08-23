OVERVIEW
----------------
Hello and welcome to the Pwned Source Repository! If you are looking for
the Java Web Start application, please refer to
http://web.mit.edu/muniz/www/se46antichess.jnlp


FOR THE IMPATIENT 
----------------
The GUI, and TextUI can be found under interfaces/. 
There are two TextUIs (TextUI.java, TextUI2.java).
TextUI2.java is a nearly-completed TextUI conforming
to the specs. Load-game and save-game are not yet 
implemented (for TextUI). 

The class controlling the AI Player, along with the
class MachinePlayer can be found at engine.player/. 

 

FILE STRUCTURE
---------------
Pawned is structured in the following fashion. 
 * controller/ 
 * engine/
      * adt/ 
      * game/
      * player/ 
      * interfaces/    
 * ruleset/
      * stdac/
      	* pieces/
      	* plies/     
      	


CONTROLLER
-------------
Controller's main class is Controller.java. It is a class representing
the Referee that controls the timers and informs the players of their
turns, executing their moves to the Game. 

ENGINE
------------
+ adt/ Contains the basic structures used by all the parts of the game (e.g. 
  interfaces and abstract classes such as Piece, Board, RuleSet, etc). 

+ game/ Contains Game.java as its main class. It is a class representing 
  a Board Game in execution (state, history, etc). 
  
+ player/ Contains all the (non-GUI) players that subscriber to the controller. 
  As of now, it contains AIPlayer.java, a random move maker, and MachinePlayer.java,
  a Player that simulates the outside moves and submits them to the controller and 
  which conforms to the required specification. 
  
+ interfaces/ Contains TextUI.java, GraphicUI.java, TextUI2.java. 

  GraphicUI is the Graphical User Interface. It is written in SWT, so if 
  the game is not being run from JAWS, it might be necessary to setup
  the classpath to some SWT jars manually, as well as to set the library 
  path to a location containing the native libraries for SWT. All the 
  required features for the preliminary release are included and working
  for this preliminary release. 

  TextUI2 is an alternative Controller-like interface that manages the 
  game autonomously and keeps its timing separately. Other than loading 
  and saving game information, TextUI2 provides the required final 
  features for TextUI. 

  
  TextUI is an attempt to conform to the final specification using the 
  provided standard Controller. For this release, this TextUI is not 
  reliable. For the final release, the Controller is expected to change
  in order to provide a safer and cleaner interaction with its players
  (TextUI and GraphicUI included), and TextUI is expected to blend in
  more nicely with the new Controller. 

+ ruleset/ Contains the classes that are specific to a particular board game.
		   The RectangularBoard can be found here, along with the pieces
		   and rules that are specific to the required variant of antichess
		   (stdac). 
		

Each of these directories has an associated test directory containing 
a set of JUnit Test Cases and Test Drivers for each of the classes 
contained in it. 


TESTING STRATEGY
---------------
JUnit Tests have been successfully run on each of the classes in the ADT 
and stdac ruleset. Some simple artificial boards and pieces were created 
in order to test the non-abstract methods in the Abstract ADTs in
engine/adt. 

Several test drivers were created to test the different classes. A test driver
was written for testing the RectangularBoard. TextUI2 was used to test 
the ruleset (game terminations, check, stalemate, etc., as well as to do 
integration testing up to game). 

The GraphicUI, Controller, Timers and AIPlayer were currently tested by hand. The
Timers were also tested by using a JUnit Test Case, not included in any suite 
since its completion requires several minutes. 

The debug/ directory contains a master test suite, which includes most of the test
cases in the test directories. The rep invariant checks are disabled, and can be 
enabled in the debug/DebugInfo.java file. The level of debugging can be selected 
in case only some invariants want to be tested. 


MISCELLANEOUS
-------------
Feel free to contact us by email with any questions. We hope you enjoy our game. 


_________________________
Last-revision: Tuesday May 01, 2007 