package fr.cnrs.iees.twcore.generators.odd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
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
	 * @param configRoot the 3Worlds configuration root node (labelled 3Worlds)
	 * @return a String of svg instructions for later use, eg as a file.
	 */
	public static String flowChart(TreeGraphDataNode configRoot) {
		UMLGenerator umlg = new UMLGenerator();
		umlg.activityDiagram(TwConfigurationAnalyser.getExecutionFlow(configRoot));
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
		// The XML is stored into svg
		final String svg = new String(os.toByteArray(),Charset.forName("UTF-8"));
		return svg;
	}

}
