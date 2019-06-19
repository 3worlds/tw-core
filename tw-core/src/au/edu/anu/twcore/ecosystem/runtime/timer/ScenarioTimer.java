package au.edu.anu.twcore.ecosystem.runtime.timer;

import au.edu.anu.twcore.ecosystem.dynamics.TimeModel;

/**
 * Implementation of Timer with a scenario time model
 *
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
public class ScenarioTimer extends AbstractTimer {

	public ScenarioTimer(TimeModel timeModel) {
		super(timeModel);
	}

	@Override
	public long dt(long time) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void advanceTime(long newTime) {
		// TODO Auto-generated method stub

	}

}
