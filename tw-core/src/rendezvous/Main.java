package rendezvous;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

	public static void main(String[] args) {
		int BOUND = 10;
		int nsim = 3;
		 
		BlockingQueue<String> expControlQueue = new LinkedBlockingQueue<>(BOUND);
		BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>(BOUND);
		BlockingQueue<String> simControlQueue = new LinkedBlockingQueue<>(BOUND);
		new Thread(new UIController(expControlQueue)).start();
		new Thread(new Experiment(expControlQueue,simControlQueue,nsim)).start();
		new Thread(new DisplayWidget(outputQueue)).start();
		for (int i=0; i<nsim; i++)
			new Thread(new Simulator(simControlQueue,outputQueue,i)).start();
		System.out.println("finished");
	}

}
