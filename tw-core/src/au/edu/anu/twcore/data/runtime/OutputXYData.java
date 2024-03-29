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
 * Data for 2D scatter plots (xy).
 * <p>Data for scatter plots are all converted to doubles for simplicity.</p>
 * <p>To be used with a plain Metadata message, with only two variables registered in the metadata.</p>
 *
 * @author J. Gignoux - 15 mai 2020
 *
 */
public class OutputXYData extends LabelledItemData implements OutputTwData {

	private double x;
	private double y;
	private DataLabel xname = null;
	private DataLabel yname = null;


	public OutputXYData(SimulatorStatus status, int senderId, int metaDataType,
			DataLabel xname,DataLabel yname) {
		super(status, senderId, metaDataType);
		this.xname = xname;
		this.yname = yname;
	}

	@Override
	public void setValue(DataLabel label, double value) {
		if (label.equals(xname))
			x = value;
		else
			y = value;
	}

	@Override
	public void setValue(DataLabel label, float value) {
		if (label.equals(xname))
			x = value;
		else
			y = value;
	}

	@Override
	public void setValue(DataLabel label, int value) {
		if (label.equals(xname))
			x = value;
		else
			y = value;
	}

	@Override
	public void setValue(DataLabel label, long value) {
		if (label.equals(xname))
			x = value;
		else
			y = value;
	}

	@Override
	public void setValue(DataLabel label, byte value) {
		if (label.equals(xname))
			x = value;
		else
			y = value;
	}

	@Override
	public void setValue(DataLabel label, short value) {
		if (label.equals(yname))
			y = value;
		else
			x = value;
	}

	@Override
	public void setValue(DataLabel label, boolean value) {
		if (label.equals(xname))
			x = value?1.0:0.0;
		else
			y = value?1.0:0.0;
	}
//
//
//	public void setX(double x) {
//		this.x = x;
//	}
//	public void setX(float x) {
//		this.x = x;
//	}
//	public void setX(long x) {
//		this.x = x;
//	}
//	public void setX(int x) {
//		this.x = x;
//	}
//	public void setX(short x) {
//		this.x = x;
//	}
//	public void setX(byte x) {
//		this.x = x;
//	}
//	public void setX(boolean x) {
//		this.x = x?1.0:0.0;
//	}
//
//	public void setY(double y) {
//		this.y = y;
//	}
//	public void setY(float y) {
//		this.y = y;
//	}
//	public void setY(long y) {
//		this.y = y;
//	}
//	public void setY(int y) {
//		this.y = y;
//	}
//	public void setY(short y) {
//		this.y = y;
//	}
//	public void setY(byte y) {
//		this.y = y;
//	}
//	public void setY(boolean y) {
//		this.y = y?1.0:0.0;
//	}
//

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		return sb.append(" x=").append(x).append(" y=").append(y).toString();
	}

}
