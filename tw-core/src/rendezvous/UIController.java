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

// producer on controlQueue
public class UIController implements Runnable {

	private BlockingQueue<String> controlQueue;
    
	public UIController(BlockingQueue<String> queue) {
	    this.controlQueue = queue;
 	}

	@Override
	public void run() {
	       try {
	    	   mimickUserSession();
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	       return;
	}
    
    private void mimickUserSession() throws InterruptedException {
    	System.out.println(Thread.currentThread().getName() + " UIController start user session");
        controlQueue.put("start simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent start simulation");
        Thread.sleep(10);
        controlQueue.put("pause simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent pause simulation");
        Thread.sleep(5);
        controlQueue.put("resume simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent resume simulation");
        Thread.sleep(10);
        controlQueue.put("reset simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent reset simulation");
        Thread.sleep(15);
        controlQueue.put("stop simulation");
    	System.out.println(Thread.currentThread().getName() + " UIController sent stop simulation");
        controlQueue.put("quit");
    	System.out.println(Thread.currentThread().getName() + " UIController sent quit");
        System.out.println(Thread.currentThread().getName() + " UIController end user session");
        return;
     }

}
