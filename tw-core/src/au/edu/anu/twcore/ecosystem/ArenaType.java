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
package au.edu.anu.twcore.ecosystem;

import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.ArenaFactory;
import au.edu.anu.twcore.ecosystem.structure.newapi.ElementType;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import java.util.Collection;


/**
 * Replacement for ecosystem node - maps to system
 * This class always makes one factory for arenaComponents, and a unique  arenaComponent with
 * always be generated because it has to be here in any model
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaType extends ElementType<ArenaFactory, ArenaComponent> {

	private boolean makeContainer = true;

	// default constructor
	public ArenaType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public ArenaType(Identity id, GraphFactory gfactory) {
		super(id, gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// if there are no ElementType descendant nodes in the model, means
		// this will be the unique instance of SystemComponent in the model
		// so no need for an associated container. Otherwise it's always here.
		Collection<TreeNode> nl = (Collection<TreeNode>) get(getChildren(),
			selectZeroOrOne(hasTheLabel(N_STRUCTURE.label())),
			children(),
			selectZeroOrMany(notQuery(
				orQuery(hasTheLabel(N_CATEGORY.label()),hasTheLabel(N_CATEGORYSET.label())))));
		if (nl==null || nl.isEmpty())
			makeContainer = false;
	}

	@Override
	public int initRank() {
		return N_SYSTEM.initRank();
	}

	@Override
	protected ArenaFactory makeTemplate(int id) {
		if (setinit!=null)
			return new ArenaFactory(categories,/*categoryId(),*/
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id),makeContainer,id());
		else
			return new ArenaFactory(categories,/*categoryId(),*/
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				null,makeContainer,id());
	}

}
