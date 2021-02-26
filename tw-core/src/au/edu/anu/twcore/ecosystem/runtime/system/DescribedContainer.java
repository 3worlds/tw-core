package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.Described;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 *
 * @author gignoux 28 sept. 2020
 *
 * @param <T>
 * @param <U>
 */
public class DescribedContainer<T extends CategorizedComponent>
		extends CategorizedContainer<T>
		implements Described<HierarchicalComponent> {

	// descriptors for this container
	private HierarchicalComponent avatar = null;

	public DescribedContainer(String proposedId,
			CategorizedContainer<T> parent,
			HierarchicalComponent data,
			int simulatorId) {
		super(proposedId, parent,simulatorId);
		avatar = data;
	}

	/**
	 * Returns the set of categories ({@linkplain Category}) associated to this
	 * container. If this container has variables and parameters, they are specified
	 * by these categories.
	 *
	 * @return the object holding all the category information
	 */
	public Categorized<?> containerCategorized() {
		return avatar.membership();
	}

	/**
	 * Returns the parameter set associated to this container. It is specified by
	 * the categories associated to the container, accessible through the
	 * {@code categoryInfo()} method.
	 *
	 * @return the parameter set - may be {@code null}
	 */
	public TwData parameters() {
		if (avatar!=null)
			return avatar.constants();
		return null;
	}

	public void resetCounters() {
		if (avatar.autoVar() instanceof ContainerData)
			((ContainerData)avatar.autoVar()).resetCounters();
	}

	@Override
	protected T cloneItem(T item) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setData(HierarchicalComponent data) {
		if (avatar==null)
			avatar = data;
	}

	@Override
	public HierarchicalComponent descriptors() {
		return avatar;
	}
	
	/**
	 * Use with caution - 
	 * @param item
	 */
	public void removeItemNow(T item) {
		throw new TwcoreException("This method should never be called");
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		sb.append("container:");
		sb.append(id().toString());
		sb.append('[');
		if (containerCategorized().categories() != null) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append("categories:");
			sb.append(containerCategorized().categories().toString());
		}
		if (avatar.constants() != null) {
			if (first)
				first = false;
			else
				sb.append(' ');
			sb.append("constants:(");
			sb.append(avatar.constants().toString());
			sb.append(')');
		}
		if (first)
			first = false;
		else
			sb.append(' ');
		sb.append("variables:(");
		if (avatar.autoVar() != null)
			sb.append(avatar.autoVar().toString());
		sb.append(')');
		// TODO: adapt this !
		if (!initialItems.isEmpty()) {
			sb.append(" initial_items:");
			sb.append(initialItems.toString());
		}
		if (!items.isEmpty()) {
			sb.append(" local_items:");
			sb.append(items.toString());
		}
		if (superContainer != null) {
			sb.append(" super_container:");
			sb.append(superContainer.id());
		}
		if (!subContainers.isEmpty()) {
			sb.append(" sub_containers:");
			sb.append(subContainers.keySet().toString());
		}
		sb.append(']');
		return sb.toString();
	}

}
