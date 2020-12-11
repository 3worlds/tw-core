package au.edu.anu.twcore.ecosystem.dynamics.initial;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_LIFECYCLE;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycle
		extends InitialisableNode
		implements Sealable, LimitedEdition<LifeCycleComponent> {

	private boolean sealed = false;
	private LifeCycleType lifeCycleType = null;
	private Map<Integer,LifeCycleComponent> lifeCycles = new HashMap<>();

	public LifeCycle(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		lifeCycleType = (LifeCycleType) getParent();
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_LIFECYCLE.initRank();
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	@Override
	public LifeCycleComponent getInstance(int id) {
		if (!sealed)
			initialise();
		if (!lifeCycles.containsKey(id)) {
			lifeCycleType.getInstance(id).setName(id());
			LifeCycleComponent lcc = lifeCycleType.getInstance(id).newInstance();
			for (TreeNode tn:getChildren())
				if (tn instanceof VariableValues)
					((VariableValues)tn).fill(lcc.currentState());
				else if (tn instanceof ConstantValues)
					((ConstantValues) tn).fill(lcc.constants());
			lifeCycles.put(id,lcc);
		}
		return lifeCycles.get(id);
	}

}
