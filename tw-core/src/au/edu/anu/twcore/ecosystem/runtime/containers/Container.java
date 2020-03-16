package au.edu.anu.twcore.ecosystem.runtime.containers;

import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.identity.IdentityScope;
import fr.cnrs.iees.identity.impl.ResettableLocalScope;
import fr.ens.biologie.generic.Resettable;

/**
 * An ancestor class for any kind of SystemComponent / SystemRelation container.
 * It maintains a unique scope for all containers, which generates unique names.
 * A reset() operation will replace the scope with a new one so that same names
 * can be used after one reset (ie, container ids are unique only between two
 * resets, not over a reset).
 *
 * @author gignoux
 *
 */
public interface Container extends Identity, Resettable {

	public static ResettableLocalScope containerScope = new ResettableLocalScope("3w-containers");

	@Override
	default void preProcess() {
		containerScope.preProcess();
	}

	@Override
	default IdentityScope scope() {
		return containerScope;
	}

}
