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

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.containers.Containing;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * A class to represent containers as system components. These are TreeGraphDataNodes, ie
 * they have children and parents through the hierarchy relation
 *
 * @author J. Gignoux - 16 avr. 2020
 *
 */

// The group parameters are actually this class lifetime constants !

public abstract class HierarchicalComponent
		extends TreeGraphDataNode
		implements CategorizedComponent<ComponentContainer>, Containing<ComponentContainer> {

	protected Categorized<? extends CategorizedComponent<ComponentContainer>> categories = null;
	private ComponentContainer content = null;

	public HierarchicalComponent(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	@Override
	public Categorized<? extends CategorizedComponent<ComponentContainer>> membership() {
		return categories;
	}

	/**
	 * CAUTION: can be set only once, ideally just after construction
	 */
	@Override
	public void setCategorized(Categorized<? extends CategorizedComponent<ComponentContainer>> cats) {
		if (categories==null)
			categories = cats;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	/**
	 * CAUTION: can be set only once, ideally just after construction
	 */
	@Override
	public void setContent(ComponentContainer container) {
		if (content==null)
			content = container;
	}

	@Override
	public ComponentContainer content() {
		return content;
	}


}
