package au.edu.anu.twcore.ecosystem.structure;

import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Factory;

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectOneOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.structure.system.SystemRelation;

/**
 * This is equivalent to the SystemFactory, but for SystemRelation
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class RelationType 
		extends InitialisableNode 
		implements Factory<SystemRelation>, Related {
	
	// a little class to record the from and to category lists
	private class cat implements Categorized {
		private SortedSet<Category> categories = new TreeSet<>();
		private String categoryId = null;
		private cat(Collection<Category>cats) {
			super();
			categories.addAll(cats);
			buildCategorySignature();
		}
		@Override
		public Set<Category> categories() {
			return categories;
		}
		@Override
		public String categoryId() {
			return categoryId;
		}
	}
	// from and to category lists
	private cat fromCat, toCat;

	public RelationType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public RelationType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		Collection<Category> tocats = (Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())), 
			edgeListEndNodes());
		toCat = new cat(tocats);
		Collection<Category> fromcats = (Collection<Category>) get(edges(Direction.OUT),
			selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())), 
			edgeListEndNodes());
		fromCat = new cat(fromcats);
	}

	@Override
	public int initRank() {
		return N_RELATIONTYPE.initRank();
	}

	@Override
	public SystemRelation newInstance() {
		// TODO finish implementation
		SystemRelation result = null;
		result.setRelated(this); 
		return result;
	}

	@Override
	public Categorized from() {
		return fromCat;
	}

	@Override
	public Categorized to() {
		return toCat;
	}

}
