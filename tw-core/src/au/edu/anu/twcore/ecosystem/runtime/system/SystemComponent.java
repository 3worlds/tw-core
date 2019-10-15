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

import java.util.List;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.DynamicSystem;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.SharedPropertyListImpl;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

/**
 * The main runtime object in 3worlds, representing "individuals" or "agents" or "system
 * components".
 * NB:
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemComponent extends ALDataNode implements DynamicSystem, Cloneable {

	/** indexes to access state variable table */
	protected static int CURRENT = 1;
	protected static int NEXT = CURRENT - 1;
	protected static int PAST0 = CURRENT + 1;
	private Categorized<SystemComponent> cats = null;
	
	public SystemComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
	}
	
	// used only once at init time
	public void setCategorized(Categorized<SystemComponent> cat) {
		if (cats==null)
			cats = cat;
	}
	
	/**
	 * 
	 * @return all the category information relevant to this component
	 */
	public Categorized<SystemComponent> membership() {
		return cats;
	}
	
	@Override
	public TwData currentState() {
		if (((SystemComponentPropertyListImpl) properties()).drivers().length > 0)
			return ((SystemComponentPropertyListImpl) properties()).drivers()[CURRENT];
		return null;
	}

	@Override
	public TwData nextState() {
		if (((SystemComponentPropertyListImpl) properties()).drivers().length > 0)
			return ((SystemComponentPropertyListImpl) properties()).drivers()[NEXT];
		return null;
	}

	@Override
	public TwData previousState() {
		if (((SystemComponentPropertyListImpl) properties()).drivers().length > 0)
			return ((SystemComponentPropertyListImpl) properties()).drivers()[PAST0];
		return null;
	}

	@Override
	public TwData previousState(int stepsBack) {
		// TODO fix this
//		if (stepsBack <= parent.memory())
//			return ((SystemComponentPropertyListImpl) properties()).drivers()[CURRENT + stepsBack];
//		else
//			throw new AotException("ComplexSystem.previous(): Attempt to recall unmemorized past state");
		return null;
	}

	@Override
	public TwData state(int stepIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void stepBackward() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stepBackward(int nSteps) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stepForward() {
		TwData[] state = ((SystemComponentPropertyListImpl)properties()).drivers();
		if (state != null) {
			// circular buffer
			TwData last = state[state.length - 1]; // this is the last
			for (int i = state.length - 1; i > 0; i--)
				state[i] = state[i - 1];
			state[0] = last;
			// copy back current values into next
			if (last != null)
				last.setProperties(state[CURRENT]);
			((SystemComponentPropertyListImpl) properties()).rotateDriverProperties(state[CURRENT]);
		}
	}

	@Override
	public void extrapolateState(long time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void interpolateState(long time) {
		// TODO Auto-generated method stub
		
	}
	
	@Override 
	public SystemComponent clone() {
		SystemComponent result = ((SystemFactory)cats).newInstance();
		result.properties().setProperties(properties());
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<SystemRelation> getRelations() {
		return (Iterable<SystemRelation>) edges(Direction.OUT);
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<SystemRelation> getRelations(String relationType) {
		List<SystemRelation> list = (List<SystemRelation>) get(edges(Direction.OUT),
			selectZeroOrMany(hasProperty("type",relationType)));
//		List<SystemRelation> list = new ArrayList<>();
//		for (ALEdge e:edges(Direction.OUT)) {
//			SystemRelation r = (SystemRelation) e;
//			if (r.properties().getPropertyValue("type").equals(relationType))
//				list.add(r);
//		}
		return list;
	}
	
	public SystemRelation relateTo(SystemComponent toComponent, String relationType) {
		SystemRelation rel = (SystemRelation) connectTo(Direction.OUT,toComponent,
			new SharedPropertyListImpl(SystemRelation.DEFAULT_PROPERTIES));
		rel.properties().setProperty("type",relationType);
		return rel;
	}
	
	public SystemData autoVar() {
		return ((SystemComponentPropertyListImpl)properties()).auto();
	}

}
