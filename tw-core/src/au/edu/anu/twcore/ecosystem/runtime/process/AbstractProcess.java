package au.edu.anu.twcore.ecosystem.runtime.process;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import fr.ens.biologie.generic.Sealable;

/**
 * An ancestor class for 3Worlds "Processes". The descendant Processes implement different
 * ways of looping on SystemComponents or SystemRelations to apply user-defined functions.
 * @author gignoux - 10 mars 2017
 *
 */
public abstract class AbstractProcess implements TwProcess, Sealable {

	private boolean sealed = false;
    private Ecosystem ecosystem = null;
    
    public AbstractProcess(Ecosystem world) {
    	super();
    	ecosystem = world;
    }

	@Override
	public final Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public final boolean isSealed() {
		return sealed;
	}
	
	public final Ecosystem ecosystem() {
		return ecosystem;
	}
	
	public abstract void addFunction(TwFunction function);
}
