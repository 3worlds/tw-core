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
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

	public static void main(String[] args) {
		int BOUND = 10;
		int nsim = 3;
		 
		BlockingQueue<String> expControlQueue = new LinkedBlockingQueue<>(BOUND);
		BlockingQueue<String> outputQueue = new LinkedBlockingQueue<>(BOUND);
		BlockingQueue<String> simControlQueue = new LinkedBlockingQueue<>(BOUND);
		new Thread(new UIController(expControlQueue)).start();
		new Thread(new Experiment(expControlQueue,simControlQueue,nsim)).start();
		new Thread(new DisplayWidget(outputQueue)).start();
		for (int i=0; i<nsim; i++)
			new Thread(new Simulator(simControlQueue,outputQueue,i)).start();
		System.out.println("finished");
	}

}
