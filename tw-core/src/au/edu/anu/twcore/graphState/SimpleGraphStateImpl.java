package au.edu.anu.twcore.graphState;

import au.edu.anu.twcore.graphState.IGraphState;

public class SimpleGraphStateImpl implements IGraphState{

	private boolean changed = false;
	@Override
	public boolean hasChanged() {
		return changed;
	}

	@Override
	public void setChanged(boolean state) {
		changed = state;
	}

}
