package au.edu.anu.twcore.experiment.runtime;

import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.rvgrid.rendezvous.RVMessage;
import fr.cnrs.iees.rvgrid.statemachine.Procedure;

/**
 * 
 * @author Jacques Gignoux - 3 sept. 2019
 *
 */
public class QuitProcedure extends Procedure {

	public QuitProcedure() { }

	@Override
	public void run(GridNode node, RVMessage message) {
		((DeployerProcedures)node).quitProc();
	}

}
