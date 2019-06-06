package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Initialisable;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/Process" node label in the 
 * 3Worlds configuration tree. Has no properties. 
 * 
 * @author Jacques Gignoux - 6 juin 2019
 *
 */
public class ProcessNode 
		extends TreeGraphNode 
		implements Initialisable, Singleton<TwProcess> {

	public ProcessNode(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
	}

	@Override
	public int initRank() {
		return N_PROCESS.initRank();
	}

	@Override
	public TwProcess getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
