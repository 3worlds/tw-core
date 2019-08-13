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

package au.edu.anu.twcore.project;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.exceptions.TwcoreException;

class ProjectTest {

	@Test
	void test() {
		Project.create("getText user string");
		System.out.println(Project.makeConfigurationFile());
		System.out.println(Project.makeLayoutFile());
		assertTrue(Project.getProjectName().equals("gettextUserString"));
		Project.close();
		Project.create("a*()*(^^:b\t\n");
		assertTrue(Project.getProjectName().equals("aB"));
		Project.close();
		try {
			Project.getProjectName();
			fail("Closed but getProjectName() succeeded");
		} catch (TwcoreException e) {
			assertTrue(true);
		}
		try {
			Project.close();
			fail("Closed and already closed Project");
		} catch (TwcoreException e) {
			assertTrue(true);
		}
		try {
			Project.getProjectDateTime();
			fail("Datetime of closed Project");
		} catch (TwcoreException e) {
			assertTrue(true);
		}
		try {
			for (int i = 0; i < 2; i++) {
				Project.create("quick");
				Project.close();
			}
		} catch (TwcoreException e) {
			assertTrue(true);
		}

		assertFalse(Project.isOpen());

		File[] files = Project.getAllProjectPaths();
		for (File f : files) {
			try {
				Project.open(f);
				Project.close();
				assertTrue(true);
			} catch (TwcoreException e) {
				fail("Failed to open " + Project.getProjectDirectory());
			}
		}
		Project.create(" The cat sat on the mat");
		assertTrue(Project.getProjectName().equals("theCatSatOnTheMat"));
		Project.close();
		// Better if tested separately with annotation "expected = TwCpreException
		try {
			Project.create("([{*~!'_^)");
			fail("Should not have created project ([{*~!'_^)");
		} catch (TwcoreException e) {
			assertTrue(true);
		}

		File crap = new File("SDFCRap");
		assertTrue(!Project.isValidProjectFile(crap));




	}

}
