package rendezvous3;

public class XRendezvousEntry {
	private int[] messageTypes;
	private XRendezvousProcess process;
	
	public XRendezvousEntry(XRendezvousProcess process, int... messageTypes) {
		this.process = process;
		this.messageTypes=messageTypes;
	}

	public int[] getMessageTypes() {
		return messageTypes;
	}

	public XRendezvousProcess getProcess() {
		return process;
	}
	

}
