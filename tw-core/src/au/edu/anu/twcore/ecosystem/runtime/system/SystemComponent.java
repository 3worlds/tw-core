package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.DynamicSystem;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.ALDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;

/**
 * The main runtime object in 3worlds, representing "individuals" or "agents" or "system
 * components".
 * 
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class SystemComponent extends ALDataNode implements DynamicSystem, Cloneable {

	/** indexes to access state variable table */
	protected static int CURRENT = 1;
	protected static int NEXT = CURRENT - 1;
	protected static int PAST0 = CURRENT + 1;
	private Categorized cats = null;
	
	protected SystemComponent(Identity id, SimplePropertyList props, GraphFactory factory) {
		super(id, props, factory);
	}
	
	// used only once at init time
	public void setCategorized(Categorized cat) {
		if (cats==null)
			cats = cat;
	}
	
	/**
	 * 
	 * @return all the category information relevant to this component
	 */
	public Categorized membership() {
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
		// TODO Auto-generated method stub
		
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
		return null;
	}

}
