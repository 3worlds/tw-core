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

import static au.edu.anu.rscs.aot.queries.CoreQueries.edgeListEndNodes;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasProperty;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrMany;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.P_RELATIONTYPE;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.DynamicSystem;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;

/**
 * The base class for system components and hierarchical components.
 *
 * @author J. Gignoux - 16 avr. 2020
 *
 */
public interface CategorizedComponent
		extends DataElement, Node, DynamicSystem, Cloneable {

	/** indexes to access state variable table */
	static final int CURRENT = 1;
	static final int NEXT = CURRENT - 1;
	static final int PAST0 = CURRENT + 1;
	
	public Categorized<? extends CategorizedComponent> membership();

	public ElementFactory<? extends CategorizedComponent> elementFactory();

	public void setCategorized(Categorized<? extends CategorizedComponent> cats);

	@Override
	public default TwData currentState() {
		if (((SystemComponentPropertyListImpl) properties()).drivers().length > 0)
			return ((SystemComponentPropertyListImpl) properties()).drivers()[CURRENT];
		return null;
	}

	@Override
	public default TwData nextState() {
		if (((SystemComponentPropertyListImpl) properties()).drivers().length > 0)
			return ((SystemComponentPropertyListImpl) properties()).drivers()[NEXT];
		return null;
	}

	@Override
	public default TwData previousState() {
		if (((SystemComponentPropertyListImpl) properties()).drivers().length > 0)
			return ((SystemComponentPropertyListImpl) properties()).drivers()[PAST0];
		return null;
	}
	@Override
	public default TwData previousState(int stepsBack) {
		// TODO fix this
//		if (stepsBack <= parent.memory())
//			return ((SystemComponentPropertyListImpl) properties()).drivers()[CURRENT + stepsBack];
//		else
//			throw new AotException("ComplexSystem.previous(): Attempt to recall unmemorized past state");
		return null;
	}

	@Override
	public default TwData state(int stepIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public default void stepBackward() {
		// TODO Auto-generated method stub

	}

	@Override
	public default void stepBackward(int nSteps) {
		// TODO Auto-generated method stub

	}

	@Override
	public default void extrapolateState(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public default void interpolateState(long time) {
		// TODO Auto-generated method stub

	}

	@Override
	public default void stepForward() {
		TwData[] state = ((SystemComponentPropertyListImpl)properties()).drivers();
		if (state != null)
			if (nextState()!=null) {
				// circular buffer
				TwData last = state[state.length - 1]; // this is the last
				last.writeEnable();
				for (int i = state.length - 1; i > 0; i--)
					state[i] = state[i - 1];
				state[0] = last;
				// copy back current values into next
				if (last != null)
					last.setProperties(state[CURRENT]);
				((SystemComponentPropertyListImpl) properties()).rotateDriverProperties(state[CURRENT]);
				last.writeDisable();
		}
	}

	public default SystemRelation relateTo(CategorizedComponent toComponent, String relationType) {
		SystemRelation rel = (SystemRelation) connectTo(Direction.OUT,toComponent,
			new SharedPropertyListImpl(SystemRelation.DEFAULT_PROPERTIES));
		rel.properties().setProperty(P_RELATIONTYPE.key(),relationType);
		return rel;
	}


	public default TwData decorators() {
		return ((SystemComponentPropertyListImpl) properties()).decorators();
	}

	public default TwData constants() {
		return ((SystemComponentPropertyListImpl) properties()).constants();
	}

	public default TwData autoVar() {
		return ((SystemComponentPropertyListImpl) properties()).auto();
	}

	public default boolean isPermanent() {
		return true;
	}

	public default SetInitialStateFunction initialiser() {
		return null;
	}

	// TODO: These three methods could be optimized y storing the edges in a Map sorted by labels

	@SuppressWarnings("unchecked")
	public default Collection<SystemRelation> getOutRelations() {
		return Collections.unmodifiableCollection((Collection<SystemRelation>) edges(Direction.OUT));
	}

	@SuppressWarnings("unchecked")
	public default Collection<SystemRelation> getInRelations() {
		return Collections.unmodifiableCollection((Collection<SystemRelation>) edges(Direction.IN));
	}

	@SuppressWarnings("unchecked")
	public default Collection<SystemRelation> getRelations() {
		return Collections.unmodifiableCollection((Collection<SystemRelation>) edges());
	}

	@SuppressWarnings("unchecked")
	public default Collection<SystemRelation> getOutRelations(String relationType) {
		List<SystemRelation> list = (List<SystemRelation>) get(edges(Direction.OUT),
			selectZeroOrMany(hasProperty(P_RELATIONTYPE.key(),relationType)));
		return list;
	}

	@SuppressWarnings("unchecked")
	public default Collection<SystemComponent> getOutRelatives(String relationType) {
		List<SystemComponent> list = (List<SystemComponent>) get(edges(Direction.OUT),
			selectZeroOrMany(hasProperty(P_RELATIONTYPE.key(),relationType)),
			edgeListEndNodes());
		return list;
	}

	public default String[] hierarchicalId() {
		return null;
	}

	public default String name() {
		return id();
	}

}
