package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.DynamicSystem;
import fr.cnrs.iees.graph.Node;

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

	public default TwData decorators() {
		return ((SystemComponentPropertyListImpl) properties()).decorators();
	}

	public default TwData constants() {
		return ((SystemComponentPropertyListImpl) properties()).constants();
	}

	public default TwData autoVar() {
		return ((SystemComponentPropertyListImpl) properties()).auto();
	}

	// TODO: get rid of this method !
	public default TwData parameters() {
		return null;
	}

}
