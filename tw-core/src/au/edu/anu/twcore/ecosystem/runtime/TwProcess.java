package au.edu.anu.twcore.ecosystem.runtime;

/**
 * the ancestor class for things that are run by the Simulator
 * @author gignoux - 10 mars 2017
 *
 */
public interface TwProcess {
	
	/** execute computation for time t with time interval dt */
	public void execute(double t, double dt);

	
}
