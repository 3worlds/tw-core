package rendezvous;

import java.util.concurrent.BlockingQueue;

// consumer on outputQueue
public class DisplayWidget implements Runnable {

	private BlockingQueue<String> outputQueue;

	public DisplayWidget(BlockingQueue<String> queue) {
		this.outputQueue = queue;
	}

	@Override
	public void run() {
	   try {
	        while (true) {
	            String output = outputQueue.take();
	            if (output.equals("quit")) {
	                return;
	            }
	            System.out.println(Thread.currentThread().getName() + " Display " + output);
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

}
