/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            *
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.runtime.biology;

/**
 * Ancestor class to all functions that make decisions based on probabilities
 *
 * @author Jacques Gignoux - 18 sept. 2019
 *
 */
public abstract class AbstractDecisionFunction extends TwFunctionAdapter
	implements DecisionFunction, SelectionFunction {

	/**
	 * constructor defining its own randm number stream
	 */
	public AbstractDecisionFunction() {
		super();
	}

	public final boolean decide(Double proba) {
		return (rng.nextDouble()<proba);
	}

	@Override
	public int select(double... weights) {
		double bounds[] = new double[weights.length];
		bounds[0] = weights[0];
		for (int i=1; i<weights.length; i++)
			bounds[i] = bounds[i-1]+weights[i];
		double totalWeight = bounds[bounds.length-1];
		int result=0;
		double proba = rng.nextDouble()*totalWeight;
		while (proba>=bounds[result])
			result++;
		// proba==1 is always possible and would return out of range index hence protection
		return Math.min(result,weights.length-1);
	}

}
