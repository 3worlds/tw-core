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

}
