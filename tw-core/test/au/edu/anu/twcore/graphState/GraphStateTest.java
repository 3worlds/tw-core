package au.edu.anu.twcore.graphState;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GraphStateTest {

	@Test
	void test() {
		GraphStateService.setImplementation(new SimpleGraphStateImpl());
		GraphState gs = GraphStateService.getImplementation();
		gs.addListener(new MockListener());
		gs.setChanged();
		assertTrue(gs.changed());
		gs.clear();
		assertFalse(gs.changed());
	}

}
