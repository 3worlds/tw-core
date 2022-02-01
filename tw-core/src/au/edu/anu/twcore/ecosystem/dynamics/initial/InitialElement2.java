package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_COMPONENT_NINST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.system.DataElement;
import au.edu.anu.twcore.experiment.runtime.DataIdentifier;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;


/**
 * An ancestor class to initial value nodes, i.e. Component, Group, LifeCycle.
 * Manages loading from dataSources
 * 
 * @author Jacques Gignoux - 14 déc. 2021
 *
 * @param <T>
 */
@Deprecated // all this to be moved to ElementType descendants
public abstract class InitialElement2<T extends DataElement> 
		extends InitialisableNode 
		implements Sealable, LimitedEdition<List<T>> {

	private boolean sealed = false;
	// the list of initial items created (and stored) with this node
	private Map<Integer,List<T>> individuals = new HashMap<>();
	// the table of all data read from data sources
	private Map<DataIdentifier, SimplePropertyList> loadedData = new HashMap<>();
	// number of instances to instantiate
	private int nInstances = 1;
	// if data came from dataSources
	private boolean fromFiles = false;

	// default constructor
	public InitialElement2(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public InitialElement2(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	// InitialisableNode
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// read data from data sources
		InitialDataLoading.loadFromDataSources(this,loadedData);
//		List<DataSource> sources = (List<DataSource>) get(edges(Direction.OUT),
//			selectZeroOrMany(hasTheLabel(E_LOADFROM.label())),
//			edgeListEndNodes());
//		for (DataSource source:sources)
//			source.getInstance().load(loadedData);
		// read data direct from config tree - will NOT be read if data sources are used
		
//		if (sources.isEmpty()) {
//			DataIdentifier id = fullId();
//			ExtendablePropertyList props = new ExtendablePropertyListImpl();
		// NB This will have to be refactored
			for (TreeNode tn:getChildren()) {
				if (tn instanceof InitialValues)
					InitialDataLoading.loadFromConfigTree((InitialValues)tn,loadedData);
//					props.addProperties(((InitialValues)tn).readOnlyProperties());
			}
//			loadedData.put(id,props);
			fromFiles = false;
//		}
//		else
//			fromFiles = true;
		if (properties().hasProperty(P_COMPONENT_NINST.key()))
			nInstances = (int) properties().getPropertyValue(P_COMPONENT_NINST.key());
		if (nInstances==0)
			nInstances=1;
	}
	
	// LimitedEdition
	
	@Override
	public final List<T> getInstance(int id) {
		if (!sealed)
			initialise();
		if (!individuals.containsKey(id)) {
			List<T> result = new ArrayList<>();
			if (fromFiles) 
				for (DataIdentifier dif: loadedData.keySet()) {
					SimplePropertyList iprops = loadedData.get(dif);
					T icomp = makeInitialComponent(id,dif,iprops);
					result.add(icomp);		
			}
			else {
				Iterator<SimplePropertyList> dataIt = loadedData.values().iterator();
				SimplePropertyList iprops = null;
				for (int i=0; i<nInstances; i++) {
					if (dataIt.hasNext())
						iprops = dataIt.next();
					else {
						dataIt = loadedData.values().iterator();
						iprops = dataIt.next();
					}
					T icomp = makeInitialComponent(id,fullId(),iprops);
					result.add(icomp);
				}
			}
			individuals.put(id,result);
		}
		return individuals.get(id);
	}

	// Sealable
	
	@Override
	public final Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public final boolean isSealed() {
		return sealed;
	}

	// local
	
	/**
	 * Make an instance of the component and populate it with data
	 * 
	 * @param simId simulator id
	 * @param itemId id of the component to make (e;g. group name)
	 * @param props properties loaded from file or config tree
	 * @return
	 */
	protected abstract T makeInitialComponent(int simId, 
		DataIdentifier itemId, 
		SimplePropertyList props);
	
	/**
	 * Work out fullId at init time, ie this is called from initialise()
	 * 
	 * @return
	 */
	protected abstract DataIdentifier fullId();
		
}
