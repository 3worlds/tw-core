package rendezvous4;

import java.util.ArrayList;
import java.util.List;

public class CtrlNode extends XGridNode {
	private List<XGridNode> ctrlListeners;

	public CtrlNode () {
		ctrlListeners = new ArrayList<>();
		this.addRendezvous(new XRendezvousProcess() {

			@Override
			public void execute(XMessage message) {
				myProcess(message);
				
			}}, Main.MSG_SIM_TO_CTRL1, Main.MSG_SIM_TO_CTRL2);
		

	}
	public void addCtrlListener(XGridNode l) {
		ctrlListeners.add(l);
	}

	public void sendCtrlMessage(int type, Object payload) {
		for (XGridNode target : ctrlListeners) {
			XMessage msg = new XMessage(type, payload);
			target.callRendezvous(msg);
		}
	}
	private void myProcess (XMessage msg) {
		System.out.println("Ctrl msg process: " + msg.getMessageHeader().getType());

	}
}
