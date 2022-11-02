package au.edu.anu.twcore.graphState;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class GraphStateTest {

	@Test
	void test() {
		GraphStateService.setImplementation(new SimpleGraphStateImpl());
		GraphStateService.getImplementation().addListener(new MockListener());
		GraphStateService.getImplementation().setChanged();
		
	}

}
