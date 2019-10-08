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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.rscs.aot.AotException;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * @author Ian Davies
 * @date 22 Jan. 2018
 * 
 *       Updated with non exact time conversion based on Gregorian calendar (new
 *       java.time architecture)
 */
public class TimeUtil {
	/*-
	 * NOTE time line can be < 0. When using the Gregorian calendar, time a t=0 (epoch) is midnight on 1/1/1970). 
	 * Therefore early dates must be -ve offset. This is stored in TimeLine.  
	 * There will be a Long overflow (292 MILLENNIUM -> microSecond). 
	 * NOTE that dt is not a constant in clock models with Gregorian TimeScaleType (see clock model dt().
	 * Simulator sendSimTimeMessage now sends a payload of time unit meta-data to widgets (TimeDisplayManager).
	 * To post an event from any process call with an edge to a queue:
	 *   postEvent(String queueName, double currentTime, double deltaTime)
	 *   Time is expressed in floating point units of this process's time model. So for a day step model
	 *   postEvent("myQueue",t,0.5) to a queue of say 1 hour step, the event will arrive at midday.
	 *   The process activated by an event can get the event by "callingEvent();".As well as containing the time 
	 *   (not very interesting for the called process since time is an argument to execute),
	 *   the TimeEvent can contain optionally, a user inserted object and BitSet from whatever process posted
	 *   the event.   
	 * 
	 * TODO 
	 *    1) Query to check that all time model units are within range of Timeline largest | smallest 
	 *         and that at least one time model == largest and one == smallest.
	 *    2) Should we allow an event producer to be at a finer resolution than the event recipient? 
	 *        That is, can a daily process post an event to a monthly event-driven time model? 
	 *        I can't think of an example which is not crazy! If so, we need a query to catch this. 
	 *        The problem with doing this is that the small scale time unit is rounded to the nearest 
	 *        larger scale which has the potential to set the time backwards. 
	 *        We could prevent this by insisting on rounding up but this defeats the purpose and would be a mess.
	 *        On the other hand, rounding from larger to smaller can produce repeated calls with dt==0. 
	 *        Doesn't cause a problem so i propose leaving this. 
	 *        I have added an exception to the simulator to catch attempts to set time backwards!
	 *        Not happy with all this as there is more to think about here. Perhaps event producers and consumers must always
	 *        have the same time step?
	 *    3) TimeScaleType.validTimeUnits(...) should be static?
	 *    4) It doesn't make sense for an event-driven time model to runAtTimeZero. 
	 *       This property should be moved to clock models only.
	 *    5) Time management not tested with dataTracker.
	 */

	public static long[] factorInexactTime(long time, List<TimeUnits> units) {
		TimeUnits smallest = units.get(0);
		long[] result = new long[units.size()];
		LocalDateTime dt = longToDate(time, smallest);
		for (int i = 0; i < units.size(); i++) {
			result[i] = getDateTimeField(dt, units.get(i));
		}
		return result;
	}

	private static long getDateTimeField(LocalDateTime dt, TimeUnits timeUnits) {
		switch (timeUnits) {
		case MICROSECOND:
			return dt.getNano();
		case SECOND:
			return dt.getSecond();
		case MINUTE:
			return dt.getMinute();
		case HOUR:
			return dt.getHour();
		case DAY:
			return dt.getDayOfMonth();
		case MONTH:
			// Careful: 1..12
			return dt.getMonthValue();
		case YEAR:
			return dt.getYear();
		default:
			// modify Gregorian TimeScaleType to prevent weeks, millis, decades, centuries and millenniums
			throw new TwcoreException("Factoring a Gregorian calander into " + timeUnits.name() + " is not supported.");
		}
	}

	public static long[] factorExactTime(long time, List<TimeUnits> units) {
		long absTime = Math.abs(time);
		TimeUnits smallest = units.get(0);
		long[] result = new long[units.size()];
		long remainder = absTime;
		for (int i = units.size()-1;i>=0; i--) {
			TimeUnits unit = units.get(i);
			result[i] = (long) TimeUtil.convertTime(remainder, smallest, unit, null);
			if (result[i] > 0) {
				long wholeUnit = (long) TimeUtil.convertTime(result[i], unit, smallest, null);
				remainder = remainder - wholeUnit;
			}
		}
		return result;
	}

