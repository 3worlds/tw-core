package au.edu.anu.twcore.rngFactory;

import java.util.Random;

import au.edu.anu.twcore.rngFactory.RngFactory.Generator;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;

/**
 * An interface for objects that hold a random number generator (RNG)
 * 
 * @author Jacques Gignoux - 5 févr. 2020
 *
 */
public interface RngHolder {

	/** The name of the 3worlds default RNG*/
	public static final String defRngName = "default 3wRNG";

	/** return the unique rng associated to the instance */
	public Random rng();

	/** sets the rng (meant to be used once per instance */
	public void setRng(Random rng);
	
	/** returns the same instance of the default 3Worlds RNG for a given index value */
	public default Random defaultRng(int index) {
		Generator gen = RngFactory.find(defRngName+":"+index);
		if (gen != null)
			return gen.getRandom();
		else {
			gen = RngFactory.newInstance(defRngName+":"+index, 0, RngResetType.never, 
				RngSeedSourceType.secure,RngAlgType.Pcg32);
			return gen.getRandom();
		}
	}

}
