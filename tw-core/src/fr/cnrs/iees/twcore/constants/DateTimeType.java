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

package fr.cnrs.iees.twcore.constants;

import java.time.LocalDateTime;

import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

/*
 * Class with a  long value representing a DateTime for use with dsls and for use
 * with a propertyEditor. 
 * 
 * TODO maybe the string should look like 1234 [DAY] to indicate the units
 */
/**
 * Author Ian Davies
 *
 * Date 28 Jan. 2019
 */
public class DateTimeType {
	private long dateTime;
	// private TimeUnits unit;// TODO not sure about this.

	public DateTimeType(long dateTime) {
		this.dateTime = dateTime;
	}

	public DateTimeType(String string) {
		dateTime = Long.parseLong(string);
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public long getDateTime() {
		return dateTime;
	}

	public LocalDateTime getLocalDateTime(TimeUnits unit) {
//		return TimeUtil.longToDate(dateTime, unit);
		return null;
	}

	@Override
	public String toString() {
		return Long.toString(dateTime);
	}

	public static DateTimeType valueOf(String string) {
		return new DateTimeType(string);
	}

	public static DateTimeType defaultValue() {
		return new DateTimeType(0L);
	}

	static {
		ValidPropertyTypes.recordPropertyType(DateTimeType.class.getSimpleName(), DateTimeType.class.getName(),
				defaultValue());
	}

}
