package au.edu.anu.twcore.ecosystem;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Set;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.structure.Category;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * Class matching the "ecosystem" node label in the 3Worlds configuration tree.
 * Has properties.
 * 
 * @author Jacques Gignoux - 27 mai 2019
 *
 */
public class Ecosystem extends InitialisableNode implements Categorized<SystemComponent> {
	
	private static final String rootCategoryId = ".";
	private static Category rootCategory = null;
	private static Set<Category> categories = new TreeSet<Category>(); 
	
	private CategorizedContainer<SystemComponent> community = null;

	private void initRootCategory() {
		if (rootCategory==null) {
			rootCategory = (Category) factory().makeNode(Category.class, rootCategoryId);
			categories.add(rootCategory);
		}
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
		community = new CategorizedContainer<>(this,"ecosystem");
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
		return categories;
	}

	@Override
	public String categoryId() {
		return rootCategoryId;
	}

	@Override
	public SystemComponent clone(SystemComponent item) {
		throw new TwcoreException("Ecosystem cannot instantiate SystemComponents");
	}

}
