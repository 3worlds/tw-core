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
package rendezvous.grid;

/**
 * <p>Use this class to associate a particular {@link RendezvousProcess} to a list
 * of {@code MessageType}s. Instances of this class are created when a {@link GridNode}
 * is initialised by calling {@code addRendezvous(...)} to setup which kind of message
 * it is going to understand. Later, when {@code GridNode.callRendezvous(...)} is called,
 * any message which type matches one of the {@code MessageType}s will cause the execution
 * of the associated {@code RendezvousProcess}.</p>
 * 
 * @author Shayne Flint - 2012
 *
 */
public class RendezvousEntry {

	private int[]             messageTypes   = null;
	private RendezvousProcess process        = null;
	
	public RendezvousEntry(RendezvousProcess process, int... messageType) {
		this.process = process;
		this.messageTypes = messageType;
	}

	public RendezvousProcess getProcess() {
		return process;
	}
	
	public int[] getMessageTypes() {
		return messageTypes;
	}
	
	public static RendezvousEntry acceptMessage(RendezvousProcess process, int... messageTypes) {
		return new RendezvousEntry(process, messageTypes);
	}

	public static RendezvousEntry acceptMessage(int... messageTypes) {
		return new RendezvousEntry(null, messageTypes);
	}

	public static RendezvousEntry acceptAnyMessage(RendezvousProcess process) {
		return new RendezvousEntry(process);
	}

	public String toString() {
		return "[process " + process + ", messageTypes=" + /* MessageHeader.typeString(messageTypes) + */"]";
	}
	
}
