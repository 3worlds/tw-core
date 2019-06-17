package au.edu.anu.twcore;

import java.util.logging.Logger;
import java.util.logging.Level;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.Initialisable;

/**
 * An ancestor to configuration nodes - mainly here to deal with the logging
 * 
 * @author Jacques Gignoux - 17 juin 2019
 *
 */
public abstract class InitialisableNode extends TreeGraphDataNode implements Initialisable {
	
	protected Logger log = Logger.getLogger(this.getClass().getName());

	protected InitialisableNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		// change this to get less output
		log.setLevel(Level.INFO);
	}

	// descendants must call super.initialise() to get any logging done
	@Override
	public void initialise() {
		log.info(initRank()+": initialising "+toShortString());
	}

}
