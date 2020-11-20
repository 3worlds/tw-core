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
 * generated by CentralResourceGenerator on Fri Nov 20 11:36:57 CET 2020
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

@SuppressWarnings("unused")
public enum TimeUnits {

// UNSPECIFIED: an arbitrary time unit
	UNSPECIFIED ("an arbitrary time unit",
		"t.u"),

// MICROSECOND: microsecond
	MICROSECOND ("microsecond",
		"\u00B5s"),

// MILLISECOND: millisecond = 1000 microseconds
	MILLISECOND ("millisecond = 1000 microseconds",
		"ms"),

// SECOND: second = 1000 milliseconds
	SECOND ("second = 1000 milliseconds",
		"s"),

// MINUTE: minute = 60 seconds
	MINUTE ("minute = 60 seconds",
		"min"),

// HOUR: hour = 60 minutes
	HOUR ("hour = 60 minutes",
		"h"),

// DAY: day = 24 hours
	DAY ("day = 24 hours",
		"d"),

// WEEK: week = 7 days
	WEEK ("week = 7 days",
		"w"),

// FORTNIGHT_15: French-style fortnight = 15 days
	FORTNIGHT_15 ("French-style fortnight = 15 days",
		"f\'"),

// MONTH_28: month = 4 weeks of 7 days
	MONTH_28 ("month = 4 weeks of 7 days",
		"mo\'"),

// MONTH_30: month = 30 days
	MONTH_30 ("month = 30 days",
		"mo\'"),

// MONTH: calendar month (= 1/12 of a calendar year), _i.e._ approx. 30,44 days, but with irregular durations (28,29, 30 or 31 days)
	MONTH ("calendar month (= 1/12 of a calendar year), _i.e._ approx. 30,44 days, but with irregular durations (28,29, 30 or 31 days)",
		"mo"),

// BIMONTH_61: 2 months = 61 days
	BIMONTH_61 ("2 months = 61 days",
		"bmo\'"),

// YEAR_336: year = 12 months of 4 weeks of 7 days
	YEAR_336 ("year = 12 months of 4 weeks of 7 days",
		"y\'"),

// YEAR_360: year = 12 months of 30 days
	YEAR_360 ("year = 12 months of 30 days",
		"y\'"),

// YEAR_364: year = 52 weeks of 7 days = 13 months of 28 days
	YEAR_364 ("year = 52 weeks of 7 days = 13 months of 28 days",
		"y\'"),

// YEAR_365: year = 365 days
	YEAR_365 ("year = 365 days",
		"y\'"),

// YEAR: calendar year, _i.e._ approx. 365.25 days, but with irregular durations (365 or 366 days)
	YEAR ("calendar year, _i.e._ approx. 365.25 days, but with irregular durations (365 or 366 days)",
		"y"),

// YEAR_366: year = 6 bimonths of 61 days
	YEAR_366 ("year = 6 bimonths of 61 days",
		"y\'"),

// DECADE: decade = 10 years
	DECADE ("decade = 10 years",
		"dY"),

// CENTURY: century = 10 decades
	CENTURY ("century = 10 decades",
		"hY"),

// MILLENNIUM: millennium = 10 centuries
	MILLENNIUM ("millennium = 10 centuries",
		"kY");
	
	private final String description;
	private final String abbreviation;

	private TimeUnits(String description, String abbreviation) {
		this.description = description;
		this.abbreviation = abbreviation;
	}

	public String description() {
		return description;
	}

	public String abbreviation() {
		return abbreviation;
	}

	public static String[] toStrings() {
		String[] result = new String[TimeUnits.values().length];
		for (TimeUnits s: TimeUnits.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (TimeUnits e: TimeUnits.values())
			result.add(e.toString());
		return result;
	}

	public static TimeUnits defaultValue() {
		return UNSPECIFIED;
	}

	static {
		ValidPropertyTypes.recordPropertyType(TimeUnits.class.getSimpleName(), 
		TimeUnits.class.getName(),defaultValue());
	}

    /**
     * Various year duration in days according to  https://en.wikipedia.org/wiki/Year#Astronomical_years
     * Use these to setup a reference year duration when needed to convert between true years and
     * approximate years. Cf wikipedia for the exact meanings of these definitions.
     * All assume a 86400 seconds day.
     */
    private static double JULIAN_YEAR = 365.25;
    private static double GREGORIAN_YEAR = 365.2425;
    private static double SIDEREAL_YEAR = 365.256363051;
    private static double TROPICAL_YEAR = 365.2421898;
    private static double ANOMALISTIC_YEAR = 365.259635864;
    private static double GAUSSIAN_YEAR = 365.2568983;
    // select one value among the previous ones as the reference year
    public static double REFERENCE_YEAR = JULIAN_YEAR;
    // well I think I would like a galactic year actually
}

