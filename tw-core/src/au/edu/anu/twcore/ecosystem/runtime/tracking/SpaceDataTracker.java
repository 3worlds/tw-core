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

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_TIMELINE_TIMEORIGIN;

import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.ens.biologie.generic.utils.Logging;
import au.edu.anu.twcore.ui.runtime.*;
import au.edu.anu.twcore.ecosystem.structure.*;
import au.edu.anu.twcore.ecosystem.dynamics.*;

/**
 * <p>A data tracker for spatial data. It sends change data to whoever is listening (usually a 
 * {@link WidgetGUI} or {@link Widget} understanding {@link SpaceData} messages). Messages consist in a time stamp and a list
 * of changes to the previous state of the space (deletion/creation/move of points and lines).</p>
 *
 * <p>The metadata are the space properties, namely:</p> 
 * <ul><li>type (SpaceType),</li> 
 * <li>edgeEffects (EdgeEffects),</li> 
 * <li>precision (double),</li> 
 * <li>units (String),</li></ul> 
 * <p>plus the descendant-specific properties:</p>
 *
 * <ul><li>for FlatSurface:
 * 		<ul><li>x-limits (Interval) and</li><li> y-limits (Interval)</li></ul>
 * </li> 
 * <li>for SquareGrid:<ul>
 * <li>cellSize(double),</li><li>x-nCells (int),</li><li>y-nCells (int) (optional, if absent =
 * x-nCells)</li></ul></li>
 *</ul>
 * <p>This DataTracker is not instantiated by a {@link DataTrackerNode}, but by the
 * {@link SpaceNode} it points to.</p>
 *
 * @author Jacques Gignoux - 14 févr. 2020
 *
 */
public class SpaceDataTracker extends AbstractDataTracker<SpaceData, Metadata> {

	private static Logger log = Logging.getLogger(SpaceDataTracker.class);
	
	private long currentTime;
	private Metadata metadata = null;
	private SpaceData ctMessage = null;
	private SimulatorStatus currentStatus = null;

	public SpaceDataTracker(int simId, ReadOnlyPropertyList meta) {
		super(DataMessageTypes.SPACE,simId);
		metadata = new Metadata(simId, meta);
		setInitialTime();
//		setSender(simId);
	}

	public void setInitialTime() {
		DateTimeType dtt = (DateTimeType) metadata.properties().getPropertyValue(P_TIMELINE_TIMEORIGIN.key());
		currentTime = dtt.getDateTime();
	}

	@Override
	public void openTimeRecord(SimulatorStatus status, long time) {
		currentTime = time;
		currentStatus = status;
	}

	@Override
	public void openRecord() {
		if (currentStatus==null)
			log.warning(()->"Attempt to record data before time has been recorded");
		if (ctMessage==null) {
			ctMessage = new SpaceData(currentStatus, senderId, metadata.type());
			ctMessage.setTime(currentTime);
		}
		else
			log.warning(()->"Attempt to send a space data message before the previous has been sent");
	}

	@Override
	public void closeRecord() {
		/**
		 * This data continues to be written to by other processes AFTER sending.
		 * Therefore, to avoid concurrentModification exceptions, it must be cloned by
		 * the recipient IN THIS THREAD. This is a bit expensive so a redesign to avoid
		 * this would be preferable.
		 */
		if (ctMessage!=null)
			sendData(ctMessage);
		else
			log.warning(()->"Attempt to close uninitialised SpaceData message.");
		ctMessage = null;
	}

	public void createPoint(double[] coord, String... labels) {
		if (ctMessage!=null)
			ctMessage.createPoint(new DataLabel(labels), coord);
		else
			log.warning(()->"Attempt to write data to uninitialised SpaceData message.");
	}

	public void movePoint(double[] newCoord, String... labels) {
		if (ctMessage!=null)
			ctMessage.movePoint(new DataLabel(labels), newCoord);
		else
			log.warning(()->"Attempt to write data to uninitialised SpaceData message.");
	}

	public void deletePoint(String... labels) {
		if (ctMessage!=null)
			ctMessage.deletePoint(new DataLabel(labels));
		else
			log.warning(()->"Attempt to write data to uninitialised SpaceData message.");
	}

	public void createLine(String[] startLabels, String[] endLabels, String type) {
		if (ctMessage!=null) {
			ctMessage.createLine(new DataLabel(startLabels), new DataLabel(endLabels), type);
		}
		else
			log.warning(()->"Attempt to write data to uninitialised SpaceData message.");
	}

	public void deleteLine(String[] startLabels, String[] endLabels, String type) {
		if (ctMessage!=null)
			ctMessage.deleteLine(new DataLabel(startLabels), new DataLabel(endLabels), type);
		else
			log.warning(()->"Attempt to write data to uninitialised SpaceData message.");
	}

	public void closeTimeRecord() {
//		/**
//		 * This data continues to be written to by other processes AFTER sending.
//		 * Therefore, to avoid concurrentModification exceptions, it must be cloned by
//		 * the recipient IN THIS THREAD. This is a bit expensive so a redesign to avoid
//		 * this would be preferable.
//		 */
//		if (ctMessage!=null)
//			sendData(ctMessage);
//		else
//			log.warning(()->"Attempt to close uninitialised SpaceData message.");
//		ctMessage = null;
		currentStatus = null;
	}

	@Override
	public Metadata getInstance() {
		return metadata;
	}

}
