package au.edu.anu.twcore.ecosystem.runtime;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * An interface for objects that record data from 3worlds objects (ie data trackers)
 *
 * @author J. Gignoux - 15 mai 2020
 *
 */
public interface DataRecorder {

	public void recordTime(long time);

	public void recordItem(String... labels);

	public void record(SimulatorStatus status, TwData... props);

	public boolean isTracked(CategorizedComponent sc);

}
