package au.edu.anu.twcore.ecosystem.runtime.biology;

import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 *
 * @author J. Gignoux - 8 avr. 2020
 *
 */
public abstract class SetOtherInitialStateFunction extends TwFunctionAdapter {

	/**
	 * sets the initial state of a newly created SystemComponent (<em>other</em>) given a
	 * parent component (<em>focal</em>). Notice that some parameters may be null when
	 * calling the method (as denoted by 'if any').
	 *
	 * @param t	current time
	 * @param dt current time step
	 * @param limits boundary of the space set in the enclosing Process, if any
	 * @param ecosystemPar ecosystem parameters, if any
	 * @param ecosystemPop ecosystem population data
	 * @param lifeCyclePar life cycle parameters, if any
	 * @param lifeCyclePop life cycle population data, if any
	 * @param groupPar focal group parameters, if any
	 * @param groupPop focal group population data
	 * @param otherGroupPar other group parameters,if any
	 * @param otherGroupPop other group population data
	 * @param focalAuto focal automatic variables (age and birthDate)
	 * @param focalLtc focal lifetime constants, if any
	 * @param focalDrv focal driver variables at current time, if any
	 * @param focalDec focal decorator variables, if any
	 * @param focalLoc focal location at current time, if any
	 *
	 * @param otherLtc other lifetime constants, if any
	 * @param otherDrv other driver variables at current time, if any
	 * @param otherLoc other location at current time, if any
	 */
//	public abstract void setOtherInitialState(
//			double t,
//			double dt,
//			Box limits,
//			TwData ecosystemPar,
//			ComponentContainer ecosystemPop,
//			TwData lifeCyclePar,
//			ComponentContainer lifeCyclePop,
//			TwData groupPar,
//			ComponentContainer groupPop,
//			TwData otherGroupPar,
//			ComponentContainer otherGroupPop,
//			ComponentData focalAuto,
//			TwData focalLtc,
//			TwData focalDrv,
//			TwData focalDec,
//			Point focalLoc,
//			// read-write parameters
//			TwData otherLtc,
//			TwData otherDrv,
//			double[] otherLoc
//	);

	public SetOtherInitialStateFunction() {
		super();
		fType = TwFunctionTypes.SetOtherInitialState;
	}

	/**
	 *
	 * @param t
	 * @param dt
	 * @param arena
	 * @param lifeCycle
	 * @param group
	 * @param space
	 * @param focal
	 * @param otherLifeCycle
	 * @param otherGroup
	 * @param other
	 * @param nextFocalLoc
	 * @param nextOtherLoc
	 */
	public abstract void setOtherInitialState(
			double t,
			double dt,
			CategorizedComponent<ComponentContainer> arena,
			CategorizedComponent<ComponentContainer> lifeCycle,
			CategorizedComponent<ComponentContainer> group,
			CategorizedComponent<ComponentContainer> space,
			CategorizedComponent<ComponentContainer> focal,
			CategorizedComponent<ComponentContainer> otherLifeCycle,
			CategorizedComponent<ComponentContainer> otherGroup,
			CategorizedComponent<ComponentContainer> other,
			double[] nextFocalLoc,
			double[] nextOtherLoc
	);


}
