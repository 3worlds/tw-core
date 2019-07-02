package rendezvous4;

public class Main {
	public static int MSG_CTRL_TO_SIM1 = 10;
	public static int MSG_CTRL_TO_SIM2 = 11;
	public static int MSG_SIM_TO_CTRL1 = 20;
	public static int MSG_SIM_TO_CTRL2 = 21;

	public static void main(String[] args) {
		SimNode sim = new SimNode();
		CtrlNode ctrl = new CtrlNode();
		ctrl.addCtrlListener(sim);
		sim.addSimListener(ctrl);
	

		int nMsgs = 100;
		Object payload = new Object();

		Runnable task1 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					ctrl.sendCtrlMessage(MSG_CTRL_TO_SIM1, payload);
			}
		};
		Thread thread1 = new Thread(task1);
		Runnable task3 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					ctrl.sendCtrlMessage(MSG_CTRL_TO_SIM2, payload);

			}
		};
		Thread thread3 = new Thread(task3);

		Runnable task2 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					sim.sendSimListenerMessage(MSG_SIM_TO_CTRL1, payload);

			}
		};
		Thread thread2 = new Thread(task2);

		Runnable task4 = new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < nMsgs; i++)
					sim.sendSimListenerMessage(MSG_SIM_TO_CTRL2, payload);

			}
		};
		Thread thread4 = new Thread(task4);

		thread1.start();
		thread3.start();
		thread4.start();
		thread2.start();


	}

}
