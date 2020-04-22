package au.edu.anu.twcore.ecosystem.structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemData;
import au.edu.anu.twcore.exceptions.TwcoreException;

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectOneOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BELONGSTO;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_DECORATORCLASS;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_DRIVERCLASS;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_LTCONSTANTCLASS;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_PARAMETERCLASS;

/**
 * Matches the structure&gt;template node of the specifications.
 *
 * @author J. Gignoux - 22 avr. 2020
 *
 */
public class TemplateNode
		extends InitialisableNode
		implements LimitedEdition<Template>, Categorized<SystemComponent>, Sealable {

	private boolean sealed = false;
	private Map<Integer,Template> templates = new HashMap<>();
	private SortedSet<Category> categories = new TreeSet<>();
	private List<String> categoryNames = null;
	private String categoryId = null;
	/** TwData templates to clone to create new systems */
	private ReadOnlyPropertyList autoVarTemplate = null;
	private TwData parameterTemplate = null;
	private TwData driverTemplate = null;
	private TwData decoratorTemplate = null;
	private TwData lifetimeConstantTemplate = null;

	// default constructor
	public TemplateNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public TemplateNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		Collection<Category> nl = (Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_BELONGSTO.label())),
			edgeListEndNodes());
		categories.addAll(getSuperCategories(nl));
		categoryNames = new ArrayList<>(categories.size());
		for (Category c:categories)
			categoryNames.add(c.id()); // order is maintained
		// user-defined data structures
		// These ARE optional - inserted by codeGenerator!
		String s = null;
		if (properties().hasProperty(P_DRIVERCLASS.key())) {
			s = (String) properties().getPropertyValue(P_DRIVERCLASS.key());
			if (s!=null)
				if (!s.trim().isEmpty())
					driverTemplate = loadDataClass(s);
		}
		if (properties().hasProperty(P_DECORATORCLASS.key())) {
			s = (String) properties().getPropertyValue(P_DECORATORCLASS.key());
			if (s!=null)
				if (!s.trim().isEmpty())
					decoratorTemplate = loadDataClass(s);
		}
		if (properties().hasProperty(P_LTCONSTANTCLASS.key())) {
			s = (String) properties().getPropertyValue(P_LTCONSTANTCLASS.key());
			if (s!=null)
				if (!s.trim().isEmpty())
					lifetimeConstantTemplate = loadDataClass(s);
		}
		sealed = true; // important - next statement access this class methods
		categoryId = buildCategorySignature();
	}

	@Override
	public int initRank() {
		return N_TEMPLATE.initRank();
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
	public Template getInstance(int id) {
		if (!sealed)
			initialise();
		if (!templates.containsKey(id))
			templates.put(id,makeTemplate(id));
		return templates.get(id);
	}

	private Template makeTemplate(int id) {
		Template result = null;
		// predefined categories of the composition category set
		if (categoryNames.contains(Category.population)) {
			// make container
		}
		else if (categoryNames.contains(Category.individual)) {
			// find container
		}
		// predefined categories of the lifespan category set
		if (categoryNames.contains(Category.permanent)) {

		}
		else if (categoryNames.contains(Category.ephemeral)) {
			autoVarTemplate = new SystemData();
		}
		// in the next statement different factories are produced
		// predefined categories of the concepts category set
		if (categoryNames.contains(Category.arena)) {
			// must be unique - make a singleton factory
		}
		else if (categoryNames.contains(Category.lifeCycle)) {

		}
		else if (categoryNames.contains(Category.group)) {

		}
		else if (categoryNames.contains(Category.component)) {
			// make a componentfactory ( currently systemfactory)
		}
		else if (categoryNames.contains(Category.relation)) {
			// get the fromCat and toCat to generate a relationFactory
		}
		else if (categoryNames.contains(Category.space)) {

		}
		return result;
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

}
