/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package rendezvous4;

/**
 * This is a MINIMAL impl of Shayne's rendezvous system. I use a hash table to
 * map msg type as the key to map to the required process. This may not always
 * be correct - wait and see.
 * 
 * Rendezvous.java and RendezvousEntry.java are redundant with this approach and
 * have been removed. I've removed the timeout system (I think its purpose may
 * have been for ssh to remote systems - no longer envisioned). MessageHeader
 * just has the type int so it may seem useless to keep this class. I haven't
 * included the src/target id in the msg header but we may in future (i.e
 * multi-sim ctrl) - wait and see.
 * 
 * We still need to deal with crashed simulators but I don't think this is
 * something specific to this approach.
 * 
 * Multi-tasking takes place when producers/consumers are in they're own thread
 * (simulator/ui Platform.runLater() or some big data processing task). We just
 * need to make sure process.execute does not start something that takes a long
 * time. The approach should be thread-safe simply because callRendezvous is
 * Synchronized. I don't think it is safe to expect a pause or quit cmd to
 * interrupt simulations any time other than when a step is complete. Otherwise
 * its a mess.
 */

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
