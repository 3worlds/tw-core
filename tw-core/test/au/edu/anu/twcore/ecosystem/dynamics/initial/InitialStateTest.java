package au.edu.anu.twcore.ecosystem.dynamics.initial;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.session.SimulationSession;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.GraphImporter;

class InitialStateTest {

	@SuppressWarnings("unchecked")
	@Test
	final void testInitialise() {
		TreeGraph<TreeGraphNode,ALEdge> specs = (TreeGraph<TreeGraphNode,ALEdge>) 
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
		TreeGraph<TreeGraphNode,ALEdge> specs = (TreeGraph<TreeGraphNode,ALEdge>) 
			GraphImporter.importGraph("initialState2.utg",this.getClass());
		SimulationSession s = new SimulationSession(specs);
		Ecosystem eco = (Ecosystem) specs.findNode("my_ecosystem");
		System.out.println(eco.getInstance().toString());	
		assertNotNull(s);
	}

}
