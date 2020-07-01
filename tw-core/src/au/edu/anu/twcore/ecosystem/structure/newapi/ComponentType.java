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
package au.edu.anu.twcore.ecosystem.structure.newapi;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.N_COMPONENTTYPE;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentFactory;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * replacement for ComponentType
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ComponentType extends ElementType<ComponentFactory, SystemComponent> {

	public ComponentType(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public ComponentType(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	protected ComponentFactory makeTemplate(int id) {
		if (setinit!=null)
			return new ComponentFactory(categories,/*categoryId(),*/
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,
				(SetInitialStateFunction)setinit.getInstance(id));
		else
			return new ComponentFactory(categories,/*categoryId(),*/
				autoVarTemplate,driverTemplate,decoratorTemplate,lifetimeConstantTemplate,null);
	}

	@Override
	public int initRank() {
		return N_COMPONENTTYPE.initRank();
	}

}
