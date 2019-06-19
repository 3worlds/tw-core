package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.timer.ClockTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.EventTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.ScenarioTimer;
import au.edu.anu.twcore.ecosystem.runtime.timer.TimeUtil;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel" node label in the 3Worlds configuration tree.
 *
 * NB grain has been removed (now equals 1)
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class TimeModel
		extends InitialisableNode
		implements Singleton<Timer>, Sealable, Resettable {

	private boolean sealed = false;

	/** the reference time scale, normally belonging to the TimerModelSimulator */
	protected TimeLine timeLine;

	private TimeUnits timeUnit;

	private int nTimeUnits;

	protected boolean isExact = false;

	/** if isExact is false grainsPerBaseUnit will be zero */
	protected long grainsPerBaseUnit = 0L;

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
		sealed = false;
		timeLine = (TimeLine) getParent();
		timeUnit = (TimeUnits) properties().getPropertyValue("timeUnit");
		nTimeUnits = (Integer) properties().getPropertyValue("nTimeUnits");
		long unitConversionFactor = TimeUtil.timeUnitExactConversionFactor(timeUnit, timeLine.shortestTimeUnit());
		isExact = unitConversionFactor > 0L;
		if (timeUnit.equals(TimeUnits.UNSPECIFIED))
			grainsPerBaseUnit = nTimeUnits;
		else
			grainsPerBaseUnit = nTimeUnits * unitConversionFactor;
		// Clock timer
		if (properties().getPropertyValue("subclass")
				.equals("au.edu.anu.twcore.ecosystem.runtime.timer.ClockTimer")) {
			timer = new ClockTimer(this);
		}
		// event-driven timer
		else if (properties().getPropertyValue("subclass")
				.equals("au.edu.anu.twcore.ecosystem.runtime.timer.EventTimer")) {
			EventQueue eq = (EventQueue) get(this.getChildren(),
				selectOne(hasTheLabel(N_EVENTQUEUE.label())));
			timer = new EventTimer(eq,this);
		}
		// scenario timer
		else if (properties().getPropertyValue("subclass")
				.equals("au.edu.anu.twcore.ecosystem.runtime.timer.ScenarioTimer")) {
			timer = new ScenarioTimer(this);
		}
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_TIMEMODEL.initRank();
	}

	@Override
	public Timer getInstance() {
		if (sealed)
			return timer;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public int nTimeUnits() {
		if (sealed)
			return nTimeUnits;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public TimeUnits timeUnit() {
		if (sealed)
			return timeUnit;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public TimeLine timeLine() {
		if (sealed)
			return timeLine;
		throw new TwcoreException("attempt to access uninitialised data");
	}

	public boolean isExact() {
		if (sealed)
			return isExact;
		throw new TwcoreException("attempt to access uninitialised data");
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
	public void reset() {
		if (sealed)
			timer.reset();
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	/**
	 * Utility to convert user time of this time unit to base time in timeLine time
	 * grains.
	 * 
	 * Watch out for this now: if you want the value of a time segment between t1
	 * and t2 you need to call this function for each t1 and t2 and take the
	 * difference to get the time line segment value.
	 * 
	 * 
	 * 
	 * @param modelTime
	 *            the user time to convert from
	 * @return internal time (= number of timegrains)
	 */
	public final long modelToBaseTime(double modelTime) {
		if (isExact)
			return Math.round(modelTime * grainsPerBaseUnit);
		else {
			double result = TimeUtil.convertTime(modelTime, timeUnit, timeLine.shortestTimeUnit(),
					timeLine.getTimeOrigin());
			result = result * nTimeUnits;
			return Math.round(result);
		}
	}

}
