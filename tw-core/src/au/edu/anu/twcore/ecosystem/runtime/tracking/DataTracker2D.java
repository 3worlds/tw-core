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

import au.edu.anu.twcore.data.runtime.Output2DData;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

import java.util.Collection;
import java.util.List;

import au.edu.anu.twcore.data.runtime.Metadata;

/**
 * A data tracker for map data
 *
 * @author Jacques Gignoux - 1 oct. 2019
 *
 */
public class DataTracker2D extends SamplerDataTracker<CategorizedComponent,Output2DData, Metadata> {

	public DataTracker2D(int simulatorId, 
			SamplingMode selection, 
			int sampleSize,
			Collection<CategorizedComponent> samplingPool, 
			List<CategorizedComponent> trackedComponents) {
		super(DataMessageTypes.DIM2, simulatorId, selection, sampleSize, samplingPool, trackedComponents);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void recordItem(String... labels) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Metadata getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void record(TwData... props) {
		// TODO Auto-generated method stub
		
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
