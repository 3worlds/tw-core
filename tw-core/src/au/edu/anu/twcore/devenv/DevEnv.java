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
import java.util.Set;

// static singleton class for java IDE-independent info
// Not particularly useful (yet?)
// This should be a factory
public class DevEnv {
	private DevEnv() {
	};

	private static IDevEnv impl;

	public static void initialise(IDevEnv impl) {
		DevEnv.impl = impl;
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

	public static File classForSource(File source) {
		return impl.classForSource(source);
	}
}
