package au.edu.anu.twcore.ecosystem.runtime.timer;

/**
 * An interface for user code to generate time events for an EventTimer
 *
 * @author J. Gignoux - 29 mai 2020
 *
 */
public interface EventQueue {

	public void postTimeEvent(double nextTime);

}
