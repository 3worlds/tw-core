package au.edu.anu.twcore.ecosystem.runtime.simulator;

/**
 * 
 * @author Jacques Gignoux - 30 août 2019
 *
 */
public interface SimulatorProcedures {
	
	public void runProc();
	public void waitProc();
	public void stepProc();
	public void finishProc();
	public void pauseProc();
	public void quitProc();

}
