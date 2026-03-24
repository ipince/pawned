# Pawned

A game engine for Antichess (and other board games).

Written in 2007 as a final project for MIT's 6.170 (Software Design). The
objective was to build an Antichess game — like chess, but your goal is to
lose all your pieces, and you must capture whenever possible. We went beyond
that and built a general-purpose board game engine, with Antichess implemented
on top of it. To demonstrate the design, we also implemented Connect-N
(a generalized Connect Four) on the same engine. Won the Best Design award
that semester.

Authors: Rodrigo Ipince, Jose Alberto Muniz

## Getting Started

### Prerequisites

Install [Devbox](https://www.jetpack.io/devbox/). Then from the project root:

```
devbox shell
```

This installs JDK 21 and drops you into a shell with everything you need.

### Build

```
devbox run build
```

### Run

```
devbox run run
```

This starts the text-based interface. Commands:

- `StartNewGame [white] [time] [black] [time]` — Start a game. Each player
  is `human` or `computer`. Time is in milliseconds (0 for untimed).
  Example: `StartNewGame human 0 computer 0`
- `PrintBoard` — Print the board state (XML format).
- `PrintAllMoves` — List all legal moves for the current player.
- `MakeMove [move] [time]` — Make a human move. Example: `MakeMove e2-e3 0`
- `GetNextMove` — Compute the computer's next move.
- `MakeNextMove` — Execute the computer's computed move.
- `IsLegalMove [move]` — Check if a move is legal.
- `GetTime [white|black]` — Show remaining time.
- `SaveGame [filename]` / `LoadGame [filename]` — Save/load game as XML.
- `QuitGame` — Exit.

### Debug

```
devbox run debug
```

Same as `run`, but in debug mode: prints a visual board representation
(instead of XML) and echoes comment lines starting with `#`.

## Code Organization

All source code lives under `src/`. The project is organized into layers:

```
src/
├── engine/          Core game engine (game-agnostic)
│   ├── adt/         Abstract data types: Board, Piece, Ply, Action, RuleSet
│   ├── game/        Game state, history, and termination
│   ├── player/      Player interface and GameObserver
│   └── exception/   Board and position exceptions
│
├── ruleset/         Game-specific rule implementations
│   ├── antichess/   Antichess rules (StandardAC, EnCastleAC)
│   ├── board/       RectangularBoard, coordinate parsing, XML board factory
│   ├── piece/       Chess pieces (King, Queen, Rook, Bishop, Knight, Pawn)
│   │                and Connect-N pieces (GravityChip)
│   ├── ply/         Move types (Move, Castle, EnPassant, Coronation, Add)
│   ├── connectn/    Connect-N rules
│   └── eval/        Board evaluation heuristics for the AI
│
├── player/          AI player implementations
│   ├── AIPlayer     Minimax + alpha-beta pruning search
│   ├── GameSearcher Game tree search algorithm
│   └── Evaluator    Board evaluation interface
│
├── controller/      Game control layer
│   ├── Controller   Referee: links players to game, manages timers, executes moves
│   ├── StopWatch    Per-player timer
│   └── XmlFactory   Game save/load (XML serialization)
│
├── interfaces/      User interfaces
│   ├── TextUI       Text-based interactive interface (the main entry point)
│   └── GraphicUI    SWT-based GUI (currently excluded from build; needs SWT)
│
├── net/             Stubs for the original MIT antichess server API
│
└── debug/           Debug utilities and master test suite
```

### How It Works

The engine is designed around a few core abstractions in `engine/adt/`:

- **Board** — An N-dimensional grid of cells. Each cell can hold at most one
  Piece. The board is synchronized for thread safety. Supports undo via an
  internal action stack.
- **Piece** — An abstract class associated with a specific Board. Each piece
  knows how to generate its list of valid Plies given the current board state.
- **Ply** — A sequence of Actions (add, remove, move a piece) representing
  one player's turn.
- **RuleSet** — Defines everything needed for a specific game: which pieces
  exist (PieceFactory), how to parse moves (PlyFactory), how to create boards
  (BoardFactory), what the valid plies are, and when the game ends.

A **Game** (`engine/game/`) holds a Board, tracks turn history, and validates
and executes plies according to a RuleSet. When a termination condition is met,
it throws a `GameTermination`.

The **Controller** (`controller/`) acts as a referee, connecting two Players to
a Game. It manages timers, alternates turns, and routes moves between players.

The **AI** (`player/`) uses minimax search with alpha-beta pruning
(`GameSearcher`) to evaluate game trees. It's game-agnostic — it works with
any RuleSet, given an appropriate `Evaluator` that scores board positions.
The antichess AI searches to depth 2; Connect-N uses depth 4.

### Implementing a New Game

The engine's extensibility is demonstrated by having two games on one engine.
To add a new game, implement:

1. A `RuleSet` with its factories (pieces, plies, boards)
2. Concrete `Piece` subclasses with move generation logic
3. Concrete `Ply` subclasses for the game's move types
4. An `Evaluator` for AI play

See `ruleset/antichess/` and `ruleset/connectn/` for examples.

## Notes

- The GraphicUI (SWT-based) is excluded from the build. It would need
  platform-specific SWT JARs for macOS to work.
- The `net/antichess/ai/` package contains stub interfaces replacing the
  original `antichess-ai.jar` from MIT's antichess server (no longer available).
- This code was originally managed in CVS, then imported to Git.