	/**
	 * Non-Gregorian time formatting units assumed sorted from largest to smallest
	 */
	public static String formatExactTimeScales(long time, List<TimeUnits> units) {
		long[] unitTimes = factorExactTime(time, units);
		int nFields = 0;
		for (int i = 0; i < unitTimes.length; i++)
			if (unitTimes[i] > 0)
				nFields++;
		TimeUnits smallest = units.get(units.size() - 1);
		String result = "";
		for (int i = 0; i < unitTimes.length; i++)
			if (time >= 0)
				result = result + units.get(i).abbreviation() + ": " + unitTimes[i] + " ";
			else
				result = result + units.get(i).abbreviation() + ": " + (-1) * unitTimes[i] + " ";

		// Don't bother adding total of smallest unit if there is only 1 field
		if (nFields > 1)
			return result + "[Total " + smallest.abbreviation() + ": " + time + "]";
		else
			return result;
	}

	private static void testFormatting() {
		List<TimeUnits> units = new ArrayList<>();
		double rawTime = Math.PI;
		long time;
		units.add(TimeUnits.MILLENNIUM);
		units.add(TimeUnits.CENTURY);
		units.add(TimeUnits.DECADE);
		units.add(TimeUnits.YEAR);
		time = (long) convertTime(rawTime, TimeUnits.MILLENNIUM, TimeUnits.YEAR, null);
		System.out.println(formatExactTimeScales(time, units));

		units.clear();
		units.add(TimeUnits.DAY);
		units.add(TimeUnits.HOUR);
		units.add(TimeUnits.MINUTE);
		units.add(TimeUnits.SECOND);
		units.add(TimeUnits.MILLISECOND);
		units.add(TimeUnits.MICROSECOND);
		time = (long) convertTime(rawTime, TimeUnits.DAY, TimeUnits.MICROSECOND, null);
		System.out.println(formatExactTimeScales(time, units));

	}

	// removed from TimeModel for wider application.
	public static String timeUnitName(TimeUnits timeUnit, int nTimeUnits) {
		String n = timeUnit.name();
		if (timeUnit.equals(TimeUnits.UNSPECIFIED))
			n = timeUnit.abbreviation();
		if (nTimeUnits == 1)
			return n;
		else
			return nTimeUnits + "-" + n;
	}

	public static String timeUnitAbbrev(TimeUnits timeUnit, int nTimeUnits) {
		String n = timeUnit.abbreviation();
		if (nTimeUnits == 1)
			return n;
		else
			return nTimeUnits + "-" + n;
	}

