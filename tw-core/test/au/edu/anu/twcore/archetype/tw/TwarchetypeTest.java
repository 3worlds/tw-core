package au.edu.anu.twcore.archetype.tw;

import org.junit.jupiter.api.Test;

import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.TwArchetype;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.io.GraphImporter;

class TwarchetypeTest {

	@Test
	void test() {
		TwArchetype a = new TwArchetype();
		TreeGraph<?,?> specs = (TreeGraph<?, ?>) GraphImporter.importGraph("testSpecs.utg",this.getClass());
		Iterable<CheckMessage> errors = a.checkSpecifications(specs);
		if (errors!=null)
			for (CheckMessage m:errors)
				System.out.println(m);
		System.out.println(specs.toDetailedString());
	}

}
