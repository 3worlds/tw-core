package rendezvous.grid;

/**
 * <p>Use this class to associate a particular {@link RendezvousProcess} to a list
 * of {@code MessageType}s. Instances of this class are created when a {@link GridNode}
 * is initialised by calling {@code addRendezvous(...)} to setup which kind of message
 * it is going to understand. Later, when {@code GridNode.callRendezvous(...)} is called,
 * any message which type matches one of the {@code MessageType}s will cause the execution
 * of the associated {@code RendezvousProcess}.</p>
 * 
 * @author Shayne Flint - 2012
 *
 */
public class RendezvousEntry {

	private int[]             messageTypes   = null;
	private RendezvousProcess process        = null;
	
	public RendezvousEntry(RendezvousProcess process, int... messageType) {
		this.process = process;
		this.messageTypes = messageType;
	}

	public RendezvousProcess getProcess() {
		return process;
	}
	
	public int[] getMessageTypes() {
		return messageTypes;
	}
	
	public static RendezvousEntry acceptMessage(RendezvousProcess process, int... messageTypes) {
		return new RendezvousEntry(process, messageTypes);
	}

	public static RendezvousEntry acceptMessage(int... messageTypes) {
		return new RendezvousEntry(null, messageTypes);
	}

	public static RendezvousEntry acceptAnyMessage(RendezvousProcess process) {
		return new RendezvousEntry(process);
	}

	public String toString() {
		return "[process " + process + ", messageTypes=" + /* MessageHeader.typeString(messageTypes) + */"]";
	}
	
}
