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

import au.edu.anu.rscs.aot.errorMessaging.impl.ErrorMessageText;

/**
 * @author Ian Davies
 *
 * @date 14 Mar. 2021
 */
public class QueryMessages extends ErrorMessageText {

	public static String[] EdgeToOneChildOfQuery(String nodeRef) {
		String am;
		String cm;
		if (isFrench()) {
			am = "Ajouter un bord à un enfant de <<" + nodeRef + ">>.";
			cm = "Bord attendu pour un enfant de <<" + nodeRef + ">> mais n'en trouve aucun.";
		} else {
			am = "Add edge to a child of '" + nodeRef + "'";
			cm = "Expected edge to a child of '" + nodeRef + "' but found none.";
		}
		String[] result = { am, cm };
		return result;
	};
}
