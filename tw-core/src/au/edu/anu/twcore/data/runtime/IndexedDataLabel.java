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
package au.edu.anu.twcore.data.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import au.edu.anu.omugi.collections.tables.IndexString;

/**
 * A specialized data label class for items that may contain a SIMPLE index
 * String (ie not one using the index syntax of StringIndex)
 * 
 * @author Jacques Gignoux - 30 oct. 2019
 *
 */
public class IndexedDataLabel extends DataLabel {

	private List<int[]> index = new ArrayList<int[]>();

	/**
	 * Default constructor
	 */
	public IndexedDataLabel() {
		super();
	}

	/**
	 * Constructor for a DataLabel.
	 * 
	 * @param lab The initial DataLabel
	 */
	public IndexedDataLabel(DataLabel lab) {
		super();
		append(lab.label);
		resetIndexes();
	}

	/**
	 * Constructor with variable parts args.
	 * 
	 * @param labelParts variable list of parts.
	 */
	public IndexedDataLabel(String... labelParts) {
		super(labelParts);
		resetIndexes();
	}

	/**
	 * Constructor from a collection of parts.
	 * 
	 * @param labelParts parts collection.
	 */
	public IndexedDataLabel(Collection<String> labelParts) {
		super(labelParts);
		resetIndexes();
	}

	private void resetIndexes() {
		index.clear();
		for (String s : label) {
			if (s.contains("[")) {
				String ixs = s.substring(s.indexOf("["));
				ixs = ixs.substring(1, ixs.length() - 1);
				String[] ss = ixs.split(",", -1);
				int[] ix = new int[ss.length];
				for (int i = 0; i < ix.length; i++)
					ix[i] = Integer.valueOf(ss[i]);
				index.add(ix);
			} else
				index.add(null);
		}
	}

	/**
	 * Get the ith value.
	 * 
	 * @param i Index.
	 * @return value at i
	 */
	public int[] getIndex(int i) {
		if ((i >= 0) && (i < index.size()))
			return index.get(i);
		return null;
	}

	@Override
	public void append(Collection<String> labelParts) {
		super.append(labelParts);
		resetIndexes();
	}

	@Override
	public void append(String... labelParts) {
		super.append(labelParts);
		resetIndexes();
	}

	@Override
	public IndexedDataLabel clone() {
		return new IndexedDataLabel(label);
	}

	/**
	 * Returns a Indexed data label formed from a 'hierarchical description' String
	 * 
	 * @param text the hierarchical String, i.e. containing '>' separating String
	 *             items
	 * @return the matching Indexed data label.
	 */
	public static IndexedDataLabel valueOf(String text) {
		return new IndexedDataLabel(text.split(HIERARCHY_DOWN));
	}

	// helper for below (recursive)
	private static List<IndexedDataLabel> mergeLabels(List<IndexedDataLabel> list1, List<IndexedDataLabel> list2) {
		List<IndexedDataLabel> result = new ArrayList<>();
		if (list2 != null)
			for (IndexedDataLabel dl1 : list1)
				for (IndexedDataLabel dl2 : list2) {
					IndexedDataLabel dl = dl1.clone();
					dl.append(dl2.label);
					result.add(dl);
				}
		else
			result = list1;
		return result;
	}

	// helper for below
	private static List<IndexedDataLabel> mergeLabels(List<List<IndexedDataLabel>> list) {
		List<IndexedDataLabel> result = list.get(0);
		for (int i = 1; i < list.size(); i++)
			result = mergeLabels(result, list.get(i));
		return result;
	}

	/**
	 * Returns a list of DataLabel when the input contains expandable index
	 * specifications
	 * 
	 * @param multiLabel a datalabel with possible table indices in there
	 * @param dims       the dimensions matching levels of the Label where dims are
	 *                   needed, in the same order
	 * @return
	 */
	public static List<IndexedDataLabel> expandIndexes(DataLabel multiLabel, Map<String, int[]> dims) {
		List<List<IndexedDataLabel>> result = new ArrayList<>();
		for (int i = 0; i < multiLabel.size(); i++)
			result.add(new ArrayList<IndexedDataLabel>());
		for (int k = 0; k < multiLabel.label.size(); k++) {
			String s = multiLabel.label.get(k);
			int[][] index = null;
			String ix = "";
			if (s.contains("[")) {
				ix = s.substring(s.indexOf("["));
				s = s.substring(0, s.indexOf("["));
			}
			if (dims != null) {
				if (dims.containsKey(s)) { // means this variable is a table
					index = IndexString.stringToIndex(ix, dims.get(s));
					for (int j = 0; j < index.length; j++) {
						result.get(k).add(new IndexedDataLabel(s + Arrays.toString(index[j]).replace(" ", "")));
					}
				} else
					result.get(k).add(new IndexedDataLabel(s));
			} else
				result.get(k).add(new IndexedDataLabel(s));
		}
		return mergeLabels(result);
	}

}
