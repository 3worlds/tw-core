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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import fr.ens.biologie.generic.utils.Duple;

/**
 * This class is used to send spatial information to widgets. It contains the
 * coordinates of a single item (usually a SystemComponent) and its DataLabel.
 * NB may also contain a line. NB now replaced by a series of changes (all
 * changes occurring during one time step)
 * 
 * It is meant to be used as a message to send every time a new item is located /
 * unlocated by the space (for economy: only changes are sent)
 * 
 * 
 * @author Jacques Gignoux - 14 févr. 2020
 *
 */
public class SpaceData extends TimeData implements Cloneable {

	/** List of points to definitely remove because their Component is gone */
	private Set<DataLabel> pointsToDelete = new HashSet<>();
	/** List of lines to definitely remove because their Edge is gone */
	private Set<Duple<DataLabel, DataLabel>> linesToDelete = new HashSet<>();
	/** List of new points to draw from scratch */
	private Map<DataLabel, double[]> pointsToCreate = new HashMap<>();
	/** List of new lines to draw from scratch */
	private Set<Duple<DataLabel, DataLabel>> linesToCreate = new HashSet<>();
	/** List of points to move, i.e. they are first erased, then redrawn */
	private Map<DataLabel, double[]> pointsToMove = new HashMap<>();

	public SpaceData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}

	public void createPoint(DataLabel label, double... coord) {
		pointsToCreate.put(label, coord.clone());
		System.out.println("createPoint\tTime: "+this.time()+"#: "+pointsToCreate.size()+"\tThread: "+Thread.currentThread().getId());
	}

	public void deletePoint(DataLabel label) {
		pointsToDelete.add(label);
	}

	public void movePoint(DataLabel label, double[] newCoord) {
		pointsToMove.put(label, newCoord);
	}

	public void createLine(DataLabel startLabel, DataLabel endLabel) {
		linesToCreate.add(new Duple<>(startLabel, endLabel));
	}

	public void deleteLine(DataLabel startLabel, DataLabel endLabel) {
		linesToDelete.add(new Duple<>(startLabel, endLabel));
	}

	public Collection<DataLabel> pointsToDelete() {
		return pointsToDelete;
	}

	public Map<DataLabel, double[]> pointsToCreate() {
		return pointsToCreate;
	}

	public Map<DataLabel, double[]> pointsToMove() {
		return pointsToMove;
	}

	public Collection<Duple<DataLabel, DataLabel>> linesToCreate() {
		return linesToCreate;
	}

	public Collection<Duple<DataLabel, DataLabel>> linesToDelete() {
		return linesToDelete;
	}

	@Override
	public SpaceData clone() {
		// Cloning for recipients
		SpaceData result = new SpaceData(status(), sender(), type());
		result.setTime(time());
		result.linesToCreate.addAll(linesToCreate);
		result.linesToDelete.addAll(linesToDelete);
		result.pointsToDelete.addAll(pointsToDelete);
		pointsToMove.forEach((k, v) -> {
			result.pointsToMove().put(k, v);
		});
		pointsToCreate.forEach((k, v) -> {
			result.pointsToCreate.put(k, v);
		});
		return result;

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("SpaceData: ");
		sb.append("Sender ").append(sender()).append("; ");
		if (!pointsToCreate.isEmpty())
			sb.append("new points ").append(pointsToCreate.keySet().toString()).append("; ");
		if (!pointsToDelete.isEmpty())
			sb.append("deleted points ").append(pointsToDelete.toString()).append("; ");
		if (!pointsToMove.isEmpty())
			sb.append("moved points ").append(pointsToMove.keySet().toString()).append("; ");
		if (!linesToCreate.isEmpty())
			sb.append("new lines ").append(linesToCreate.toString()).append("; ");
		if (!linesToDelete.isEmpty())
			sb.append("deleted lines ").append(linesToDelete.toString()).append("; ");
		return sb.toString();
	}

}
