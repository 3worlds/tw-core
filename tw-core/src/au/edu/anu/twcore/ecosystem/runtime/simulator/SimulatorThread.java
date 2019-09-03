package au.edu.anu.twcore.ecosystem.runtime.simulator;

import au.edu.anu.twcore.experiment.runtime.Deployer;

/**
 * The thread in which a simulator is running
 * 
 * @author Jacques Gignoux - 30 août 2019 - copied from Shayne's Simulator$RunningStateThread
 *
 */
public class SimulatorThread implements Runnable {

	private Deployer dep = null;

	public SimulatorThread(Deployer dep) {
		super();
		this.dep = dep;
	}
	
	// code found there: https://stackoverflow.com/questions/16758346/how-pause-and-then-resume-a-thread
	private volatile boolean running = true;
	private volatile boolean paused = false;
	private final Object pauseLock = new Object();
	
    @Override
    public void run() {
        while (running) {
            synchronized (pauseLock) {
                if (!running) { // may have changed while waiting to
                    // synchronize on pauseLock
                    break;
                }
                if (paused) {
                    try {
                        synchronized (pauseLock) {
                            pauseLock.wait(); // will cause this Thread to block until 
                            // another thread calls pauseLock.notifyAll()
                            // Note that calling wait() will 
                            // relinquish the synchronized lock that this 
                            // thread holds on pauseLock so another thread
                            // can acquire the lock to call notifyAll()
                            // (link with explanation below this code)
                        }
                    } catch (InterruptedException ex) {
                        break;
                    }
                    if (!running) { // running might have changed since we paused
                        break;
                    }
                }
            }
            // Your code here
            dep.stepProc();
        }
    }

    public void stop() {
        running = false;
        // you might also want to interrupt() the Thread that is 
        // running this Runnable, too, or perhaps call:
        resume();
        // to unblock
    }

    public void pause() {
        // you may want to throw an IllegalStateException if !running
        paused = true;
    }

    public void resume() {
        synchronized (pauseLock) {
            paused = false;
            pauseLock.notifyAll(); // Unblocks thread
        }
    }

// Shayne's code	    
	
//	private boolean quit = false;
//	private final Object lock = new Object();
//	private Deployer dep = null;
//	
//	
//
//	@Override
//	public void run() {
//		while (runContinue()) {
//			dep.stepProc();
//			Thread.yield();
//		}
//	}
//	
//	public void quit() {
//		synchronized (lock) {
//			quit = true;
//		}
//	}
//	
//	
//	private boolean runContinue() {
//		synchronized (lock) {
//			return !quit;
//		}
//	}

	
	
	
}
