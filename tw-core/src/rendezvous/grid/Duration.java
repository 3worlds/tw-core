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
package rendezvous.grid;

/**
 * 
 * @author Shayne Flint - 2012
 *
 */
public class Duration implements DurationConstants {
	
	
	private long milliseconds = 0;
	
	public Duration(long milliseconds) {
		this.milliseconds = milliseconds;
	}
	
	public Duration() {
		this.milliseconds = 0;
	}
	
	public long duration() {
		return milliseconds;
	}
	
	public long days() {
		return milliseconds / DAYS;
	}
	
	public long hours() {
		return (milliseconds - days()*DAYS) / HOURS;
	}
	
	public long minutes() {
		return (milliseconds - days()*DAYS - hours()*HOURS) / MINUTES;
	}
	
	public long seconds() {
		return (milliseconds - days()*DAYS - hours()*HOURS - minutes()*MINUTES) / SECONDS;
	}
	
	public long milliseconds() {
		return (milliseconds - days()*DAYS - hours()*HOURS - minutes()*MINUTES - seconds()*SECONDS);
	}
	
	public String toString() {
		String result = "[Duration ";
		if (days() == 1) 
			result = result + "1 day ";
		if (days() > 1) 
			result = result + days() + " days ";

		if (hours() == 1) 
			result = result + "1 hour ";
		if (hours() > 1) 
			result = result + hours() + " hours ";
		
		if (minutes() == 1) 
			result = result + "1 minute ";
		if (minutes() > 1) 
			result = result + minutes() + " minutes ";
		
		if (seconds() == 1) 
			result = result + "1 second ";
		if (seconds() > 1) 
			result = result + seconds() + " seconds ";
		
		if (milliseconds() == 1) 
			result = result + "1 second ";
		if (milliseconds() > 1) 
			result = result + milliseconds() + " ms ";
		
		result = result + "(total " + milliseconds + " ms)]";
		return result;
	}

	
	// TESTING
	//
	
	public static void main(String[] args) {
		System.out.println(new Duration(1234));
		System.out.println(new Duration(1*DAY + 2*HOURS + 34*MINUTES + 3*SECONDS + 250*MILLISECONDS));
	}
}
