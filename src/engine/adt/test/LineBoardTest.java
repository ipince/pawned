package engine.adt.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import engine.adt.Action;
import engine.adt.Board;
import engine.adt.Piece;
import engine.adt.Ply;
import engine.exception.UnusableCellException;

import junit.framework.TestCase;

/**
 * Unit tests for the Board class.
 */
public class LineBoardTest extends TestCase {

	/**
	 * Tests that a blank board has no pieces and has empty 
	 * and blocked (unusable) cells as specified by LineBoard.
	 */
	public void testBlankBoard() { 
		Board board = new LineBoard(); 
		
		assertFalse(board.getPieces(true).iterator().hasNext());
		assertFalse(board.getPieces(false).iterator().hasNext());
		
		// Check that 1,2 are empty
		assertFalse(board.isEmpty(new int[] { 0 }));
		assertTrue(board.isEmpty(new int[] { 1 }));
		assertTrue(board.isEmpty(new int[] { 2 }));
		assertFalse(board.isEmpty(new int[] { 3 }));
		assertFalse(board.isEmpty(new int[] { 0,1 }));
		
		// Empty = Usable here, since all usable places are empty
		assertFalse(board.isUsable(new int[] { 0 }));
		assertTrue(board.isUsable(new int[] { 1 }));
		assertTrue(board.isUsable(new int[] { 2 }));
		assertFalse(board.isUsable(new int[] { 3 }));
		assertFalse(board.isUsable(new int[] { 0,1 }));
		
		// Our board shouldn't contain anything
		assertFalse(board.contains(new CountingPiece(false, board))); 
		assertFalse(board.contains(null)); 
		
		// Again, no pieces
		assertNull( board.getPiece(new int[] {0})); 
		assertNull( board.getPiece(new int[] {2})); 
		assertNull( board.getPiece(new int[] {3})); 

		// Nothing happens
		board.removePiece(new CountingPiece(false, board)); 
	}
	
	/** 
	 * Tests adding one piece in an illegal position
	 */
	public void testOneIllegal() {
		LineBoard board = new LineBoard(); 
		Piece toAdd = new CountingPiece(false, board);
		
		try {
			board.addPiece(toAdd, new int[]{0}); 
		}
		catch (UnusableCellException uce) {
			//Try adding piece not assigned to the board
			Piece fakeAdd = new CountingPiece(false, new LineBoard());
			try { 
				board.addPiece(fakeAdd, new int[]{1});
			}
			catch (RuntimeException rte) {
				try { 
					//Try adding piece with null board 
					board.addPiece(null, new int[]{1});
				}
				catch (RuntimeException rte2) {
					return; 
				}
				fail("Piece added with null board assignment");
			}
			fail("Piece added with wrong board assignment"); 
		}
		
		fail("Piece added at unusable cell"); 
		
	}
	
	
	/**
	 * Try adding a piece and verifying that 
	 * it got added and notified. Execute some 
	 * stuff while the piece is there. 
	 */
	public void testOneGood() { 
		LineBoard board = new LineBoard(); 
		CountingPiece toAdd = new CountingPiece(false, board);

		board.addPiece(toAdd, new int[]{1});
		
		
		//Check if the object stored is the same
		assertSame("Retrived piece is not equal to our original piece", 
				toAdd, board.getPiece(new int[]{1}));
		assertNotSame("Sanity check", new CountingPiece(false, board), 
				board.getPiece(new int[]{1})); 
		
		//Check if piece got notified about right stuff
		assertEquals(1,toAdd.timesAdded); 
		assertEquals(0,toAdd.timesRemoved); 
		assertEquals(0,toAdd.timesPly); 
		
		//Try to add piece in same spot 
		try { 
			board.addPiece(toAdd, new int[]{1});		
		}
		catch (RuntimeException re) {
			assertEquals(1,toAdd.timesAdded); 
			assertEquals(0,toAdd.timesRemoved); 
			assertEquals(0,toAdd.timesPly); 
			
			//Check no notifications happened
			board.removePiece(new CountingPiece(false, board)); 
			assertEquals(1,toAdd.timesAdded); 
			assertEquals(0,toAdd.timesRemoved); 
			assertEquals(0,toAdd.timesPly); 
			
			board.removePiece(null);
			
			return;
		}
		fail("Board addition did not fail when it should have"); 
	}
	
