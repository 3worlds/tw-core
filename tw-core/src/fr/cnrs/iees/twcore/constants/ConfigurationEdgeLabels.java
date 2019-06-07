package fr.cnrs.iees.twcore.constants;

import au.edu.anu.twcore.data.SizedByEdge;
import au.edu.anu.twcore.experiment.BaseLineEdge;
import au.edu.anu.twcore.experiment.ModelSetupEdge;
import au.edu.anu.twcore.experiment.StopOnEdge;

import au.edu.anu.twcore.ecosystem.structure.DriverEdge;
import au.edu.anu.twcore.ecosystem.structure.DecoratorEdge;
import au.edu.anu.twcore.ecosystem.structure.ParameterEdge;
import au.edu.anu.twcore.ecosystem.structure.BelongsToEdge;
import au.edu.anu.twcore.ecosystem.structure.InitialisedByEdge;
import au.edu.anu.twcore.ecosystem.structure.ToCategoryEdge;
import au.edu.anu.twcore.ecosystem.structure.FromCategoryEdge;

import au.edu.anu.twcore.ecosystem.dynamics.AppliesToEdge;
import au.edu.anu.twcore.ecosystem.dynamics.DependsOnEdge;
import au.edu.anu.twcore.ecosystem.dynamics.EffectedByEdge;
import au.edu.anu.twcore.ecosystem.dynamics.StopSystemEdge;

/**
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public enum ConfigurationEdgeLabels {
	//=========================================================================
	//			| label				| 	class				
	//-------------------------------------------------------------------------
	// data definition
	E_SIZEDBY 		("sizedBy",			SizedByEdge.class),
	// experiment
	E_BASELINE 		("baseLine",		BaseLineEdge.class),
	E_MODELSETUP	("modelSetup",		ModelSetupEdge.class),
	E_STOPON		("stopOn",			StopOnEdge.class),
	// ecosystem / structure
	E_DRIVERS		("drivers",			DriverEdge.class),
	E_DECORATORS	("decorators",		DecoratorEdge.class),
	E_PARAMETERS	("parameters",		ParameterEdge.class),
	E_BELONGSTO		("belongsTo",		BelongsToEdge.class),
	E_INITIALISEDBY	("initialisedBy",	InitialisedByEdge.class),
	E_TOCATEGORY	("toCategory",		ToCategoryEdge.class),
	E_FROMCATEGORY	("fromCategory",	FromCategoryEdge.class),
	// ecosystem / dynamics
	E_APPLIESTO		("appliesTo",		AppliesToEdge.class),
	E_DEPENDSON		("dependsOn",		DependsOnEdge.class),
	E_EFFECTEDBY	("effectedBy",		EffectedByEdge.class),
	E_STOPSYSTEM	("stopSystem",		StopSystemEdge.class),
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
