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

import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.DateTimeType;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * A data tracker for spatial data of SystemComponents (no edges at the moment).
 *
 * The metadata are the space properties, namely: type (SpaceType), edgeEffects
 * (EdgeEffects), precision (double), units (String), plus the
 * descendant-specific properties:
 *
 * for FlatSurface: x-limits 'Interval) and y-limits (Interval) for SquareGrid:
 * cellSize(double), x-nCells (int), y-nCells (int) (optional, if absent =
 * x-nCells)
 *
 * This DataTracker is not instantiated by a DataTrackerNode, but by the
 * SpaceNode it points to.
 *
 * @author Jacques Gignoux - 14 févr. 2020
 *
 */
public class SpaceDataTracker extends AbstractDataTracker<SpaceData, Metadata> {

	private long currentTime;
	private Metadata metadata = null;
	private SpaceData ctMessage = null;

	public SpaceDataTracker(int simId, ReadOnlyPropertyList meta) {
		super(DataMessageTypes.SPACE, simId);
		metadata = new Metadata(simId, meta);
		setInitialTime();
//		setSender(simId);
	}

	public void setInitialTime() {
		DateTimeType dtt = (DateTimeType) metadata.properties().getPropertyValue(P_TIMELINE_TIMEORIGIN.key());
		currentTime = dtt.getDateTime();
	}

	public void recordTime(SimulatorStatus status, long time) {
		currentTime = time;
		ctMessage = new SpaceData(status, senderId, metadata.type());
		ctMessage.setTime(currentTime);
	}

	public void createPoint(double[] coord, String... labels) {
		ctMessage.createPoint(new DataLabel(labels), coord);
	}

	public void movePoint(double[] newCoord, String... labels) {
		ctMessage.movePoint(new DataLabel(labels), newCoord);
	}

	public void deletePoint(String... labels) {
		ctMessage.deletePoint(new DataLabel(labels));
	}

	public void createLine(String[] startLabels, String[] endLabels) {
		ctMessage.createLine(new DataLabel(startLabels), new DataLabel(endLabels));
	}

	public void deleteLine(String[] startLabels, String[] endLabels) {
		ctMessage.deleteLine(new DataLabel(startLabels), new DataLabel(endLabels));
	}

	public void closeTimeStep() {
		/**
		 * This data continues to be written to by other processes AFTER sending.
		 * Therefore, to avoid concurrentModification exceptions, it must be cloned by
		 * the recipient IN THIS THREAD. This is a bit expensive so a redesign to avoid
		 * this would be preferable.
		 */
		/// FIXED!
		sendData(ctMessage);
		ctMessage = null;
	}

	@Override
	public Metadata getInstance() {
		return metadata;
	}

}
