package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALGraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.LifespanType;
import fr.ens.biologie.generic.Factory;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.*;
import static au.edu.anu.twcore.ecosystem.runtime.system.SystemComponentPropertyListImpl.*;
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
		implements Factory<SystemComponent>, Categorized<SystemComponent>, Sealable {
	
	
	// the factory for SystemComponents and SystemRelations
	private static GraphFactory SCfactory = null;
	static {
		Map<String,String> labels = new HashMap<>();
		labels.put("component", "au.edu.anu.twcore.ecosystem.runtime.SystemComponent");
		labels.put("relation", "au.edu.anu.twcore.ecosystem.runtime.SystemRelation");
		SCfactory = new ALGraphFactory("3w",labels);
	}
	
	private SortedSet<Category> categories = new TreeSet<>();
	private String categoryId = null;
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
		categories.addAll(getSuperCategories(nl));
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
		if (driverTemplate != null)
			for (String key : driverTemplate.getKeysAsSet())
				propertyMap.put(key, DRIVERS);
		for (String key : SystemData.keySet)
			propertyMap.put(key, AUTO);
		if (decoratorTemplate != null)
			for (String key : decoratorTemplate.getKeysAsSet())
				propertyMap.put(key, DECO);
		sealed = true; // important - next statement access this class methods
		if (generateDataClasses) {
			// we reach here only if no data has been specified or no data class has been generated
			buildUniqueDataList(E_PARAMETERS.label());
			buildUniqueDataList(E_DRIVERS.label());
			buildUniqueDataList(E_DECORATORS.label());
		}
		categoryId = buildCategorySignature();
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
		SystemComponent result = (SystemComponent) SCfactory.makeNode(SystemComponent.class,"C0",props);
		result.setCategorized(this);
		return result;
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
	
	@Override
	public String categoryId() {
		if (sealed)
			return categoryId;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public SystemComponent clone(SystemComponent item) {
		SystemComponent result = newInstance();
		result.properties().setProperties(item.properties());
		return result;
	}
	
}
