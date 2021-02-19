package au.edu.anu.twcore.ecosystem.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * An ancestor for data trackers which track a flow of timed data
 * 
 * @author Jacques Gignoux - 19 févr. 2021
 *
 */
public interface SimulationTracker {
	
	/**
	 * Perform any operations required at the beginning of a time step (eg start recording)
	 *  
	 * @param status the simulator status
	 * @param time the time step (in simulator TimeLine units)
	 */
	public void openTimeRecord(SimulatorStatus status, long time);
	
	/**
	 * Perform any operations required at the end of a time step (eg stop recording and flush data)
	 */
	public void closeTimeRecord();

}
