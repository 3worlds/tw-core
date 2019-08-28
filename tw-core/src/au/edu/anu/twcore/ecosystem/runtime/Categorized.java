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

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.endNode;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectOneOrMany;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrOne;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BELONGSTO;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_DRIVERS;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_RECORD;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_DYNAMIC;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.DynamicList;
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
import fr.ens.biologie.generic.SaveableAsText;

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
		return categories().containsAll(cs);
	}
	
	/** returns the category stamp of this instance for easy comparison */
	public Set<Category> categories();
	
	/** returns a string representation of the category set */
	public String categoryId();
		
	/** utility to work out a signature from a category list */
	public default String buildCategorySignature() {
		StringBuilder sb = new StringBuilder();
		Set<Category> set = categories();
		int i=0;
		for (Category c:set) {
			sb.append(c.name());
			if (i<set.size()-1)
				sb.append(CATEGORY_SEPARATOR);
			i++;
		}
		return sb.toString();
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
	public default Collection<Category> getSuperCategories(Collection<Category> cats) {
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
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
			edgeListEndNodes());
		Collection<Category> result = new LinkedList<Category>();
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
	 * @param system
	 *            the system for which the data merging is made
	 * @param categoryList
	 *            the list of categories to merge
	 * @param dataGroup
	 *            "drivers", "parameters" or "decorators" to specify which data
	 *            structure is built
	 * @return
	 */
	public static TreeGraphDataNode buildUniqueDataList(TreeGraphDataNode node, 
			String dataGroup) {
		TreeGraphDataNode mergedRoot = null;
		DynamicList<TreeGraphDataNode> roots = new DynamicList<TreeGraphDataNode>();
		Collection<Category> cats = getSuperCategories(node);
		for (Category cat:cats) {
			TreeGraphDataNode n = (TreeGraphDataNode) get(cat.edges(Direction.OUT), 
				selectZeroOrOne(hasTheLabel(dataGroup)), 
				endNode());
			if (n != null)
				roots.add(n);
		}
		NodeFactory factory = null;
		if (roots.size() >= 1) {
			mergedRoot = roots.iterator().next();
			factory = mergedRoot.factory();
		}
		if (roots.size() > 1) {
			// work out merged root name
			StringBuilder mergedRootName = new StringBuilder();
			for (TreeGraphDataNode n : roots)
				mergedRootName.append(n.id()).append('_');
			mergedRootName.append(dataGroup);
			// make a single root record and merge data requirements into it
			mergedRoot = (TreeGraphDataNode) factory.makeNode(Record.class,mergedRootName.toString());
			for (TreeGraphDataNode n:roots)
				if (n.classId().equals(N_RECORD.label()))
					mergedRoot.connectChildren(n.getChildren()); // caution: this changes the graph
				else
					mergedRoot.connectChild(n);
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
		// we need a URL classLoader here
		ClassLoader classLoader = OmugiClassLoader.getURLClassLoader();
		Class<? extends TwData> dataClass;
		try {
			dataClass = (Class<? extends TwData>) Class.forName(className, false, classLoader);
			Constructor<? extends TwData> dataConstructor = dataClass.getDeclaredConstructor();
			newData = dataConstructor.newInstance();
			newData.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return newData;
	}

}
