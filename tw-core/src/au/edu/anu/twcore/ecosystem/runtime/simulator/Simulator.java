package au.edu.anu.twcore.ecosystem.runtime.simulator;

import java.util.List;
import java.util.logging.Logger;

import au.edu.anu.twcore.ecosystem.dynamics.TimeLine;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;

/**
 * The class which runs a single simulation on a single parameter set
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class Simulator {

	private static Logger log = Logger.getLogger(Simulator.class.getName());
	
	private List<Timer> timerList = null;
	private long[] currentTimes;
	private int[] timeModelMasks; // bit pattern for every timeModel
	private long startTime = 0L;
	protected long lastTime = 0L;
	// there is always exactly one stopping condition
	// when there are many, they are organized as a tree
	protected StoppingCondition stoppingCondition;
	private TimeLine refTimer;

	// simulator state
	private boolean started = false;
	private boolean finished = false;

	public Simulator(StoppingCondition stoppingCondition, 
			TimeLine refTimer,
			List<Timer> timers) {
		super();
		this.stoppingCondition = stoppingCondition;
		this.refTimer = refTimer;
		this.timerList=timers;
		timeModelMasks = new int[timerList.size()];
		int i = 0;
		int mask = 0x40000000;
		for (Timer tm : timerList) {
			timeModelMasks[i] = mask >> i;
			i++;
		}
		// looping aids
		currentTimes = new long[timerList.size()];
	}
	
	// run one simulation step
	public void step() {
		started = true;
		// 1 find next time step by querying timeModels
		long nexttime = Long.MAX_VALUE;
		int i = 0;
		for (Timer tm : timerList) {
			currentTimes[i] = tm.nextTime(lastTime);
			nexttime = Math.min(nexttime, currentTimes[i]);
			i++;
		}
		// advance main timer clock 
		if (nexttime == Long.MAX_VALUE)
			finished = true;
		// setState(SimulatorState.FINISHED);
		else {
			long step = nexttime - lastTime;
			lastTime = nexttime;
			// 2 find all timeModels which must execute now - using bitmasks for
			// searches
			i = 0;
			int ctmask = 0;
			for (Timer tm : timerList) { 
				if (currentTimes[i] == nexttime) {
					ctmask = ctmask | timeModelMasks[i];
				}
				i++;
			}
			runSelectedProcesses(ctmask, nexttime, step);
//			sendSimTimeMessage(null, nexttime/refTimer.timeGrain());
		}
	}
	
	private void runSelectedProcesses(int mask, long nexttime, long step) {
//		// 3 execute all the processes depending on these time models
//		List<List<ProcessNode>> currentProcesses = processCallingOrder.get(mask);
//		// loop on dependency rank
//		for (int j = 0; j < currentProcesses.size(); j++) {
//			List<ProcessNode> torun = currentProcesses.get(j);
//			// execute all processes at the same dependency level
//			for (ProcessNode p : torun) {
//				p.execute(nexttime, step); 
//			}
//			// update the state of systems depending on processes at the
//			// same dependency level
//			//TODO !	
////			for (ProcessNode p : torun)
////				p.updateState();
//			
//		}
//		// 4 advance time ONLY for those time models that were processed
//		int i = 0;
//		for (TimeModel tm : timerList) {
//			if ((timeModelMasks[i] & mask) != 0)
//				tm.advanceTime(lastTime);
//			i++;
//		}
//		myWorld.update(lastTime);
//		
////		// 7 Send graph data to whoever is listening
////		graphWidgets = getGraphListeners();
////		for (GridNode gn:graphWidgets) {
////			Payload p = new Payload().startWriting();
////			p.writeInt(Sim3wMessageType.GRAPH_NEW);
////			p.writeNodeList(configuration);
////			p.endWriting();
////			GraphMessage msg = new GraphMessage(
////				new MessageHeader((Integer)gn.getPropertyValue("messageID")+Sim3wMessageType.GRAPH_NEW,this,gn),
////				p);
////			gn.callRendezvous(msg);
////		}
	}

	
	// resets a simulation at its initial state
	public void resetSimulation() {
		lastTime = startTime;
		stoppingCondition.reset();
		started = false;
		finished = false;
	}

	// returns true if stopping condition is met
	public boolean stop() {
		return stoppingCondition.stop();
	}
	
	public boolean isStarted() {
		return started;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public long currentTime() {
		return lastTime;
	}
}