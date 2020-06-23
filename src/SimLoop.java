/**
 * This class acts as the loop that runs in the background of the 
 * simulation which forces simulation logic to be handled and 
 * executed in time steps rather than the run time of the hardware.
 * 
 * Author: Tomas L. Dougan
 * Date of last modification: 23 June 2020
 */

package nbodysim;

public abstract class SimLoop implements Runnable{
	
	private boolean running;
	
	/** Constructor: Set running to true.*/
	public SimLoop(){
		running = true;
	}
	
	/** Begin execution of the loop.*/
	@Override
	public void run(){
		loop();
	}
	
	/** 
	 * Execute a standard variable time-step loop that pauses the
	 * loop until the frame has passed.
	 */
	public void loop(){
		final int MAX_FRAMES_PER_SECOND = 60;
		final long OPTIMAL_TIME = 1000000000 // One second in nanoseconds
				/ MAX_FRAMES_PER_SECOND;
		double deltaTime = 0;
		double elapsedTime = 0;
		double lastLoopTime = 0;
		int frames = 0;
		long startTime = System.nanoTime();
		while (running){
			/*
			 * Find how much time has elapsed since the last
			 * update.
			 */
			long now = System.nanoTime();
			elapsedTime = now - startTime;		
			startTime = System.nanoTime();
			/*
			 * Delta time will represent the accuracy of the
			 * actual speed it takes to run the loop so that
			 * the physics updates don't move things farther than
			 * how much time has passed.
			 */
			deltaTime = elapsedTime / OPTIMAL_TIME;
			lastLoopTime += elapsedTime;
			// Count the frame-rate properly
			frames++;
			if (lastLoopTime >= 1000000000){
				lastLoopTime = 0;
				frames = 0;
			}
			/*
			 * Physics updates are passed the discrepancy between
			 * time passed and time in between updates to account 
			 * for latency.  This is so that objects in the simulation
			 * with time-based motion move consistently despite 
			 * slight variations in hardware speed.
			 */
			update(deltaTime);
			// Render the graphics
			render();
			/*
			 *  Timing mechanism: wait for the remainder of 
			 *  the time left to finish the frame before moving 
			 *  on to the next one.
			 */
			try {
				Thread.sleep(Math.abs((startTime-System.nanoTime() 
						+ OPTIMAL_TIME)/1000000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 
	 * Make updates to physics at every frame in proportion to
	 * how much time has passed in between updates.
	 * 
	 * @param the ratio of time elapsed to the expected time
	 *   it takes to elapse
	 */
	abstract void update(double deltaTime);
	
	/** Make updates to graphics at every frame.*/
	abstract void render();
}
