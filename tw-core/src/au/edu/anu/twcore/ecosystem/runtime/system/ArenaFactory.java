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
package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The class building the op container, ie the system arena. This is a singleton in any simulation.
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class ArenaFactory extends ElementFactory<ArenaComponent> {

	private ArenaComponent arena = null;
	private boolean makeContainer = true;
	private String name = null;

	public ArenaFactory(Set<Category> categories, /*String categoryId,*/ TwData auto, TwData drv, TwData dec,
			TwData ltc, SetInitialStateFunction setinit,boolean makeContainer,String name) {
		super(categories, /*categoryId,*/ auto, drv, dec, ltc, setinit);
		this.makeContainer = makeContainer;
		this.name = name;
	}

	@Override
	public ArenaComponent getInstance() {
		if (arena==null) {
			ComponentContainer community = null;
			if (makeContainer) {
				community = new ComponentContainer(name,null,null);
				autoVarTemplate = new ContainerData(community);
			}
			SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
				driverTemplate,decoratorTemplate,lifetimeConstantTemplate,2,propertyMap);
			arena = (ArenaComponent) SCfactory.makeNode(ArenaComponent.class,name,props);
			arena.setCategorized(this);
			if (makeContainer) {
				community.setData(arena);
				arena.setContent(community);
			}
		}
		return arena;
	}

}