	/**
	 * Computes the exact number of smaller time units in a larger time unit (as an
	 * integer and assuming the units are compatible). Note: arguments must be
	 * passed in size order, ie the greater unit first.
	 * 
	 * @param larger
	 * @param smaller
	 * @return the number of smaller units in the larger unit as a long integer, 0
	 *         if units are inconvertible
	 */
	public static long timeUnitExactConversionFactor(TimeUnits larger, TimeUnits smaller) {
		if (larger.equals(smaller))
			return 1L;
		switch (smaller) {
		case MILLENNIUM:
			switch (larger) {
			// case MILLENNIUM: return 1L;
			default:
				return 0L;
			}
		case CENTURY:
			switch (larger) {
			case MILLENNIUM:
				return 10L;
			// case CENTURY: return 1L;
			default:
				return 0L;
			}
		case DECADE:
			switch (larger) {
			case MILLENNIUM:
				return 100L;
			case CENTURY:
				return 10L;
			// case DECADE: return 1L;
			default:
				return 0L;
			}
		case YEAR_366:
			switch (larger) {
			case MILLENNIUM:
				return 1000L;
			case CENTURY:
				return 100L;
			case DECADE:
				return 10L;
			default:
				return 0L;
			}
		case YEAR:
			switch (larger) {
			case MILLENNIUM:
				return 1000L;
			case CENTURY:
				return 100L;
			case DECADE:
				return 10L;
			// case YEAR: return 1L;
			default:
				return 0L;
			}
		case YEAR_365:
			switch (larger) {
			case MILLENNIUM:
				return 1000L;
			case CENTURY:
				return 100L;
			case DECADE:
				return 10L;
			default:
				return 0L;
			}
		case YEAR_364:
			switch (larger) {
			case MILLENNIUM:
				return 1000L;
			case CENTURY:
				return 100L;
			case DECADE:
				return 10L;
			// case YEAR_364: return 1L;
			default:
				return 0L;
			}
		case YEAR_360:
			switch (larger) {
			case MILLENNIUM:
				return 1000L;
			case CENTURY:
				return 100L;
			case DECADE:
				return 10L;
			// case YEAR_360: return 1L;
			default:
				return 0L;
			}
		case YEAR_336:
			switch (larger) {
			case MILLENNIUM:
				return 1000L;
			case CENTURY:
				return 100L;
			case DECADE:
				return 10L;
			default:
				return 0L;
			}
		case BIMONTH_61:
			switch (larger) {
			case MILLENNIUM:
				return 6000L;
			case CENTURY:
				return 600L;
			case DECADE:
				return 60L;
			case YEAR_366:
				return 6L;
			// case BIMONTH_61:return 1L;
			default:
				return 0L;
			}
		case MONTH:
			switch (larger) {
			case MILLENNIUM:
				return 12000L;
			case CENTURY:
				return 1200L;
			case DECADE:
				return 120L;
			case YEAR:
				return 12L;
			// case MONTH: return 1L;
			default:
				return 0L;
			}
		case MONTH_30:
			switch (larger) {
			case MILLENNIUM:
				return 12000L;
			case CENTURY:
				return 1200L;
			case DECADE:
				return 120L;
			case YEAR_360:
				return 12L;
			// case MONTH_30: return 1L;
			default:
				return 0L;
			}
		case MONTH_28:
			switch (larger) {
			case MILLENNIUM:
				return 13000L;
			case CENTURY:
				return 1300L;
			case DECADE:
				return 130L;
			case YEAR_364:
				return 13L;
			case YEAR_336:
				return 12L;
			// case MONTH_28: return 1L;
			default:
				return 0L;
			}
		case FORTNIGHT_15:
			switch (larger) {
			case MILLENNIUM:
				return 24000L;
			case CENTURY:
				return 2400L;
			case DECADE:
				return 240L;
			case YEAR_360:
				return 24L;
			case MONTH_30:
				return 2L;
			// case FORTNIGHT_15: return 1L;
			default:
				return 0L;
			}
		case WEEK:
			switch (larger) {
			case YEAR_364:
				return 52L;
			case YEAR_336:
				return 48L;
			case MONTH_28:
				return 4L;
			// case WEEK: return 1L;
			default:
				return 0L;
			}
		case DAY:
			switch (larger) {
			case YEAR_366:
				return 366L;
			case YEAR_365:
				return 365L;
			case YEAR_364:
				return 364L;
			case YEAR_360:
				return 360L;
			case YEAR_336:
				return 336L;
			case BIMONTH_61:
				return 61L;
			case MONTH_30:
				return 30L;
			case MONTH_28:
				return 28L;
			case FORTNIGHT_15:
				return 15L;
			case WEEK:
				return 7L;
			// case DAY: return 1L;
			default:
				return 0L;
			}
		case HOUR:
			switch (larger) {
			// case HOUR: return 1L;
			default:
				return 24L * timeUnitExactConversionFactor(larger, TimeUnits.DAY);
			}
		case MINUTE:
			switch (larger) {
			// case MINUTE:return 1L;
			default:
				return 60L * timeUnitExactConversionFactor(larger, TimeUnits.HOUR);
			}
		case SECOND:
			switch (larger) {
			// case SECOND:return 1L;
			default:
				return 60L * timeUnitExactConversionFactor(larger, TimeUnits.MINUTE);
			}
		case MILLISECOND:
			switch (larger) {
			// case MILLISECOND: return 1L;
			default:
				return 1000L * timeUnitExactConversionFactor(larger, TimeUnits.SECOND);
			}
		case MICROSECOND:
			switch (larger) {
			// case MICROSECOND: return 1L;
			default:
				return 1000L * timeUnitExactConversionFactor(larger, TimeUnits.MILLISECOND);
			}
		case UNSPECIFIED:
			return 0L;
		}
		return 0L;
	}

	public static boolean isExactConversion(TimeUnits from, TimeUnits to) {
		if (to.compareTo(from) < 0)
			return timeUnitExactConversionFactor(from, to) > 0L;
		else
			return timeUnitExactConversionFactor(to, from) > 0L;
	}

	/**
	 * 
	 * @param time
	 * @param tu   : a unit which is a multiple of MILLISECOND
	 * @return LocalDateTime (immutable) of this instant without TimeZone offset
	 *         (UTC).
	 */
	public static LocalDateTime longToDate(long time, TimeUnits tu) {
		long factor = TimeUtil.timeUnitExactConversionFactor(tu, TimeUnits.MILLISECOND);
		Instant instant = longToInstant(factor * time);
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}