	/** 
	 * Tests adding one piece and plies.
	 */
	public void testOnePly() {

		final List<Action> list = Arrays.asList(Action.makeRemove(new int[] {1}), 
												Action.makeAdd(new int[] {2}, null)); 
		
		Ply ply = new Ply() {

			@Override
			public Iterator<Action> iterator() {
				return list.iterator();
			}

			@Override
			public String toString() {
				return "Cool ply"; 
			}
			
		};
		
		LineBoard board = new LineBoard(); 
		CountingPiece toAdd = new CountingPiece(false, board);

		board.addPiece(toAdd, new int[]{1});

		Collection<Piece> set = board.executePly(ply);
		assertEquals(set.size(), 0); 
		
		assertTrue(board.isEmpty(new int[] {1})); 
		assertFalse(board.isEmpty(new int[] {2})); 

		//Check that , although the piece was removed, 
		//he did not get notified. 
		assertEquals(1,toAdd.timesAdded); 
		assertEquals(0,toAdd.timesRemoved); 
		assertEquals(1,toAdd.timesPly); 
		

		final List<Action> list2 = Arrays.asList(Action.makeRemove(new int[] {2})); 
		Ply ply2 = new Ply() {

			@Override
			public Iterator<Action> iterator() {
				return list2.iterator();
			}

			@Override
			public String toString() {
				return "Cool ply"; 
			} 
			
		};
		
		Collection<Piece> pieces2 = board.executePly(ply2);
	
		//He should be removed now 
		assertEquals(1, pieces2.size()); 
		//And should be the same 
		assertSame(toAdd, pieces2.iterator().next()); 
		
		//Now the piece has to be removed
		assertEquals(1,toAdd.timesAdded); 
		assertEquals(1,toAdd.timesRemoved); 
		assertEquals(2,toAdd.timesPly); 

		final List<Action> list3 = Arrays.asList(Action.makeRemove(new int[] {3})); 
		Ply ply3 = new Ply() {

			@Override
			public Iterator<Action> iterator() {
				return list3.iterator();
			}

			@Override
			public String toString() {
				return "Cool ply"; 
			} 
			
		};
		
		pieces2 = board.executePly(ply3);

		
		//Ply should do nothing
		assertTrue(pieces2.isEmpty()); 
		
		assertEquals(1,toAdd.timesAdded); 
		assertEquals(1,toAdd.timesRemoved); 
		assertEquals(2,toAdd.timesPly); 
	}
	

