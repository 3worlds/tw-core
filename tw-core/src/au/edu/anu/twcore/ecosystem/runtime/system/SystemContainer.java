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

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * A container for SystemComponents 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class SystemContainer extends CategorizedContainer<SystemComponent> {
	
	public SystemContainer(Categorized<SystemComponent> cats, 
			String proposedId, 
			SystemContainer parent,
			TwData parameters,
			TwData variables) {
		super(cats,proposedId,parent,parameters,variables);
	}

	@Override
	public SystemComponent newInstance() {
		if (categoryInfo() instanceof SystemFactory)
			return ((SystemFactory)categoryInfo()).newInstance();
		throw new TwcoreException("SystemContainer "+id()+" cannot instantiate SystemComponents");
	}

	@Override
	public final SystemComponent clone(SystemComponent item) {
		SystemComponent result = newInstance();
		result.properties().setProperties(item.properties());
		return result;
	}
	
	/**
	 * Advances state of all SystemComponents contained in this container only.
	 */
	public void step() {
		for (SystemComponent sc:items())
			sc.stepForward();
	}
	
	/**
	 * Advances state of all SystemComponents contained in this container and its sub-containers
	 * (recursive).
	 */
	public void stepAll() {
		step();
		for (CategorizedContainer<SystemComponent> sc:subContainers())
			((SystemContainer)sc).stepAll();
	}

	@Override
	public void rename(String oldId, String newId) {
		throw new TwcoreException ("Renaming of '"+this.getClass().getSimpleName()+"' is not implemented.");
		
	}

}
