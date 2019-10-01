package au.edu.anu.twcore.ecosystem.runtime.simulator;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeData;
import au.edu.anu.twcore.ecosystem.Ecosystem;
import au.edu.anu.twcore.ecosystem.dynamics.ProcessNode;
import au.edu.anu.twcore.ecosystem.dynamics.TimeLine;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataMessageTypes;
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
	
	// CLASSES
	
	/** a data tracker to send time data */
	private class TimeTracker extends AbstractDataTracker<TimeData,Metadata> {
		private TimeTracker() {
			super(DataMessageTypes.TIME);
		}
	}
	
	// FIELDS
	
	private static Logger log = Logging.getLogger(Simulator.class);
	/** class constant = number of simulators in this running session */
	private static int N_INSTANCES = 0;
	/** this simulator's unique id */ 
	private int id = 0;
	/** helper local field to build up and send metadata to data observers */
	private Metadata metadata;
	/** the list of timers (timeModels) in use in this simulator */
	private List<Timer> timerList = null;
	/** the current time of each timer */
	private long[] currentTimes;
	/** a bit pattern uniquely identifying each timer  (NB: this is probably useless optimisation) */
	private int[] timeModelMasks; // bit pattern for every timeModel
	/** the time origin */
	private long startTime = 0L;
	/** the last time for which a computation with done, ie the one just before current time */
	protected long lastTime = 0L;
	/** the stopping condition.
	 There is always exactly one stopping condition.
	 When there are many, they are organized as a tree */
	protected StoppingCondition stoppingCondition;
	/** the time line for this simulator, common to all timers */
	private TimeLine refTimer;
	/** the calling order of processes depending on the combination of
	 * simultaneous time models */
	private Map<Integer, List<List<ProcessNode>>> processCallingOrder;
	/** the timeTracker, sending time information to whoever is listening */
	private TimeTracker timetracker; 
	/** simulator state fields */
	private boolean started = false;
	private boolean finished = false;
	/** container for all SystemComponents */
	private SystemContainer community;
	
	// CONSTRUCTORS

	/**
	 * Every call to this constructor increments N_INSTANCES. Every new instance has a unique id.  
	 * 
	 * @param stoppingCondition
	 * @param refTimer
	 * @param timers
	 * @param timeModelMasks
	 * @param processCallingOrder
	 * @param ecosystem
	 */
	public Simulator(StoppingCondition stoppingCondition, 
			TimeLine refTimer,
			List<Timer> timers,
			int[] timeModelMasks,
			Map<Integer, List<List<ProcessNode>>> processCallingOrder,
			Ecosystem ecosystem) {
		super();
		N_INSTANCES++;
		id = N_INSTANCES;
		this.stoppingCondition = stoppingCondition;
		this.refTimer = refTimer;
		this.timerList=timers;
		this.timeModelMasks = timeModelMasks;
		this.processCallingOrder = processCallingOrder;
		this.community = (SystemContainer) ecosystem.community();
		// looping aids
		currentTimes = new long[timerList.size()];
		// data tracking
		timetracker = new TimeTracker();
		// copies initial community to current community to start properly
		community.reset();
	}
	
	// METHODS
	
	private DataTrackerStatus status() {
		if (started)
			if (finished)
				return DataTrackerStatus.Final;
			else
				return DataTrackerStatus.Active;
		else
			return DataTrackerStatus.Initial;
	}
	
	public void addObserver(DataReceiver<TimeData,Metadata> observer) {
		timetracker.addObserver(observer);
		// as metadata, send all properties of the reference TimeLine of this simulator.
		metadata = new Metadata(status(),id,refTimer.properties());
		timetracker.sendMetadata(metadata);
	}
	
	// run one simulation step
	@SuppressWarnings("unused")
	public void step() {
		if (!started)
			resetSimulation();
		started = true;
		log.info("Time = "+lastTime);
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
			// 3 execute all the processes depending on these time models
			List<List<ProcessNode>> currentProcesses = processCallingOrder.get(ctmask);
			// loop on dependency rank
			for (int j = 0; j < currentProcesses.size(); j++) {
				List<ProcessNode> torun = currentProcesses.get(j);
				// execute all processes at the same dependency level
				for (ProcessNode p : torun) {
					p.execute(nexttime, step); 
				}
			}
			// 4 advance time ONLY for those time models that were processed
			i = 0;
			for (Timer tm : timerList) {
				if ((timeModelMasks[i] & ctmask) != 0)
					tm.advanceTime(lastTime);
				i++;
			}
			// apply all changes to community
			community.stepAll();
			community.effectAllChanges();
//			
////			// 7 Send graph data to whoever is listening
////			graphWidgets = getGraphListeners();
////			for (GridNode gn:graphWidgets) {
////				Payload p = new Payload().startWriting();
////				p.writeInt(Sim3wMessageType.GRAPH_NEW);
////				p.writeNodeList(configuration);
////				p.endWriting();
////				GraphMessage msg = new GraphMessage(
////					new MessageHeader((Integer)gn.getPropertyValue("messageID")+Sim3wMessageType.GRAPH_NEW,this,gn),
////					p);
////				gn.callRendezvous(msg);
////			}
			timetracker.sendData(makeTimeRecord());
		}
	}
	
	private TimeData makeTimeRecord() {
//		TimeData output = new TimeData(status(),id,DataMessageTypes.TIME);
		TimeData output = new TimeData(status(),id,metadata.type());
		output.setTime(lastTime);
		return output;
	}
	
//	private void runSelectedProcesses(int mask, long nexttime, long step) {
//		// 3 execute all the processes depending on these time models
//		List<List<ProcessNode>> currentProcesses = processCallingOrder.get(mask);
//		// loop on dependency rank
//		for (int j = 0; j < currentProcesses.size(); j++) {
//			List<ProcessNode> torun = currentProcesses.get(j);
//			// execute all processes at the same dependency level
//			for (ProcessNode p : torun) {
//				p.execute(nexttime, step); 
//			}
//		}
//		// TODO: replace current by next state ! update age !
//		community.effectAllChanges();
//		// 4 advance time ONLY for those time models that were processed
//		int i = 0;
//		for (Timer tm : timerList) {
//			if ((timeModelMasks[i] & mask) != 0)
//				tm.advanceTime(lastTime);
//			i++;
//		}
////		myWorld.update(lastTime);
////		
//////		// 7 Send graph data to whoever is listening
//////		graphWidgets = getGraphListeners();
//////		for (GridNode gn:graphWidgets) {
//////			Payload p = new Payload().startWriting();
//////			p.writeInt(Sim3wMessageType.GRAPH_NEW);
//////			p.writeNodeList(configuration);
//////			p.endWriting();
//////			GraphMessage msg = new GraphMessage(
//////				new MessageHeader((Integer)gn.getPropertyValue("messageID")+Sim3wMessageType.GRAPH_NEW,this,gn),
//////				p);
//////			gn.callRendezvous(msg);
//////		}
//	}

	
	// resets a simulation at its initial state
	public void resetSimulation() {
//		if (started) { // otherwise no point to reset
			lastTime = startTime;
			stoppingCondition.reset();
			started = false;
			finished = false;
			for (Timer t:timerList)
				t.reset();
			timetracker.sendData(makeTimeRecord());
			community.reset();
//		}
	}

	// returns true if stopping condition is met
	public boolean stop() {
		finished = stoppingCondition.stop(); 
		return finished;
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
