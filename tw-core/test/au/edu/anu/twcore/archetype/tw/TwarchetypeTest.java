package au.edu.anu.twcore.archetype.tw;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import au.edu.anu.rscs.aot.archetype.CheckMessage;
import au.edu.anu.twcore.archetype.TwArchetype;
import fr.cnrs.iees.graph.impl.TreeGraph;
import fr.cnrs.iees.graph.io.GraphImporter;

/**
 * 
 * @author Jacques Gignoux - 29 mai 2019
 *
 */
class TwarchetypeTest {

	@Test
	void test() {
		TwArchetype a = new TwArchetype();
		TreeGraph<?,?> specs = (TreeGraph<?, ?>) GraphImporter.importGraph("testSpecs.utg",this.getClass());
		Iterable<CheckMessage> errors = a.checkSpecifications(specs);
		if (errors!=null) {
			System.out.println("There were errors in specifications: ");
			for (CheckMessage m:errors)
				System.out.println(m.toString()+"\n");
		}
		else 
			System.out.println("Specifications checked with no error.");
		System.out.println(specs.toDetailedString());
		assertNull(errors);
	}

}
