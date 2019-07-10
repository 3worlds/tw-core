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

// consumer on controlQueue
// producer on simControlQueue
public class Experiment implements Runnable {

	private BlockingQueue<String> controlQueue;
	private BlockingQueue<String> simControlQueue;
	
	private int repeat = 0;

	public Experiment(BlockingQueue<String> expqueue,BlockingQueue<String> simqueue,int nsim) {
		this.controlQueue = expqueue;
		this.simControlQueue = simqueue;
		repeat = nsim;
	}
	
	@Override
	public void run() {
	   try {
	        while (true) {
	            String command = controlQueue.take();
	            System.out.println(Thread.currentThread().getName() + " Experiment received " + command);
            	// nb this will only work for 0<=i<=9
            	for (int i=0; i<repeat; i++) {
    	            System.out.println(Thread.currentThread().getName() + " Experiment sent " + command+ " "+i);
            		simControlQueue.put(command+i);
            	}
	            if (command.equals("quit")) {
					System.out.println(Thread.currentThread().getName() + " Experiment received quit");
	                return;
	            }
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

}
