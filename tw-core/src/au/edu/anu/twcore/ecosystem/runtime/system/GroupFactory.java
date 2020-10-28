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
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.utils.Logging;

/**
 * A factory to instantiate {@code GroupComponent}s, ie the variables and constants attached to a group.
 * The container is managed by the {@code Group} node.
 *
 * @author J. Gignoux - 23 avr. 2020
 *
 */
public class GroupFactory extends ElementFactory<GroupComponent> {

	private static Logger log = Logging.getLogger(GroupFactory.class);
	
	private String groupName = null;
	private String groupTypeName = null;
	private ComponentContainer parent = null;

	public GroupFactory(Set<Category> categories, /*String categoryId,*/ 
			TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit, String name, ComponentContainer parent) {
		super(categories, /*categoryId,*/ auto, drv, dec, ltc, setinit,true);
		this.parent = parent;
		groupTypeName = name;
	}

	/**
	 * This MUST be called before newInstance() in order for the correct name to be used.
	 * Otherwise the GroupType name is used to generate a group
	 * @param name
	 */
	public void setName(String name) {
		groupName = name;
	}

	@Override
	public GroupComponent newInstance() {
		GroupComponent group = null;
		ComponentContainer container = null;
		if (groupName!=null) {
			if (!ComponentContainer.containerScope.contains(groupName)) 
				container = new ComponentContainer(groupName,parent,null);
			else { // groupName already in use
				container = new ComponentContainer(groupTypeName,parent,null);
				String s = container.id();
				log.warning(()->"Group container couldnt be created with name '"+groupName
					+"' - name '" + s + "' used instead.");
			}
		}
		else
			container = new ComponentContainer(groupTypeName,parent,null);
		autoVarTemplate = new ContainerData(container);
		SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
		driverTemplate,decoratorTemplate,lifetimeConstantTemplate,2,propertyMap);
		group = (GroupComponent) SCfactory.makeNode(GroupComponent.class,container.id(),props);
		group.setCategorized(this);
		container.setData(group);
		group.setContent(container);
		return group;
	}

}