	/** 
	 * Test "Add piece ply" (Adds three pieces) 
	 * and that pieces are included in white list of
	 * pieces.
	 */
	 public void testSeveral() { 
			LineBoard board = new LineBoard(); 
			CountingPiece toAdd = new CountingPiece(false, board);
			CountingPiece toAdd2 = new CountingPiece(false, board);
			CountingPiece toAdd3 = new CountingPiece(false, board);

			board.addPiece(toAdd, new int[]{1});
			board.addPiece(toAdd2, new int[]{2});
			try { 
				board.addPiece(toAdd3, new int[]{2});
			}
			catch (RuntimeException e) {
				assertEquals(toAdd.timesAdded, 1); 
				assertEquals(toAdd2.timesAdded, 1); 
				assertEquals(toAdd3.timesAdded, 0); 
				
				assertEquals(toAdd.timesRemoved, 0); 
				assertEquals(toAdd2.timesRemoved, 0); 
				assertEquals(toAdd3.timesRemoved, 0); 
				
				assertEquals(toAdd.timesPly, 0); 
				assertEquals(toAdd2.timesPly, 0); 
				assertEquals(toAdd3.timesPly, 0); 
				
				return;
			}
			fail("Execution not stopped");
	 }
	 
	 
	 /** 
	  * Test "Add piece ply" (Adds three pieces) 
	  * and that pieces are included in white list of
	  * pieces 
	  */
	 public void testPiece() { 

		 LineBoard board = new LineBoard(); 
			CountingPiece toAdd = new CountingPiece(false, board);
			CountingPiece toAdd2 = new CountingPiece(false, board);
			
			CountingPiece toAdd3 = new CountingPiece(true, board);
			CountingPiece toAdd4 = new CountingPiece(true, board);

			board.addPiece(toAdd, new int[] { 1}); 
			board.addPiece(toAdd2, new int[] { 2}); 
		 
		 
			final List<Action> list = Arrays.asList
		    (Action.makeRemove(new int[] {1}), 
			Action.makeRemove(new int[] {2}),
			Action.makeAdd(new int[] {1}, toAdd3),
			Action.makeAdd(new int[] {2}, toAdd4)); 
	 
		Ply ply = new Ply() {

			@Override
			public Iterator<Action> iterator() {
				return list.iterator();
			}

			@Override
			public String toString() {
				return "SwapFest"; 
			} 
			
		};
		
		//Check our pieces got collected		
		Collection<Piece> pieces = board.executePly(ply); 
		assertEquals(pieces.size(), 2); 
		assertTrue(pieces.contains(toAdd));
		assertTrue(pieces.contains(toAdd2)); 
		
		
		//Check ourp ieces are no longer in the board
		assertFalse(board.contains(toAdd));
		assertFalse(board.contains(toAdd2));
		//But it should contain the new ones 
		assertTrue(board.contains(toAdd3));
		assertTrue(board.contains(toAdd4));
		
		
		//Now check messages 
		assertEquals(toAdd.timesRemoved, 1); 
		assertEquals(toAdd2.timesRemoved, 1); 
		assertEquals(toAdd3.timesRemoved, 0); 
		assertEquals(toAdd4.timesRemoved, 0); 

		assertEquals(toAdd.timesAdded, 1); 
		assertEquals(toAdd2.timesAdded, 1); 
		assertEquals(toAdd3.timesAdded, 1); 
		assertEquals(toAdd4.timesAdded, 1); 
		
		assertEquals(toAdd.timesPly, 1); 
		assertEquals(toAdd2.timesPly, 1); 
		assertEquals(toAdd3.timesPly, 0); 
		assertEquals(toAdd4.timesPly, 0); 
		
		//Check that the old ones are actually gone
		assertNull(board.getPosition(toAdd));
		assertNull(board.getPosition(toAdd2));
		assertNotNull(board.getPosition(toAdd3));
		assertNotNull(board.getPosition(toAdd4));
		
		assertTrue(Arrays.equals(new int[]{2}, board.getPosition(toAdd4)));
		
		//Check list of white and black 
		assertEquals(false, board.getPieces(false).iterator().hasNext()); 
		assertEquals(true, board.getPieces(true).iterator().hasNext()); 
		
		Iterator<Piece> whites = board.getPieces(true).iterator(); 
		Piece p1 = whites.next(); 
		Piece p2 = whites.next(); 
		
		assertTrue(p1 != p2); 
		assertFalse(whites.hasNext()); 
		assertTrue(p1 == toAdd4 || p1 == toAdd3);
		assertTrue(p2 == toAdd4 || p2 == toAdd3);
		
	 }

	 
	 /** 
	  * Tests that plies that are invalid throw a RuntimeException
	  *
	  */
	 public void testInvalidPly() {
		 //Try adding an initial piece that doesn't exist
		 
		Board bd = new LineBoard(); 
		CountingPiece pc = new CountingPiece(true, bd); 
		
		final List<Action> list = Arrays.asList(
			Action.makeAdd(new int[] {1}, null),
			Action.makeAdd(new int[] {2}, pc)); 
		
		Ply ply = new Ply() {

			@Override
			public Iterator<Action> iterator() {
				return list.iterator();
			}

			@Override
			public String toString() {
				return "Not working ply";
			}
			
		};
		
		
		try {
			bd.executePly(ply);
		}
		catch(RuntimeException re) {
			assertFalse(bd.contains(pc));
			return;
		}
		
		fail("Fails to recognize an invalid ply");
	 }
	 /** 
	  * Tests "Piece swapping ply" and
	  * that the pieces are included in the
	  * black list of pieces 
	  *
	  */

