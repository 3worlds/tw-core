package au.edu.anu.twcore.ecosystem.dynamics;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.session.SimulationSession;
import fr.cnrs.iees.graph.impl.ALEdge;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * 
 * @author Jacques Gignoux - 9 août 2019
 *
 */
class StoppingConditionNodeTest {
	
	private TreeGraph<TreeGraphNode,ALEdge> specs = null;
	
	@BeforeEach
	@SuppressWarnings("unchecked")
	private void init() {
		specs = (TreeGraph<TreeGraphNode,ALEdge>) 
				GraphImporter.importGraph("stoppingCondition.utg",this.getClass());
//		System.out.println(specs.toDetailedString());
	}

	@Test
	final void testInitialise() {
		// this should call initialise in proper order
		new SimulationSession(specs);
		for (TreeGraphNode nn:specs.nodes()) {
			InitialisableNode n = (InitialisableNode) nn;
			if (n instanceof StoppingConditionNode)
				assertNotNull(((StoppingConditionNode)n).getInstance());
		}
	}

	@Test
	final void testInitRank() {
		for (TreeGraphNode nn:specs.nodes()) {
			InitialisableNode n = (InitialisableNode) nn;
			System.out.println(n.classId()+":"+n.id()+":"+n.initRank());
			if (n.id().equals("A"))
				assertEquals(n.initRank(),10);
			if (n.id().equals("C"))
				assertEquals(n.initRank(),12);
			if (n.id().equals("I"))
				assertEquals(n.initRank(),10);
			if (n.id().equals("F"))
				assertEquals(n.initRank(),11);
			if (n.id().equals("E"))
				assertEquals(n.initRank(),10);
			if (n.id().equals("G"))
				assertEquals(n.initRank(),10);
		}
	}

}
