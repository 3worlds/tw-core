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
import au.edu.anu.twcore.userProject.UserProjectLink;
import fr.cnrs.iees.graph.Element;
import fr.cnrs.iees.graph.Node;

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

	private String labelId(Element e) {
		return e.classId() + ":" + e.id();
	}

	public boolean ignore() {
		return ignore;
	}

	private void buildMessages() {
		switch (msgType) {
		case MODEL_FILE_BACKUP: {
			File localSrcFile = (File) args[0];
			verbose1 = category() + "Check and refresh linked Java project. Model file '" + localSrcFile.getName()
					+ "' has changed structure due to configuration edits.";
			verbose2 = category() + errorName() +  "Check and refresh linked Java project. Model file '" + localSrcFile.getName()
			+ "' has changed structure due to configuration edits.\n" + //
					"Old Model file has been backed up and renamed with ext *.orig<n>";
			
			break;

		}
		case COMPILER_ERROR: {
			File file = (File) args[0];
			String compileResult = "unknown";
			if (args.length>0)
			compileResult = (String) args[1];
			verbose1 = category() + "There were compiling warnings/errors in " + file.getName() + ".";
			verbose2 = category() + errorName() + "There were compiling warnings/errors in " + file.getName()
					+ ". Errors: " + compileResult;
			if (UserProjectLink.haveUserProject()) {
				verbose2 = category() + errorName() + "There were compiling warnings/errors in " + file.getName()
						+ ". Errors: " + compileResult + ".\nFile has not been pushed to "
						+ UserProjectLink.projectRoot().getName();
			}

			break;
		}
		case COMPILER_MISSING: {
			verbose1 = category() + "Java compiler not found.";
			verbose2 = category() + errorName()
					+ "Java compiler not found. Check installation of Java Development Kit (JDK)";
			break;
		}
		case DEPLOY_CLASS_MISSING: {
			File cls = (File) args[0];
			File src = (File) args[1];
			verbose1 = category() + "Class file is missing [" + cls.getName() + "].";
			verbose1 = category() + errorName() + "Class file missing:\n" + //
					cls.getAbsolutePath() + "\n" + //
					src.getAbsoluteFile();

			break;
		}
		case DEPLOY_CLASS_OUTOFDATE: {
			/*- remoteSrcFile, remoteClsFile, ftSrc, ftCls*/
			File remoteSrcFile = (File) args[0];
			File remoteClsFile = (File) args[1];
			FileTime ftSrc = (FileTime) args[2];
			FileTime ftCls = (FileTime) args[3];

			verbose1 = category() + "Refresh Java project: Compiled class file is older than Java source file '"
					+ remoteSrcFile.getName() + ".";
			verbose2 = category() + errorName()
					+ "Refresh Java project.  Compiled class file is older than Java source file:\n" + //
					remoteClsFile.getAbsolutePath() + " Time = " + ftCls.toString() + "\n" + //
					remoteSrcFile.getAbsolutePath() + " Time = " + ftSrc.toString();
			break;
		}
		case SPECIFICATION: {
			// Translate error msg a bit for mm
			/*- SpecificationErrorMsg se)*/

			SpecificationErrorMsg sem = (SpecificationErrorMsg) args[0];
//			@SuppressWarnings("unchecked")
//			TreeGraph<TreeGraphDataNode, ALEdge> graph = (TreeGraph<TreeGraphDataNode, ALEdge>) args[1];
			verbose1 = sem.verbose1();
			verbose2 = sem.verbose2();
			switch (sem.error()) {
			case NODE_RANGE_INCORRECT2: {
				Node parent = (Node) sem.args()[0];
				String childClassName = (String) sem.args()[1];
				IntegerRange range = (IntegerRange) sem.args()[2];
				Integer nChildren = (Integer) sem.args()[3];
				if (nChildren < range.getLast())
					verbose1 = sem.category() + "Add node '" + childClassName + ":' to '" + labelId(parent) + "'.";
			}
				break;
			case EDGE_RANGE_INCORRECT: {
				Node fromNode = (Node) sem.args()[0];
				IntegerRange range = (IntegerRange) sem.args()[1];
				String label = (String) sem.args()[2];
				String reference = (String) sem.args()[3];
				Integer nEdges = (Integer) sem.args()[4];
//				if (!findNodeWithClassId(refToClassId(reference), graph))
//					ignore = true;
//				else { // can't do this! will fall through to generateCode and crash!
				if (nEdges < range.getLast())
					verbose1 = sem.category() + "Add edge '" + label + ":' from '" + labelId(fromNode) + "' to '"
							+ reference + "'.";
//				}
				break;
			}
			default: {
				// do nothing
			}
			}
			break;
		}
		case DEPLOY_PROJECT_UNSAVED: {
			// no args
			verbose1 = category() + "Configuration is unsaved [press Ctrl+S].";
			verbose2 = category() + errorName()
					+ "Configuration is unsaved [press Ctrl+S]. Project must be saved before model can be deployed from ModelMaker.";
			break;
		}
		case DEPLOY_RESOURCE_MISSING: {
			/*- file */
			File file = (File) args[0];
			String hint = (String) args[1];
			verbose1 = category() + "Resource missing [" + file.getName() + "].";
			verbose2 = category() + errorName() + "Resource missing [" + file.getAbsolutePath() + "]. " + hint;
			break;
		}
		case DEPLOY_EXCEPTION:{
			Exception e = (Exception) args[0];
			File errorFile = (File)args[1];
			File prjFile = (File)args[2];
			verbose1 = category()+"Failed to launch ModelRunner.";
			verbose2 = category()+errorName()+"Failed to launch ModelRunner.\n"+//
					"Project="+prjFile.getAbsolutePath()+"\n"+//
					"ErrorLog="+errorFile.getAbsolutePath()+"\n"+//
					"Exception="+e.toString();
			
			break;	
		}
		case DEPLOY_FAIL:{
			Exception e = (Exception) args[0];
			File errorLog = (File)args[1];
			File project = (File)args[2];
			verbose1 = category()+"ModelRunner crashed on startup.";
			verbose2 = category()+errorName()+"ModelRunner crashed on startup.\n"+//
					"Log="+errorLog.getAbsoluteFile()+"\n"+//
					"Project="+project.getAbsoluteFile()+"\n"+//
					"Exception="+e.toString();
			break;
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
