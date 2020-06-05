package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * A class for TwFunctions attached to ComponentTypes, ie SetnitialState functions.
 *
 * @author J. Gignoux - 11 mai 2020
 *
 */
public class InitFunctionNode extends FunctionNode implements LimitedEdition<TwFunction>, Sealable {

	// the TimeLine shortest time unit - needed in case this function is used to initialise an EventTimer
	private TimeUnits btu = null;

	public InitFunctionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public InitFunctionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		TreeNode root = this;
		while (root.getParent()!=null)
			root = root.getParent();
		TreeGraphDataNode timeLine = (TreeGraphDataNode) get(root.getChildren(),
			selectOne(hasTheLabel(N_SYSTEM.label())),
			children(),
			selectOne(hasTheLabel(N_DYNAMICS.label())),
			children(),
			selectOne(hasTheLabel(N_TIMELINE.label())));
		btu = (TimeUnits) timeLine.properties().getPropertyValue(P_TIMELINE_SHORTTU.key());
	}

	protected TwFunction makeFunction(int index) {
		TwFunction result = super.makeFunction(index);
		if (result instanceof SetInitialStateFunction)
			((SetInitialStateFunction) result).setTimeUnit(btu);
		return result;
	}

}
