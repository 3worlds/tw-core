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

package au.edu.anu.twcore.userProject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Ian Davies - 23 Aug 2019
 */
public class UPLEclipse extends AbstractUPL{
	File srcDir;
	File classDir;
	File projectDir;
	private static final String libraryFile = ".classpath";
	private static final String entryPrefix = "<classpathentry kind=\"lib\" path=\"";
	private static final String entrySuffix = "\"/>";


	public UPLEclipse(File projectDir/** other non-standard dirs*/) {
		super();
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

	private boolean pathContains(String line,Set<String>exclusions) {
		for (String s :exclusions) {
			if (line.contains(s))
				return true;			
		}
		return false;
	}
	@Override
	public File[] getUserLibraries(Set<String> exclusions) {
		List<File> fileList = new ArrayList<>();
		File filename = new File(projectDir.getAbsoluteFile()+File.separator+libraryFile);
		BufferedReader infile=null;
		try {
			infile = new BufferedReader(new FileReader(filename));
			String line = null;
			while ((line = infile.readLine()) != null) {
				if (line.contains(entryPrefix)) {
					String path = line.replace(entryPrefix, "").replace(entrySuffix, "");
					if (!pathContains(path,exclusions)) {
							path = path.replace("\t", "");
							File libFile = new File(path);
							if (!libFile.exists())
								throw new IllegalArgumentException("Attempting to add non-existent library: " + libFile);
							fileList.add(new File(path));
						}
				}
			}
			infile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				infile.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (fileList.isEmpty())
			return null;
		else {
			return fileList.toArray(new File[fileList.size()]);
		}
	}

	@Override
	public File projectRoot() {
		return projectDir;
	}



	




}