	public static Long dateToLong(LocalDateTime dateTime, TimeUnits tu, LocalDateTime timeZero) {
		Long seconds = dateTime.toEpochSecond(ZoneOffset.UTC);
		return Math.round(TimeUtil.convertTime(seconds, TimeUnits.SECOND, tu, timeZero));
	}

	/**
	 * 
	 * @param time in milliseconds (0 = 00:00 01/01/1970)
	 * @return Instant
	 */
	public static Instant longToInstant(long time) {
		return Instant.ofEpochMilli(time);
	}

	/**
	 * Formatter for Gregorian Calendar
	 * 
	 * @param tu
	 * @return Based on the TimeUnit, returns a formatter of the appropriate
	 *         resolution. These patterns don't seem to support milli-of-second or
	 *         micro-of-milli but only nano-of-second
	 */
	public static DateTimeFormatter getGregorianFormat(TimeUnits tu) {
		switch (tu) {
		case MICROSECOND:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:nnnnnn");
		case MILLISECOND:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:nnn");
		case SECOND:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		case MINUTE:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		case HOUR:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
		case DAY:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd");
		case WEEK:
			return DateTimeFormatter.ofPattern("yyyy-MM-dd");
		case MONTH:
			return DateTimeFormatter.ofPattern("yyyy-MM");
		default:
			return DateTimeFormatter.ofPattern("yyyy");
		}

	}

	/**
	 * 
	 * @param tu
	 * @return Helper method to convert from TimeUnits to ChronoUnit for use with
	 *         the new Java date system.
	 */
	private static ChronoUnit timeUnitsToChronoUnit(TimeUnits tu) {
		switch (tu) {
		case MILLENNIUM:
			return ChronoUnit.MILLENNIA;
		case CENTURY:
			return ChronoUnit.CENTURIES;
		case DECADE:
			return ChronoUnit.DECADES;
		case YEAR:
			return ChronoUnit.YEARS;
		case MONTH:
			return ChronoUnit.MONTHS;
		case WEEK:
			return ChronoUnit.WEEKS;
		case DAY:
			return ChronoUnit.DAYS;
		case HOUR:
			return ChronoUnit.HOURS;
		case MINUTE:
			return ChronoUnit.MINUTES;
		case SECOND:
			return ChronoUnit.SECONDS;
		case MILLISECOND:
			return ChronoUnit.MILLIS;
		case MICROSECOND:
			return ChronoUnit.MICROS;
		default: {
			throw new AotException("Unable to convert " + tu + "to ChronoUnit");
		}
		}
	}

	/**
	 * 
	 * @param earliest
	 * @param latest
	 * @param unit
	 * @return number of time units of type 'unit' from earliest up to but not
	 *         including latest dates
	 */
	public static long timeBetween(LocalDateTime earliest, LocalDateTime latest, TimeUnits unit) {
		return timeUnitsToChronoUnit(unit).between(earliest, latest);
	}

	/**
	 * 
	 * @param date
	 * @param unit
	 * @param n
	 * @return date incremented n times the unit
	 * 
	 */
	public static LocalDateTime getIncrementedDate(LocalDateTime date, TimeUnits unit, long n) {
		switch (unit) {
		case MICROSECOND:
			return date.plusNanos(n * 1_000);
		case MILLISECOND:
			return date.plusNanos(n * 1_000_000);
		case SECOND:
			return date.plusSeconds(n);
		case MINUTE:
			return date.plusMinutes(n);
		case HOUR:
			return date.plusHours(n);
		case DAY:
			return date.plusDays(n);
		case WEEK:
			return date.plusWeeks(n);
		case MONTH:
			return date.plusMonths(n);
		case YEAR:
			return date.plusYears(n);
		case DECADE:
			return date.plusYears(n * 10L);
		case CENTURY:
			return date.plusYears(n * 100L);
		case MILLENNIUM:
			return date.plusYears(n * 1000L);
		default: {
			throw new AotException("Unable to advance " + unit);
		}

		}
	}

	/**
	 * Converts time between any units (?)
	 * 
	 * @param time
	 * @param from
	 * @param to
	 * @param baseTime
	 * @return
	 */
	public static double convertTime(double time, TimeUnits from, TimeUnits to, LocalDateTime startDateTime) {
		long factor;
		if (from.equals(to))
			return time;
		if (time == 0.0d)
			return time;
		if (to.compareTo(from) < 0) {
			factor = timeUnitExactConversionFactor(from, to);
			if (factor != 0L) {
				return factor * time;
			} else
				return timeInexactConversionFiner(time, startDateTime, from, to);

		} else {
			factor = timeUnitExactConversionFactor(to, from);
			if (factor != 0L) {
				return (1.0 / (1.0 * factor)) * time;
			} else
				return timeInexactConversionCoarser(time, startDateTime, from, to);
		}
	}