	 public void testSwapPly() { 
			final List<Action> list = Arrays.asList
			    (Action.makeRemove(new int[] {1}), 
				Action.makeRemove(new int[] {2}),
				Action.makeAdd(new int[] {1}, null),
				Action.makeAdd(new int[] {2}, null)); 
		 
			Ply ply = new Ply() {

				@Override
				public Iterator<Action> iterator() {
					return list.iterator();
				}

				@Override
				public String toString() {
					return "SwapFest"; 
				} 
				
			};
			
			LineBoard board = new LineBoard(); 
			CountingPiece toAdd = new CountingPiece(false, board);
			CountingPiece toAdd2 = new CountingPiece(false, board);
			
			board.addPiece(toAdd, new int[] { 1}); 
			board.addPiece(toAdd2, new int[] { 2}); 
			
			//Check no pieces got collected			
			assertEquals(board.executePly(ply).size(), 0); 
			assertFalse(board.getPieces(true).iterator().hasNext());
			
			
			//Check pieces got swapped
			assertSame(toAdd2, board.getPiece(new int[] {1})); 
			assertNotSame(toAdd2, board.getPiece(new int[] {2})); 
			assertSame(toAdd, board.getPiece(new int[] {2})); 
			assertNotSame(toAdd, board.getPiece(new int[] {1})); 
			
			
			//Check iterators on pieces
			Iterator<Piece> blacks = board.getPieces(false).iterator(); 
			Piece p1 = blacks.next(); 
			Piece p2 = blacks.next(); 
			
			assertTrue(p1 != p2); 
			assertFalse(blacks.hasNext()); 
			assertTrue(p1 == toAdd || p1 == toAdd2);
			assertTrue(p2 == toAdd || p2 == toAdd2);
			
			//Check messages on pieces 
			assertEquals(toAdd.timesAdded, 1); 
			assertEquals(toAdd2.timesAdded, 1); 
			assertEquals(toAdd.timesRemoved, 0); 
			assertEquals(toAdd2.timesRemoved, 0); 
			assertEquals(toAdd.timesPly, 1); 
			assertEquals(toAdd2.timesPly, 1); 
			
			//Now remove one and check messages again
			
			board.removePiece(toAdd); 
			assertEquals(toAdd.timesAdded, 1); 
			assertEquals(toAdd2.timesAdded, 1); 
			assertEquals(toAdd.timesRemoved, 1); 
			assertEquals(toAdd2.timesRemoved, 0); 
			assertEquals(toAdd.timesPly, 1); 
			assertEquals(toAdd2.timesPly, 1); 
			
			
	 }
	 
	 /** 
	  * Clones, checking that there is no referencial equality between 
	  * 
	  *
	  */
	 public void testClone() {
		 LineBoard board = new LineBoard(); 
		 CountingPiece toAdd = new CountingPiece(true, board);
		 CountingPiece toAdd2 = new CountingPiece(false, board);

		 board.addPiece(toAdd, new int[] { 1}); 
		 board.addPiece(toAdd2, new int[] { 2}); 

		 assertTrue(board.contains(toAdd)); // sanity check
		 assertTrue(board.contains(toAdd2)); // sanity check
		 
		 LineBoard clonedBoard = board.clone();
		 
		 assertFalse("Board failed to clone itself and return a different Board",
				 clonedBoard == board);
		 assertTrue("Board failed to clone its attributes",
				 board.getPieces(true).size()==clonedBoard.getPieces(true).size());
		 assertTrue("Board failed to clone its attributes",
				 board.getPieces(false).size()==clonedBoard.getPieces(false).size());
		 assertFalse("Board failed to clone its attributes",
				 clonedBoard.isUsable(new int[] {0}));
		 assertFalse("Board failed to reproduce its Pieces for cloning",
				 clonedBoard.contains(toAdd));
		 assertFalse("Board failed to reproduce its Pieces for cloning",
				 clonedBoard.contains(toAdd2));
		 assertFalse("Board failed to add the Pieces to its clone at the same cell",
				 clonedBoard.isEmpty(new int[] {1}));
		 assertFalse("Board failed to add the Pieces to its clone at the same cell",
				 clonedBoard.isEmpty(new int[] {2}));
		 assertTrue("Board modified itself when cloning",
				 board.contains(toAdd));
		 assertTrue("Board modified itself when cloning",
				 board.contains(toAdd2));
		 assertTrue("Board failed to add the same type of Piece",
				 clonedBoard.getPiece(board.getPosition(toAdd)).toString().equals(toAdd.toString()));
		 assertFalse("Board failed to clone its rep",
				 clonedBoard.getCell(new int[] {2})==board.getCell(new int[] {2}));
		 
		 clonedBoard.removePiece(clonedBoard.getPiece(new int[] {2}));
		 		 
		 assertTrue(clonedBoard.isEmpty(new int[] {2}));
		 assertFalse(board.isEmpty(new int[] {2}));

	 }
	 
	 public void testMutability() {
		 fail("Not yet implemented");
	 }
}