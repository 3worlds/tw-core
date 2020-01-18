/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package fr.cnrs.iees.twcore.constants;

import au.edu.anu.twcore.data.SizedByEdge;
import au.edu.anu.twcore.experiment.BaseLineEdge;
import au.edu.anu.twcore.experiment.ModelSetupEdge;
import au.edu.anu.twcore.experiment.StopOnEdge;
import au.edu.anu.twcore.experiment.SourceEdge;

import au.edu.anu.twcore.ecosystem.structure.DriverEdge;
import au.edu.anu.twcore.ecosystem.structure.DecoratorEdge;
import au.edu.anu.twcore.ecosystem.structure.ParameterEdge;
import au.edu.anu.twcore.ecosystem.structure.BelongsToEdge;
import au.edu.anu.twcore.ecosystem.structure.InitialisedByEdge;
import au.edu.anu.twcore.ecosystem.structure.ToCategoryEdge;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.ecosystem.structure.FromCategoryEdge;

import au.edu.anu.twcore.ecosystem.dynamics.AppliesToEdge;
import au.edu.anu.twcore.ecosystem.dynamics.DependsOnEdge;
import au.edu.anu.twcore.ecosystem.dynamics.EffectedByEdge;
import au.edu.anu.twcore.ecosystem.dynamics.StopSystemEdge;
import au.edu.anu.twcore.ecosystem.dynamics.ConditionEdge;
import au.edu.anu.twcore.ecosystem.dynamics.UseRNGEdge;

import au.edu.anu.twcore.ecosystem.dynamics.initial.GroupOfEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.CycleEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.InstanceOfEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LoadFromEdge;

import au.edu.anu.twcore.ui.TrackTimeEdge;
import au.edu.anu.twcore.ui.TrackTimeSeriesEdge;
import au.edu.anu.twcore.ui.TrackFieldEdge;
import au.edu.anu.twcore.ui.TrackTableEdge;
import au.edu.anu.twcore.ui.TrackPopulationEdge;
import au.edu.anu.twcore.ui.TrackComponentEdge;

/**
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public enum ConfigurationEdgeLabels {
	//=========================================================================
	//			| label					|def name		| 	class				
	//-------------------------------------------------------------------------
	// data definition
	E_SIZEDBY 		("sizedBy",			"szBy",			SizedByEdge.class),
	// experiment
	E_BASELINE 		("baseLine",		"bsln",			BaseLineEdge.class),
	E_MODELSETUP	("modelSetup",		"moSu",			ModelSetupEdge.class),
	E_STOPON		("stopOn",			"stpOn",		StopOnEdge.class),
	E_SOURCE		("source",			"srcd",			SourceEdge.class),
	// ecosystem / structure
	E_DRIVERS		("drivers",			"drvs",			DriverEdge.class),
	E_DECORATORS	("decorators",		"decs",			DecoratorEdge.class),
	E_PARAMETERS	("parameters",		"pars",			ParameterEdge.class),
	E_BELONGSTO		("belongsTo",		"blngsTo",		BelongsToEdge.class),
	E_INITIALISEDBY	("initialisedBy",	"initBy",		InitialisedByEdge.class),
	E_TOCATEGORY	("toCategory",		"to",			ToCategoryEdge.class),
	E_FROMCATEGORY	("fromCategory",	"from",			FromCategoryEdge.class),
	// ecosystem / dynamics
	E_APPLIESTO		("appliesTo",		"aplyTo",		AppliesToEdge.class),
	E_DEPENDSON		("dependsOn",		"depsOn",		DependsOnEdge.class),
	E_EFFECTEDBY	("effectedBy",		"effdBy",		EffectedByEdge.class),
	E_STOPSYSTEM	("stopSystem",		"stpSys",		StopSystemEdge.class),
	E_CONDITION		("condition",		"condOf",			ConditionEdge.class),
	E_GROUPOF		("groupOf",			"grpOf",		GroupOfEdge.class),
	E_CYCLE			("cycle",			"cycl",			CycleEdge.class),
	E_INSTANCEOF	("instanceOf",		"instOf",		InstanceOfEdge.class),
	E_LOADFROM		("loadFrom",		"ldFrom",		LoadFromEdge.class),
	E_USERNG		("useRNG",			"uses",			UseRNGEdge.class),
	// user interface
	E_TRACKTIME		("trackTime",		"trksTmOf",		TrackTimeEdge.class),
	E_TRACKSERIES	("trackSeries",		"trksSrsOf",	TrackTimeSeriesEdge.class),
	E_TRACKFIELD	("trackField",		"trksFldOf",	TrackFieldEdge.class),
	E_TRACKTABLE	("trackTable",		"trksTblOf",	TrackTableEdge.class),
	E_TRACKPOP		("trackPopulation",	"trksPopOf",	TrackPopulationEdge.class),
	E_TRACKCOMPONENT("trackComponent",	"trksCmpOf",	TrackComponentEdge.class)
	;
	//=========================================================================
	private final String label;
	private final String defName;
	private final Class<?> type;
	
	private ConfigurationEdgeLabels(String label,String defName,Class<?> type) {
		this.label = label;
		this.defName=defName;
		this.type = type;
	}
	
	public String label() {
		return label;
	}
	
	public String defName() {
		return defName;
	}
	
	public Class<?> type() {
		return type;
	}
	public static ConfigurationEdgeLabels labelValueOf(String label) {
		for (ConfigurationEdgeLabels lbl: ConfigurationEdgeLabels.values()) {
			if (lbl.label.equals(label))
				return lbl;
		}
		throw new TwcoreException("'"+label+"' not found in "+ConfigurationEdgeLabels.class.getSimpleName());
	}
}
