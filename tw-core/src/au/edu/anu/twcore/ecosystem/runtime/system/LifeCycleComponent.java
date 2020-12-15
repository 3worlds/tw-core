package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.HashMap;
import java.util.Map;
import au.edu.anu.twcore.ecosystem.runtime.biology.ChangeCategoryDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
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
//	public void setGroups(List<GroupComponent> grps) {
//		if (groups==null) {
//			groups = new HashMap<>();
//			for (GroupComponent gc:grps)
//				groups.put(gc.content().itemCategorized().categoryId(),gc);
//		}
//	}
	protected void addGroup(GroupComponent group) {
		groups.put(group.content().itemCategorized().categoryId(),group);
	}


	public GroupComponent produceGroup (CreateOtherDecisionFunction function) {
		String toCat = elementFactory().toCategories(function);
		return groups.get(toCat);
//		if (parent.membership().belongsTo(elementFactory().fromCategories(function))) { // or the reverse?
//			Set<Category> toCat = elementFactory().toCategories(function);
//			for (CategorizedContainer<SystemComponent> groupCont:content().subContainers())
//				if (groupCont.itemCategorized()!=null)
//					if (groupCont.itemCategorized().belongsTo(toCat))
//						return new Duple<ComponentContainer,ComponentFactory>(
//							(ComponentContainer) groupCont,
//							(ComponentFactory) groupCont.itemCategorized() );
//		}
//		return null;
	}

	public GroupComponent recruitGroup(ChangeCategoryDecisionFunction function) {
		String toCat = elementFactory().toCategories(function);
		return groups.get(toCat);
	}


}
