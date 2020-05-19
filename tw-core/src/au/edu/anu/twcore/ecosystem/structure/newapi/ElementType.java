package au.edu.anu.twcore.ecosystem.structure.newapi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import au.edu.anu.twcore.ecosystem.dynamics.InitFunctionNode;
import au.edu.anu.twcore.ecosystem.dynamics.initial.ConstantValues;
import au.edu.anu.twcore.ecosystem.dynamics.initial.VariableValues;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.DataElement;
import au.edu.anu.twcore.ecosystem.runtime.system.ElementFactory;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentData;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BELONGSTO;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

/**
 * An ancestor to nodes that produce factories that create SystemCOmponents with data in
 *
 * @author J. Gignoux - 22 avr. 2020
 *
 */
public abstract class ElementType<T extends ElementFactory<U>,U extends DataElement>
		extends InitialisableNode
		implements LimitedEdition<T>, Categorized<U>, Sealable {

	boolean sealed = false;
	Map<Integer,T> templates = new HashMap<>();
	protected SortedSet<Category> categories = new TreeSet<>();
	private List<String> categoryNames = null;
	private String categoryId = null;
	/** TwData templates to clone to create new systems */
	protected TwData autoVarTemplate = null;
	protected TwData driverTemplate = null;
	protected TwData decoratorTemplate = null;
	protected TwData lifetimeConstantTemplate = null;
	SimplePropertyList properties = null;
	protected InitFunctionNode setinit = null;

	// default constructor
	public ElementType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public ElementType(Identity id, GraphFactory gfactory) {
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
		if (categoryNames.contains(Category.ephemeral))
			autoVarTemplate = new ComponentData();
		// NB: maybe permanent systems should have a different scope for their identities, so that
		// the same name can be used at every reset
		// on the other hand, the container name can be used for unicity
		if (categoryNames.contains(Category.population))
			autoVarTemplate = null; // this must be done in descendants
		// user-defined data structures
		// These ARE optional - inserted by codeGenerator!
		String s = null;
		if (properties().hasProperty(P_DRIVERCLASS.key())) {
			s = (String) properties().getPropertyValue(P_DRIVERCLASS.key());
			if (s!=null)
				if (!s.trim().isEmpty()) {
					driverTemplate = loadDataClass(s);
					for (TreeNode tn:getChildren())
						if (tn instanceof VariableValues)
							((VariableValues) tn).fill(driverTemplate);
				}
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
				if (!s.trim().isEmpty()) {
					lifetimeConstantTemplate = loadDataClass(s);
					for (TreeNode tn:getChildren())
						if (tn instanceof ConstantValues)
							((ConstantValues) tn).fill(lifetimeConstantTemplate);
				}
		}
		// Find the setInitialState function
		setinit = (InitFunctionNode) get(getChildren(),selectZeroOrOne(hasTheLabel(N_INITFUNCTION.label())));
		sealed = true; // important - next statement access this class methods
		categoryId = buildCategorySignature();
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
	public T getInstance(int id) {
		if (!sealed)
			initialise();
		if (!templates.containsKey(id))
			templates.put(id,makeTemplate(id));
		return templates.get(id);
	}

	protected abstract T makeTemplate(int id);

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
