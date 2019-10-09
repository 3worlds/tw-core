package au.edu.anu.twcore.experiment.runtime.deployment;

import au.edu.anu.twcore.experiment.runtime.DeployerProcedures;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.statemachine.Procedure;

/**
 * Procedure executed when a simulator or deployer enters the 'waiting' state
 * 
 * @author Jacques Gignoux - 30 août 2019
 *
 */
public class WaitProcedure extends Procedure {

	@Override
	public void run(GridNode node, RVMessage message) {
		((DeployerProcedures)node).waitProc();
	}

	public WaitProcedure() {
		// TODO Auto-generated constructor stub
	}

}
