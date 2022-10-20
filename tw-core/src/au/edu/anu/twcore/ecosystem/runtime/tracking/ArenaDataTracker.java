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
package au.edu.anu.twcore.ecosystem.runtime.tracking;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.RuntimeGraphData;
import au.edu.anu.twcore.ecosystem.runtime.system.EcosystemGraph;
import fr.cnrs.iees.omugi.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

public class ArenaDataTracker extends AbstractDataTracker<RuntimeGraphData, Metadata> {
	private Metadata metadata;
	private long currentTime;

	public ArenaDataTracker(int simId, ReadOnlyPropertyList meta) {
		super(AbstractDataTracker.RUNTIMEGRAPH, simId);
		metadata = new Metadata(simId, meta);
		setInitialTime();
	}

	public void recordTime(long time) {
		currentTime = time;
	}

	public void recordItem(SimulatorStatus status, EcosystemGraph ecosystem, String... labels) {
		RuntimeGraphData msg = new RuntimeGraphData(status, senderId, metadata.type());
		msg.setTime(currentTime);
		msg.setItemLabel(labels);
		msg.setTree(ecosystem);
		sendData(msg);
	}

	@Override
	public Metadata getInstance() {
		return metadata;
	}

	public void setInitialTime() {
		if (metadata.properties().hasProperty(P_TIMELINE_TIMEORIGIN.key())) {
			DateTimeType dtt = (DateTimeType) metadata.properties().getPropertyValue(P_TIMELINE_TIMEORIGIN.key());
			currentTime = dtt.getDateTime();
		} else
			currentTime = 0l;
	}

	@Override
	public void openTimeRecord(SimulatorStatus status, long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeTimeRecord() {
		// TODO Auto-generated method stub

	}

}
