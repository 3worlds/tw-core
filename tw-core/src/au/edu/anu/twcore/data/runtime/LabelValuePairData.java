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

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * The class for sending just a value with a label. Value can be a number or a String
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
@Deprecated
public class LabelValuePairData extends OutputData {

	// the label as a hierarchical name
	private DataLabel label = null; 
	private Number value = null;
	private String svalue = null;
	
	public LabelValuePairData(SimulatorStatus status, 
			int senderId,
			int metaDataType) {
		super(status,senderId,metaDataType);
	}
		
	/**
	 * returns the numeric value - may be null if the value is not a number
	 * 
	 * @return
	 */
	public Number value() {
		return value;
	}
	
	/**
	 * sets a numeric value
	 * 
	 * @param n
	 */
	public void setValue(Number n) {
		value = n;
	}
	
	/**
	 * sets a string value
	 * 
	 * @param s
	 */
	public void setValue(String s) {
		svalue = s;
	}
	
	public void setLabel(String...labelParts) {
		label = new DataLabel(labelParts);
	}

	public void setLabel(DataLabel label) {
		this.label = label;
	}

	
	public DataLabel label() {
		return label;
	}
	
	/**
	 * returns the numeric value as a String, or the String value if the value is not numeric
	 * 
	 * @return
	 */
	public String asString() {
		if (value!=null)
			return value.toString();
		else
			return svalue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(label.toString());
		sb.append('=');
		sb.append(asString());
		return sb.toString();
	}
	
}
