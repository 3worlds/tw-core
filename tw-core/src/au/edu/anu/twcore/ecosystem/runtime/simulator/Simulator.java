package au.edu.anu.twcore.ecosystem.runtime.simulator;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.AbstractDataTracker;
import au.edu.anu.twcore.data.runtime.DataMessageTypes;
import au.edu.anu.twcore.data.runtime.LabelValuePairData;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeData;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.TimeLine;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.twcore.constants.DataTrackerStatus;
import fr.ens.biologie.generic.utils.Logging;

/**
 * The class which runs a single simulation on a single parameter set
 * 
 * @author Jacques Gignoux - 29 août 2019
 *
 */
public class Simulator {

	private static Logger log = Logging.getLogger(Simulator.class);
	private static int N_INSTANCES = 0;
	private int id = 0;
	private Metadata metadata;
	
	// a data tracker to send time data
//	private class timeTracker extends AbstractDataTracker<LabelValuePairData,Metadata> {
//		private timeTracker() {
//			super(DataMessageTypes.TIME);
//		}
//	}
	private class TimeTracker extends AbstractDataTracker<TimeData,Metadata> {
		private TimeTracker() {
			super(DataMessageTypes.TIME);
		}
	}
	
	private List<Timer> timerList = null;
	private long[] currentTimes;
	private int[] timeModelMasks; // bit pattern for every timeModel
	private long startTime = 0L;
	protected long lastTime = 0L;
	// there is always exactly one stopping condition
	// when there are many, they are organized as a tree
	protected StoppingCondition stoppingCondition;
	private TimeLine refTimer;
	/**
	 * the calling order of processes depending on the combination of
	 * simultaneous time models
	 */
	private Map<Integer, List<List<ProcessNode>>> processCallingOrder;
	//private timeTracker timetracker; 
	private TimeTracker timetracker; 

	// simulator state
	private boolean started = false;
	private boolean finished = false;
	// container for SystemComponents
	private SystemContainer community;

	public Simulator(StoppingCondition stoppingCondition, 
			TimeLine refTimer,
			List<Timer> timers,
			int[] timeModelMasks,
			Map<Integer, List<List<ProcessNode>>> processCallingOrder) {
		super();
		N_INSTANCES++;
		id = N_INSTANCES;
		this.stoppingCondition = stoppingCondition;
		this.refTimer = refTimer;
		this.timerList=timers;
		this.timeModelMasks = timeModelMasks;
		this.processCallingOrder = processCallingOrder;
		// looping aids
		currentTimes = new long[timerList.size()];
		// data tracking
//		timetracker = new timeTracker();		
		timetracker = new TimeTracker();		
	}
	
	private DataTrackerStatus status() {
		if (started)
			if (finished)
				return DataTrackerStatus.Final;
			else
				return DataTrackerStatus.Active;
		else
			return DataTrackerStatus.Initial;
	}
	
//	public void addObserver(DataReceiver<LabelValuePairData,Metadata> observer) {
//		timetracker.addObserver(observer);
//		// as metadata, send all properties of the reference TimeLine of this simulator.
//		metadata = new Metadata(status(),id,refTimer.properties());
//		timetracker.sendMetadata(metadata);
//	}
	public void addObserver(DataReceiver<TimeData,Metadata> observer) {
		timetracker.addObserver(observer);
		// as metadata, send all properties of the reference TimeLine of this simulator.
		metadata = new Metadata(status(),id,refTimer.properties());
		timetracker.sendMetadata(metadata);
	}
	
	// run one simulation step
	@SuppressWarnings("unused")
	public void step() {
		started = true;
		System.out.println("time = "+lastTime);
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
			timetracker.sendData(makeTimeRecord());
		}
	}
	
//	private LabelValuePairData makeTimeRecord() {
//		LabelValuePairData output = new LabelValuePairData(status(),id,metadata.type());
//		output.setValue(lastTime);
//		output.setLabel("t");
//		return output;
//	}
	private TimeData makeTimeRecord() {
		TimeData output = new TimeData(status(),id,DataMessageTypes.TIME);
		output.setTime(lastTime);
		return output;
	}
	
	private void runSelectedProcesses(int mask, long nexttime, long step) {
		// 3 execute all the processes depending on these time models
		List<List<ProcessNode>> currentProcesses = processCallingOrder.get(mask);
		// loop on dependency rank
		for (int j = 0; j < currentProcesses.size(); j++) {
			List<ProcessNode> torun = currentProcesses.get(j);
			// execute all processes at the same dependency level
			for (ProcessNode p : torun) {
				p.execute(nexttime, step); 
			}
			// update the state of systems depending on processes at the
			// same dependency level
			//TODO !	
//			for (ProcessNode p : torun)
//				p.updateState();
			
		}
		// 4 advance time ONLY for those time models that were processed
		int i = 0;
		for (Timer tm : timerList) {
			if ((timeModelMasks[i] & mask) != 0)
				tm.advanceTime(lastTime);
			i++;
		}
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
		if (started) { // otherwise no point to reset
			lastTime = startTime;
			stoppingCondition.reset();
			started = false;
			finished = false;
			for (Timer t:timerList)
				t.reset();
			timetracker.sendData(makeTimeRecord());
		}
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
