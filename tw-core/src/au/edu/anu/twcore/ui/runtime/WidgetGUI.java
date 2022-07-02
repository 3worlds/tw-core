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

import au.edu.anu.omhtk.preferences.Preferences;

/**
 * Implemented by widgets with a graphic user interface.
 * 
 * @author Ian Davies - 2 Sep 2019
 */
public interface WidgetGUI extends Widget {

	/**
	 * Method in which implementations create GUI controls.
	 * 
	 * @return implementation-specific root container of the GUI.
	 */
	public Object getUserInterfaceContainer();

	/**
	 * Get the menu (optional) to be place in the main menu bar for this widget.
	 * 
	 * @return root object containing any menu items for this widget (can be null).
	 */
	public Object getMenuContainer();

	/**
	 * Put the widget's control settings in the {@link Preferences} system.
	 */
	public void putUserPreferences();

	/**
	 * Get the widget's control settings from the {@link Preferences} system.
	 */
	public void getUserPreferences();

}
