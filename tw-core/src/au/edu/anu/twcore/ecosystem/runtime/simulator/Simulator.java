package au.edu.anu.twcore.ecosystem.runtime.simulator;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.TimeData;
import au.edu.anu.twcore.ecosystem.dynamics.TimeLine;
import au.edu.anu.twcore.ecosystem.runtime.StoppingCondition;
import au.edu.anu.twcore.ecosystem.runtime.Timer;
import au.edu.anu.twcore.ecosystem.runtime.TwProcess;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemContainer;
import au.edu.anu.twcore.ecosystem.runtime.tracking.AbstractDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.DataMessageTypes;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;
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
		// returns quickly if there are no observers - no point building a TimeData
		private void sendData(long time) {
			if (hasObservers()) {
				TimeData output = new TimeData(status,id,metadata.type());
				output.setTime(lastTime);
				sendData(output);
			}
		}
	}
	
	// FIELDS
	
	private static Logger log = Logging.getLogger(Simulator.class);
	/** this simulator's unique id */ 
	private int id = -1;
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
//	/** the time line for this simulator, common to all timers */
//	private TimeLine refTimer;
	/** the calling order of processes depending on the combination of
	 * simultaneous time models */
	private Map<Integer, List<List<TwProcess>>> processCallingOrder;
	/** the timeTracker, sending time information to whoever is listening */
	private TimeTracker timetracker; 
	/** simulator state fields */
//	private boolean started = false;
//	private boolean finished = false;
	/** container for all SystemComponents */
	private SystemContainer community;
	/** simulator status */
	private SimulatorStatus status = SimulatorStatus.Initial;
	
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
	public Simulator(int id,
			StoppingCondition stoppingCondition, 
			TimeLine refTimer,
			List<Timer> timers,
			int[] timeModelMasks,
			Map<Integer, List<List<TwProcess>>> processCallingOrder,
			SystemContainer ecosystem) {
		super();
		this.id = id;
		this.stoppingCondition = stoppingCondition;
//		this.refTimer = refTimer;
		this.timerList=timers;
		this.timeModelMasks = timeModelMasks;
		this.processCallingOrder = processCallingOrder;
		this.community = ecosystem;
		// looping aids
		currentTimes = new long[timerList.size()];
		// data tracking
		timetracker = new TimeTracker();
		metadata = new Metadata(status,id,refTimer.properties());
		// copies initial community to current community to start properly
		community.reset();
	}
	
	// METHODS
	
//	private SimulatorStatus status() {
//		if (started)
//			if (finished)
//				return SimulatorStatus.Final;
//			else
//				return SimulatorStatus.Active;
//		else
//			return SimulatorStatus.Initial;
//	}
	
	public void addObserver(DataReceiver<TimeData,Metadata> observer) {
		timetracker.addObserver(observer);
		// as metadata, send all properties of the reference TimeLine of this simulator.		
		timetracker.sendMetadata(metadata);
	}
	
	// run one simulation step
	@SuppressWarnings("unused")
	public void step() {
		if (!isStarted())
			resetSimulation();
		status = SimulatorStatus.Active;
//		started = true;
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
			status = SimulatorStatus.Final;
//			finished = true;
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
			List<List<TwProcess>> currentProcesses = processCallingOrder.get(ctmask);
			// loop on dependency rank
			for (int j = 0; j < currentProcesses.size(); j++) {
				List<TwProcess> torun = currentProcesses.get(j);
				// execute all processes at the same dependency level
				for (TwProcess p : torun) {
//					p.execute(nexttime, step); 
					p.execute(status,nexttime,step);
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
			timetracker.sendData(lastTime);
		}
	}
	
	// resets a simulation at its initial state
	public void resetSimulation() {
		lastTime = startTime;
		stoppingCondition.reset();
		status = SimulatorStatus.Initial;
//		started = false;
//		finished = false;
		for (Timer t:timerList)
			t.reset();
		timetracker.sendData(lastTime);
		community.reset();
	}

	// returns true if stopping condition is met
	public boolean stop() {
		boolean finished = stoppingCondition.stop();
		if (finished)
			status = SimulatorStatus.Final;
		return finished;
	}
	
	public boolean isStarted() {
		return (status != SimulatorStatus.Initial);
	}
	
	public boolean isFinished() {
		return (status == SimulatorStatus.Final);
	}
	
	public long currentTime() {
		return lastTime;
	}
	
	public SystemContainer community() {
		return community;
	}
}
