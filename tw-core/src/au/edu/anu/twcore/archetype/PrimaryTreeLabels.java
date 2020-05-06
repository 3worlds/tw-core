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
package au.edu.anu.twcore.archetype;

import java.util.HashSet;
import java.util.Set;

import fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels;

public class PrimaryTreeLabels {
	private static Set<String> labelSet = new HashSet<>();
	static {
		labelSet.add(ConfigurationNodeLabels.N_SYSTEM.label());
		labelSet.add(ConfigurationNodeLabels.N_DYNAMICS.label());
		labelSet.add(ConfigurationNodeLabels.N_STRUCTURE.label());
		labelSet.add(ConfigurationNodeLabels.N_DATADEFINITION.label());
		labelSet.add(ConfigurationNodeLabels.N_EXPERIMENT.label());
		labelSet.add(ConfigurationNodeLabels.N_UI.label());		
	}
	public static boolean contains(String label) {
		return labelSet.contains(label);
	}

}
