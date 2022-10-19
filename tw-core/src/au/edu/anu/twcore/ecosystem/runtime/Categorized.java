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
package au.edu.anu.twcore.ecosystem.runtime;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.rscs.aot.collections.DynamicList;
import au.edu.anu.omugi.graph.property.Property;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.NodeFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SimplePropertyListImpl;
import fr.cnrs.iees.omhtk.SaveableAsText;

/**
 * To be associated to objects of class T sorted by category
 *
 * @author Jacques Gignoux - 23 avr. 2013
 *
 */
public interface Categorized<T extends Identity> {

	public static final char CATEGORY_SEPARATOR = SaveableAsText.COLON;

	/** checks if this instance belongs to all categories specified in the argument */
	public default boolean belongsTo(Set<Category> cs) {
		if (categories()==null)
			return false;
		return categories().containsAll(cs);
	}

	/** checks if candidate signature belongs to reference */
	public static boolean belongsTo(String candidate, String reference) {
		if ((reference==null)||reference.isBlank())
			return false;
		String sep =  Character.toString(CATEGORY_SEPARATOR);
		String[] cands = candidate.split(sep);
		for (int i=0; i<cands.length; i++)
			if (!reference.contains(cands[i]))
				return false;
		return true;
	}


	/** returns the category stamp of this instance for easy comparison */
	public Set<Category> categories();

	/** returns a string representation of the category set */
	public String categoryId();

	/** utility to work out a signature from a category list */
	public default String buildCategorySignature() {
		return Categorized._signature(categories());
	}

	/** if data structures are associated to the categories, return them based on a
	 * type selector - eg "parameters" and "drivers" or "variables"...
	 * */
	public default ReadOnlyPropertyList newDataStructure(String type) {
		return null;
	}

	/**
	 * Climbs up the category tree to get all the categories this object is nested
	 * in (helper method for below).
	 *
	 * RECURSIVE
	 */
	private static void getSuperCategories(Category cat,Collection<Category> result) {
		CategorySet partition = (CategorySet) cat.getParent();
		TreeNode tgn = partition.getParent();
		if (tgn instanceof Category) {
			Category superCategory = (Category) tgn;
			if (superCategory!=null) {
				result.add(superCategory);
				getSuperCategories(superCategory,result);
			}
		}
	}

	/**
	 * Given a list of {@link Category} objects, gets all the super-categories in which they are nested
	 * and returns the full list of all categories. Use this to setup the category list associated
	 * to a Categorized object.
	 *
	 * @param cats the initial category list
	 * @return the final category list, including all nesting super-categories
	 */
	public static Collection<Category> getSuperCategories(Collection<Category> cats) {
		Collection<Category> result = new LinkedList<Category>();
		result.addAll(cats);
		for (Category cat:cats)
			getSuperCategories(cat,result);
		return result;
	}

