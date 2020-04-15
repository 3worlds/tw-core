package fr.cnrs.iees.twcore.generators.process;

/**
 * Groups of arguments for use in TwFunction descendants
 *
 * @author gignoux
 *
 */
public enum ArgumentGroups {
	// read-only arguments
	// generic arguments: time and space
	t				("double", "current time"),
	dt				("double", "current time step"),
	limits			("fr.cnrs.iees.uit.space.Box", "space boundaries"),
	// group information
	ecosystemPar	("au.edu.anu.twcore.data.runtime.TwData", "ecosystem "),
	ecosystemPop	("au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer", "ecosystem "),
	lifeCyclePar	("au.edu.anu.twcore.data.runtime.TwData", "life cycle "),
	lifeCyclePop	("au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer", "life cycle "),
	// FLAW: what about OTHER life cycle ? it may exist and be different ?
	groupPar		("au.edu.anu.twcore.data.runtime.TwData", "focal component group "),
	groupPop		("au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer", "focal component group "),
	otherGroupPar	("au.edu.anu.twcore.data.runtime.TwData", "other component group "),
	otherGroupPop	("au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer", "other component group "),
	// focal system component data
	focalAuto		("au.edu.anu.twcore.ecosystem.runtime.system.SystemData", "focal component "),
	focalLtc		("au.edu.anu.twcore.data.runtime.TwData", "focal component "),
	focalDrv		("au.edu.anu.twcore.data.runtime.TwData", "focal component current "),
	focalDec		("au.edu.anu.twcore.data.runtime.TwData", "focal component "),
	focalLoc		("fr.cnrs.iees.uit.space.Point", "focal component location "),
	// other system component data
	otherAuto		("au.edu.anu.twcore.ecosystem.runtime.system.SystemData", "other component "),
	otherLtc		("au.edu.anu.twcore.data.runtime.TwData", "other component "),
	otherDrv		("au.edu.anu.twcore.data.runtime.TwData", "other component current "),
	otherDec		("au.edu.anu.twcore.data.runtime.TwData", "other component "),
	otherLoc		("fr.cnrs.iees.uit.space.Point", "other component location "),

	// writeable arguments
	nextFocalDrv	("au.edu.anu.twcore.data.runtime.TwData", "focal component next drivers"),
	nextFocalLoc	("double[]", "focal component new location"),
	nextOtherDrv	("au.edu.anu.twcore.data.runtime.TwData", "other component next drivers"),
	nextOtherLoc	("double[]", "other component new location"),

	// utilities
	random			("java.util.Random", "random number generator"),
	decider			("au.edu.anu.twcore.ecosystem.runtime.biology.DecisionFunction", "decision function")
	;
	private final String type;
	private final String description;

	private ArgumentGroups(String type, String description) {
		this.type = type;
		this.description = description;
	}

	public String type() {
		return type;
	}

	public String description() {
		return description;
	}
}
