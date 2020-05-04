package fr.cnrs.iees.twcore.generators.process;

/**
 * Arguments used in TwFunction descendants
 *
 * @author J. Gignoux - 27 avr. 2020

 */
public enum TwFunctionArguments {

	_t				("double", "current time"),
	_dt				("double", "current time step"),
	// component Processes
	arena			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "whole system "),
	lifeCycle		("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "focal life cycle "),
	group			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "focal group "),
	space			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "space "),
	focal			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "focal component "),
	// relation Processes
	otherLifeCycle	("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "other life cycle "),
	otherGroup		("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "other group "),
	other			("au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent", "other component "),
	// writeable arguments
	_nextFocalLoc	("double[]", "focal component new location"),
	_nextOtherLoc	("double[]", "other component new location"),
	// utilities
	_random			("java.util.Random", "random number generator"),
	_decider		("au.edu.anu.twcore.ecosystem.runtime.biology.DecisionFunction", "decision function")
	;
	private final String type;
	private final String description;
	private TwFunctionArguments(String type, String description) {
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
