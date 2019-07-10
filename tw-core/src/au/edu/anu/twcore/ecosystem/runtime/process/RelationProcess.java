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

import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.*;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import fr.cnrs.iees.graph.Edge;

/**
 * A TwProcess that loops on established relations and executes methods on them or on their
 * start and end nodes
 * @author gignoux - 10 mars 2017
 *
 */
public class RelationProcess extends AbstractProcess implements Related {
	
	private RelationType myRelation;
    private List<ChangeOtherCategoryDecisionFunction> COCfunctions = 
    	new LinkedList<ChangeOtherCategoryDecisionFunction>();
    private List<ChangeOtherStateFunction> COSfunctions = 
    	new LinkedList<ChangeOtherStateFunction>();
    private List<DeleteOtherDecisionFunction> DOfunctions = 
    	new LinkedList<DeleteOtherDecisionFunction>();
    private List<MaintainRelationDecisionFunction> MRfunctions = 
    	new LinkedList<MaintainRelationDecisionFunction>();
    private List<ChangeRelationStateFunction> CRfunctions = 
    	new LinkedList<ChangeRelationStateFunction>();

	public RelationProcess(Ecosystem world, RelationType relation) {
		super(world);
		myRelation = relation;
	}

	@Override
	public void execute(double t, double dt) {
//		// loop on this:
//		Iterable<Edge> relations = myRelation.relations();
//		for (Edge e:relations) {
//			SystemComponent focal = (SystemComponent) e.startNode();
//			SystemComponent target = (SystemComponent) e.endNode();
//			focal.currentState().writeDisable();
//			target.currentState().writeDisable();
//			focal.nextState().writeDisable();
//			target.nextState().writeEnable();
//			// change state of another SystemComponent
//			for (ChangeOtherStateFunction function:COSfunctions) {
//				function.changeOtherState(t, dt, focal, target);
//			}			
//			target.nextState().writeDisable();
//			// change stage of another SystemComponent
//			for (ChangeOtherCategoryDecisionFunction function:COCfunctions) {
//				String result = function.changeCategory(t, dt, focal, target);
//				if (result!=null) {
//					SystemComponent newRecruit = target.lifeCycle().newRecruit(target,result);
//					for (ChangeOtherStateFunction func:function.getConsequences())
//						func.changeOtherState(t, dt, target, newRecruit);
//					// TODO: there may be changes due to focal ??
//					tagSystemForDeletion(target);
//					tagSystemForInsertion(newRecruit);
//				}
//			}
//			// delete another SystemComponent
//			for (DeleteOtherDecisionFunction function:DOfunctions) {
//				if (function.delete(t, dt, focal, target)) {
//					focal.nextState().writeEnable();
//					for (ChangeOtherStateFunction func:function.getConsequences())
//						func.changeOtherState(t, dt, target, focal);
//					tagSystemForDeletion(target);	
//				}
//			}
//			// maintain a relation
//			for (MaintainRelationDecisionFunction function:MRfunctions) {
//				if (!function.maintainRelation(t, dt, e, focal, target))
//					myRelation.tagRelationForDeletion(e);
//			}
//			// change relation state
//			//TODO: make Edge write enabled
//			for (ChangeRelationStateFunction function:CRfunctions) {
//				function.changeRelationState(t, dt, focal, target, e);
//			}
//		}
	}

	@Override
	public void addFunction(TwFunction function) {
		if (!isSealed()) {
			if (ChangeOtherCategoryDecisionFunction.class.isAssignableFrom(function.getClass()))
				COCfunctions.add((ChangeOtherCategoryDecisionFunction) function);
			else if (ChangeOtherStateFunction.class.isAssignableFrom(function.getClass()))
				COSfunctions.add((ChangeOtherStateFunction) function);
			else if (ChangeRelationStateFunction.class.isAssignableFrom(function.getClass()))
				CRfunctions.add((ChangeRelationStateFunction) function);
			else if (DeleteOtherDecisionFunction.class.isAssignableFrom(function.getClass()))
				DOfunctions.add((DeleteOtherDecisionFunction) function);
			else if (MaintainRelationDecisionFunction.class.isAssignableFrom(function.getClass()))
				MRfunctions.add((MaintainRelationDecisionFunction) function);
		}		
	}

	@Override
	public Categorized from() {
		return myRelation.from();
	}

	@Override
	public Categorized to() {
		return myRelation.to();
	}

}
