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
package au.edu.anu.twcore.ecosystem.runtime;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import au.edu.anu.twcore.ecosystem.runtime.process.AbstractProcess;
import au.edu.anu.twcore.ecosystem.runtime.process.HierarchicalContext;
import au.edu.anu.twcore.rngFactory.RngHolder;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;
import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.*;

/**
 * Ancestor for the class doing the user-defined computation
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public interface TwFunction extends RngHolder {
	
	static EnumMap<TwFunctionTypes,EnumSet<TwFunctionTypes>> ftypes = 
		new EnumMap<TwFunctionTypes, EnumSet<TwFunctionTypes>>(TwFunctionTypes.class);
	
	/**
	 * Connects a function to its process, only once (at construction time).
	 * This function is not meant to be used by end-users.
	 * 
	 * @param process the process
	 */
	public void initProcess(AbstractProcess process);
	
	public AbstractProcess process();

	public void addConsequence(TwFunction function);
	
	public void setFocalContext(HierarchicalContext context);
	
	public void setOtherContext(HierarchicalContext context);
	
	public default List<? extends TwFunction> getConsequences() {
		return null;
	}
	
	/**
	 * Utility to find the proper consequences of every function type. Always returns a valid
	 * iterable (sometimes empty).
	 */
	public static Collection<TwFunctionTypes> consequenceTypes(TwFunctionTypes func) {
		if (ftypes.isEmpty()) {
			ftypes.put(ChangeState,EnumSet.noneOf(TwFunctionTypes.class));
			ftypes.put(ChangeCategoryDecision,EnumSet.of(ChangeOtherState));
			ftypes.put(CreateOtherDecision,EnumSet.of(ChangeOtherState,ChangeState,RelateToDecision));
			ftypes.put(DeleteDecision,EnumSet.of(ChangeOtherState));
			ftypes.put(ChangeOtherState, EnumSet.noneOf(TwFunctionTypes.class));
			ftypes.put(ChangeOtherCategoryDecision,EnumSet.of(ChangeOtherState));
			ftypes.put(DeleteOtherDecision,EnumSet.of(ChangeOtherState));
			ftypes.put(RelateToDecision, EnumSet.noneOf(TwFunctionTypes.class));
			ftypes.put(MaintainRelationDecision, EnumSet.noneOf(TwFunctionTypes.class));
			ftypes.put(ChangeRelationState, EnumSet.noneOf(TwFunctionTypes.class));
		}
		return ftypes.get(func);
	}
	
}
