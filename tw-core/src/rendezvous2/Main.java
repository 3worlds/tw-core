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
package rendezvous2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
	
	// NB the state machine is a bit shaky - paused and stopped should be different
	private static String[] initialTransitions = {"start","quit"};
	private static String[] runningTransitions = {"pause","reset","stop","quit"};
	private static String[] stoppedTransitions = {"reset","resume","quit"};
	private static String[] finalTransitions = {"start","reset","quit"};

	private enum states {
		INITIAL (initialTransitions), 
		RUNNING (runningTransitions), 
		STOPPED	(stoppedTransitions), 
		FINAL  	(finalTransitions);
		private final String[] transitions;
		private states(String[] transitions) {
			this.transitions = transitions;
		}
	}

	// a rudimentary user interface coupled to a state machine
	// NB you have to type the commands very quickly once started because you will 
	// see a lot of output coming out and messing with the command invitation
	public static void main(String[] args) {
		states currentState = states.INITIAL;
		boolean goOn = true;
		
		BlockingQueue<String> expControlQueue = new LinkedBlockingQueue<>(50);
		BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>(50);
		
		while (goOn) {
			// user interaction
			StringBuilder sb = new StringBuilder("User command (");
			for (int i=0; i<currentState.transitions.length; i++) {
				sb.append(currentState.transitions[i]);
				if (i<currentState.transitions.length-1)
					sb.append('/');
			}
			sb.append("): ");
			System.out.print(sb.toString());
			String command = null;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				command = br.readLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if (command!=null) {
				boolean found = false;
				for (String s:currentState.transitions)
					if (command.equals(s)) {
						found = true;
						break;
					}
				if (found) {
					switch (command ) {
					case "start":
						if (currentState==states.INITIAL) {
							new Thread(new Experiment(expControlQueue,outputQueue)).start();
						}
						currentState = states.RUNNING;
						break;
					case "pause":
						currentState = states.STOPPED;
						break;
					case "resume":
						currentState = states.RUNNING;
						break;
					case "reset":
						currentState = states.RUNNING;
						break;
					case "stop":
						currentState = states.STOPPED;
						break;
					case "quit":
						currentState = states.FINAL;
						goOn = false;
						break;
					}
					try {
						expControlQueue.put(command);
						System.out.println("transition to state: "+currentState);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			// display of sim outputs
			// Well, this doesnt work properly - display widgets should have their own thread
			// maybe. Anyway, I think it's impossible with this construct to loop on two blockinq
			// queues at the same time (I mean for sending commands and receiving output. 
//			try {
//				String output = outputQueue.take();
//				System.out.println(Thread.currentThread().getName() + " Display " + output);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		System.out.println("User session finished.");
	}

}
