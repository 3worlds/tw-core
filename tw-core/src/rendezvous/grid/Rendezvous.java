package rendezvous.grid;

import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.DynamicList;

/**
 * 
 * @author Shayne Flint - 2012
 *
 */
public class Rendezvous {

	private Logger log = Logger.getLogger(Rendezvous.class.getName());

	private DynamicList<RendezvousEntry> entries = new DynamicList<RendezvousEntry>();
	private Duration timeout;
	private Thread   timer;
	private RendezvousProcess timeoutProcess;

	public Rendezvous() {
		timeout = null;
		timeoutProcess = null;
	}

	public Rendezvous(RendezvousEntry... entries) {
		this();
		for (RendezvousEntry entry : entries)
			this.entries.add(entry);
	}

	public Rendezvous addEntry(RendezvousEntry entry) {
		entries.add(entry);
		return this;
	}

	public Rendezvous addEntry(RendezvousProcess process, int... messageTypes) {
		return addEntry(new RendezvousEntry(process, messageTypes));
	}

	public Rendezvous setTimeout(Duration timeout, RendezvousProcess timeoutProcess) {
		this.timeout = timeout;
		this.timeoutProcess = timeoutProcess;
		return this;
	}

	public DynamicList<RendezvousEntry> getEntries() {
		return entries;
	}

	public Duration getTimeout() {
		return timeout;
	}

	public RendezvousProcess getTimeoutProcess() {
		return timeoutProcess;
	}

	@Override
	public void finalize() {
		if (timer != null) {
			timer.interrupt();
			timer = null;
		}
	}

	public void initialise() {
		finalize();
		if (timeout != null) {
			log.info("Node: setting up timeout for " + timeout);
			timer = new Thread(new RendezvousTimer(timeout));
			timer.start();
			log.info("Node: new timer created and started");	
		}
	}

	private class RendezvousTimer implements Runnable {

		private long delay;

		public RendezvousTimer(Duration delay) {
			this.delay = delay.duration();
		}

		@Override
		public void run() {
			try {
				while (true) {
					log.info("Node: timer started - about to sleep");
					Thread.sleep(delay);
					log.info("Node: timer woke up from sleep");
					timeoutProcess.execute(null);
					log.info("Node: rendezvousDelayExpired() called");
				}
			} catch (InterruptedException e) {
				log.info("Node: timer interrupted");
				Thread.currentThread().interrupt();			}
		}

	}


	public String toString() {
		String result = "[Rendezvous " + entries.toLongString();
		if (timeout != null) 
			result = result + ", timeout=" + timeout + ", timeoutProcess=" + timeoutProcess;
		return result + "]";
	}
}
