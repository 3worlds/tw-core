package au.edu.anu.twcore.ecosystem.runtime.containers;

import static fr.cnrs.iees.twcore.constants.PopulationVariables.*;

import java.util.HashSet;
import java.util.Set;

import au.edu.anu.rscs.aot.graph.property.PropertyKeys;
import au.edu.anu.twcore.ecosystem.runtime.Population;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

public abstract class AbstractPopulationContainer<T extends Identity>
		implements SimpleContainer<T>, Population {

	// class-level constants
	protected static Set<String> props = new HashSet<String>();
	protected static PropertyKeys propsPK;
	static {
		props.add(COUNT.shortName());
		props.add(NADDED.shortName());
		props.add(NREMOVED.shortName());
		propsPK = new PropertyKeys(props);
	}

	// Population data
	protected class popData implements ReadOnlyPropertyList {
		public int count = 0;
		public int nAdded = 0;
		public int nRemoved = 0;

		@Override
		public Object getPropertyValue(String key) {
			if (key.equals(COUNT.shortName()))
				return count;
			else if (key.equals(NADDED.shortName()))
				return nAdded;
			else if (key.equals(NREMOVED.shortName()))
				return nRemoved;
			return null;
		}

		@Override
		public boolean hasProperty(String key) {
			if (key.equals(COUNT.shortName()) || key.equals(NADDED.shortName()) || key.equals(NREMOVED.shortName()))
				return true;
			return false;
		}

		@Override
		public Set<String> getKeysAsSet() {
			return props;
		}

		@Override
		public int size() {
			return props.size();
		}

		@Override
		public ReadOnlyPropertyList clone() {
			SimplePropertyList pl = new SharedPropertyListImpl(propsPK);
			pl.setProperties(this);
			return pl;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(1024);
			boolean first = true;
			for (String key : props)
				if (first) {
					sb.append(key).append("=").append(getPropertyValue(key));
					first = false;
				} else
					sb.append(' ').append(key).append("=").append(getPropertyValue(key));
			return sb.toString();
		}
	}

	protected popData populationData = new popData();


	public AbstractPopulationContainer() {
		super();
	}

	@Override
	public final int count() {
		return populationData.count;
	}

	@Override
	public final int nAdded() {
		return populationData.nAdded;
	}

	@Override
	public final int nRemoved() {
		return populationData.nRemoved;
	}

	@Override
	public void resetCounters() {
		populationData.nAdded = 0;
		populationData.nRemoved = 0;
	}

}
