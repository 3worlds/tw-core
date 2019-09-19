package au.edu.anu.twcore.ecosystem.runtime.biology;

import java.security.SecureRandom;
import java.util.Random;

import au.edu.anu.omhtk.rng.Pcg32;
import au.edu.anu.omhtk.rng.RngFactory;
import au.edu.anu.omhtk.rng.RngFactory.ResetType;

/**
 * Ancestor class to all functions that make decisions based on probabilities
 * 
 * @author Jacques Gignoux - 18 sept. 2019
 *
 */
public class AbstractDecisionFunction extends TwFunctionAdapter {

	private Random rng = null;
	
	/**
	 * Constructor using a random number stream
	 * @param rng
	 */
	public AbstractDecisionFunction(Random rng) {
		super();
		this.rng = rng;
	}

	/**
	 * constructor defining its own randm number stream
	 */
	public AbstractDecisionFunction() {
		super();
		RngFactory.makeRandom("default 3wRNG", 0, ResetType.NEVER, new Pcg32());
		this.rng = RngFactory.getRandom("default 3wRNG");
	}

	/**
	 * A function to make a decision based on a probablility. It draws a random number
	 * and returns true if the number is smaller than the proba argument, false otherwise.
	 * It may be used by end-users in their code, e.g.:
	 * 
	 * @param proba the probability of the decision
	 * @return true with probability = proba
	 */
	public final boolean decide(Double proba) {
		return (rng.nextDouble()<proba);
	}

}
