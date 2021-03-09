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
import java.util.Arrays;
import java.util.List;

import au.edu.anu.rscs.aot.errorMessaging.ErrorMessagable;
import au.edu.anu.rscs.aot.errorMessaging.impl.SpecificationErrorMsg;
import au.edu.anu.twcore.exceptions.TwcoreException;
import au.edu.anu.twcore.userProject.UserProjectLink;

/**
 * @author Ian Davies
 *
 * @date 23 Nov 2019
 */
public class ModelBuildErrorMsg implements ErrorMessagable {

	private ModelBuildErrors msgType;
	private Object[] args;
	private String actionsMsg;
	private String detailsMsg;
	private String debugMsg;
//	private boolean ignore;

	public ModelBuildErrorMsg(ModelBuildErrors msgType, Object... args) {
		this.msgType = msgType;
		this.args = args;
//		this.ignore = false;
		buildMessages();
	}

//	public boolean ignore() {
//		return ignore;
//	}

	private void buildMessages() {
		switch (msgType) {
		case SPECIFICATION: {// messages from the archetype checking
			SpecificationErrorMsg sem = (SpecificationErrorMsg) args[0];
			actionsMsg = sem.actionInfo();
			detailsMsg = sem.detailsInfo();
			debugMsg = sem.debugInfo();
			break;
		}
		case MODEL_FILE_BACKUP: {
			File localSrcFile = (File) args[0];
			String actionStr = "Update code in newly created java file.";
			String constraintStr = "Configuration has changed requiring creation of a new Java file. The previous file has been backed up and renamed with ext *.orig<n>.";
			actionsMsg = category() + localSrcFile.getName() + ": " + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;
			detailsMsg += "\nItem: " + localSrcFile.getName();

			debugMsg = "\nAction: " + actionStr;
			debugMsg += "\nConstraint: " + constraintStr;
			debugMsg += "\nCategory: " + msgType.category();
			debugMsg += "\nMessage Class: " + msgType;
			debugMsg += "\nItem: " + localSrcFile;
			break;

		}
		case COMPILER_ERROR: {
			File file = (File) args[0];
			String compileResult = "unknown";
			if (args.length > 0)
				compileResult = (String) args[1];

			String actionStr;
			if (UserProjectLink.haveUserProject())
				actionStr = "Correct coding errors in " + UserProjectLink.projectRoot().getName() + ".";
			else
				actionStr = "Correct coding errors in code snippets.";

			String constraintStr = compileResult;

			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;
			detailsMsg += "\nFile/Directory: " + file.getName();

			debugMsg = "\nAction: " + actionStr;
			debugMsg += "\nConstraint: " + constraintStr;
			debugMsg += "\nPath: " + file;
			debugMsg += "\nCategory: " + msgType.category();
			debugMsg += "\nMessage Class: " + msgType;
			if (UserProjectLink.haveUserProject())
				debugMsg += "\nInfo: File has not been pushed to " + UserProjectLink.projectRoot().getName();
			break;
		}
		case COMPILER_MISSING: {
			String actionStr = "Install Java Development Kit (JDK)";
			String constraintStr = "A Java compiler is required and not found on the system.";
			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;

			debugMsg = detailsMsg;
			debugMsg += "\nCategory: " + msgType.category();
			debugMsg += "\nMessage Class: " + msgType;
			debugMsg += "\nJava runtime version: " + System.getProperty("java.runtime.version");
			break;
		}
		case DEPLOY_CLASS_MISSING: {
			File cls = (File) args[0];
			File src = (File) args[1];
			String actionStr = "A Java class file is missing";
			String constraintStr = "Expected class file for '" + src.getName() + "' but none found.";
			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;

			debugMsg = detailsMsg;
			debugMsg += "\nCategory: " + msgType.category();
			debugMsg += "\nMessage Class: " + msgType;
			debugMsg += "\nJava file: " + src;
			debugMsg += "\nClass file: " + cls;
			break;
		}
		case DEPLOY_CLASS_OUTOFDATE: {
			/*- remoteSrcFile, remoteClsFile, ftSrc, ftCls*/
			File remoteSrcFile = (File) args[0];
			File remoteClsFile = (File) args[1];
			FileTime ftSrc = (FileTime) args[2];
			FileTime ftCls = (FileTime) args[3];

			String actionStr = "Refresh Java project.";
			String constraintStr = "Compiled class file is older than Java source file";

			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;
			detailsMsg += "\nSource file: " + remoteSrcFile.getName() + "[time: " + ftSrc.toString() + "]";
			detailsMsg += "\nClass file: " + remoteClsFile.getName() + "[time: " + ftCls.toString() + "]";

			debugMsg = detailsMsg;
			debugMsg += "\nSource path: " + remoteSrcFile;
			debugMsg += "\nClass path: " + remoteClsFile;

			break;
		}
		case DEPLOY_PROJECT_UNSAVED: {
			// no args
			String actionStr = "Press [Ctrl+s] to save configuration.";
			String constraintStr = "Configuration must be saved to allowed deployment.";
			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;

			debugMsg = detailsMsg;
			debugMsg += "\nCategory: " + msgType.category();
			debugMsg += "\nMessage Class: " + msgType;

			break;
		}
		case DEPLOY_RESOURCE_MISSING: {
			/*- file */
			String resourceName = (String) args[0];
			String location = (String) args[1];
			String actionStr = "Add '" + resourceName + "' to '" + location + "'.";
			String constraintStr = "Resource must be present for deployment";

			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;
			detailsMsg += "\nCategory: " + msgType.category();

			debugMsg = detailsMsg;
			debugMsg += "\nMessage Class: " + msgType;
			break;
		}
		case DEPLOY_EXCEPTION: {
			Exception e = (Exception) args[0];
			@SuppressWarnings("unchecked")
			List<String> cmds = (List<String>) args[1];
			String actionStr = "Failed to launch ModelRunner.";
			String constraintStr = e.getMessage();
			actionsMsg = category() + actionStr;

			detailsMsg = "\nAction: " + actionStr;
			detailsMsg += "\nConstraint: " + constraintStr;
			detailsMsg += "\nCommands: " + Arrays.deepToString(cmds.toArray());

			debugMsg = detailsMsg;
			debugMsg += "\nException: " + e;
			break;
		}
		case DEPLOY_FAIL: {// not used 
			// Exception e = (Exception) args[0];
			@SuppressWarnings("unchecked")
			List<String> lines = (List<String>) args[0];
			File project = (File) args[1];
			actionsMsg = category() + "ModelRunner has errors.";
			StringBuilder sb = new StringBuilder();
			for (String line : lines)
				sb.append(line).append("\n");
			detailsMsg = category() + errorName() + "ModelRunner has errors.\n" + //
					"Log=" + sb.toString() + //
					"Project=" + project.getAbsoluteFile();
			throw new TwcoreException("Message type not handled [" + msgType + "]");
//			break;
		}
		default: {
			throw new TwcoreException("Message type not handled [" + msgType + "]");
		}
		}

	}

//	private boolean findNodeWithClassId(String classId, TreeGraph<TreeGraphDataNode, ALEdge> graph) {
//		for (Node node : graph.nodes())
//			if (node.classId().equals(classId))
//				return true;
//		return false;
//	}

//	private String refToClassId(String ref) {
//		return ref.replace(":", "");
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("verbose1: ");
		sb.append(actionsMsg);
		sb.append("\n");
		sb.append("verbose2: ");
		sb.append(detailsMsg);
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

	@Override
	public String actionInfo() {
		return actionsMsg;
	}

	@Override
	public String detailsInfo() {
		return detailsMsg;
	}

	@Override
	public String debugInfo() {
		return debugMsg;
	}

}
