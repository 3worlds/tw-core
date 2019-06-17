package au.edu.anu.twcore.session;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.TwArchetype;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * 
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
class SimulationSessionTest {

	@SuppressWarnings("unchecked")
	@Test
	final void testSimulationSession() {
		TreeGraph<TreeGraphNode,ALEdge> specs = (TreeGraph<TreeGraphNode,ALEdge>) GraphImporter.importGraph("testSpecs.utg",this.getClass());
		TwArchetype a = new TwArchetype();
		Iterable<CheckMessage> errors = a.checkSpecifications(specs);
		if (errors==null) {
			SimulationSession s = new SimulationSession(specs);
		}
	}

}
