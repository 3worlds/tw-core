package au.edu.anu.twcore.ecosystem.runtime.containers;

/**
 * An interface for objects that represent a container
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public interface Containing<T extends Container> {

	/**
	 * sets its container, only once.
	 *
	 * @param container
	 */
	public void setContent(T container);

	/**
	 * returns its container
	 *
	 * @return
	 */
	public T content();

}
