package au.edu.anu.twcore.experiment.runtime;

/**
 * 
 * @author Jacques Gignoux - 30 août 2019
 *
 */
public interface DeployerProcedures {
	
	public void runProc();
	public void waitProc();
	public void stepProc();
	public void finishProc();
	public void pauseProc();
	public void quitProc();

}
