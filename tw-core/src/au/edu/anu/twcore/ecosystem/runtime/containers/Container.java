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
package au.edu.anu.twcore.ecosystem.runtime.containers;

import java.util.Map;
import java.util.TreeMap;

import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.identity.impl.ResettableLocalScope;
import fr.cnrs.iees.omhtk.Resettable;

/**
 * An ancestor class for any kind of SystemComponent / SystemRelation container.
 * It maintains a unique scope for all containers, which generates unique names.
 * A reset() operation will replace the scope with a new one so that same names
 * can be used after one reset (ie, container ids are unique only between two
 * resets, not over a reset).
 *
 * @author gignoux
 *
 */
public interface Container extends Identity, Resettable {

	// as in ElementFactory: scopes may be indexed by simulator instance id, hence this
	// complicated multi-singleton implementation
	public class scopes {
		private Integer simId;
		private static Map<Integer,ResettableLocalScope> containerScopes = new TreeMap<>();
		public Integer getSimId() {
			return simId;
		}
		public void setSimId(Integer simId) {
			this.simId = simId;
		}
		public ResettableLocalScope getContainerScope(Integer i) {
			return containerScopes.get(i);
		}
		public void setContainerScope( Integer i, ResettableLocalScope containerScope) {
			containerScopes.put(i,containerScope);
		}
	}
	public static String containerScopeName = "3w-containers";

	@Override
	default void preProcess() {
		scope().preProcess();
	}

	@Override
	public ResettableLocalScope scope();

	@Override
	default void postProcess() {
		scope().postProcess();
	}

}
