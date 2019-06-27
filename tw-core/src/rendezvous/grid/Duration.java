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
