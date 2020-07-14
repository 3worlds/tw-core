package au.edu.anu.twcore.ecosystem.runtime.space;

import fr.cnrs.iees.identity.Identity;

/**
 * Interface for objects which have a shape
 *
 * @author J. Gignoux - 14 juil. 2020
 *
 */
public interface Shaped<I extends Identity, S extends Shape> extends Identity  {

	public S Shape();

	public void shape(S shape);

	public I item();

}
