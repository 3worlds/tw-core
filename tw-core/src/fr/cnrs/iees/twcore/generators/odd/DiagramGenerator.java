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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import fr.cnrs.iees.omugi.graph.impl.TreeGraphDataNode;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

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
	 * @param system - the root node of a configuration model.
	 * @return a String of svg instructions for later use, eg as a file.
	 */
	public static String flowChart(TreeGraphDataNode system) {
		UMLGenerator umlg = new UMLGenerator();
		umlg.activityDiagram(TwConfigurationAnalyser.getSystemExecutionFlow(system));
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
