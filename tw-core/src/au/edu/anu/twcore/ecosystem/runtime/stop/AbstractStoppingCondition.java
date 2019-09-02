package au.edu.anu.twcore.ecosystem.runtime.stop;

import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.simulator.Simulator;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.ens.biologie.generic.Sealable;

/**
 * Old stuff - refactored by:
 * @author gignoux - 7 mars 2017
 *
 */

public abstract class AbstractStoppingCondition implements StoppingCondition, Sealable {

	private Simulator sim = null;
	private boolean sealed = false;
	
	@Override
	public void attachSimulator(Simulator sim) {
		this.sim = sim;
		sealed = true;
	}
	
	@Override
	public Simulator simulator() {
		if (isSealed())
			return sim;
		else
			throw new TwcoreException("Attempt to access unsealed data");
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}


}
