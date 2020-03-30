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

import static fr.cnrs.iees.twcore.constants.TwFunctionTypes.*;

import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.space.DynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.LocatedSystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.RelationContainer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.twcore.constants.TwFunctionTypes;

/**
 * Ancestor to RelationProcess and SearchProcess
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public abstract class AbstractRelationProcess 
		extends AbstractProcess 
		implements Related<SystemComponent> {

	protected RelationContainer relContainer;
	protected String focalCategoryId = null;
	protected String otherCategoryId = null;
	protected SortedSet<Category> focalCategories = new TreeSet<>();
	protected SortedSet<Category> otherCategories = new TreeSet<>();

	public AbstractRelationProcess(ComponentContainer world, RelationContainer relation, 
			Timer timer, DynamicSpace<SystemComponent,LocatedSystemComponent> space, double searchR) {
		super(world, timer, space, searchR);
		relContainer = relation;
		focalCategoryId = relContainer.from().buildCategorySignature();
		otherCategoryId = relContainer.to().buildCategorySignature();
		focalCategories.addAll(relContainer.from().categories());
		otherCategories.addAll(relContainer.to().categories());
	}

	@Override
	public final Categorized<SystemComponent> from() {
		return relContainer.from();
	}

	@Override
	public final Categorized<SystemComponent> to() {
		return relContainer.to();
	}

	public static TwFunctionTypes[] compatibleFunctionTypes = {
		ChangeOtherCategoryDecision,
		ChangeOtherState,
		DeleteOtherDecision,
		ChangeRelationState,
		MaintainRelationDecision,
		RelateToDecision
	};

}
