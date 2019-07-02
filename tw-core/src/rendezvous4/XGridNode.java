package rendezvous4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class XGridNode {
	// used only when msgs bank up because rendezvous is yet to be added.
	private Queue<XMessage> messageQueue = new LinkedList<>();
	private Map<Integer, XRendezvousProcess> rendezvousProcesses = new HashMap<>();;

	public XGridNode addRendezvous(XRendezvousProcess process, int... types) {
		/**
		 * Duplicate the process entry for each type. Not sure when this would be the case
		 * unless a process uses a switch statement on msg type?
		 */
		for (int type : types)
			rendezvousProcesses.put(type, process);
		// Process any pending msgs if possible.
		if (!messageQueue.isEmpty()) {
			return callRendezvous(messageQueue.remove());
		}
		return this;
	}

	public synchronized XGridNode callRendezvous(XMessage message) {
		XRendezvousProcess process = rendezvousProcesses.get(message.getMessageHeader().getType());
		if (process == null)
			messageQueue.offer(message);
		else
			process.execute(message);
		return this;
	}

}
