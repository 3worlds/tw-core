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
package au.edu.anu.twcore.ui.runtime;

import fr.cnrs.iees.properties.SimplePropertyList;
import au.edu.anu.twcore.ui.*;

/**
 * Interface for run-time widgets that have no GUI (must be children of {@link UIHeadless}).
 * 
 * @author Ian Davies -19 Sep 2019
 */

public interface Widget {

	/**
	 * Provides access to the properties in the {@link WidgetNode}.
	 * 
	 * @param id         Widgets id. This is unique with the scope of the graph and
	 *                   can be made use of for file naming.
	 * @param properties Property list of the {@link WidgetNode}. Additional
	 *                   properties may be added at construction time.
	 */
	public void setProperties(String id, SimplePropertyList properties);

}
