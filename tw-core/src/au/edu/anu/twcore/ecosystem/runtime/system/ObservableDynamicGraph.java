package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.rvgrid.observer.Observable;

/**
 *
 * @author J. Gignoux - 18 févr. 2021
 *
 */
public interface ObservableDynamicGraph<N extends Node,E extends Edge>
		extends Observable<DynamicGraphObserver<N,E>> {

	@Override
	default boolean hasObservers() {
		return !observers().isEmpty();
	}

	@Override
	default void sendMessage(int msgType, Object payload) {
		throw new TwcoreException("Never call this method");
	}

}
