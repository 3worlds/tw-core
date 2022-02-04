package au.edu.anu.twcore.ecosystem.dynamics.initial;

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleFactory;
import au.edu.anu.twcore.ecosystem.structure.LifeCycleType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static au.edu.anu.rscs.aot.queries.CoreQueries.isClass;
import static au.edu.anu.rscs.aot.queries.CoreQueries.parent;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
@Deprecated
public class LifeCycle
		extends InitialElement<LifeCycleComponent>
		implements DefaultStrings {

	private LifeCycleType lifeCycleType = null;
//	private Map<Integer,LifeCycleComponent> lifeCycles = new HashMap<>();
//	// the data read from file for this lifeCycle
//	private SimplePropertyList loadedData = null;

	public LifeCycle(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		lifeCycleType = (LifeCycleType) getParent();
//		// load data from files
//		Map<DataIdentifier, SimplePropertyList> loaded = new HashMap<>();
//		List<DataSource> sources = (List<DataSource>) get(edges(Direction.OUT),
//			selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
//			edgeListEndNodes());
//		for (DataSource source:sources)
//			source.getInstance().load(loaded);
//		// sort out which loaded data match this group.
//		// there should be only one normally
//		for (DataIdentifier dif:loaded.keySet())
//			if (dif.lifeCycleId().equals(this.id())) {
//				loadedData = loaded.get(dif);
//				break;
//		}
		seal();
	}

	@Override
	public int initRank() {
		throw new TwcoreException("obsolete code");
//		return N_LIFECYCLE.initRank();
	}

//	@Override
//	public LifeCycleComponent getInstance(int id) {
//		if (!sealed)
//			initialise();
//		if (!lifeCycles.containsKey(id)) {
//			lifeCycleType.getInstance(id).setName(id());
//			LifeCycleComponent lcc = lifeCycleType.getInstance(id).newInstance();
//			// fill lifeCycle with initial values from the configuration file
//			for (TreeNode tn:getChildren()) {
//				// TODO: restore this
//				
////				if (tn instanceof InitialValues)
////					props.addProperties(((InitialValues)tn).readOnlyProperties());
//			}
////			for (TreeNode tn:getChildren())
////				if (tn instanceof VariableValues)
////					((VariableValues)tn).fill(lcc.currentState());
////				else if (tn instanceof InitialValues)
////					((InitialValues) tn).fill(lcc.constants());
//			// fill group with initial values read from file - overtake the previous
//			if (loadedData!=null)
//				for (String pkey:lcc.properties().getKeysAsSet())
//					if (loadedData.hasProperty(pkey))
//						lcc.properties().setProperty(pkey,loadedData.getPropertyValue(pkey));
//			lifeCycles.put(id,lcc);
//		}
//		return lifeCycles.get(id);
//	}


	@Override
	protected LifeCycleComponent makeInitialComponent(int simId, 
			DataIdentifier itemId, 
			SimplePropertyList props) {
		LifeCycleFactory lcf = lifeCycleType.getInstance(simId);
		ArenaType parent = (ArenaType) get(this,parent(isClass(ArenaType.class)));
		ComponentContainer superContainer = (ComponentContainer)parent.getInstance(simId).getInstance().content();
		if (itemId!=null)
			lcf.setName(itemId.lifeCycleId());
		else
			lcf.setName(null);
		LifeCycleComponent lcc = lcf.newInstance(superContainer);
		lcc.connectParent(parent);
		for (String pkey:lcc.properties().getKeysAsSet())
			if (props.hasProperty(pkey))
				lcc.properties().setProperty(pkey,props.getPropertyValue(pkey));
		return lcc;
	}

	@Override
	protected DataIdentifier fullId() {
		String groupId = "";
		String componentId = "";
		String LCId = id();		
		return new DataIdentifier(LCId,groupId,componentId);
	}

}
