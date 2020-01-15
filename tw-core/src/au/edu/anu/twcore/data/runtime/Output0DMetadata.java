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

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <p>A class storing time series metadata to initialise time series widgets.
 * It is meant to be passed as the "timeSeriesMetadata" property in a Metadata message property list.
 * </p>
 * <p>
 * Data for time series are written in arrays to messages, as doubles, longs or strings. The ranking of
 * labels to indices in the arrays is passed in this metadata message, together with all other metadata
 * (eg precision, min, max, units).
 * </p>
 * 
 * 
 * @author Jacques Gignoux - 30 sept. 2019
 *
 */
public class Output0DMetadata {
	
	public static String TSMETA = "timeSeriesMetadata";
	
	private SortedMap<DataLabel,Integer> nameIndex = new TreeMap<DataLabel,Integer>();
	private List<DataLabel> intNames = new ArrayList<DataLabel>();
	private List<DataLabel> doubleNames = new ArrayList<DataLabel>();
	private List<DataLabel> stringNames = new ArrayList<DataLabel>(); 

	public Output0DMetadata() {
		// TODO Auto-generated constructor stub
	}
	
	// create inner order of variables
	public void addIntVariable(DataLabel key) {
		int nextIx = intNames.size();
		intNames.add(key);
		nameIndex.put(key, nextIx);
	}
	
	public void addDoubleVariable(DataLabel key) {
		int nextIx = doubleNames.size();
		doubleNames.add(key);
		nameIndex.put(key, nextIx);
	}

	public void addStringVariable(DataLabel key) {
		int nextIx = stringNames.size();
		stringNames.add(key);
		nameIndex.put(key, nextIx);
	}
	
	// looping helpers
	public Iterable<DataLabel> intNames() {
		return intNames;
	}

	public Iterable<DataLabel> doubleNames() {
		return doubleNames;
	}
	
	public Iterable<DataLabel> stringNames() {
		return stringNames;
	}
	
	public int indexOf(DataLabel key) {
		return nameIndex.get(key);
	}

	public int nInt() {
		return intNames.size();
	}

	public int nDouble() {
		return doubleNames.size();
	}

	public int nString() {
		return stringNames.size();
	}

}
