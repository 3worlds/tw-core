package rendezvous;

import java.util.concurrent.BlockingQueue;

// consumer on controlQueue
// producer on simControlQueue
public class Experiment implements Runnable {

	private BlockingQueue<String> controlQueue;
	private BlockingQueue<String> simControlQueue;
	
	private int repeat = 0;

	public Experiment(BlockingQueue<String> expqueue,BlockingQueue<String> simqueue,int nsim) {
		this.controlQueue = expqueue;
		this.simControlQueue = simqueue;
		repeat = nsim;
	}
	
	@Override
	public void run() {
	   try {
	        while (true) {
	            String command = controlQueue.take();
	            System.out.println(Thread.currentThread().getName() + " Experiment received " + command);
            	// nb this will only work for 0<=i<=9
            	for (int i=0; i<repeat; i++) {
    	            System.out.println(Thread.currentThread().getName() + " Experiment sent " + command+ " "+i);
            		simControlQueue.put(command+i);
            	}
	            if (command.equals("quit")) {
					System.out.println(Thread.currentThread().getName() + " Experiment received quit");
	                return;
	            }
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

}
