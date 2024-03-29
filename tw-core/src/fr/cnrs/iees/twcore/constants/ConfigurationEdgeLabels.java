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
import au.edu.anu.twcore.experiment.TreatsEdge;
import au.edu.anu.twcore.experiment.SourceEdge;

import au.edu.anu.twcore.ecosystem.structure.AutoVarEdge;
import au.edu.anu.twcore.ecosystem.structure.DriverEdge;
import au.edu.anu.twcore.ecosystem.structure.DecoratorEdge;
import au.edu.anu.twcore.ecosystem.structure.ConstantsEdge;
import au.edu.anu.twcore.ecosystem.structure.BelongsToEdge;
import au.edu.anu.twcore.ecosystem.structure.InitialisedByEdge;
import au.edu.anu.twcore.ecosystem.structure.ToCategoryEdge;
import au.edu.anu.twcore.ecosystem.structure.FromCategoryEdge;
import au.edu.anu.twcore.ecosystem.structure.CoordinateMappingEdge;
import au.edu.anu.twcore.ecosystem.dynamics.AppliesToEdge;
import au.edu.anu.twcore.ecosystem.dynamics.DependsOnEdge;
import au.edu.anu.twcore.ecosystem.dynamics.EffectedByEdge;
import au.edu.anu.twcore.ecosystem.dynamics.StopSystemEdge;
import au.edu.anu.twcore.ecosystem.dynamics.ConditionEdge;
import au.edu.anu.twcore.ecosystem.dynamics.UseRNGEdge;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessSpaceEdge;
import au.edu.anu.twcore.ecosystem.dynamics.FedByEdge;
import au.edu.anu.twcore.ecosystem.dynamics.initial.LoadFromEdge;

import au.edu.anu.twcore.ui.TrackTimeEdge;
import au.edu.anu.twcore.ui.TrackTimeSeriesEdge;
import fr.cnrs.iees.omugi.graph.Edge;
import au.edu.anu.twcore.ui.TrackFieldEdge;
import au.edu.anu.twcore.ui.TrackTableEdge;
import au.edu.anu.twcore.ui.SampleComponentEdge;
import au.edu.anu.twcore.ui.SampleGroupEdge;
import au.edu.anu.twcore.ui.SampleLifeCycleEdge;
import au.edu.anu.twcore.ui.SampleSystemEdge;
import au.edu.anu.twcore.ui.TrackSpaceEdge;

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
	E_SIZEDBY 		("sizedBy",			"szBy1",		SizedByEdge.class),
	// experiment
	E_BASELINE 		("baseLine",		"bsln1",		BaseLineEdge.class),
	E_MODELSETUP	("modelSetup",		"moSu1",		ModelSetupEdge.class),
	E_STOPON		("stopOn",			"stpOn1",		StopOnEdge.class),
	E_SOURCE		("source",			"src1",			SourceEdge.class),
	E_TREATS		("treats",			"trts",			TreatsEdge.class),
	// ecosystem / structure
	E_AUTOVAR		("autoVar",			"auto1",		AutoVarEdge.class),
	E_DRIVERS		("drivers",			"drvs1",		DriverEdge.class),
	E_DECORATORS	("decorators",		"decs1",		DecoratorEdge.class),
	E_CONSTANTS		("constants",		"cnsts1",		ConstantsEdge.class),
	E_BELONGSTO		("belongsTo",		"bt1",			BelongsToEdge.class),
	E_INITIALISEDBY	("initialisedBy",	"ib1",			InitialisedByEdge.class),
	E_TOCATEGORY	("toCategory",		"to1",			ToCategoryEdge.class),
	E_FROMCATEGORY	("fromCategory",	"from1",		FromCategoryEdge.class),
	E_COORDMAPPING	("coordinate",		"crdmp",		CoordinateMappingEdge.class),	// ecosystem / dynamics
	E_APPLIESTO		("appliesTo",		"aplyTo1",		AppliesToEdge.class),
	E_DEPENDSON		("dependsOn",		"depsOn1",		DependsOnEdge.class),
	E_EFFECTEDBY	("effectedBy",		"effdBy1",		EffectedByEdge.class),
	E_STOPSYSTEM	("stopSystem",		"stpSys1",		StopSystemEdge.class),
	E_CONDITION		("condition",		"condOf1",		ConditionEdge.class),
//	E_GROUPOF		("groupOf",			"grpOf1",		GroupOfEdge.class),
//	E_CYCLE			("cycle",			"cycl1",		CycleEdge.class),
//	E_INSTANCEOF	("instanceOf",		"instOf1",		InstanceOfEdge.class),
	E_LOADFROM		("loadFrom",		"ldFrom1",		LoadFromEdge.class),
	E_USERNG		("useRNG",			"uses1",		UseRNGEdge.class),
	E_SPACE			("inSpace",			"in1",			ProcessSpaceEdge.class),
	E_FEDBY			("fedBy",			"fedBy1",		FedByEdge.class),// edge from function to EventQueue
	// user interface
	E_TRACKTIME		("trackTime",		"trks1",		TrackTimeEdge.class),
	E_TRACKSERIES	("trackSeries",		"trks1",		TrackTimeSeriesEdge.class),
	E_TRACKFIELD	("trackField",		"trks1",		TrackFieldEdge.class),
	E_TRACKTABLE	("trackTable",		"trks1",		TrackTableEdge.class),
	E_SAMPLECOMPONENT("sampleComponent","trks1",		SampleComponentEdge.class),
	E_SAMPLEGROUP	("sampleGroup",		"trks1",		SampleGroupEdge.class),
	E_SAMPLELIFECYCLE("sampleLifeCycle","trks1",		SampleLifeCycleEdge.class),
	E_SAMPLEARENA	("sampleArena",		"trks1",		SampleSystemEdge.class),
	E_TRACKSPACE	("trackSpace",		"trks1",		TrackSpaceEdge.class),
	// Utility - in some cases one may save a child node as an out node with a specific label
	E_CHILD			("_CHILD",			"child",		Edge.class)
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
		throw new IllegalStateException("'"+label+"' not found in "+ConfigurationEdgeLabels.class.getSimpleName());
	}
}
