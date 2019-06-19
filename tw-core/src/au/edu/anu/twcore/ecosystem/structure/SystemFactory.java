package au.edu.anu.twcore.ecosystem.structure;

import au.edu.anu.rscs.aot.collections.DynamicList;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.SystemComponent;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels;
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.cnrs.iees.twcore.constants.ThreeWorldsGraphReference;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class matching the "ecosystem/structure/component" node label in the 3Worlds configuration tree.
 * Factory for system components, ie the simulated items.
 * 
 * @author Jacques Gignoux - 25 avr. 2013
 *
 */
public class SystemFactory 
		extends InitialisableNode 
		implements Factory<SystemComponent>, Categorized, Sealable {
	
	private SortedSet<Category> categories = new TreeSet<>();
	private boolean sealed = false;
	private boolean permanent;
	/** TwData templates to clone to create new systems */
	private TwData parameterTemplate = null;
	private TwData driverTemplate = null;
	private TwData decoratorTemplate = null;

	public SystemFactory(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SystemFactory(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		Collection<Category> nl = (Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
			edgeListEndNodes());
		categories.addAll(nl);
		getSuperCategories();
		permanent = ((LifespanType) properties().getPropertyValue(P_COMPONENT_LIFESPAN.key()))==LifespanType.permanent;
		sealed = true;
		// These ARE optional - inserted by codeGenerator!
		boolean generateDataClasses = true;
		if (properties().hasProperty(P_PARAMETERCLASS.key())) {
			parameterTemplate = loadDataClass((String) properties().getPropertyValue(P_PARAMETERCLASS.key()));
			generateDataClasses = false;
		}
		if (properties().hasProperty(P_DRIVERCLASS.toString())) {
			driverTemplate = loadDataClass((String) properties().getPropertyValue(P_DRIVERCLASS.key()));
			generateDataClasses = false;
		}
		if (properties().hasProperty(P_DECORATORCLASS.toString())) {
			decoratorTemplate = loadDataClass((String) properties().getPropertyValue(P_DECORATORCLASS.key()));
			generateDataClasses = false;
		}
		if (generateDataClasses) {
			// we reach here only if no data has been specified or no data class has been generated
			getDataTree(E_PARAMETERS);
			getDataTree(E_DRIVERS);
			getDataTree(E_DECORATORS);
		}
	}

	@SuppressWarnings("unchecked")
	private TwData loadDataClass(String className) {
		TwData newData = null;
		ClassLoader c = Thread.currentThread().getContextClassLoader();
		Class<? extends TwData> dataClass;
		try {
			dataClass = (Class<? extends TwData>) Class.forName(className, false, c);
			Constructor<? extends TwData> dataConstructor = dataClass.getDeclaredConstructor();
			newData = dataConstructor.newInstance();
			newData.clear();
		} catch (Exception e) {
			// return null;
			e.printStackTrace();
		}
		return newData;
	}

	@Override
	public int initRank() {
		return N_COMPONENT.initRank();
	}

	@Override
	public SystemComponent newInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Category> categories() {
		if (sealed)
			return categories;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	public boolean isPermanent() {
		if (sealed)
			return permanent;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	
	/**
	 * climbs up the category tree to get all the categories this system is nested
	 * in 
	 *  
	 * RECURSIVE
	 */
	private void getSuperCategories(Category cat) {
		CategorySet partition = (CategorySet) cat.getParent();
		Category superCategory = (Category) partition.getParent();
		if (superCategory!=null) {
			categories.add(superCategory);
			getSuperCategories(superCategory);
		}
	}
	@SuppressWarnings("unchecked")
	private void getSuperCategories() {
		for (Category cat:(Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
			edgeListEndNodes()))
		getSuperCategories(cat);
	}

	/**
	 * <p>
	 * returns the root node of the (tree) data structure constructed by merging all
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
	@SuppressWarnings("unchecked")
	private ALNode buildUniqueDataList(String dataGroup) {
		ALNode mergedRoot = null;
		DynamicList<ALNode> roots = new DynamicList<ALNode>();
		for (Category cat : categories) {
			ALNode n = (ALNode) get(cat.edges(Direction.OUT), 
				selectZeroOrOne(hasTheLabel(dataGroup)), 
				endNode());
			if (n != null)
				roots.add(n);
		}
		if (roots.size() == 1)
			mergedRoot = roots.iterator().next();
		
// TODO: work in progress		
		
//		else if (roots.size() > 1) {
//			mergedRoot = new AotNode().setLabel(N_RECORD.toString());
//			mergedRoot = new Record();
//			mergedRoot.addProperty("generated", true);
//			String mergedRootName = "";
//			for (AotNode n : roots) {
//				mergedRootName += n.getName() + " ";
//				if (n.getLabel().equals(N_RECORD.toString())) {
//					Iterable<AotNode> nl = (Iterable<AotNode>) get(n.getEdges(Direction.OUT), edgeListEndNodes(),
//							selectZeroOrMany(orQuery(hasTheLabel(N_RECORD.toString()), hasTheLabel(N_FIELD.toString()),
//									hasTheLabel(N_TABLE.toString()))));
//					for (AotNode nn : nl)
//						new AotEdge(mergedRoot, nn).setLabel(Trees.CHILD_LABEL);
//				} else {
//					new AotEdge(mergedRoot, n).setLabel(Trees.CHILD_LABEL);
//				}
//			}
//			mergedRoot.setName(NameUtils.wordUpperCaseName(mergedRootName + " " + dataGroup));
//		}
//		if (mergedRoot != null)
//			if (dataGroup.equals(E_DRIVERS.toString()))
//				mergedRoot.addProperty(P_DYNAMIC.toString(), true);
//			else
//				mergedRoot.addProperty(P_DYNAMIC.toString(), false);
		return mergedRoot;
	}

	/**
	 * assembles a tree describing the data structure needed for
	 * parameters/drivers/decorators for this system, based on its category
	 * membership and on the hierarchy between categories
	 * 
	 * @return the root node of the data structure tree
	 */
	private ALNode getDataTree(ConfigurationEdgeLabels edgeLabel) {
		ALNode tree = null;
		tree = buildUniqueDataList(edgeLabel.label());
		return tree;
	}

	
}
