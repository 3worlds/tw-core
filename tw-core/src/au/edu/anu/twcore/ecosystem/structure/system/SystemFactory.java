package au.edu.anu.twcore.ecosystem.structure.system;

import au.edu.anu.rscs.aot.collections.DynamicList;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.Record;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.ecosystem.structure.CategorySet;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.ALGraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.twcore.ecosystem.structure.system.SystemComponentPropertyListImpl.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
	
	// the factory for SystemComponents and SystemRelations
	private static GraphFactory SCfactory = null;
	static {
		Map<String,String> labels = new HashMap<>();
		labels.put("component", "au.edu.anu.twcore.ecosystem.runtime.SystemComponent");
		labels.put("relation", "au.edu.anu.twcore.ecosystem.runtime.SystemRelation");
		SCfactory = new ALGraphFactory("3w",labels);
	}
	
	private SortedSet<Category> categories = new TreeSet<>();
	private boolean sealed = false;
	private boolean permanent;
	/** TwData templates to clone to create new systems */
	private TwData parameterTemplate = null;
	private TwData driverTemplate = null;
	private TwData decoratorTemplate = null;
	private Map<String, Integer> propertyMap = new HashMap<String, Integer>();

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
			buildUniqueDataList(E_PARAMETERS.label());
			buildUniqueDataList(E_DRIVERS.label());
			buildUniqueDataList(E_DECORATORS.label());
		}
		if (driverTemplate != null)
			for (String key : driverTemplate.getKeysAsSet())
				propertyMap.put(key, DRIVERS);
		for (String key : SystemData.keySet)
			propertyMap.put(key, AUTO);
		if (decoratorTemplate != null)
			for (String key : decoratorTemplate.getKeysAsSet())
				propertyMap.put(key, DECO);
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
			e.printStackTrace();
		}
		return newData;
	}

	@Override
	public int initRank() {
		return N_COMPONENT.initRank();
	}

	/**
	 * 
	 * @return a new SystemComponent with the proper data structure
	 */
	@Override
	public final SystemComponent newInstance() {
		SimplePropertyList props = new SystemComponentPropertyListImpl(driverTemplate,
			decoratorTemplate,2,propertyMap);
		return (SystemComponent) SCfactory.makeNode(SystemComponent.class,"C0",props);
	}

	/** returns a new parameterSet of the proper structure for this SystemFactory */
	public final TwData newParameterSet() {
		if (parameterTemplate != null)
			return parameterTemplate.clone().clear();
		else
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
		TreeNode tgn = partition.getParent();
		if (tgn instanceof Category) {
			Category superCategory = (Category) tgn;
			if (superCategory!=null) {
				categories.add(superCategory);
				getSuperCategories(superCategory);
		}
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
	private TreeGraphDataNode buildUniqueDataList(String dataGroup) {
		TreeGraphDataNode mergedRoot = null;
		DynamicList<TreeGraphDataNode> roots = new DynamicList<TreeGraphDataNode>();
		for (Category cat : categories) {
			TreeGraphDataNode n = (TreeGraphDataNode) get(cat.edges(Direction.OUT), 
				selectZeroOrOne(hasTheLabel(dataGroup)), 
				endNode());
			if (n != null)
				roots.add(n);
		}
		if (roots.size() == 1)
			mergedRoot = roots.iterator().next();
		else if (roots.size() > 1) {
			// work out merged root name
			StringBuilder mergedRootName = new StringBuilder();
			for (TreeGraphDataNode n : roots)
				mergedRootName.append(n.id()).append('_');
			mergedRootName.append(dataGroup);
			// make a single root record and merge data requirements into it
			mergedRoot = (TreeGraphDataNode) factory().makeNode(Record.class,mergedRootName.toString());
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
	
}
