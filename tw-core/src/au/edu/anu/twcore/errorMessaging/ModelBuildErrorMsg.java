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
package au.edu.anu.twcore.errorMessaging;

import java.io.File;
import java.nio.file.attribute.FileTime;

import au.edu.anu.rscs.aot.errorMessaging.ErrorMessagable;
import au.edu.anu.rscs.aot.errorMessaging.impl.SpecificationErrorMsg;
import au.edu.anu.rscs.aot.util.IntegerRange;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.project.Project;
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;

/**
 * @author Ian Davies
 *
 * @date 23 Nov 2019
 */
public class ModelBuildErrorMsg implements ErrorMessagable {

	private ModelBuildErrors msgType;
	private Object[] args;
	private String verbose1;
	private String verbose2;
	private boolean ignore;

	public ModelBuildErrorMsg(ModelBuildErrors msgType, Object... args) {
		this.msgType = msgType;
		this.args = args;
		this.ignore = false;
		buildMessages();
	}

	public boolean ignore() {
		return ignore;
	}

	private void buildMessages() {
		switch (msgType) {
		case PROCESS_CLASS_CHANGE: {
			String localAncestorClass = (String) args[0];
			String remoteAncestorClass = (String) args[1];
			File localSrcFile = (File) args[2];
			/*
			 * msg1 =
			 * "Refresh and check Java project: Process class has changed ("+name+")"; msg2
			 * = msg1 + "\nOld class: "+oldAncestorClass+"\nNew class: "+newAncestorClass;
			 * msg3 = msg2;
			 */
			break;
		}
		case COMPILER_ERROR: {
			/*
			 * ErrorList.add(new ModelConstructionErrorMessage(ecologyFiles,
			 * "Files not pushed to linked project. " + result)); //
			 * ComplianceManager.add(new CompileErr(ecologyFiles,
			 * "Files not pushed to linked project. " + result)); else
			 * ComplianceManager.add(new CompileErr(ecologyFiles,result));
			 */
			/*
			 * msg1 = "There were compiling warnings/errors in " + classFile.getName(); msg2
			 * = msg1 + "\n" + error; msg3 = msg2;
			 */

			File file = (File) args[0];
			String compileResult = (String) args[1];
			if (UserProjectLink.haveUserProject()) {

			}

			break;
		}
		case COMPILER_MISSING: {
			/*-msg1 = "Java compiler not found.";
			msg2 = msg1 + " Check installation of Java Development Kit (JDK)";
			msg3 = msg2;*/
			break;
		}
		case DEPLOY_CLASS_MISSING: {
			/*-msg1 = "Refresh Java Project: Compiled class file is missing for "+sourceFile.getName();
			msg2 = msg1+ "\n"+sourceFile.getAbsolutePath();
			msg3 = msg2+ "\n"+classFile.getAbsolutePath();
			*/
			File cls = (File) args[0];
			File src = (File) args[1];

			break;
		}
		case DEPLOY_CLASS_OUTOFDATE: {
			// remoteSrcFile, remoteClsFile, ftSrc, ftCls
			/*-"Refresh Java project: Compiled class file is older than Java source file '" + sourceFile.getName()
				+ "'.";
			msg2 = msg1 + "\n" + sourceFile.getName() + "(" + ageSource.toString() + ")";
			msg2 = msg2 + "\n" + classFile.getName() + "(" + ageClass.toString() + ")";
			*/
			File remoteSrcFile = (File) args[0];
			File remoteClsFile = (File) args[1];
			FileTime ftSrc = (FileTime) args[2];
			FileTime ftCls = (FileTime) args[3];
			break;
		}
		case SPECIFICATION: {
			// Here is where context is shifted from whatever the specs say to whatever the
			// user can do.
			/*- SpecificationErrorMsg se)*/

			SpecificationErrorMsg sem = (SpecificationErrorMsg) args[0];
			TreeGraph<TreeGraphDataNode, ALEdge> graph = (TreeGraph<TreeGraphDataNode, ALEdge>) args[1];
			verbose1 = sem.verbose1();
			verbose2 = sem.verbose2();
			switch (sem.error()) {
			case CHILD_MULTIPLICITY_INCORRECT: {
				Node parent = (Node) sem.args()[0];
				String childClassName = (String) sem.args()[1];
				IntegerRange range = (IntegerRange) sem.args()[2];
				Integer nChildren = (Integer) sem.args()[3];
				if (nChildren < range.getLast())
					verbose1 = sem.category() + "Add '" + childClassName + "' to " + parent.toUniqueString() + ".";
			}
				break;
			case EDGE_OUT_OF_RANGE: {
				Node fromNode = (Node) sem.args()[0];
				IntegerRange range = (IntegerRange) sem.args()[1];
				String label = (String) sem.args()[2];
				String reference = (String) sem.args()[3];
				Integer nEdges = (Integer) sem.args()[4];
				if (!findNodeWithClassId(refToClassId(reference), graph))
					ignore = true;
				else {
					if (nEdges < range.getLast())
						verbose1 = sem.category() + "Add edge of class '" + label + "' from '"
								+ fromNode.toUniqueString() + "' to a node of class '" + reference + "'.";
				}
				break;
			}
			}
			break;
		}
		case DEPLOY_PROJECT_UNSAVED: {
			// no args
			verbose1 = category() + "Configuration is unsaved [press Ctrl+S].";
			verbose2 = category() + errorName()
					+ "Configuration is unsaved [press Ctrl+S]. To deploy the experiment from ModelMaker, the configuration must first be saved.";
			break;
		}
		default: {
			throw new TwcoreException("Message type not handled [" + msgType + "]");
		}
		}
	}

	private boolean findNodeWithClassId(String classId, TreeGraph<TreeGraphDataNode, ALEdge> graph) {
		for (Node node : graph.nodes())
			if (node.classId().equals(classId))
				return true;
		return false;
	}

	private String refToClassId(String ref) {
		return ref.replace(":", "");
	}

	@Override
	public String verbose1() {
		return verbose1;
	}

	@Override
	public String verbose2() {
		return verbose2;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("verbose1: ");
		sb.append(verbose1);
		sb.append("\n");
		sb.append("verbose2: ");
		sb.append(verbose2);
		sb.append("\n");
		return sb.toString();
	}

	@Override
	public String category() {
		return "[" + msgType.category() + "] ";
	}

	@Override
	public String errorName() {
		return "[" + msgType.name() + "] ";
	}

	public ModelBuildErrors error() {
		return msgType;
	}
}
