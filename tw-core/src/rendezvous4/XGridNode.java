package rendezvous4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * This is a MINIMAL and rudimentary impl of Shayne's rendezvous system. I use a
 * hash map to id required process for a given msg type.
 */
public class XGridNode {
	private Queue<XMessage> messageQueue = new LinkedList<>();
	private Map<Integer, XRendezvousProcess> rendezvousProcesses = new HashMap<>();;

	public XGridNode addRendezvous(XRendezvousProcess process, int... types) {
		for (int type : types)
			rendezvousProcesses.put(type, process);
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
