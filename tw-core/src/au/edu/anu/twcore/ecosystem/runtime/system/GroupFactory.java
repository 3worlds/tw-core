package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class GroupFactory extends ElementFactory<GroupComponent> {

	private String name = null;
	private ComponentContainer parent = null;

	public GroupFactory(Set<Category> categories, String categoryId, TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit, String name, ComponentContainer parent) {
		super(categories, categoryId, auto, drv, dec, ltc, setinit);
		this.name = name;
		this.parent = parent;
	}

	@Override
	public GroupComponent newInstance() {
		GroupComponent group = null;
		ComponentContainer container = new ComponentContainer(name,parent,null);
		autoVarTemplate = new ContainerData(container);
		SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
		driverTemplate,decoratorTemplate,lifetimeConstantTemplate,2,propertyMap);
		group = (GroupComponent) SCfactory.makeNode(GroupComponent.class,name,props);
		group.setCategorized(this);
		container.setData(group);
		group.setContent(container);
		return group;
	}

}