	private static double timeInexactConversionCoarser(double time, LocalDateTime baseDate, TimeUnits finest,
			TimeUnits coarsest) {
		// opportunity to save time if fractional part ==0;
		LocalDateTime date = getIncrementedDate(baseDate, finest, (long) time);
		long coarseBetween = timeBetween(baseDate, date, coarsest);
		LocalDateTime lowerBoundDate = getIncrementedDate(baseDate, coarsest, coarseBetween);
		long nFine = timeBetween(baseDate, lowerBoundDate, finest);
		LocalDateTime upperBoundDate = getIncrementedDate(lowerBoundDate, coarsest, 1);
		double nFineInLastLarge = timeBetween(lowerBoundDate, upperBoundDate, finest);
		double nFineRemainder = time - nFine;
		double coarseFraction = nFineRemainder / nFineInLastLarge;
		return coarseBetween + coarseFraction;
	}

	/**
	 * @param time:    as a floating point value
	 * @param baseDate
	 * @param from:    TimeUnits of 'time'
	 * @param to:      TimeUnits of result
	 * @return fractional value in units 'to'.
	 */
	private static double timeInexactConversionFiner(double time, LocalDateTime baseDate, TimeUnits coarsest,
			TimeUnits finest) {
		long lower = (long) time;
		long upper = lower;
		if (Math.round(time) != time)
			upper = lower + 1;
		// opportunity to save time if fractional part ==0;
		LocalDateTime ldtLower = getIncrementedDate(baseDate, coarsest, lower);
		LocalDateTime ldtUpper = getIncrementedDate(baseDate, coarsest, upper);
		long lowerTo = timeBetween(baseDate, ldtLower, finest);
		long upperTo = timeBetween(baseDate, ldtUpper, finest);
		long diff = upperTo - lowerTo;
		double frac = (time - lower) * diff;
		return lowerTo + frac;
	}

	@SuppressWarnings("unused")
	private static void testConversions(TimeScaleType tst) {
		long startTime = 0L;
		LocalDateTime timeOrigin = longToDate(startTime, TimeUnits.MILLISECOND);
		System.out.println("Start of Epoch: " + timeOrigin);
		SortedSet<TimeUnits> units = new TreeSet<TimeUnits>();
		units.clear();
		TimeUnits u;
		u = tst.yearUnit();
		if (u != null)
			units.add(u);
		u = tst.monthUnit();
		if (u != null)
			units.add(u);
		u = tst.weekUnit();
		if (u != null)
			units.add(u);
		units.add(TimeUnits.DAY);
		units.add(TimeUnits.HOUR);
		units.add(TimeUnits.SECOND);

		// Boring things
		// units.add(TimeUnits.MILLENNIUM);
		// units.add(TimeUnits.CENTURY);
		// units.add(TimeUnits.DECADE);
		// units.add(TimeUnits.MILLISECOND);
		// units.add(TimeUnits.MICROSECOND);

		// Gregorian: There will be long overflow MILLENNIUM TO MICROSECOND for time >
		// 292.0
		long MILLENNIUMMicros = 1000L * 365L * 24L * 60L * 60 * 1000L * 1000L;
		System.out.println("Maximum millenia if using microseconds: " + Long.MAX_VALUE / MILLENNIUMMicros);

		double time = 1.0; // i.e.(<292)
		int i = 0;
		List<TimeUnits> lst = new ArrayList<>();
		lst.addAll(units);
		System.out.println("=========" + tst.longName());
		for (int j = 0; j < lst.size(); j++) {
			TimeUnits from = lst.get(j);
			for (int k = j; k < lst.size(); k++) {
				TimeUnits to = lst.get(k);
				double time1 = time;
				i++;
				System.out.println(i + ")");
				// System.out.print(from + " to " + to + ": ");
				double time2 = convertTime(time1, from, to, timeOrigin);
				System.out.println("  " + time1 + " " + from + " = " + time2 + " " + to);

				// System.out.print(to + " to " + from + ": ");
				time2 = convertTime(time1, to, from, timeOrigin);
				System.out.println("  " + time1 + " " + to + " = " + time2 + " " + from + "\n");
			}
		}

	}

	public static void main(String[] args) {
		testFormatting();
		testConversions(TimeScaleType.MONO_UNIT);

	}

}
