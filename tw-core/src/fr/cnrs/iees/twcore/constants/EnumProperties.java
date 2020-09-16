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

import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.BoxImpl;
import fr.cnrs.iees.uit.space.Point;
import fr.cnrs.iees.uit.space.Sphere;
import fr.cnrs.iees.uit.space.SphereImpl;

/**
 * A class to initialise user-defined properties - must be called before any use of ValidProperties
 * is attempted
 * @author Jacques Gignoux - 8 août 2019
 *
 */
public class EnumProperties {

	private EnumProperties() {}

	// these references trigger the static block intialisation of all these classes,
	// which record them in ValidPropertyTypes
	// this method must be called early in application setup
	// TODO: a cleaner coding by scanning the directory, finding all the class names
	// and invoking any method but this seems non trivial
	// Its possible that compiler optimisation will prevent calling these statements - check.
	public static void recordEnums() {
		DataElementType.defaultValue();
		DateTimeType.defaultValue();
		ExperimentDesignType.defaultValue();
		FileType.defaultValue();
		Grouping.defaultValue();
		LifespanType.defaultValue();
		SamplingMode.defaultValue();
		SnippetLocation.defaultValue();
		SpaceType.defaultValue();
		StatisticalAggregates.defaultValue();
		StatisticalAggregatesSet.defaultValue();
		PopulationVariables.defaultValue();
		PopulationVariablesSet.defaultValue();
		TimeScaleType.defaultValue();
		TimeUnits.defaultValue();
		TwFunctionTypes.defaultValue();
		UIContainerOrientation.defaultValue();
		TrackerType.defaultValue();
		RngResetType.defaultValue();
		RngSeedSourceType.defaultValue();
		RngAlgType.defaultValue();
		BorderType.defaultValue();
		EdgeEffectCorrection.defaultValue();
	}

	// register geometric classes so they can be used as properties
	static {
		// TODO: Caution: the real implemented class must be used. Here, IT'S NOT GOING TO WORK
		// because there are many point subclasses.
		// code must be changed in ValidPropertyType to handle subclassing.
		ValidPropertyTypes.recordPropertyType(Point.class.getSimpleName(),
			Point.class.getName(),
			Point.newPoint(0.0));
		// Caution: the real implemented class must be used. Here, BoxImpl
		ValidPropertyTypes.recordPropertyType(Box.class.getSimpleName(),
			BoxImpl.class.getName(),
			Box.boundingBox(Point.newPoint(0.0),Point.newPoint(0.0)));
		// Caution: the real implemented class must be used. Here, SphereImpl
		ValidPropertyTypes.recordPropertyType(Sphere.class.getSimpleName(),
			SphereImpl.class.getName(),
			Sphere.newSphere(Point.newPoint(0.0),0.0));
	}
}
