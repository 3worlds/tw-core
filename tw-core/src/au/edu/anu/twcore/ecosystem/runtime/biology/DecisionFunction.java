package au.edu.anu.twcore.ecosystem.runtime.biology;

/**
 * An interface to make AbstractDecisionFunction.decide(...) (and only it) available to end-users
 *
 * @author J. Gignoux - 15 avr. 2020
 *
 */
public interface DecisionFunction {

	/**
	 * A function to make a decision based on a probablility. It draws a random number
	 * and returns true if the number is smaller than the proba argument, false otherwise.
	 * It may be used by end-users in their code, e.g.:
	 *
	 * @param proba the probability of the decision
	 * @return true with probability = proba
	 */
	public boolean decide(Double proba);

}
