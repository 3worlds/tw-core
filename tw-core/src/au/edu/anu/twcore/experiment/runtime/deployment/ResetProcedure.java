package au.edu.anu.twcore.experiment.runtime.deployment;

import au.edu.anu.twcore.experiment.runtime.DeployerProcedures;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.statemachine.Procedure;

/**
 * 
 * @author Jacques Gignoux - 11 mars 2020
 *
 */
public class ResetProcedure extends Procedure {

	public ResetProcedure() {	}
	
	@Override
	public void run(GridNode node, RVMessage message) {
		((DeployerProcedures)node).resetProc();
	}


}
