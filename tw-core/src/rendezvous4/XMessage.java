package rendezvous4;

public class XMessage {
	private XMessageHeader messageHeader;
	private Object payload;

	public XMessage(int type,Object payload) {
		this.messageHeader = new XMessageHeader(type);
		this.payload=payload;
	}

	public XMessageHeader getMessageHeader() {
		return messageHeader;
	}
	
	

}
