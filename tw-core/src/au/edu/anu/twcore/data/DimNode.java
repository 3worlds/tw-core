package au.edu.anu.twcore.data;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "dimensioner" node label in the 3Worlds configuration tree.
 * Has the "size" property.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class DimNode 
		extends InitialisableNode 
		implements Singleton<Dimensioner> {

	private Dimensioner dimensioner = null;
	
	public DimNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DimNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}
	
	@Override
	public void initialise() {
		super.initialise();
		dimensioner = new Dimensioner((int)properties().getPropertyValue(P_DIMENSIONER_SIZE.key()));
	}

	@Override
	public int initRank() {
		return N_DIMENSIONER.initRank();
	}

	public String name() {
		return classId();
	}

	public int dim() {
		return (int)properties().getPropertyValue(P_DIMENSIONER_SIZE.key());
	}

	@Override
	public Dimensioner getInstance() {
		return dimensioner;
	}
}
