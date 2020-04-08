package fr.cnrs.iees.twcore.generators.process;

/**
 * Groups of arguments for use in TwFunction descendants
 *
 * @author gignoux
 *
 */
public enum ArgumentGroups {
	// generic arguments: time and space
	t				("double"),
	dt				("double"),
	limits			("Box"),
	// group information
	ecosystemPar	("TwData"),
	ecosystemPop	("ComponentContainer"),
	lifeCyclePar	("TwData"),
	lifeCyclePop	("ComponentContainer"),
	groupPar		("TwData"),
	groupPop		("ComponentContainer"),
	otherGroupPar	("TwData"),
	otherGroupPop	("ComponentContainer"),
	// focal system component data
	focalAuto		("SystemData"),
	focalLtc		("TwData"),
	focalDrv		("TwData"),
	focalDec		("TwData"),
	focalLoc		("Point"),
	// other system component data
	otherAuto		("SystemData"),
	otherLtc		("TwData"),
	otherDrv		("TwData"),
	otherDec		("TwData"),
	otherLoc		("Point");

	private final String type;

	private ArgumentGroups(String type) {
		this.type = type;
	}

	public String type() {
		return type;
	}

}
