package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.utils.Logging;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleFactory extends ElementFactory<LifeCycleComponent> {

	private static Logger log = Logging.getLogger(LifeCycleFactory.class);

	private String lifeCycleName = null;
	private ComponentContainer parent = null;
	private String lifeCycleTypeName = null;

	public LifeCycleFactory(Set<Category> categories,
			TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit,
			String name, ComponentContainer parent) {
		super(categories, auto, drv, dec, ltc, setinit, true);
		this.parent = parent;
		lifeCycleTypeName = name;
	}

	/**
	 * This MUST be called before newInstance() in order for the correct name to be used.
	 * Otherwise the LifeCycleType name is used to generate a group
	 * @param name
	 */
	public void setName(String name) {
		lifeCycleName = name;
	}

	@Override
	public LifeCycleComponent newInstance() {
		LifeCycleComponent lifeCycle = null;
		ComponentContainer container = null;
		if (lifeCycleName!=null) {
			if (!ComponentContainer.containerScope.contains(lifeCycleName))
				container = new ComponentContainer(lifeCycleName,parent,null);
			else { // groupName already in use
				container = new ComponentContainer(lifeCycleTypeName,parent,null);
				String s = container.id();
				log.warning(()->"LifeCycle container couldnt be created with name '"+lifeCycleName
					+"' - name '" + s + "' used instead.");
			}
		}
		else
			container = new ComponentContainer(lifeCycleTypeName,parent,null);
		autoVarTemplate = new ContainerData(container);
		SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
		driverTemplate,decoratorTemplate,lifetimeConstantTemplate,2,propertyMap);
		lifeCycle = (LifeCycleComponent) SCfactory.makeNode(LifeCycleComponent.class,container.id(),props);
		lifeCycle.setCategorized(this);
		container.setData(lifeCycle);
		lifeCycle.setContent(container);
		return lifeCycle;
	}

}
