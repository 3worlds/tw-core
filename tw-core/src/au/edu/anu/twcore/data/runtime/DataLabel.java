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

import java.util.*;

import static fr.cnrs.iees.omhtk.SaveableAsText.*;

/**
 * A class for hierarchical names
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class DataLabel implements Comparable<DataLabel>, Cloneable {
	private static final int HIERARCHYix = 3;
	/**
	 * Hierarchy down direction character.
	 */
	public static final String HIERARCHY_DOWN = Character.toString(BLOCK_DELIMITERS[HIERARCHYix][BLOCK_CLOSE]);
	/**
	 * Hierarchy up direction character.
	 */
	public static final String HIERARCHY_UP = Character.toString(BLOCK_DELIMITERS[HIERARCHYix][BLOCK_OPEN]);
	protected List<String> label = new ArrayList<String>();

	/**
	 * Default constructor
	 */
	public DataLabel() {
		super();
	}

	/**
	 * Constructor with variable parts args.
	 * 
	 * @param labelParts variable list of parts.
	 */
	public DataLabel(String... labelParts) {
		super();
		for (String lab : labelParts)
			label.add(lab);
	}

	/**
	 * Constructor from a collection of parts.
	 * 
	 * @param labelParts parts collection.
	 */
	public DataLabel(Collection<String> labelParts) {
		super();
		for (String lab : labelParts)
			label.add(lab);
	}

	/**
	 * Append a collection of parts.
	 * 
	 * @param labelParts parts collection.
	 */
	public void append(Collection<String> labelParts) {
		label.addAll(labelParts);
	}

	/**
	 * Append from a variable list of parts.
	 * 
	 * @param labelParts parts array.
	 */
	public void append(String... labelParts) {
		for (String lab : labelParts)
			label.add(lab);
	}

	/**
	 * @return The number of label parts.
	 */
	public int size() {
		return label.size();
	}

	/**
	 * Getter for part by index.
	 * 
	 * @param i index of requrested part.
	 * @return requested part of null if index is out-of-range.
	 */
	public String get(int i) {
		if ((i >= 0) && (i < label.size()))
			return label.get(i);
		return null;
	}

	/**
	 * Remove the last part of the list.
	 */
	public void stripEnd() {
		label.remove(label.size() - 1);
	}

	/**
	 * @return the last part of the list.
	 */
	public String getEnd() {
		return label.get(label.size() - 1);
	}

	/**
	 * @return toString() without the first part unless there is only one part.
	 */
	public String toStringSkipRoot() {
		StringBuilder sb = new StringBuilder();
		if (label.size() == 1)
			return label.get(0);
		for (int i = 1; i < label.size(); i++) {
			sb.append(label.get(i));
			if (i < label.size() - 1)
				sb.append(HIERARCHY_DOWN);
		}
		return sb.toString();
	}

//	/**
//	 * Replace all parts of the label with length>1 with ellipsis. e.g. if l = 2 abc
//	 * = ab... where the ellipsis is one char
//	 */
//	@Deprecated // TODO I don't think this is needed any longer
//	public String toAbbreviatedString(int l) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < label.size(); i++) {
//			sb.append(StringUtils.abbreviate(label.get(i), l));
//			if (i < label.size() - 1)
//				if (i < label.size() - 1)
//					sb.append(HIERARCHY_DOWN);
//		}
//		return sb.toString();
//	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < label.size(); i++) {
			sb.append(label.get(i));
			if (i < label.size() - 1)
				sb.append(HIERARCHY_DOWN);
		}
		return sb.toString();
	}

	private String lazyString = null;

	/**
	 * 
	 * The toString() can be an efficiency problem if called often without there
	 * being any underlying change. Use this method to avoid frequent calls to
	 * toString()
	 * 
	 * @return String value
	 */
	public String toLazyString() {
		if (lazyString == null)
			lazyString = toString();
		return lazyString;
	}

	// slow ?
	@Override
	public int compareTo(DataLabel o) {
		return toString().compareTo(o.toString());
	}

	/**
	 * Returns a data label formed from a 'hierarchical description' String
	 * 
	 * @param text the hierarchical String, i.e. containing '>' separating String
	 *             items
	 * @return the matching DataLabel
	 */
	public static DataLabel valueOf(String text) {
		return new DataLabel(text.split(HIERARCHY_DOWN));
	}

	@Override
	public DataLabel clone() {
		return new DataLabel(label);
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof DataLabel) {
			DataLabel dl = (DataLabel) obj;
			if (label.size() != dl.label.size())
				return false;
			boolean result = true;
			for (int i = 0; i < label.size(); i++) {
				if (label.get(i) == null) {
					if (dl.label.get(i) == null)
						result &= true;
					else
						return false;
				} else {
					if (label.get(i).equals(dl.label.get(i)))
						result &= true;
					else
						return false;
				}
			}
			return result;
		}
		return false;
	}

}
