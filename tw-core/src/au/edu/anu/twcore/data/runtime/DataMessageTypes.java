package au.edu.anu.twcore.data.runtime;

/**
 * Records constants used for sending data messages
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public interface DataMessageTypes {
	
	public static final int MSGBASE = 1500;
	
	public static final int METADATA  = MSGBASE;
	
	public static final int VALUE_PAIR = MSGBASE + 1;
	public static final int TIME_SERIES = VALUE_PAIR + 1;
	public static final int TIME = TIME_SERIES + 1;
	// etc...

}
