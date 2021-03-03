package au.edu.anu.twcore.data.runtime;

/**
 * An interface for data messages that get their data from the TwData hierarchy
 * 
 * @author Jacques Gignoux - 3 mars 2021
 *
 */
public interface OutputTwData  {

	public default void setValue(DataLabel label, double value) {}

	public default void setValue(DataLabel label, float value) {}

	public default void setValue(DataLabel label, int value) {}

	public default void setValue(DataLabel label, long value) {}

	public default void setValue(DataLabel label, byte value) {}

	public default void setValue(DataLabel label, short value) {}

	public default void setValue(DataLabel label, boolean value) {}

	public default void setValue(DataLabel label, String value) {}

}
