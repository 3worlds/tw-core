package au.edu.anu.twcore.graphState;

public class MockListener implements GraphStateListener{

	@Override
	public void onStateChange(boolean state) {
		System.out.println(state);
		
	}

}
