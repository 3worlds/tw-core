package au.edu.anu.twcore.ecosystem.structure;

import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.ChangeState;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.SetInitialState;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;

import au.edu.anu.twcore.ecosystem.ArenaType;
import au.edu.anu.twcore.ecosystem.dynamics.FunctionNode;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.biology.ChangeCategoryDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleFactory;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import fr.ens.biologie.generic.utils.Duple;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleType extends ElementType<LifeCycleFactory,LifeCycleComponent> {

	// maps of to- and from-categories matching recruit and produce nodes identified
	// by their function node (1..1 relation between produce/recruit and function through
	// 'effectedBy' edge)
	private Map<FunctionNode,Duple<String,String>>
		produceNodes = new HashMap<>(),
		recruitNodes = new HashMap<>();

	public LifeCycleType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public LifeCycleType(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// collect produce nodes information for factory
		Collection<Category> lcat = null;
		Collection<TreeGraphNode> lprod = (Collection<TreeGraphNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_PRODUCE.label())));
		for (TreeGraphNode prod:lprod) {
			SortedSet<Category> fromProduceCat = new TreeSet<>();
			lcat = (Collection<Category>) get(prod.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListEndNodes());
			fromProduceCat.addAll(lcat);
			SortedSet<Category> toProduceCat = new TreeSet<>();
			lcat = (Collection<Category>) get(prod.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListEndNodes());
			toProduceCat.addAll(lcat);
			FunctionNode fnode = (FunctionNode) get(prod.edges(Direction.OUT),
				selectOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			produceNodes.put(fnode,new Duple<>(Categorized.signature(fromProduceCat),
				Categorized.signature(toProduceCat)));
		}
		// collect recruit nodes information for factory
		Collection<TreeGraphNode> lrec = (Collection<TreeGraphNode>) get(getChildren(),
			selectZeroOrMany(hasTheLabel(N_RECRUIT.label())));
		for (TreeGraphNode rec:lrec) {
			SortedSet<Category> fromRecruitCat = new TreeSet<>();
			lcat = (Collection<Category>) get(rec.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_FROMCATEGORY.label())),
				edgeListEndNodes());
			fromRecruitCat.addAll(lcat);
			SortedSet<Category> toRecruitCat = new TreeSet<>();
			lcat = (Collection<Category>) get(rec.edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_TOCATEGORY.label())),
				edgeListEndNodes());
			toRecruitCat.addAll(lcat);
			FunctionNode fnode = (FunctionNode) get(rec.edges(Direction.OUT),
				selectOne(hasTheLabel(E_EFFECTEDBY.label())),
				endNode());
			recruitNodes.put(fnode,new Duple<>(Categorized.signature(fromRecruitCat),
					Categorized.signature(toRecruitCat)));
		}
	}

	@Override
	public int initRank() {
		return N_LIFECYCLETYPE.initRank();
	}

	@Override
	protected LifeCycleFactory makeTemplate(int id) {
		ArenaType system = (ArenaType) getParent().getParent();
		ComponentContainer superContainer = (ComponentContainer) system.getInstance(id).getInstance().content();
		Map<CreateOtherDecisionFunction,Duple<String,String>>
			prMap = new HashMap<>();
		for (FunctionNode f:produceNodes.keySet())
			prMap.put((CreateOtherDecisionFunction)f.getInstance(id),produceNodes.get(f));
		Map<ChangeCategoryDecisionFunction,Duple<String,String>>
			rcMap = new HashMap<>();
		for (FunctionNode f:recruitNodes.keySet())
			rcMap.put((ChangeCategoryDecisionFunction)f.getInstance(id), recruitNodes.get(f));
		if (setinit!=null) {
			return new LifeCycleFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),id(),superContainer,
				prMap,rcMap);
		}
		else {
			return new LifeCycleFactory(categories,
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				null,id(),superContainer,
				prMap,rcMap);
		}
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