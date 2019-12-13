package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.util.Random;

import au.edu.anu.twcore.rngFactory.RngFactory;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;

/**
 * 
 * @author Jacques Gignoux - 19 sept. 2019
 *
 */

// Ian: I'm not sure if this is necessary. Any nodes that require an rng, must have an edge to one in the DataDefinition.
// TODO modify the arch to reflect this.
public abstract class TwRandomStreamFunction extends TwFunctionAdapter {

	Random rng = null;

	/**
	 * Constructor using a random number stream
	 * @param rng
	 */
	public TwRandomStreamFunction(Random rng) {
		super();
		this.rng = rng;
	}
	
	/**
	 * constructor defining its own randm number stream
	 */
	public TwRandomStreamFunction() {
		super();
		// cf au.edu.anu.twcore.data.RngNode.java. It copies the DimNode pattern.
		if (!RngFactory.exists("default 3wRNG"))
			RngFactory.makeRandom("default 3wRNG", 0, RngResetType.never, RngSeedSourceType.table, RngAlgType.Pcg32);
		this.rng = RngFactory.getRandom("default 3wRNG");
	}
	
	public Random rng() {
		return rng;
	}

}
