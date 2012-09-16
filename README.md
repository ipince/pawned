pawned
======

Pawned game engine for antichess (and other board games).

This code is a bit old (2007).
It is a game engine we wrote for 6.170, our software design class at MIT.
The objective was to build an Antichess game (like chess, but your objective
is to lose all your pieces, and you must capture a piece whenever you can).
We went beyond that and built a more general game engine, with antichess
implemented on top of it. So, for instance, the engine/adt package defines
all the interfaces and abstract classes on which our game engine runs
(e.g. a Board is an arbitrary arrangement of vector-addressed Cells, a Ply
is a sequence of Actions on Pieces, a RuleSet defines the set of valid Plies,
game-terminating conditions, etc). Then, we can define game-specific rules
and pieces, as we do in ruleset/antichess and ruleset/piece. We demonstrated
the benefits of our design by implementing Connect-N on the same engine
(on ruleset/connectn). The game also contains an AI player that uses minimax
and ab-pruning (all the search part of the AI player is rules-agnostic too,
so it works for both antichess and connectn, provided the Evaluators used
make sense for each game). We won the Best Design award that semester.

See README.txt (old, but useful).

Note: This code was recently imported from an old CVS repo. It is only set up
to build with Eclipse (sad, I know). Maybe I'll remove the dependency on Eclipse
some day.