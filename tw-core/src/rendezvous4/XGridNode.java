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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class XGridNode {
	// used only when msgs bank up because rendezvous is yet to be added.
	private Queue<XMessage> messageQueue = new LinkedList<>();
	private Map<Integer, XRendezvousProcess> rendezvousProcesses = new HashMap<>();;

	public XGridNode addRendezvous(XRendezvousProcess process, int... types) {
		/**
		 * Duplicate the process entry for each type. Not sure when this would be the case
		 * unless a process uses a switch statement on msg type?
		 */
		for (int type : types)
			rendezvousProcesses.put(type, process);
		// Process any pending msgs if possible.
		if (!messageQueue.isEmpty()) {
			return callRendezvous(messageQueue.remove());
		}
		return this;
	}

	public synchronized XGridNode callRendezvous(XMessage message) {
		XRendezvousProcess process = rendezvousProcesses.get(message.getMessageHeader().getType());
		if (process == null)
			messageQueue.offer(message);
		else
			process.execute(message);
		return this;
	}

}
