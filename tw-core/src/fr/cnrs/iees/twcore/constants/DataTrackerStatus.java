package fr.cnrs.iees.twcore.constants;

/**
 * Status of objects sending data for output
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public enum DataTrackerStatus {
	
	Initial,  	// data tracker is up but is not yet sending data
	Active,		// data tracker is up and sending data
	Final;		// data tracker is up but has finished sending data

}
