package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleComponent extends HierarchicalComponent {

	// the list of groups linked by this life cycle.
	// key is a category signature
	private Map<String,GroupComponent> groups = new HashMap<>();

	public LifeCycleComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public LifeCycleFactory elementFactory() {
		return (LifeCycleFactory) membership();
	}

	public String name() {
		return content().id();
	}

	// to be called only once just after construction
	// The reverse mapping of groups by categories is only possible because all groups of a
	// life cycle MUST be of different categories
	protected void addGroup(GroupComponent group) {
		// this looks incredeby slow - at least in the debugger
		SetView<Category> set = Sets.intersection(elementFactory().stageCategories(),
			group.content().itemCategorized().categories());
		SortedSet<Category> sset = new TreeSet<>();
		sset.addAll(set);
		String s = Categorized.signature(sset);
		groups.put(s,group);
	}


	public GroupComponent produceGroup (CreateOtherDecisionFunction function) {
//		System.out.println(groups.toString());
		String toCat = elementFactory().toCategories(function);
		return groups.get(toCat);
	}

	public GroupComponent recruitGroup(String toCat) {
		return groups.get(toCat);
	}


}
