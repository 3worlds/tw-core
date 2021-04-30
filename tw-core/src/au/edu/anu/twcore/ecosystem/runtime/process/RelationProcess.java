/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          *
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            *
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.runtime.process;

import java.util.LinkedList;
import java.util.List;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.DescribedContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.GroupComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.HierarchicalComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.LifeCycleComponent;

/**
 * A TwProcess that loops on established relations and executes methods on them or on their
 * start and end nodes
 * @author gignoux - 10 mars 2017
 *
 */
public class RelationProcess extends AbstractRelationProcess {

	// functions
    private List<ChangeOtherStateFunction> COSfunctions =
    	new LinkedList<ChangeOtherStateFunction>();
    private List<MaintainRelationDecisionFunction> MRfunctions =
    	new LinkedList<MaintainRelationDecisionFunction>();
    private List<ChangeRelationStateFunction> CRfunctions =
    	new LinkedList<ChangeRelationStateFunction>();

	// local variables for looping
	private ArenaComponent arena = null;
	private CategorizedComponent focalLifeCycle = null;
	private CategorizedComponent otherLifeCycle = null;
	private CategorizedComponent focalGroup = null;
	private CategorizedComponent otherGroup = null;

	public RelationProcess(ArenaComponent world, RelationContainer relation,
			Timer timer, DynamicSpace<SystemComponent> space, double searchR, int searchN) {
		super(world,relation,timer,space,searchR,searchN);
	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (function instanceof ChangeOtherStateFunction)
				COSfunctions.add((ChangeOtherStateFunction) function);
			else if (function instanceof ChangeRelationStateFunction)
				CRfunctions.add((ChangeRelationStateFunction) function);
			else if (function instanceof MaintainRelationDecisionFunction)
				MRfunctions.add((MaintainRelationDecisionFunction) function);
		}
	}

	private void executeFunctions(double t, double dt,
			CategorizedComponent focal,
			CategorizedComponent other,
			SystemRelation rel) {
		// ChangeOtherStateFunction-----------------------------------------------------------
        for (ChangeOtherStateFunction function:COSfunctions) {
        	if (other.currentState()!=null) {
	        	other.currentState().writeDisable();
	        	other.nextState().writeEnable();
        	}
        	function.changeOtherState(t,dt,
        		arena, focalLifeCycle, focalGroup, focal,
        		otherLifeCycle, otherGroup, other, space);
//			if (space!=null) {
//				relocate((SystemComponent)other);
//			}
        	if (other.currentState()!=null)
        		other.nextState().writeDisable();
        }
        // MaintainRelationDecisionFunction for ephemeral relations----------------------------
        if (!rel.container().isPermanent()) {
	        // MaintainRelationDecision
	        for (MaintainRelationDecisionFunction function:MRfunctions) {
	        	if (!function.maintainRelation(t, dt, arena,
	        		focalLifeCycle, focalGroup, focal,
	        		otherLifeCycle, otherGroup, other, space)) {
	        		rel.container().removeItem(rel);
	        	}
	        }
        }
        // ChangeRelationStateFunction---------------------------------------------------------
        for (ChangeRelationStateFunction function:CRfunctions) {
        	if (other.currentState()!=null) {
	        	other.currentState().writeDisable();
	        	other.nextState().writeEnable();
        	}
        	if (focal.currentState()!=null) {
        		focal.currentState().writeDisable();
        		focal.nextState().writeEnable();
        	}
        	function.changeRelationState(t, dt, arena, focalLifeCycle, focalGroup, focal,
    			otherLifeCycle, otherGroup, other, space);
        	if (other.currentState()!=null)
        		other.nextState().writeDisable();
        	if (focal.currentState()!=null)
        		focal.nextState().writeDisable();
        }
	}

	// manages the looping over others
	// NB: two possible optimisations here
	// * process both ends of a relation in one single pass - changeOtherState enables it
	// * replace edge list by a map indexed by edge labels -> faster access to the proper edges
	private void loopOnOthers(double t, double dt, SystemComponent focal) {
		for (SystemRelation sr:focal.getOutRelations()) {
			if (sr.membership().to().equals(to())) {
				SystemComponent other = (SystemComponent) sr.endNode();
				HierarchicalComponent hc = other.container().descriptors();
				if (hc instanceof ArenaComponent) {
					// arena is already set
					otherLifeCycle = null;
					otherGroup = null;
				}
				else if (hc instanceof GroupComponent) {
					// arena is already set
					otherGroup = hc;
					if (hc.getParent() instanceof LifeCycleComponent)					
						otherLifeCycle = (LifeCycleComponent) hc.getParent();
					else
						otherLifeCycle = null;
				}
				other.container().change();
				// TODO: fix this:
				executeFunctions(t,dt,focal,other,sr);
			}
		}
	}

	// almost same as in ComponentProcess
	// RECURSIVE
	// manages the looping over focals
	@Override
	protected void loop(double t, double dt, HierarchicalComponent component) {
		if (component.content()!=null) {
			if (component instanceof ArenaComponent) {
				arena = (ArenaComponent) component;
				focalLifeCycle = null;
				focalGroup = null;
			}
			else if(component instanceof LifeCycleComponent) {
				focalLifeCycle = (LifeCycleComponent) component;
				focalGroup = null;
			}
			else if (component instanceof GroupComponent) {
				focalGroup = component;
			}
			// execute function on contained items, if any, and of proper categories
			if (component.content().itemCategorized()!=null) // if null, means all content is in subcontainers
				if (component.content().itemCategorized().belongsTo(focalCategories)) {
					component.content().change();
					for (SystemComponent sc:component.content().items())
						loopOnOthers(t, dt, sc);
				}
			// in all cases, recurse on subcontainers to find more matching items
			// and recursively add context information to context.
			for (CategorizedContainer<SystemComponent> cc:component.content().subContainers()) {
				loop(t,dt,((DescribedContainer<SystemComponent>)cc).descriptors());
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" functions {");
		for (TwFunction f:COSfunctions) sb.append(f.toString()).append(", ");
		for (TwFunction f:MRfunctions) sb.append(f.toString()).append(", ");
		for (TwFunction f:CRfunctions) sb.append(f.toString()).append(", ");
		if (sb.charAt(sb.length()-2)==',') {
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append('}');
		return sb.toString();
	}

}
