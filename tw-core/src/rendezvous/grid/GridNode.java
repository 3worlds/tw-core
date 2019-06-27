package rendezvous.grid;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GridNode {
	
	private BlockingQueue<String> rendezvousList = new LinkedBlockingQueue<>(10);
	
	public GridNode() {
	
	}
	
	public void addRendezvous(String rendezvous) {
		rendezvousList.add(rendezvous);
	}
	
	public synchronized void callRendezvous(String message) {
		
	}
}
