package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The class building the op container, ie the system arena. This is a singleton in any simulation.
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaFactory extends ElementFactory<ArenaComponent> {

	private ArenaComponent arena = null;
	private boolean makeContainer = true;
	private String name = null;

	public ArenaFactory(Set<Category> categories, /*String categoryId,*/ TwData auto, TwData drv, TwData dec,
			TwData ltc, SetInitialStateFunction setinit,boolean makeContainer,String name) {
		super(categories, /*categoryId,*/ auto, drv, dec, ltc, setinit);
		this.makeContainer = makeContainer;
		this.name = name;
	}

	@Override
	public ArenaComponent getInstance() {
		if (arena==null) {
			ComponentContainer community = null;
			if (makeContainer) {
				community = new ComponentContainer(name,null,null);
				autoVarTemplate = new ContainerData(community);
			}
			SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
				driverTemplate,decoratorTemplate,lifetimeConstantTemplate,2,propertyMap);
			arena = (ArenaComponent) SCfactory.makeNode(ArenaComponent.class,name,props);
			arena.setCategorized(this);
			if (makeContainer) {
				community.setData(arena);
				arena.setContent(community);
			}
		}
		return arena;
	}

}
