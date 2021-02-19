package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Collection;

import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.rvgrid.observer.Observer;

/**
 *
 * @author J. Gignoux - 18 févr. 2021
 *
 */
public interface DynamicGraphObserver<N extends Node,E extends Edge>
		extends Observer {

	public default void onEdgeAdded(E e) {}
	
	public default void onEdgesAdded(Collection<E> es) {
		for (E e:es) onEdgeAdded(e);
	}	
	
	public default void onEdgeRemoved(E e) {}
	
	public default void onEdgesRemoved(Collection<E> es) {
		for (E e:es) onEdgeRemoved(e);
	}
	
	public default void onEdgeChanged(E e) {}
	
	public default void onEdgesChanged(Collection<E> es) {
		for (E e:es) onEdgeChanged(e);
	}

	
	public default void onNodeAdded(N n) {}
	
	public default void onNodesAdded(Collection<N> ns) {
		for (N n:ns) onNodeAdded(n);
	}
	
	public default void onNodeRemoved(N n) {}
	
	public default void onNodesRemoved(Collection<N> ns) {
		for (N n:ns) onNodeRemoved(n);
	}
	
	public default void onNodeChanged(N n) {}
	
	public default void onNodesChanged(Collection<N> ns) {
		for (N n:ns) onNodeChanged(n);
	}

}
