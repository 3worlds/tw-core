package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.InitialisableNode;

/**
 * Class matching the "category" node label in the 3Worlds configuration tree.
 * Has no properties.
 * Categories are singleton, ie only one category of a given type can exist. This is normally
 * guaranteed by the fact that category name = category id, and since ids are unique
 * no duplication is possible.
 * 
 * @author Jacques Gignoux - 29 mai 2019
 *
 */
public class Category extends InitialisableNode implements Comparable<Category> {

	// default constructor
	public Category(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Category(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
	}

	@Override
	public int initRank() {
		return N_CATEGORY.initRank();
	}

	public String name() {
		return id();
	}
	
	public CategorySet categorySet() {
		return (CategorySet) getParent();
	}

	@Override
	public int compareTo(Category other) {
		return this.id().compareTo(other.id());
	}

}
