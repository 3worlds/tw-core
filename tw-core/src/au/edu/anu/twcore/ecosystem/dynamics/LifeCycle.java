package au.edu.anu.twcore.ecosystem.dynamics;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_DRIVERCLASS;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_PARAMETERCLASS;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Logger;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * Class matching the "ecosystem/dynamics/lifeCycle" node label in the 
 * 3Worlds configuration tree. Has no properties.
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class LifeCycle 
		extends InitialisableNode 
		implements Categorized<SystemComponent>, Sealable {

	private static Logger log = Logger.getLogger(LifeCycle.class.getName());
	
	private boolean sealed = false;
	private SortedSet<Category> categories = new TreeSet<>();
	private String categoryId = null;
	
	private TwData parameterTemplate = null;
	private TwData variableTemplate = null;
	
	// The SystemComponent containers instantiated by this LifeCycle
	private Map<String,SystemContainer> containers = new HashMap<String,SystemContainer>();
	
	// default constructor
	public LifeCycle(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public LifeCycle(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		// manage categories
		Collection<Category> nl = (Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())), 
			edgeListEndNodes());
		categories.addAll(getSuperCategories(nl));
		// check if user-defined data classes were generated
//		boolean generateDataClasses = true;
		if (properties().hasProperty(P_PARAMETERCLASS.key())) {
			parameterTemplate = loadDataClass((String) properties().getPropertyValue(P_PARAMETERCLASS.key()));
//			generateDataClasses = false;
		}
		if (properties().hasProperty(P_DRIVERCLASS.toString())) {
			variableTemplate = loadDataClass((String) properties().getPropertyValue(P_DRIVERCLASS.key()));
//			generateDataClasses = false;
		}
		sealed = true; // important - next statement access this class methods
		// else produce information to generate data classes
//		if (generateDataClasses) {
//			// we reach here only if no data has been specified or no data class has been generated
//			// TODO: get this result to generate code !
//			buildUniqueDataList(E_PARAMETERS.label());
//			buildUniqueDataList(E_DRIVERS.label());
//		}
		categoryId = buildCategorySignature();
	}

	@Override
	public int initRank() {
		return N_LIFECYCLE.initRank();
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
	public Set<Category> categories() {
		if (sealed)
			return categories;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public String categoryId() {
		if (sealed)
			return categoryId;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	public Collection<SystemContainer> containers() {
		if (sealed)
			return containers.values();
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	/**
	 * returns the container matching the name.
	 * 
	 * @param name
	 * @return
	 */
	public SystemContainer container(String name) {
		if (sealed)
			return containers.get(name);
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
	
	/**
	 * Either return container matching 'name' or create it if not yet there. This way, only
	 * one instance of that container will exist.
	 * will only make a container if it does not yet exist under that name */
	public SystemContainer makeContainer(String name) {
		if (sealed) {
			SystemContainer result = containers.get(name);
			if (result==null) {
				SystemContainer sc = ((Ecosystem)getParent().getParent()).getInstance();
				result = new SystemContainer(this, name, sc, 
					parameterTemplate.clone(), variableTemplate.clone());
				if (!result.id().equals(name))
					log.warning("Unable to instantiate a container with id '"+name+"' - '"+result.id()+"' used instead");
				containers.put(result.id(),result);
			}
			return result;
		} else
			throw new TwcoreException("attempt to access uninitialised data");
	}
}
