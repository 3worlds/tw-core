package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The thing which makes SystemComponents at runtime
 * replacement for SystemFactory
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ComponentFactory extends ElementFactory<SystemComponent> {

	public ComponentFactory(Set<Category> categories, /*String categoryId,*/
			TwData auto, TwData drv,
			TwData dec, TwData ltc, SetInitialStateFunction setinit) {
		super(categories, /*categoryId,*/ auto, drv, dec, ltc, setinit);
	}

	@Override
	public SystemComponent newInstance() {
		SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
			driverTemplate,
			decoratorTemplate,
			lifetimeConstantTemplate,
			2,
			propertyMap);
		SystemComponent result = (SystemComponent)
			SCfactory.makeNode(SystemComponent.class,"C0",props);
		result.setCategorized(this);
		// call setInitialState !
		//		initialiser().setInitialState(t, dt, limits, ecosystemPar, ecosystemPop, lifeCyclePar, lifeCyclePop, groupPar, groupPop, focalLtc, focalDrv, focalLoc);
		return result;
	}



}
