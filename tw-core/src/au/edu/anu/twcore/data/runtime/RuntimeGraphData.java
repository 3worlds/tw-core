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
package au.edu.anu.twcore.data.runtime;

import au.edu.anu.twcore.ecosystem.runtime.system.EcosystemGraph;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * @author Ian Davies - 29 Jun 2020
 */
public class RuntimeGraphData extends LabelledItemData {

	public RuntimeGraphData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}

	private EcosystemGraph ecosystem;

	public void setTree(EcosystemGraph ecosystem) {
		// TODO: must derive info here not just take a reference.
		this.ecosystem = ecosystem;
	}

	public EcosystemGraph getEcosystem() {
		return ecosystem;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder().append("RuntimeGraphData: ");
		sb.append("Sender ").append(sender()).append("; ");
		sb.append("Arena: ").append(ecosystem.arena().membership().categoryId()).append("; ");
		sb.append("Community: ").append(ecosystem.community());
		return sb.toString();
	}

}
