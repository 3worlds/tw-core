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

package au.edu.anu.twcore.errorMessaging.deploy;

import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import au.edu.anu.twcore.errorMessaging.ErrorMessageAdaptor;

/**
 * Author Ian Davies
 *
 * Date Dec 12, 2018
 */
public class JarErr extends ErrorMessageAdaptor{
	public JarErr(Manifest manifest, String jarPath, Exception e) {
		msg1 = "Unable to create jar '" + jarPath;
		msg2 = msg1 + "\n" + e;
		msg3 = msg2 + "\n";
		for (Entry<String, Attributes> entry : manifest.getEntries().entrySet()) {
			msg3+=entry.getKey()+":"+entry.getValue().toString()+"\n";
		}
	}
}
