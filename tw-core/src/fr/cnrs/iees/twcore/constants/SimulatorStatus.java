package fr.cnrs.iees.twcore.constants;

/**
 * Status of simulator for informing data receivers
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public enum SimulatorStatus {
	
	Initial,  	// simulator is up but is not yet sending data
	Active,		// simulator is up and sending data
	Final;		// simulator is up but has finished sending data

}
