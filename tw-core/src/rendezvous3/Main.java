package rendezvous3;
/**
 * This is a MINIMAL and rudimentary impl of Shayne's rendezvous system. I
 * havn't attempted to factor Gridnode so that it works with descendants. i.e.
 * there are 2 static methods called by each Rendezvous processes in this class
 * rather than methods in some descendant.
 * 
 * TESTING: 1) Looking for race conditions. It seems the rv system requires no
 * threads to run - "synchronised" is sufficient.
 */

public class Main {
	public static int MSG_CTRL_TO_SIM1 = 10;
	public static int MSG_CTRL_TO_SIM2 = 11;
	public static int MSG_SIM_TO_CTRL1 = 20;
	public static int MSG_SIM_TO_CTRL2 = 21;

	public static void main(String[] args) {
		XGridNode sim = new XGridNode();
		XGridNode ctrl = new XGridNode();
		ctrl.addListener(sim);
		sim.addListener(ctrl);

		XRendezvousProcess process1 = new XRendezvousProcess() {
			@Override
			public void execute(XMessage message) {
				XGridNode.method1(message);
			}
		};
		XRendezvous rendezvous1 = new XRendezvous();
		rendezvous1.getEntries().add(new XRendezvousEntry(process1, MSG_CTRL_TO_SIM1, MSG_CTRL_TO_SIM2));

		// ---------------

		XRendezvousProcess process2 = new XRendezvousProcess() {
			@Override
			public void execute(XMessage message) {
				XGridNode.method2(message);
			}
		};

		XRendezvous rendezvous2 = new XRendezvous();
		rendezvous2.getEntries().add(new XRendezvousEntry(process2, MSG_SIM_TO_CTRL1, MSG_SIM_TO_CTRL2));

		// msgs will accumulate indefinitely if either of the rv are not added.
		// This is ok if its a programming error. But if the system is slow to install
		// the rv then maybe the queue will become too large?
		ctrl.addRendezvous(rendezvous2);
		sim.addRendezvous(rendezvous1);

		int nMsgs = 10;
		Object payload = new Object();

		Runnable task1 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					ctrl.sendMessages(MSG_CTRL_TO_SIM1, payload);

			}
		};
		Thread thread1 = new Thread(task1);
		Runnable task3 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					ctrl.sendMessages(MSG_CTRL_TO_SIM2, payload);

			}
		};
		Thread thread3 = new Thread(task3);

		Runnable task2 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					sim.sendMessages(MSG_SIM_TO_CTRL1, payload);

			}
		};
		Thread thread2 = new Thread(task2);

		Runnable task4 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					sim.sendMessages(MSG_SIM_TO_CTRL2, payload);

			}
		};
		Thread thread4 = new Thread(task4);

		thread1.start();
		thread3.start();
		thread4.start();
		thread2.start();
		//sim.addRendezvous(rendezvous1); // we should throw exception is a rv is added twice?


	}

}
