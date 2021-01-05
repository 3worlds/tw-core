package au.edu.anu.twcore.ecosystem.runtime.biology;

/**
 * A function for multinomial decision making
 *
 * @author J. Gignoux - 5 janv. 2021
 *
 */
public interface SelectionFunction {

	/**
	 * A function to make a selection based on a set of weights. It draws a random number
	 * and returns the index i such that weights[i-1]<=proba<weights[i]
	 * It may be used by end-users in their code, e.g.:
	 *
	 * @param proba the probability of the decision
	 * @return true with probability = proba
	 */
	public int select(double...weights);

}
