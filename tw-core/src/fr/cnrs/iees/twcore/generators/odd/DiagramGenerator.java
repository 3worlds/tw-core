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
package fr.cnrs.iees.twcore.generators.odd;

import static au.edu.anu.rscs.aot.queries.CoreQueries.childTree;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectOne;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_SYSTEM;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.ArenaType;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * A class to generate diagrams from 3w config and code.
 * 
 * @author Jacques Gignoux - 28 oct. 2021
 *
 */
// UNTESTED
public class DiagramGenerator {
	
	private DiagramGenerator() {}
	
	/**
	 * <p>Produce a svg flowchart from the 3worlds root node.</p>
	 * <p>Code copied from <a href="https://plantuml.com/fr/api">here</a>.</p>
	 * 
	 * @param configRoot the 3Worlds configuration root node (labelled 3Worlds)
	 * @return a String of svg instructions for later use, eg as a file.
	 */
	public static String flowChart(TreeGraphDataNode configRoot) {
//		List<TreeGraphDataNode> systems = (List<TreeGraphDataNode>) get(configRoot,
//				children(),
//				selectOneOrMany(hasTheLabel(N_SYSTEM.label())));
//		Map<String,String> result = new HashMap<>();
//		for (TreeGraphDataNode system:systems) {
//			UMLGenerator umlg = new UMLGenerator();
//			umlg.activityDiagram(TwConfigurationAnalyser.getExecutionFlow(configRoot,system));
//			String diag = umlg.umlString();
//			SourceStringReader reader = new SourceStringReader(diag);
//			final ByteArrayOutputStream os = new ByteArrayOutputStream();
//			// Write the first image to "os"
//			try {			
////				String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
//				reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
//				os.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			// The UML is stored into svg
//			final String svg = new String(os.toByteArray(),Charset.forName("UTF-8"));
//			result.put(system.id(),svg);
//
//		}
//		return result;
		
		UMLGenerator umlg = new UMLGenerator();
		umlg.activityDiagram(TwConfigurationAnalyser.getExecutionFlow(configRoot,null));
		String diag = umlg.umlString();
		SourceStringReader reader = new SourceStringReader(diag);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();
		// Write the first image to "os"
		try {			
//			String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// The UML is stored into svg
		final String svg = new String(os.toByteArray(),Charset.forName("UTF-8"));
		return svg;
	}

}
