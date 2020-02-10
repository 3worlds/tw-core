/*
*                    *** 3Worlds - A software for the simulation of ecosystems ***
*                    *                                                           *
*                    *        by:  Jacques Gignoux - jacques.gignoux@upmc.fr     *
*                    *             Ian D. Davies   - ian.davies@anu.edu.au       *
*                    *             Shayne R. Flint - shayne.flint@anu.edu.au     *
*                    *                                                           *
*                    *         http:// ???                                       *
*                    *                                                           *
*                    *************************************************************
* CAUTION: generated code - do not modify
* generated by CentralResourceGenerator on Mon Feb 10 15:11:37 CET 2020
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Collection;
import java.util.EnumSet;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

public class PopulationVariablesSet {

	private EnumSet<PopulationVariables> values = null;

	/** constructor from list of enum values */
	public PopulationVariablesSet(Collection<PopulationVariables> ag) {
		super();
		values = EnumSet.copyOf(ag);
	}

	/** constructor for an empty Set */
	public PopulationVariablesSet() {
		super();
		values = EnumSet.noneOf(PopulationVariables.class);
	}

	/** constructor from single enum value */
	public PopulationVariablesSet(PopulationVariables sa) {
		super();
		values = EnumSet.of(sa);
	}

	public static PopulationVariablesSet valueOf(String value) {
		String ss = value.substring(1,value.indexOf('}'));
		String [] sl = ss.split(",");
		PopulationVariablesSet e = new PopulationVariablesSet();
		for (String s:sl)
			e.values.add(PopulationVariables.valueOf(PopulationVariables.class,s.trim()));
		return e;
	}

	public EnumSet<PopulationVariables> values() {
		return values;
	}

	@Override
	public boolean equals(Object o) {
		return values.equals(o);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('{');
		int n = values.size();
		int i=0;
		for (PopulationVariables sa:values) {
			sb.append(sa);
			i++;
			if (i<n) sb.append(',');
		}
		sb.append('}');
		return sb.toString();
	}

	public static PopulationVariablesSet defaultValue() {
		return new PopulationVariablesSet(PopulationVariables.defaultValue());
	}

	static {
		ValidPropertyTypes.recordPropertyType(PopulationVariablesSet.class.getSimpleName(),
			PopulationVariablesSet.class.getName(),defaultValue());
	}

}

