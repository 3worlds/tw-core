package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;

/**
 * Interface for a container with parameters and state variables
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public interface StateContainer {

	/**
	 * Returns the parameter set associated to this container. It is specified by
	 * the categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 * 
	 * @return the parameter set - may be {@code null}
	 */
	public TwData parameters();

	/**
	 * Returns the variables associated to this container. It is specified by the
	 * categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 * 
	 * @return the variables - may be {@code null}
	 */
	public TwData variables();
	
	public void clearVariables();

}
