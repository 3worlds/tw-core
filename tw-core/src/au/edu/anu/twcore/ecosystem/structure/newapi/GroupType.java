package au.edu.anu.twcore.ecosystem.structure.newapi;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import java.util.Collection;

import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupFactory;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * Replacement for the Group class
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class GroupType extends ElementType<GroupFactory,GroupComponent> {

	private static final int baseInitRank = N_GROUP.initRank();

	public GroupType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public GroupType(Identity id,  GraphFactory gfactory) {
		super(id, gfactory);
	}

	// this to call groups in proper dependency order, i.e. higher groups must be initialised first
	private int initRank(GroupType g, int rank) {
		if (g.getParent() instanceof GroupType)
			rank = initRank((GroupType)g.getParent(),rank) + 1;
		return rank;
	}

	@Override
	public int initRank() {
		return initRank(this,baseInitRank);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected GroupFactory makeTemplate(int id) {
		ComponentContainer superContainer = null;
		Collection<TreeNode> lcl = (Collection<TreeNode>) get(getParent().getChildren(),
			selectZeroOrMany(hasTheLabel(N_LIFECYCLE.label())));
		// if no life cycle present, the super container must be the arena
		if (lcl.isEmpty()) {
			ArenaType system = (ArenaType) getParent().getParent();
			superContainer = system.getInstance(id).getInstance().content();
		}
		else {
			// TODO ! get the proper life cycle container !
			throw new TwcoreException("Not yet implemented!");
		}
		if (setinit!=null)
			return new GroupFactory(categories,categoryId(),
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),id(),superContainer);
		else
			return new GroupFactory(categories,categoryId(),
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				null,id(),superContainer);
	}

}
