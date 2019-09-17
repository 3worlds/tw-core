package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.session.SimulationSession;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * 
 * @author Jacques Gignoux - 12 août 2019
 *
 */
class InitialStateTest {

	@SuppressWarnings("unchecked")
	@Test
	final void testInitialise() {
		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
			GraphImporter.importGraph("initialState.utg",this.getClass());
		SimulationSession s = new SimulationSession(specs);
		Ecosystem eco = (Ecosystem) specs.findNode("my_ecosystem");
		System.out.println(eco.getInstance().toString());	
		System.out.println(eco.getInstance().subContainer("A").toString());
		assertNotNull(s);
	}

	@SuppressWarnings("unchecked")
	@Test
	final void testInitialise2() {
		TreeGraph<TreeGraphDataNode,ALEdge> specs = (TreeGraph<TreeGraphDataNode,ALEdge>) 
			GraphImporter.importGraph("initialState2.utg",this.getClass());
		SimulationSession s = new SimulationSession(specs);
		Ecosystem eco = (Ecosystem) specs.findNode("my_ecosystem");
		System.out.println(eco.getInstance().toString());	
		assertNotNull(s);
	}

}
