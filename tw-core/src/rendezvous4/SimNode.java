package rendezvous4;

import java.util.ArrayList;
import java.util.List;

public class SimNode extends XGridNode {
	private List<XGridNode> simListeners;

	public SimNode() {
		simListeners = new ArrayList<>();
		this.addRendezvous(new XRendezvousProcess() {

			@Override
			public void execute(XMessage message) {
				myProcess(message);
				
			}}, Main.MSG_CTRL_TO_SIM1, Main.MSG_CTRL_TO_SIM2);
		
	}

	public void addSimListener(XGridNode l) {
		simListeners.add(l);
	}

	public void sendSimListenerMessage(int type, Object payload) {
		for (XGridNode target : simListeners) {
			XMessage msg = new XMessage(type, payload);
			target.callRendezvous(msg);
		}
	}
	private void myProcess (XMessage msg) {
		System.out.println("Sim msg process: " + msg.getMessageHeader().getType());
	
	}

}
