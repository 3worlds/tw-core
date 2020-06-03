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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// static singleton class for java IDE-independent info
/**
 * @author Ian Davies
 *
 * @date 29 Sep 2019
 */
public class UserProjectLink {
	private UserProjectLink() {
	};

	private static IUserProjectLink impl;

	public static void initialise(IUserProjectLink impl) {
		UserProjectLink.impl = impl;
	}

	public static File srcRoot() {
		if (impl == null)
			return null;
		return impl.srcRoot();
	}

	public static File classRoot() {
		if (impl == null)
			return null;
		return impl.classRoot();
	}

	public static File classForSource(File srcFile) {
		return impl.classForSource(srcFile);
	}

	public static File sourceForClass(File clsFile) {
		return impl.sourceForClass(clsFile);
	}

	public static File projectRoot() {
		if (impl == null)
			return null;
		return impl.projectRoot();
	}

	public static boolean haveUserProject() {
		return impl != null;
	}

	public static void unlinkUserProject() {
		impl = null;
	}

	public static File[] getUserLibraries(Set<String> exclusions) {
		return impl.getUserLibraries(exclusions);
	};


	public static void clearFiles() {
		if (impl != null)
			impl.clearFiles();
	}

	public static void addModelFile(File f) {
		if (impl != null)
			impl.addModelFile(f);
	}

	public static void addDataFile(File f) {
		if (impl != null)
			impl.addDataFile(f);
	}

	public static void addFunctionFile(File f) {
		if (impl != null)
			impl.addFunctionFile(f);
	}

	public static void addInitialiserFile(File f) {
		if (impl != null)
			impl.addInitialiserFile(f);
	}

	public static void pushFiles() {
		if (impl != null)
			impl.pushFiles();
	}
	
	public void clearUnusedRemoteFiles() {
		if (impl!=null)
			impl.clearUnusedRemoteFiles();
	};

}
