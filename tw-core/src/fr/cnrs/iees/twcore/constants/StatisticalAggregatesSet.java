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
* generated by CentralResourceGenerator on Tue Sep 24 11:13:07 CEST 2019
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Collection;
import java.util.EnumSet;

public class StatisticalAggregatesSet {

	private EnumSet<StatisticalAggregates> values = null;

	/** constructor from list of enum values */
	public StatisticalAggregatesSet(Collection<StatisticalAggregates> ag) {
		super();
		values = EnumSet.copyOf(ag);
	}

	/** constructor for an empty Set */
	public StatisticalAggregatesSet() {
		super();
		values = EnumSet.noneOf(StatisticalAggregates.class);
	}

	/** constructor from single enum value */
	public StatisticalAggregatesSet(StatisticalAggregates sa) {
		super();
		values = EnumSet.of(sa);
	}

	public static StatisticalAggregatesSet valueOf(String value) {
		String ss = value.substring(1,value.indexOf('}'));
		String [] sl = ss.split(",");
		StatisticalAggregatesSet e = new StatisticalAggregatesSet();
		for (String s:sl)
			e.values.add(StatisticalAggregates.valueOf(StatisticalAggregates.class,s.trim()));
		return e;
	}

	public EnumSet<StatisticalAggregates> values() {
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
		for (StatisticalAggregates sa:values) {
			sb.append(sa);
			i++;
			if (i<n) sb.append(',');
		}
		sb.append('}');
		return sb.toString();
	}

}

