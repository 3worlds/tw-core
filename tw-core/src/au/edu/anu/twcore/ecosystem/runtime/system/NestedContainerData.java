package au.edu.anu.twcore.ecosystem.runtime.system;

import static fr.cnrs.iees.twcore.constants.PopulationVariables.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.ecosystem.runtime.containers.NestedContainer;
import au.edu.anu.twcore.ecosystem.runtime.containers.SimpleContainer;

/**
 * Automatic variable for nested containers
 *
 * @author J. Gignoux - 16 avr. 2020
 *
 */
@Deprecated// this class is actually not needed
public class NestedContainerData extends ContainerData {

	private int totalCount = 0;
	private int totalAdded = 0;
	private int totalRemoved = 0;
	private NestedContainer<?> container = null;

	private static String[] propsArray2 = {COUNT.shortName(),NADDED.shortName(),NREMOVED.shortName(),
		TCOUNT.shortName(),TNADDED.shortName(),TNREMOVED.shortName()};
	private static Set<String> props2 = new HashSet<String>(Arrays.asList(propsArray2));

	/** generic constructor */
	public NestedContainerData(Collection<?> items,
			Collection<?> itemsAdded,
			Collection<?> itemsRemoved,
			NestedContainer<?> container) {
		super(items, itemsAdded, itemsRemoved);
		this.container = container;
	}

	/** constructor for ComponentContainer*/
	public NestedContainerData(CategorizedContainer<?> container) {
		super(container);
		this.container = container;
	}

	// Note: little trick here: both arguments are actually the same object.
	private void addAll(NestedContainer<?> cont,
		SimpleContainer<?> pop) {
//		totalCount += pop.populationData().count();
//		totalAdded += pop.populationData().nAdded();
//		totalRemoved += pop.populationData().nRemoved();
//		for (SimpleContainer<?> subc : cont.subContainers())
//			if (subc instanceof NestedContainer<?>)
//				addAll((NestedContainer<?>) subc,subc);
	}

	// this must be called at te end of every time step, BEFORE new cp are added and old ones deleted
	@Override
	public void resetCounters() {
		totalCount = 0;
		totalAdded = 0;
		totalRemoved = 0;
		if (container instanceof SimpleContainer<?>) // otherwise there will be nothing to count
			addAll(container,(SimpleContainer<?>)container);
	}

	/** counts the total number of items, including those of subContainers */
	public int totalCount() {
		return totalCount;
	}

	/** counts the total number of added items, including those of subContainers */
	public int totalAdded() {
		return totalAdded;
	}

	/** counts the total number of added items, including those of subContainers */
	public int totalRemoved() {
		return totalRemoved;
	}
	@Override
	public Object getPropertyValue(String key) {
		Object result = super.getPropertyValue(key);
		if (result==null) {
			if (key.equals(TCOUNT.shortName()))
				return count();
			else if (key.equals(TNADDED.shortName()))
				return nAdded();
			else if (key.equals(TNREMOVED.shortName()))
				return nRemoved();
			return null;
		}
		return result;
	}

	@Override
	public Set<String> getKeysAsSet() {
		return props2;
	}

	@Override
	public String[] getKeysAsArray() {
		return propsArray2;
	}

	@Override
	public ContainerData clone() {
		return new NestedContainerData(items,itemsAdded,itemsRemoved,container);
	}


}
