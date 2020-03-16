package au.edu.anu.twcore.ecosystem.runtime.containers;

/**
 * An interface for objects which are contained in any kind of Container
 * @author gignoux
 *
 */
public interface Contained<T extends Container> {

	/**
	 * sets its container, only once.
	 *
	 * @param container
	 */
	public void setContainer(T container);

	/**
	 * returns its container
	 *
	 * @return
	 */
	public T container();

	/**
	 * deletes its container, making it a free-floating object. Use with caution
	 */
	public void removeFromContainer();

}
