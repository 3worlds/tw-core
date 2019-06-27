package rendezvous2;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Experiment implements Runnable {

	private BlockingQueue<String> controlQueue;
	private BlockingQueue<String> simControlQueue;
	private BlockingQueue<String> outputQueue; // not used here, passed on to simulators
	private BlockingQueue<Runnable> simQueue;
	
	public Experiment(BlockingQueue<String> expqueue,BlockingQueue<String> outputQueue) {
		this.controlQueue = expqueue;
		simControlQueue = new LinkedBlockingQueue<>(50);
		simQueue = new ArrayBlockingQueue<>(10);
		this.outputQueue = outputQueue;
	}
	
	@Override
	public void run() {
		int nsim = 4;
		ExecutorService deployer = new ThreadPoolExecutor(5,10,10,TimeUnit.SECONDS,simQueue);
		for (int i=0; i<nsim; i++)
			deployer.execute(new Simulator(simControlQueue,outputQueue,i));
		try {
			while (true) {
//	            String command = controlQueue.poll(10,TimeUnit.SECONDS);
	            String command = controlQueue.take();
	            if (command!=null) {
		            System.out.println(Thread.currentThread().getName() + " Experiment received " + command);
	            	// nb this will only work for 0<=i<=9
	            	for (int i=0; i<nsim; i++) {
	    	            System.out.println(Thread.currentThread().getName() + " Experiment sent " + command+ " "+i);
	            		simControlQueue.put(command+i);
	            	}
		            if (command.equals("quit")) {
						System.out.println(Thread.currentThread().getName() + " Experiment received quit");
						deployer.shutdownNow();
						return;
		            }
	            }
	            else {
					System.out.println(Thread.currentThread().getName() + " Experiment timed out");
					return;
	            }
	            
	            if (((ThreadPoolExecutor) deployer).getCompletedTaskCount()==nsim) {
	            	deployer.shutdown();
		            System.out.println("Number of active tasks: "+((ThreadPoolExecutor) deployer).getActiveCount());
		            
	            	System.out.println(Thread.currentThread().getName() + " Experiment: all simulations finished");
	            	return;
	            }
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	        System.out.println("coucou");
	    }
		return;
	}

}
