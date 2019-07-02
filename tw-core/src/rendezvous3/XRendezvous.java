package rendezvous3;

import java.util.LinkedList;
import java.util.List;

public class XRendezvous {
	private Long timeout=null;
	private Thread timerThread;
	private XRendezvousProcess timeoutProcess;
	private List<XRendezvousEntry> entries = new LinkedList<>();

	@Override
	public void finalize() {
		if (timerThread != null) {
			timerThread.interrupt();
			timerThread = null;
		}
	}

	public void initialise() {
		finalize();
		if (timeout !=null) {
			timerThread = new Thread(new RendezvousTimer(timeout));
			timerThread.start();
		}
	}
	
	private class RendezvousTimer implements Runnable{
		private Long delay;
		public RendezvousTimer(Long timeout) {
			this.delay=timeout;
		}
		@Override
		public void run() {
			try {
			while (true) {
				Thread.sleep(delay);
				timeoutProcess.execute(null);
			}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}				}
		
	}

	public List<XRendezvousEntry> getEntries() {
		return entries;
	}

	}

