package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Collection;

import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.rvgrid.observer.Observer;

/**
 *
 * @author J. Gignoux - 18 févr. 2021
 *
 */
public interface DynamicGraphObserver<N extends Node,E extends Edge>
		extends Observer {

	public default void onEdgeAdded(E e) {}
	public default void onEdgesAdded(Collection<E> es) {}
	public default void onEdgeRemoved(E e) {}
	public default void onEdgesRemoved(Collection<E> es) {}

	public default void onNodeAdded(N n) {}
	public default void onNodesAdded(Collection<N> ns) {}
	public default void onNodeRemoved(N n) {}
	public default void onNodesRemoved(Collection<N> ns) {}

	public default void onPropertyChanged(String key, Object value) {}
	public default void onPropertyChanged(String key, double value) {}
	public default void onPropertyChanged(String key, float value) {}
	public default void onPropertyChanged(String key, long value) {}
	public default void onPropertyChanged(String key, int value) {}
	public default void onPropertyChanged(String key, short value) {}
	public default void onPropertyChanged(String key, byte value) {}
	public default void onPropertyChanged(String key, boolean value) {}
	public default void onPropertyChanged(String key, String value) {}

	public default void onPropertiesChanged(ReadOnlyPropertyList properties) {}

}
