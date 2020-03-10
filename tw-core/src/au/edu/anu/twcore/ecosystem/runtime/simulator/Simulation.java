package au.edu.anu.twcore.ecosystem.runtime.simulator;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;
import static fr.cnrs.iees.twcore.constants.SimulatorStatus.*;

import au.edu.anu.twcore.ecosystem.runtime.Timer;
import fr.ens.biologie.generic.Resettable;

/**
 * A class following the state of a single simulation.
 * 
 * @author Jacques Gignoux - 10 mars 2020
 *
 */
public class Simulation implements Resettable {
	
	private Simulator simulator;
	private SimulatorStatus status;

	public Simulation(Simulator sim) {
		super();
		simulator = sim;
	}

	@Override
	public void preProcess() {
		status = Initial;
		simulator.stoppingCondition.preProcess();
		for (Timer t:simulator.timerList)
			t.preProcess();
		simulator.timetracker.sendData(simulator.startTime);
		simulator.ecosystem.preProcess();
	}

	@Override
	public void postProcess() {
		status = Final;
		simulator.lastTime = simulator.startTime;
		simulator.stoppingCondition.postProcess();
		for (Timer t:simulator.timerList)
			t.postProcess();
		simulator.ecosystem.postProcess();
	}

	public SimulatorStatus simulationStatus() {
		return status;
	}
	
}
