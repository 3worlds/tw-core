package au.edu.anu.twcore.ecosystem.runtime;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;

/**
 * for objects which have parameters
 * 
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
@Deprecated
public interface Parameterised {

	public TwData getParameters();
	
	public SystemContainer container();

}
