package au.edu.anu.twcore.ecosystem.dynamics.initial;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.experiment.DataSource;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_LOADFROM;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import java.util.HashMap;
import java.util.List;
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
	// the data read from file for this lifeCycle
	private SimplePropertyList loadedData = null;

	public LifeCycle(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		lifeCycleType = (LifeCycleType) getParent();
		// load data from files
		Map<DataIdentifier, SimplePropertyList> loaded = new HashMap<>();
		List<DataSource> sources = (List<DataSource>) get(edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
			edgeListEndNodes());
		for (DataSource source:sources)
			source.getInstance().load(loaded);
		// sort out which loaded data match this group.
		// there should be only one normally
		for (DataIdentifier dif:loaded.keySet())
			if (dif.lifeCycleId().equals(this.id())) {
				loadedData = loaded.get(dif);
				break;
		}
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
			// fill lifeCycle with initial values from the configuration file
			for (TreeNode tn:getChildren()) {
				// TODO: restore this
				
//				if (tn instanceof InitialValues)
//					props.addProperties(((InitialValues)tn).readOnlyProperties());
			}
//			for (TreeNode tn:getChildren())
//				if (tn instanceof VariableValues)
//					((VariableValues)tn).fill(lcc.currentState());
//				else if (tn instanceof InitialValues)
//					((InitialValues) tn).fill(lcc.constants());
			// fill group with initial values read from file - overtake the previous
			if (loadedData!=null)
				for (String pkey:lcc.properties().getKeysAsSet())
					if (loadedData.hasProperty(pkey))
						lcc.properties().setProperty(pkey,loadedData.getPropertyValue(pkey));
			lifeCycles.put(id,lcc);
		}
		return lifeCycles.get(id);
	}

}
