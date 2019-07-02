package rendezvous3;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * This is a MINIMAL and rudimentary impl of Shayne's rendezvous system. I
 * havn't attempted to factor Gridnode so that it works with descendants. i.e.
 * there are 2 static methods called by each Rendezvous processes in this class
 * rather than methods in some descendant.
 * 
 * TESTING: 1) Looking for race conditions. It seems the rv system requires no
 * threads to run - "synchronised" is sufficient.
 * 
 * 
 */
public class XGridNode {
	private List<XRendezvous> rendezvousList = new ArrayList<>();
	private Queue<XMessage> messageQueue = new LinkedList<>();
	private List<XGridNode> listeners = new ArrayList<>();

	public void addListener(XGridNode l) {
		listeners.add(l);
	}

	public void sendMessages(int type, Object payload) {
		for (XGridNode target : listeners) {
			XMessage msg = new XMessage(type, payload);
			// build and close the payload
			target.callRendezvous(msg);
		}
	}

	public XGridNode addRendezvous(XRendezvous rendezvous) {
		if (rendezvousList.contains(rendezvous))
			System.out.println("NOOO!!"); // well doesnt matter as the last will be ignored but could slow things up
		rendezvousList.add(rendezvous);
		rendezvous.initialise();
		if (!messageQueue.isEmpty()) {
			// process any pending msgs
			XMessage msg = messageQueue.remove();
			return callRendezvous(msg);
		}
		return this;
	}

	// assume a messageType and a process exist - i.e. don't bother checking for
	// nulls
	// should msg types be a set so there are no duplicates?
	public synchronized XGridNode callRendezvous(XMessage message) {
		if (rendezvousList.isEmpty()) {
			// if no rv set, put msg back in queue.
			// The queue will accumulate msgs indefinitely.
			messageQueue.offer(message);
			System.out.println(messageQueue.size());
			return this;
		} else {
			int messageType = message.getMessageHeader().getType();
			for (XRendezvous rendezvous : rendezvousList)
				for (XRendezvousEntry e : rendezvous.getEntries())
					for (int type : e.getMessageTypes())
						if (messageType == type) {
							e.getProcess().execute(message);
							// as far as i can see this timeout method is never used.
							// Should it be a time out for the last process?
							rendezvous.initialise();
							return this;
						}
		}
		System.out.println("No entry for this message here???");
		return this;
	}

	public static void method1(XMessage msg) {
		System.out.println("Method 1: " + msg.getMessageHeader().getType());
	}

	public static void method2(XMessage msg) {
		System.out.println("Method 2: " + msg.getMessageHeader().getType());
	}




}
