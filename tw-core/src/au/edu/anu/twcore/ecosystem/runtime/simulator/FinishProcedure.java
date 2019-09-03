package au.edu.anu.twcore.ecosystem.runtime.simulator;

import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.statemachine.Procedure;

/**
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public class FinishProcedure extends Procedure {

	public FinishProcedure() { }

	@Override
	public void run(GridNode node, RVMessage message) {
		((SimulatorProcedures)node).finishProc();
	}

}
