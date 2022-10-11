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

package au.edu.anu.twcore.ecosystem.runtime.simulator;


/**
 * @author Ian Davies -21 Feb 2020
 */
public class RunTimeId {
	private RunTimeId() {
	};

	private static int runTimeId = Integer.MIN_VALUE;

	/* set once from the cmd line when MR starts */
	public static void setRunTimeId(int id) {
		if (runTimeId != Integer.MIN_VALUE)
			throw new IllegalStateException("Attempt to reinitialise RunTimeId [" + runTimeId + "->" + id + "].");
		runTimeId = id;
	}

//	private static int getPID() {
//		java.lang.management.RuntimeMXBean runtime = java.lang.management.ManagementFactory.getRuntimeMXBean();
//		java.lang.reflect.Field jvm;
//		try {
//			jvm = runtime.getClass().getDeclaredField("jvm");
//			jvm.setAccessible(true);
//			Object mgmt =  jvm.get(runtime);
//			java.lang.reflect.Method pid_method = mgmt.getClass().getDeclaredMethod("getProcessId");
//			pid_method.setAccessible(true);
//			int pid = (Integer) pid_method.invoke(mgmt);
//			return pid;
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return 0;
//	}

	/* obtained by anything that wants to know - file i/o etc */
	public static int runTimeId() {
		if (runTimeId == Integer.MIN_VALUE)
			throw new IllegalStateException("Attempt to access uninitialised RunTimeId.");
		return runTimeId;
	}
}
