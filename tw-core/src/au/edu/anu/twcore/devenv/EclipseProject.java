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

package au.edu.anu.twcore.devenv;

import java.io.File;
import java.util.List;

import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * @author Ian Davies
 *
 * @date 23 Aug 2019
 */
public class EclipseProject implements IDevEnv{
	File srcDir;
	File classDir;
	File projectDir;
	

	public EclipseProject(File projectDir/** other non-standard dirs*/) {
		this.projectDir=projectDir;
		this.srcDir = new File(projectDir.getAbsoluteFile()+File.separator+"src");
		this.classDir = new File(projectDir.getAbsoluteFile()+File.separator+"bin");
	}

	@Override
	public File srcRoot() {
		return srcDir;
	}

	@Override
	public File classRoot() {
		return classDir;
	}

	@Override
	public List<File> getUserLibraries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public File projectRoot() {
		return projectDir;
	}

}
