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

import fr.cnrs.iees.omugi.graph.property.PropertyKeys;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_RELATIONTYPE;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.containers.Contained;
import fr.cnrs.iees.graph.EdgeFactory;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.graph.impl.ALDataEdge;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The second main runtime object, representing relations between System components.
 * No properties at the moment.
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemRelation extends ALDataEdge implements Contained<RelationContainer> {

	protected static PropertyKeys DEFAULT_PROPERTIES = new PropertyKeys(P_RELATIONTYPE.key());

	private Related<CategorizedComponent> relation = null;
	private RelationContainer container = null;

	public SystemRelation(Identity id, Node start, Node end, SimplePropertyList props, EdgeFactory graph) {
		super(id, start, end, props, graph);
	}

	public void setRelated(Related<CategorizedComponent> rel) {
		if (relation==null)
			relation = rel;
	}

	public Related<CategorizedComponent> membership() {
		return relation;
	}

	@Override
	public void setContainer(RelationContainer container) {
		if (this.container==null)
			this.container = container;
	}

	@Override
	public RelationContainer container() {
		return container;
	}

	@Override
	public void detachFromContainer() {
		container = null;
	}
	
	public String type() {
		return (String) properties().getPropertyValue(P_RELATIONTYPE.key());
	}

}
