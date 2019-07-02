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
import au.edu.anu.twcore.ecosystem.dynamics.initial.Group;
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
		boolean generateDataClasses = true;
		if (properties().hasProperty(P_PARAMETERCLASS.key())) {
			parameterTemplate = loadDataClass((String) properties().getPropertyValue(P_PARAMETERCLASS.key()));
			generateDataClasses = false;
		}
		if (properties().hasProperty(P_DRIVERCLASS.toString())) {
			variableTemplate = loadDataClass((String) properties().getPropertyValue(P_DRIVERCLASS.key()));
			generateDataClasses = false;
		}
		// if generated classes are here, build the container list from initial state data
		if (!generateDataClasses) {
			Collection<Group> gl = (Collection<Group>) get(edges(Direction.IN),
				selectZeroOrMany(hasTheLabel(E_CYCLE.label())),
				edgeListStartNodes());
			SystemContainer sc = ((Ecosystem)getParent().getParent()).getInstance();
			for (Group g:gl) {
				// NB since the group scope and the container scope are not the same
				// there should not be any problem keeping the ids identical.
				SystemContainer s = new SystemContainer(this, g.id(), sc, 
					parameterTemplate.clone(), variableTemplate.clone());
				if (!s.id().equals(g.id()))
					log.warning("Unable to instantiate a container with id '"+g.id()+"' - '"+s.id()+"' used instead");
				containers.put(s.id(),s);
			}
		}
		sealed = true; // important - next statement access this class methods
		// else produce information to generate data classes
		if (generateDataClasses) {
			// we reach here only if no data has been specified or no data class has been generated
			// TODO: get this result to generate code !
			buildUniqueDataList(E_PARAMETERS.label());
			buildUniqueDataList(E_DRIVERS.label());
		}
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

	public SystemContainer container(String name) {
		if (sealed)
			return containers.get(name);
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}
}
