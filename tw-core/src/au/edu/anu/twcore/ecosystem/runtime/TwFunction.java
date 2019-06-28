package au.edu.anu.twcore.ecosystem.runtime;

import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;

/**
 * Ancestor for the class doing the user-defined computation
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public interface TwFunction {
	
	public void setProcess(AbstractProcess process);
	
	public AbstractProcess process();

	public void addConsequence(TwFunction function);

}
