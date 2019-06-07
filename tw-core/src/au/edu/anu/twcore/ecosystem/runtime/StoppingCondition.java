package au.edu.anu.twcore.ecosystem.runtime;

import fr.ens.biologie.generic.Resettable;

/**
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public interface StoppingCondition extends Resettable {

	public boolean stop();
	
	@Override
	public default void reset() {
		// DEFAULT: nothing to do
	}
}
