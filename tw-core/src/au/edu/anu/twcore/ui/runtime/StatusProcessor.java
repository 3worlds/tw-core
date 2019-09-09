package au.edu.anu.twcore.ui.runtime;

/**
 * An interface for objects which receive a 'status' object and process it. (eg display widgets)
 * 
 * @author Jacques Gignoux - 2 sept. 2019
 *
 */
@Deprecated
public interface StatusProcessor {
	
	public void processStatus(Object status);

}
