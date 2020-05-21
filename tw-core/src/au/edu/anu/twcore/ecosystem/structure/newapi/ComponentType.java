package au.edu.anu.twcore.ecosystem.structure.newapi;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_COMPONENTTYPE;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * replacement for ComponentType
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ComponentType extends ElementType<ComponentFactory, SystemComponent> {

	public ComponentType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public ComponentType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	protected ComponentFactory makeTemplate(int id) {
		if (setinit!=null)
			return new ComponentFactory(categories,/*categoryId(),*/
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id));
		else
			return new ComponentFactory(categories,/*categoryId(),*/
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,null);
	}

	@Override
	public int initRank() {
		return N_COMPONENTTYPE.initRank();
	}

}
