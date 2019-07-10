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

// consumer on outputQueue
public class DisplayWidget implements Runnable {

	private BlockingQueue<String> outputQueue;

	public DisplayWidget(BlockingQueue<String> queue) {
		this.outputQueue = queue;
	}

	@Override
	public void run() {
	   try {
	        while (true) {
	            String output = outputQueue.take();
	            if (output.equals("quit")) {
					System.out.println(Thread.currentThread().getName() + " DisplayWidget received quit");
	                Thread.currentThread().interrupt();
	                return;
	            }
	            System.out.println(Thread.currentThread().getName() + " Display " + output);
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }
	}

}
