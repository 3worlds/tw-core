package au.edu.anu.twcore.ecosystem.runtime.system;

import static fr.cnrs.iees.twcore.constants.PopulationVariables.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Population;

/**
 * A class to contain ComponentContainer automatic data, ie mainly population
 * sizes. This one is for simple containers only ie without nested subContainers.
 * The class is initialized with the internal lists(made immutable) of a Dynamic Container
 * implementation, so that the numbers are always in sync with the collections.
 *
 * @author J. Gignoux - 16 avr. 2020
 *
 */
public class ContainerData
		extends TwData
		implements Population {

	// the lists of items which sizes are tracked here
	final Collection<?> items;
	final Collection<?> itemsAdded;
	final Collection<?> itemsRemoved;

	private static String[] propsArray = {COUNT.shortName(),NADDED.shortName(),NREMOVED.shortName()};
	private static Set<String> props = new HashSet<String>(Arrays.asList(propsArray));
//	protected static PropertyKeys propsPK = new PropertyKeys(props);

	/** Basic constructor - can be used to track any 3 lists, actually*/
	public ContainerData(Collection<?> items,
			Collection<?> itemsAdded,
			Collection<?> itemsRemoved) {
		super();
		this.items = items;
		this.itemsAdded = itemsAdded;
		this.itemsRemoved = itemsRemoved;
	}

	/** constructor for ComponentContainer */
	public ContainerData(CategorizedContainer<?> container) {
		this(container.items.values(),container.itemsToAdd,container.itemsToRemove);
	}

	// ReadOnlyPropertyList

	@Override
	public Object getPropertyValue(String key) {
		if (key.equals(COUNT.shortName()))
			return count();
		else if (key.equals(NADDED.shortName()))
			return nAdded();
		else if (key.equals(NREMOVED.shortName()))
			return nRemoved();
		return null;
	}

	@Override
	public final boolean hasProperty(String key) {
		return getKeysAsSet().contains(key);
	}

	@Override
	public final String propertyToString(String key) {
		return getPropertyValue(key).toString();
	}

	@Override
	public final Class<?> getPropertyClass(String key) {
		return Integer.class;
	}

	// possibly unordered property list
	@Override
	public Set<String> getKeysAsSet() {
		return props;
	}

	// ordered property list
	@Override
	public String[] getKeysAsArray() {
		return propsArray;
	}

	@Override
	public ContainerData clone() {
		return new ContainerData(items,itemsAdded,itemsRemoved);
	}

	@Override
	public final int size() {
		return getKeysAsArray().length;
	}

	// Population

	@Override
	public final int count() {
		return items.size();
	}

	@Override
	public final int nAdded() {
		return itemsAdded.size();
	}

	@Override
	public final int nRemoved() {
		return itemsRemoved.size();
	}

	@Override
	public void resetCounters() {
		// DO NOTHING: it's done in containers themselves.
	}

	// Object

	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder(1024);
		boolean first = true;
		for (String key : getKeysAsArray())
			if (first) {
				sb.append(key).append("=").append(getPropertyValue(key));
				first = false;
			} else
				sb.append(' ').append(key).append("=").append(getPropertyValue(key));
		return sb.toString();
	}

	@Override
	public TwData setProperty(String key, Object value) {
		// do nothing
		return this;
	}

	@Override
	protected TwData cloneStructure() {
		return new ContainerData(items,itemsAdded,itemsRemoved);
	}

	@Override
	public TwData clear() {
		// do nothing
		return this;
	}

}