	/**
	 * Static method to build the full category list of any node having 'belongsTo' edges
	 * to categories
	 *
	 * @param node
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection<Category> getSuperCategories(TreeGraphDataNode node) {
		Collection<Category> cats = (Collection<Category>) get(node.edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())),
			edgeListEndNodes());
		Collection<Category> result = new TreeSet<Category>();
		result.addAll(cats);
		for (Category cat:cats)
			getSuperCategories(cat,result);
		return result;
	}


	/**
	 * <p>Utility to build a data structure from the category list of this Categorized object.
	 * Returns the root node of the (tree) data structure constructed by merging all
	 * categories. The recipe is: if only one root node, return it, if no root node,
	 * return null; if more than one root node, create a root record put into it
	 * every non-record sub-data node and for every record sub-data node, put all
	 * its components in.
	 * </p>
	 *
	 * @param node
	 *            the (ElementType) node for which the data merging is made
	 * @param dataGroup
	 *            "drivers", "parameters" or "decorators" to specify which data
	 *            structure is built
	 * @param log
	 *            a valid logger
	 * @return a specification node either matching a single record in case a single category
	 * is set, otherwise merging the fields of all category records into one record. May be <strong>null</strong>
	 * if no record was associated to categories
	 */
	public static TreeGraphDataNode buildUniqueDataList(TreeGraphDataNode node,
			String dataGroup,Logger log) {
		log.info("Building unique data list from categories for "+node.toShortString());
		TreeGraphDataNode mergedRoot = null;
		DynamicList<TreeGraphDataNode> roots = new DynamicList<TreeGraphDataNode>();
		// find all categories above those the node belongs to
		Collection<Category> cats = getSuperCategories(node);
		// put them in roots in category hierarchy order !
		for (Category cat:cats) {
			TreeGraphDataNode n = (TreeGraphDataNode) get(cat.edges(Direction.OUT),
				selectZeroOrOne(hasTheLabel(dataGroup)),
				endNode());
			if (n != null)
				roots.add(n);
		}
		NodeFactory factory = null;
		// use the first root id as the generated class name by default
		if (roots.size() >= 1) {
			mergedRoot = roots.iterator().next();
			factory = mergedRoot.factory();
			((ExtendablePropertyList)mergedRoot.properties()).addProperty(
				new Property(P_TWDATACLASS.key(),mergedRoot.id()) );
		}
		// if more than one root present, generate new name and merge all record fields into one record
		if (roots.size() > 1) {
			// work out merged root name
			StringBuilder mergedRootName = new StringBuilder();
			for (TreeGraphDataNode n : roots)
				mergedRootName.append(n.id()).append('_');
			mergedRootName.append(dataGroup);
			// make a single root record and merge data requirements into it
			mergedRoot = (TreeGraphDataNode) factory.makeNode(Record.class,mergedRootName.toString());
			((ExtendablePropertyList)mergedRoot.properties()).addProperty(
				new Property(P_TWDATACLASS.key(),mergedRootName.toString())) ;
			for (TreeGraphDataNode n:roots)
				// root content is a record
				if (n.classId().equals(N_RECORD.label())) {
					for (TreeNode c:n.getChildren()) {
						SimplePropertyList pl = new SimplePropertyListImpl("type");
						pl.setProperty("type", "forCodeGeneration");
						mergedRoot.connectTo(Direction.OUT,c, pl);
					}
				} 
			// root content is a table
				else {
					SimplePropertyList pl = new SimplePropertyListImpl("type");
					pl.setProperty("type", "forCodeGeneration");
					mergedRoot.connectTo(Direction.OUT,n, pl);
				}
			((ExtendablePropertyList)mergedRoot.properties()).addProperty("generated", true);
		}
		if (mergedRoot != null)
			if (dataGroup.equals(E_DRIVERS.label()))
				((ExtendablePropertyList)mergedRoot.properties()).addProperty(P_DYNAMIC.key(), true);
			else
				((ExtendablePropertyList)mergedRoot.properties()).addProperty(P_DYNAMIC.key(), false);
		return mergedRoot;
	}

	// utility for descendants
	@SuppressWarnings("unchecked")
	public default TwData loadDataClass(String className) {
		TwData newData = null;
		ClassLoader classLoader = OmugiClassLoader.getJarClassLoader();
		Class<? extends TwData> dataClass;
		try {
			dataClass = (Class<? extends TwData>) Class.forName(className, true, classLoader);
//			if (!dataClass.isInterface()) {
				Constructor<? extends TwData> dataConstructor = dataClass.getDeclaredConstructor();
				newData = dataConstructor.newInstance();
				newData.clear();
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newData;
	}

	public static String signature(SortedSet<Category> set) {
		return _signature(set);
	}

	private static String _signature(Set<Category> set) {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (Category c:set) {
			sb.append(c.name());
			if (i<set.size()-1)
				sb.append(CATEGORY_SEPARATOR);
			i++;
		}
		return sb.toString();
	}

}
