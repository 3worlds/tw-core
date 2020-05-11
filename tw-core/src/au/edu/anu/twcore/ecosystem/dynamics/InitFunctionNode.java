package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

/**
 * A class for TwFunctions attached to ComponentTypes, ie SetnitialState functions.
 *
 * @author J. Gignoux - 11 mai 2020
 *
 */
public class InitFunctionNode extends FunctionNode implements LimitedEdition<TwFunction>, Sealable {

	public InitFunctionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public InitFunctionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	protected TwFunction makeFunction(int index) {
		TwFunction result = null;
		try {
			result = fConstructor.newInstance();
			// attach a random number generator
			if (rngNode==null)
				result.defaultRng(index);
			else
				result.setRng(rngNode.getInstance(index));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
