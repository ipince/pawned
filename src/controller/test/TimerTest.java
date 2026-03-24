package controller.test;


import controller.*; 

import junit.framework.TestCase;

public class TimerTest extends TestCase {
	
	
	/** 
	 * Tests that the timer runs and depletes
	 * successfully after time when it is not 
	 * paused
	 * 
	 */
	public void testNoPause(int time) {
		StopWatch watch = new StopWatch(time, null);
		
		long start = System.currentTimeMillis(); 
		
		watch.start();
		
		try {
			watch.join(); 
		}
		catch (InterruptedException ie ) {
			//shouldnt happen 
		}
	
		assertEquals(((System.currentTimeMillis() - start)) , 
				   time, 
				   500);
		
		
		
	}
	
	public void testNoPause() {
		
		//testNoPause(1);
		//testNoPause(10);
		//10 sec
		//testNoPause(10 * 1000);
		//5 sec
		//testNoPause(5 * 1000);
		//1 sec
		//testNoPause(1 * 1000);
		//20 sec
		//testNoPause(20 * 1000);
	}
	
	
	public void testOnePause(int interval, int timeToPause,
							 int pause) {

		long start = System.currentTimeMillis(); 
		
		StopWatch watch = new StopWatch(interval, null);

		System.out.println("start at ms " +(System.currentTimeMillis() - start) );
		watch.start();
		try {
			Thread.sleep(timeToPause); 
		}
		catch(InterruptedException ie) {}

		System.out.println("paused at ms " +(System.currentTimeMillis() - start) );
		watch.pause(); 
		
		try {
			Thread.sleep(pause); 
		}
		catch(InterruptedException ie) {}
		System.out.println("unpausing at ms " +(System.currentTimeMillis() - start) );
		
		watch.proceed(); 

		try {
			watch.join(); 
		}
		catch (InterruptedException ie ) {
			//shouldnt happen 
		}
		System.out.println("ending at ms " +(System.currentTimeMillis() - start) );
		
		assertEquals(interval + pause,(System.currentTimeMillis() - start), 500);
		
	}
	
	
	public void testOnePause() {
		
		testOnePause(1000, 500, 1000);
		testOnePause(1000, 500, 2000);
		testOnePause(2000, 0, 2000);
		
//		for (int i = 0; i < 50; i++)
//			testOnePause(2000, 1, 2000);
		
//		System.out.println(">> pause at 0, end at 2001");
		testOnePause(1, 0, 2000);
//		System.out.println(">> pause at 0, end at 2005");
		testOnePause(5, 0, 2000);
		System.out.println(">> pause at 0, end at 2500");
		testOnePause(500, 0, 2000);
//		System.out.println(">> pause at 0, end at 3000");
		testOnePause(1000, 0, 2000);
		
	}
	
	
	public void testRemainingNoPause(int time) {
		StopWatch watch = new StopWatch(time, null);
		
		System.out.println(watch.remaining()); 
		assertEquals("Remaining time is inconsistent " +
			     "with reality",
			   watch.remaining(), 
			   time, 
			   100);

		long start = System.currentTimeMillis(); 
		watch.start();
		

		while (watch.isAlive()) {
			try {
				Thread.sleep(10);
				assertEquals("Remaining time is inconsistent " +
			     "with reality",
			   time - (System.currentTimeMillis() - start), 
			   watch.remaining(),
			   500);
			}
			catch(Exception e) {}
		}

		
		try {
			watch.join(); 
		}
		catch (InterruptedException ie ) {
			//shouldnt happen 
		}
	
		assertEquals("Stopwatch fails to honor its life time",
				   ((System.currentTimeMillis() - start)) , 
				   time, 
				   500);
		assertEquals("Remaining time is inconsistent" +
			     "with reality",
			   watch.remaining(), 
			   0, 
			   100);		
		
				
	}

	public void testRemainingNoPause() {
		//testRemainingNoPause(5000); 
	}
	
	
}
