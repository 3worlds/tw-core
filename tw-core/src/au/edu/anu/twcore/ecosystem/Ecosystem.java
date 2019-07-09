package au.edu.anu.twcore.ecosystem;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_BELONGSTO;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;

/**
 * Class matching the "ecosystem" node label in the 3Worlds configuration tree.
 * Has properties. Also, produces the singleton top-container for the community of
 * SystemComponents which constitute this ecosystem.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Ecosystem 
		extends InitialisableNode 
		implements Categorized<SystemComponent>, Singleton<SystemContainer> {

	// this is the top of the system, so it doesnt belong to any category	
	// except if we want to attach parameters/variables to it
	
	// a 'null' category in case no category is set by the user
	private static final String rootCategoryId = ".";
	// a set of categories in case the user set some
	private String categoryId = null;
	private Set<Category> categories = new TreeSet<Category>(); 
	
	// a singleton container for all SystemComponents within an ecosystem
	private SystemContainer community = null;

	public Ecosystem(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public Ecosystem(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		Collection<Category> cats = (Collection<Category>) get(edges(Direction.OUT),
			selectZeroOrMany(hasTheLabel(E_BELONGSTO.label())), 
			edgeListEndNodes());
		if (!cats.isEmpty()) {
			categories.addAll(getSuperCategories(cats));
			categoryId = buildCategorySignature();
		}
		else
			categoryId = rootCategoryId;
		community = new SystemContainer(this,"ecosystem",null,null,null);
	}

	@Override
	public int initRank() {
		return N_SYSTEM.initRank();
	}

	public CategorizedContainer<SystemComponent> community() {
		return community;
	}
	
	@Override
	public Set<Category> categories() {
		if (!categories.isEmpty())
			return categories;
		return null;
	}

	@Override
	public String categoryId() {
		return categoryId;
	}

	@Override
	public SystemContainer getInstance() {
		return community;
	}

}
