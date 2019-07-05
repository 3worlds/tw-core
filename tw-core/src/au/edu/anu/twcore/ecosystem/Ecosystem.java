package au.edu.anu.twcore.ecosystem;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Singleton;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Set;
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
	
//	// a singleton root category for all Ecosystem instances
	private static final String rootCategoryId = ".";
//	private static Category rootCategory = null;
//	private static Set<Category> categories = new TreeSet<Category>(); 
	
	// a singleton container for all SystemComponents within an ecosystem
	private SystemContainer community = null;

	private void initRootCategory() {
//		if (rootCategory==null) {
//			rootCategory = (Category) factory().makeNode(Category.class, rootCategoryId);
//			categories.add(rootCategory);
//		}
	}
	
	public Ecosystem(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
		initRootCategory();
	}

	public Ecosystem(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
		initRootCategory();
	}

	@Override
	public void initialise() {
		super.initialise();
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
//		return categories;
		return null;
	}

	@Override
	public String categoryId() {
		return rootCategoryId;
	}

	@Override
	public SystemContainer getInstance() {
		return community;
	}

}
