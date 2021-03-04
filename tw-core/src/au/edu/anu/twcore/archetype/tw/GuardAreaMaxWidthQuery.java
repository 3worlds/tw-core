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
package au.edu.anu.twcore.archetype.tw;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import au.edu.anu.twcore.ecosystem.structure.SpaceNode;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;
import fr.ens.biologie.generic.utils.Interval;
/**
 * Checks that a guard area fits within a space
 *
 * @author Jacques Gignoux - 16 sept. 2020
 *
 */
//Tested OK 16/6/2020

public class GuardAreaMaxWidthQuery extends QueryAdaptor {

	@Override
	public Queryable submit(Object input) {
		initInput(input);
		SpaceNode spn = (SpaceNode) input;
		if (spn.properties().hasProperty(P_SPACE_GUARDAREA.key())) {
			SpaceType stype = (SpaceType) spn.properties().getPropertyValue(P_SPACETYPE.key());
			double width = (double) spn.properties().getPropertyValue(P_SPACE_GUARDAREA.key());
			Box lim = null;
			switch (stype) {
			case continuousFlatSurface:
				Interval x = (Interval) spn.properties().getPropertyValue(P_SPACE_XLIM.key());
				Interval y = (Interval) spn.properties().getPropertyValue(P_SPACE_YLIM.key());
				lim = Box.boundingBox(Point.newPoint(x.inf(), y.inf()), Point.newPoint(x.sup(), y.sup()));
				break;
			case linearNetwork:
				// TODO
				break;
			case squareGrid:
				double cellSize = (double) spn.properties().getPropertyValue(P_SPACE_CELLSIZE.key());
				int nx = (int) spn.properties().getPropertyValue(P_SPACE_NX.key());
				int ny = nx;
				if (spn.properties().hasProperty("ny"))
					ny = (int) spn.properties().getPropertyValue(P_SPACE_NY.key());
				lim = Box.boundingBox(Point.newPoint(0.0, 0.0), Point.newPoint(nx * cellSize, ny * cellSize));
				break;
			case topographicSurface:
				// TODO
				break;
			default:
				break;
			}
			if (lim != null)
				if (!(Math.min(lim.sideLength(0), lim.sideLength(1)) > 2 * width))
					errorMsg = "'" + spn.toShortString()
							+ "' guard area width must be smaller than half the space shortest side length]";
		}
		return this;

	}

}
