package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;

/**
 * Ancestor for the class doing the user-defined computation
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public abstract class TwFunctionAdapter implements TwFunction {
	
	private AbstractProcess myProcess = null;
	
	public final void setProcess(AbstractProcess process) {
		myProcess = process;
	}
	
	public final AbstractProcess process(){
		return myProcess;
	}

	public void addConsequence(TwFunction function) {
		// do nothing - some descendants have no consequences
	}

}
