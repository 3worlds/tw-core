package au.edu.anu.twcore.ecosystem.runtime.simulator;

import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.statemachine.Procedure;

/**
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public class StepProcedure extends Procedure {

	public StepProcedure() {	}

	@Override
	public void run(GridNode node, RVMessage message) {
		((SimulatorProcedures)node).stepProc();
	}

}
