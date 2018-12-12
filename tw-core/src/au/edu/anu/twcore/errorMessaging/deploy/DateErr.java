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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

import au.edu.anu.twcore.errorMessaging.ErrorMessageAdaptor;

/**
 * Author Ian Davies
 *
 * Date Dec 12, 2018
 */
public class DateErr extends ErrorMessageAdaptor {
	public DateErr(File fSrcJava, File fSrcClass)  {
		msg1 = "Refresh Java project: Compiled class file is older than Java src file ("+fSrcJava.getName();
		FileTime ftJava = null;
		FileTime ftClass = null;
		try {
			ftJava = Files.getLastModifiedTime(fSrcJava.toPath());
			ftClass = Files.getLastModifiedTime(fSrcClass.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg2 = msg1+"\n"+fSrcJava.getName()+"("+ftJava.toString()+")";
		msg3 = msg2+"\n"+fSrcClass.getName()+"("+ftClass.toString()+")";
	}

}
