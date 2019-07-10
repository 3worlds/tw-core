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
package rendezvous;

import java.util.concurrent.BlockingQueue;

// incredible ! this seems to work !!
public class Simulator implements Runnable {

	private BlockingQueue<String> controlQueue;
	private BlockingQueue<String> outputQueue;
	private int id = -1;
	private String state = "stopped";
	int step = 0;
	int MAXITER=100;

	public Simulator(BlockingQueue<String> simqueue,BlockingQueue<String> outputqueue, int index) {
		this.controlQueue = simqueue;
		this.outputQueue = outputqueue;
		id = index;
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				String command = controlQueue.peek();
				// nb this will only work for 0<=i<=9
				if (command!=null) {
					int k = Integer.valueOf(command.substring(command.length()-1));
					if (k==id) {
						command = controlQueue.take();
						String c = command.substring(0,command.length()-1);
						if (c.equals("start simulation")) {
							state="running";
							System.out.println(Thread.currentThread().getName() + " Simulator "+id+" received start simulation");
						}
						if (c.equals("pause simulation")) {
							state="stopped";
							System.out.println(Thread.currentThread().getName() + " Simulator "+id+" received pause simulation");
						}
						if (c.equals("resume simulation")) {
							state="running";
							System.out.println(Thread.currentThread().getName() + " Simulator "+id+" received resume simulation");
						}
						if (c.equals("reset simulation")) {
							step = 0;
							state="running";
							System.out.println(Thread.currentThread().getName() + " Simulator "+id+" received reset simulation");
						}
						if (c.equals("stop simulation")) {
							state="stopped";
							System.out.println(Thread.currentThread().getName() + " Simulator "+id+" received stop simulation");
						}
						if (c.equals("quit")) {
							state="finished";
							outputQueue.put("quit");
							System.out.println(Thread.currentThread().getName() + " Simulator "+id+" received quit");
					        return;
						}
					}
				}
				if (state.equals("running"))
					step();
			}
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

	private void step() {
		step++;
		System.out.println(Thread.currentThread().getName() + " Simulator " + id + " computing step "+step);
		if (step % 5 == 0) {
			try {
				outputQueue.put("data from sim "+id+" at step = "+step);
		    } catch (InterruptedException e) {
		        Thread.currentThread().interrupt();
		    }
		}
		if (step>=MAXITER)
			state="finished";
	}
}
