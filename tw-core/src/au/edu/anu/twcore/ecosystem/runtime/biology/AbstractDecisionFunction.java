package au.edu.anu.twcore.ecosystem.runtime.biology;

/**
 * Ancestor class to all functions that make decisions based on probabilities
 * 
 * @author Jacques Gignoux - 18 sept. 2019
 *
 */
public abstract class AbstractDecisionFunction extends TwFunctionAdapter {

	/**
	 * constructor defining its own randm number stream
	 */
	public AbstractDecisionFunction() {
		super();
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
