package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.timer.ClockTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.TimeUtil;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel" node label in the 3Worlds configuration tree.
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class TimeModel extends InitialisableNode implements Singleton<Timer> {

	/** the reference time scale, normally belonging to the TimerModelSimulator */
	protected TimeLine timeLine;
	
	private TimeUnits timeUnit;

	private int nTimeUnits;

	protected boolean isExact;
	
	/** if isExact is false grainsPerBaseUnit will be zero */
	protected long grainsPerBaseUnit;
	
	private Timer timer = null;


	public TimeModel(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public TimeModel(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		timeLine = (TimeLine) getParent();
		timeUnit = (TimeUnits) properties().getPropertyValue("timeUnit");
		nTimeUnits = (Integer) properties().getPropertyValue("nTimeUnits");
		long unitConversionFactor = TimeUtil.timeUnitExactConversionFactor(timeUnit, timeLine.shortestTimeUnit());
		isExact = unitConversionFactor > 0L;
		if (timeUnit.equals(TimeUnits.UNSPECIFIED))
			grainsPerBaseUnit = nTimeUnits;
		else
			grainsPerBaseUnit = nTimeUnits * unitConversionFactor;
		if (properties().getPropertyValue("subclass").equals("au.edu.anu.twcore.ecosystem.runtime.timer.ClockTimer")) {
			timer = new ClockTimer(this);
		}
	}

	@Override
	public int initRank() {
		return N_TIMEMODEL.initRank();
	}

	@Override
	public Timer getInstance() {
		return timer;
	}
	
}
