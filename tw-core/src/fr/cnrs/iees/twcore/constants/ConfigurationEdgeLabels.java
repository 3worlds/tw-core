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
import au.edu.anu.twcore.ecosystem.structure.FromCategoryEdge;

import au.edu.anu.twcore.ecosystem.dynamics.AppliesToEdge;
import au.edu.anu.twcore.ecosystem.dynamics.DependsOnEdge;
import au.edu.anu.twcore.ecosystem.dynamics.EffectedByEdge;
import au.edu.anu.twcore.ecosystem.dynamics.StopSystemEdge;
import au.edu.anu.twcore.ecosystem.dynamics.ConditionEdge;

import au.edu.anu.twcore.ecosystem.dynamics.initial.GroupOfEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.CycleEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.InstanceOfEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LoadFromEdge;

import au.edu.anu.twcore.ui.TrackTimeEdge;
import au.edu.anu.twcore.ui.TrackTimeSeriesEdge;
import au.edu.anu.twcore.ui.TrackFieldEdge;
import au.edu.anu.twcore.ui.TrackTableEdge;
import au.edu.anu.twcore.ui.TrackPopulationEdge;

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
	E_SOURCE		("source",			SourceEdge.class),
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
	E_CONDITION		("condition",		ConditionEdge.class),
	E_GROUPOF		("groupOf",			GroupOfEdge.class),
	E_CYCLE			("cycle",			CycleEdge.class),
	E_INSTANCEOF	("instanceOf",		InstanceOfEdge.class),
	E_LOADFROM		("loadFrom",		LoadFromEdge.class),
	// user interface
	E_TRACKTIME		("trackTime",		TrackTimeEdge.class),
	E_TRACKSERIES	("trackSeries",		TrackTimeSeriesEdge.class),
	E_TRACKFIELD	("trackField",		TrackFieldEdge.class),
	E_TRACKTABLE	("trackTable",		TrackTableEdge.class),
	E_TRACKPOP		("trackPopulation",	TrackPopulationEdge.class)
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
