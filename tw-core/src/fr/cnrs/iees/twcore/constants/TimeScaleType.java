/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *                    *** 3Worlds - A software for the simulation of ecosystems ***
 *                    *                                                           *
 *                    *        by:  Jacques Gignoux - jacques.gignoux@upmc.fr     *
 *                    *             Ian D. Davies   - ian.davies@anu.edu.au       *
 *                    *             Shayne R. Flint - shayne.flint@anu.edu.au     *
 *                    *                                                           *
 *                    *         http:// ???                                       *
 *                    *                                                           *
 *                    *************************************************************
 * CAUTION: generated code - do not modify
 * generated by CentralResourceGenerator on Wed Apr 07 15:03:12 CEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

import java.util.SortedSet;
import java.util.TreeSet;
import static fr.cnrs.iees.twcore.constants.TimeUnits.*;

public enum TimeScaleType {

// ARBITRARY: arbitrary time units with no predefined name
	ARBITRARY ("arbitrary time units with no predefined name",
		"arbitrary units",
		null,
		null,
		null,
		UNSPECIFIED,
		UNSPECIFIED,
		0.0),

// GREGORIAN: real calendar time
	GREGORIAN ("real calendar time",
		"Gregorian calendar",
		YEAR,
		MONTH,
		WEEK,
		MICROSECOND,
		MILLENNIUM,
		1.0),

// YEAR_365D: 365-days years, no weeks, no months
	YEAR_365D ("365-days years, no weeks, no months",
		"365-day year",
		YEAR_365,
		null,
		null,
		MICROSECOND,
		MILLENNIUM,
		365.0/REFERENCE_YEAR),

// YEAR_13M: 28-days months, 13-months/52-weeks years
	YEAR_13M ("28-days months, 13-months/52-weeks years",
		"13-month year",
		YEAR_364,
		MONTH_28,
		WEEK,
		MICROSECOND,
		MILLENNIUM,
		364.0/REFERENCE_YEAR),

// WMY: 28-days months, 12-months/48-weeks years
	WMY ("28-days months, 12-months/48-weeks years",
		"week-month-year",
		YEAR_336,
		MONTH_28,
		WEEK,
		MICROSECOND,
		MILLENNIUM,
		336.0/REFERENCE_YEAR),

// MONTH_30D: 30-days months, weeks replaced by 15-days fortnights
	MONTH_30D ("30-days months, weeks replaced by 15-days fortnights",
		"30-day month",
		YEAR_360,
		MONTH_30,
		FORTNIGHT_15,
		MICROSECOND,
		MILLENNIUM,
		360.0/REFERENCE_YEAR),

// YEAR_366D: 366-days year, months replaced by 61-days bi-months
	YEAR_366D ("366-days year, months replaced by 61-days bi-months",
		"366-day year",
		YEAR_366,
		BIMONTH_61,
		null,
		MICROSECOND,
		MILLENNIUM,
		366.0/REFERENCE_YEAR),

// LONG_TIMES: long time units only (month or longer), calendar-compatible
	LONG_TIMES ("long time units only (month or longer), calendar-compatible",
		"long time units",
		YEAR,
		MONTH,
		null,
		MONTH,
		MILLENNIUM,
		1.0),

// SHORT_TIMES: short time units only (week or shorter), calendar-compatible
	SHORT_TIMES ("short time units only (week or shorter), calendar-compatible",
		"short time units",
		null,
		null,
		WEEK,
		MICROSECOND,
		WEEK,
		1.0),

// MONO_UNIT: single time unit, calendar-compatible
	MONO_UNIT ("single time unit, calendar-compatible",
		"mono-unit",
		YEAR,
		MONTH,
		WEEK,
		MICROSECOND,
		MILLENNIUM,
		1.0);
	
	private final String description;
	private final String longName;
	private final TimeUnits yearUnit;
	private final TimeUnits monthUnit;
	private final TimeUnits weekUnit;
	private final TimeUnits shortestUnit;
	private final TimeUnits longestUnit;
	private final double inflationFactor;

	private TimeScaleType(String description, String longName, TimeUnits yearUnit, TimeUnits monthUnit, TimeUnits weekUnit, TimeUnits shortestUnit, TimeUnits longestUnit, double inflationFactor) {
		this.description = description;
		this.longName = longName;
		this.yearUnit = yearUnit;
		this.monthUnit = monthUnit;
		this.weekUnit = weekUnit;
		this.shortestUnit = shortestUnit;
		this.longestUnit = longestUnit;
		this.inflationFactor = inflationFactor;
	}

	public String description() {
		return description;
	}

	public String longName() {
		return longName;
	}

	public TimeUnits yearUnit() {
		return yearUnit;
	}

	public TimeUnits monthUnit() {
		return monthUnit;
	}

	public TimeUnits weekUnit() {
		return weekUnit;
	}

	public TimeUnits shortestUnit() {
		return shortestUnit;
	}

	public TimeUnits longestUnit() {
		return longestUnit;
	}

	public double inflationFactor() {
		return inflationFactor;
	}

	public static String[] toStrings() {
		String[] result = new String[TimeScaleType.values().length];
		for (TimeScaleType s: TimeScaleType.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (TimeScaleType e: TimeScaleType.values())
			result.add(e.toString());
		return result;
	}

	public static TimeScaleType defaultValue() {
		return ARBITRARY;
	}

	static {
		ValidPropertyTypes.recordPropertyType(TimeScaleType.class.getSimpleName(), 
		TimeScaleType.class.getName(),defaultValue());
	}

    public boolean calendarCompatible() {
        return (inflationFactor==1.0);
    }
    
    public static SortedSet<TimeUnits> validTimeUnits(TimeScaleType scale) {
        SortedSet<TimeUnits> result = new TreeSet<TimeUnits>();
        if (scale.equals(ARBITRARY))
            result.add(TimeUnits.UNSPECIFIED);
        else {
            for (TimeUnits tu:TimeUnits.values())
                if (tu.compareTo(scale.longestUnit)<=0)
                    if (tu.compareTo(scale.shortestUnit)>=0)
                        switch(tu) {
                        case MILLENNIUM:
                        case CENTURY:
                        case DECADE:
                        case DAY:
                        case HOUR:
                        case MINUTE:
                        case SECOND:
                        case MILLISECOND:
                        case MICROSECOND:
                            result.add(tu);
                            break;
                        default:;
                        }
            TimeUnits u = scale.yearUnit;
            if (u!=null) 
                if (u.compareTo(scale.longestUnit)<=0)
                    if (u.compareTo(scale.shortestUnit)>=0)
                        result.add(u);
            u = scale.monthUnit;
            if (u!=null) 
                if (u.compareTo(scale.longestUnit)<=0)
                    if (u.compareTo(scale.shortestUnit)>=0)
                        result.add(u);
            u = scale.weekUnit();
            if (u!=null)
                if (u.compareTo(scale.longestUnit)<=0)
                    if (u.compareTo(scale.shortestUnit)>=0)
                        result.add(u);
        }
        return result;
    }
}

