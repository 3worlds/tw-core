package au.edu.anu.twcore.ecosystem.structure;

import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.ChangeState;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.SetInitialState;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_LIFECYCLETYPE;

import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleFactory;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleType extends ElementType<LifeCycleFactory,LifeCycleComponent> {

	public LifeCycleType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycleType(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		// containerData initialised in LifeCycleFactory
	}

	@Override
	public int initRank() {
		return N_LIFECYCLETYPE.initRank();
	}

	@Override
	protected LifeCycleFactory makeTemplate(int id) {
		ArenaType system = (ArenaType) getParent().getParent();
		ComponentContainer superContainer = (ComponentContainer) system.getInstance(id).getInstance().content();
		if (setinit!=null)
			return new LifeCycleFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),id(),superContainer);
		else
			return new LifeCycleFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				null,id(),superContainer);
	}

	/**
	 * The list of function types that are compatible with a LifeCycleType
	 */
	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeState,				// a life cycle may change its drivers
		SetInitialState,			// a life cycle may set its constants at creation time
// THESE are not possible because relations are only between SystemComponents
//		CreateOtherDecision,		// a group may create new items of its ComponentType
//		ChangeOtherCategoryDecision,// a group may change the category of a component
//		ChangeOtherState,			// a group may change the state of a component
//		DeleteOtherDecision,		// a group may delete another component
//		ChangeRelationState,		// a group may change the state of a relation
//		MaintainRelationDecision,	// a group may maintain a relation
//		RelateToDecision,			// a group may relate to a new component (ALWAYS unindexed search)
//		SetOtherInitialState		// a group may set the initial state of another component ???
	};

}