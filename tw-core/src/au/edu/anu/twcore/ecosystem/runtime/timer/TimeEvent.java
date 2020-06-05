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
package au.edu.anu.twcore.ecosystem.runtime.timer;

import java.util.BitSet;

/**
 * @author Jacques Gignoux - 24 mai 2012 NB not sure the refactoring (9-2017)
 *         was OK - maybe more data storage needed
 *
 *
 *
 */
// public class TimeEvent implements DataContainer, TimeEventConstants{
public class TimeEvent {
	// Time is the fundamental time of the TimeLine
	private long time;
	// obj can be anything the modeller requires
	private Object obj;
	// flags can be anything the modeller requires.
	private BitSet flags;

	public TimeEvent(long t) {
		time = t;
	}

	public TimeEvent(long t, Object obj) {
		this(t);
		this.obj = obj;
	}

	public TimeEvent(long t, Object obj, BitSet flags) {
		this(t, obj);
		this.flags = flags;
	}

	public long getTime() {
		return time;
	}

	public Object getObject() {
		return obj;
	}

	public BitSet getFlags() {
		return flags;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[event: t=").append(time);
		if (obj!=null)
			sb.append("; obj=").append(obj.toString());
		if (flags!=null)
			sb.append("flags=").append(flags);
		sb.append(']');
		return sb.toString();
	}


}
