package fr.cnrs.iees.twcore.constants;

import au.edu.anu.twcore.data.SizedByEdge;
import au.edu.anu.twcore.experiment.BaseLineEdge;
import au.edu.anu.twcore.experiment.ModelSetupEdge;
import au.edu.anu.twcore.experiment.StopOnEdge;

import au.edu.anu.twcore.ecosystem.structure.DriverEdge;
import au.edu.anu.twcore.ecosystem.structure.DecoratorEdge;
import au.edu.anu.twcore.ecosystem.structure.ParameterEdge;

public enum ConfigurationEdgeLabels {
	//=========================================================================
	//			| label				| 	class				
	//-------------------------------------------------------------------------
	E_SIZEDBY 		("sizedBy",			SizedByEdge.class),
	E_BASELINE 		("baseLine",		BaseLineEdge.class),
	E_MODELSETUP	("modelSetup",		ModelSetupEdge.class),
	E_STOPON		("stopOn",			StopOnEdge.class),	
	E_DRIVERS		("drivers",			DriverEdge.class),
	E_DECORATORS	("decorators",		DecoratorEdge.class),
	E_PARAMETERS	("parameters",		ParameterEdge.class),
	;
	//=========================================================================
	private final String label;
	private final Class<?> type;
	
	private ConfigurationEdgeLabels(String label,Class<?> type) {
		this.label = label;
		this.type = type;
	}
	
	public String label() {
		return label;
	}
	
	public Class<?> type() {
		return type;
	}

}
