package controller;

import java.util.Vector;

import java.util.List;
	
	/**
	 * <p>A StopWatch represents a scheduling device that has 
	 * the ability of being paused and resumed at any point 
	 * during its execution. After its time expires, a 
	 * StopWatch executes a <tt>Runnable</tt> task. </p>
	 * 
	 * <p>A StopWatch should be seen as a low-precision measuring
	 * device. If the desired scheduling needs to be precise
	 * in the order of milliseconds, a StopWatch might be unable
	 * to provide the desired precision. The computer platform,
	 * along with the amount of threads running at a given time 
	 * can vary the precision of a StopWatch. </p>
	 *
	 * 
	 * @specfield timeRemaining : number // the time in milliseconds 
	 * 									    remaining in this watch
	 * @specfield state : state // the state of the current 
	 *                             watch (running or paused)
	 * @specfield task //the task to be executed after the time 
	 *            passes 
	 */
	public class StopWatch extends Thread {
		
		//Let delta(t) = abs( current time - t )
		//
		//AF(x) 
		// Abstraction Function:
		//    AF(c) = stop watch SW such that
		//
		//      SW.state = paused if x.lastChecked = -1
        //               = unpaused otherwise
		//
		//      SW.timeRemaining  = x.remaining if SW.state = paused
		//                        = x.remaining - delta(x.lastChecked)
		//
		//      SW.task = x.runnable 

		// Rep Invariant:
		// * runnable != null
		// * 
		//    + Let T_o be the initial time at which the clock was
		//      started and T_f be the actual time. 
		//    + Let {r_n} be the sequence of values set for
		//      remaining. Then S = sum {r_{k} - r{k-1}} from k = 2 to n
		//      is the total time that has been consumed from the stopwatch.
		//    Then, 
		//      T_f = T_o + S + P, where P is the total sum of the time 
		//                         when the stopwatch was paused.
		
		private class RepInvariant {
			//the time this clock has remained paused
			int timePaused; 
			//the hour at which the clock was started
			long startingTime; 
			//the hour at which the current pause (if applicable) 
			long currentPause = 0;
			//tolerance for error 
			final static int epsilon = 1000; 

			//the list of values that the variable 'remaining' 
			//has acquired 
			List<Integer> remainderUpdates; 
			public RepInvariant(int duration) {
				this.startingTime = System.currentTimeMillis();
				remainderUpdates = new Vector<Integer>();
				remainderUpdates.add(duration);
			}
			
			public void remaininingUpdated() {
				remainderUpdates.add(remaining);
			}
			public void paused(){
				currentPause = System.currentTimeMillis(); 
			}
			public void resumed(){
				timePaused += (int) System.currentTimeMillis() - currentPause; 
				currentPause = 0;
				
			}
			public void checkRep() {
				
				
				int deltaT = (int) System.currentTimeMillis() - (int) startingTime;
				
				if (Math.abs(deltaT - (timePaused + sumDiff())) > epsilon) 
					throw new RuntimeException("Invariant violated " +  remainderUpdates);
			}
			
			private int sumDiff() {
				if (remainderUpdates.size() < 2)
					return 0; 
								
				int sum = 0;
				for (int i = 2; i < remainderUpdates.size(); i++) {
					int end = remainderUpdates.get(i);
					int start = remainderUpdates.get(i - 1);
					
					sum += (start - end);
				}
					
				return sum;
			}
		}
		
		private volatile int remaining; 
		private volatile long lastChecked; 
		//shouldn't need to be volatile since it's always read 
		//from synchronized context 
		
		private Runnable runnable; 
		private RepInvariant invariant; 
		public StopWatch(int timeInMillis, Runnable run) {
			invariant = new RepInvariant(timeInMillis); 			
			remaining = timeInMillis; 
			lastChecked = -1; 
			runnable = run;

			invariant.remaininingUpdated(); 
			invariant.checkRep(); 
			
		}
	
	
		public void start() {
			synchronized (this) {
				lastChecked = System.currentTimeMillis(); 
				}
			super.start(); 
		}
		
		
		
		
		
		public void run() {
			while (remaining > 0) {
				try {
					synchronized(this) {
						if (isInterrupted()) 
							throw new RuntimeException("Interrupted"); 
						
						if (lastChecked != -1) {
						//System.err.println(remaining);
							long current = System.currentTimeMillis(); 
							remaining -=  (current - lastChecked); 
							lastChecked = current;

							invariant.remaininingUpdated(); 
							invariant.checkRep(); 
						}
					}
					if (remaining > 0) {
						sleep(remaining); 
					}
				}
				catch (Exception ie) {
					if (lastChecked != -1) {
						remaining -= (int)(System.currentTimeMillis() - lastChecked); 
						invariant.remaininingUpdated(); 
						invariant.checkRep(); 
					}
					
					return; 
				}
			}
			//do processing here . we're done :)
			remaining = 0;

			invariant.remaininingUpdated(); 
			invariant.checkRep(); 
			
			lastChecked = -1; 
			new Thread(runnable).start();
			return;
		} 
		
		public synchronized void proceed() {
			if (remaining == 0) {
				return;
			}

			invariant.resumed(); 
			lastChecked = System.currentTimeMillis(); 
		}
		
		public synchronized void pause() {
			if (remaining == 0) {
				return;
			}
			
			remaining -= (System.currentTimeMillis() - lastChecked); 
			lastChecked = -1; 

			invariant.remaininingUpdated(); 
			invariant.paused();
			invariant.checkRep(); 

		}
		
		
		public synchronized int remaining() {
			
			if (lastChecked != -1) {
				return Integer.valueOf("" + (remaining - (System.currentTimeMillis() - lastChecked))); 
			}
			else 
				return Integer.valueOf("" + (remaining));  

				
		}
		
	}