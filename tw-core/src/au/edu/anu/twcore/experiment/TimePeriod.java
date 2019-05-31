package au.edu.anu.twcore.experiment;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Initialisable;
import fr.ens.biologie.generic.Resettable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * Class matching the "experiment/timePeriod" node label in the 3Worlds configuration tree.
 * Has the "start" and "end" properties.
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public class TimePeriod extends TreeGraphDataNode implements Initialisable, Resettable {

	private long start;
	private long end;
	// TODO: 
//	private StoppingCondition stopOn = null;
	
	public TimePeriod(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		reset();
	}
	
	public TimePeriod(Identity id,GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
		reset();
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_TIMEPERIOD.initRank();
	}

	public long start() {
		return start;
	}
	
	public long end() {
		return end;
	}

	// call this every time properties are edited
	@Override
	public void reset() {
		if (properties().hasProperty(P_TIMEPERIOD_START.key()))
			start = (long) properties().getPropertyValue(P_TIMEPERIOD_START.key());
		else
			start = 0L;
		if (properties().hasProperty(P_TIMEPERIOD_END.key()))
			end = (long) properties().getPropertyValue(P_TIMEPERIOD_END.key());
		else
			end = Long.MAX_VALUE;
	}
}
