package au.edu.anu.twcore.ecosystem.runtime.space;

/**
 * For objects which can be located using LocationData
 *
 * @author J. Gignoux - 23 nov. 2020
 *
 * to Ian: Isnt that name ugly? I tried to find the worst one...
 * other possible names: Placeable? Localisable? Situable? Schtroumpfable?
 *
 */
public interface Locatable {

	/**
	 * A LocationData object stores values that are used to compute spatial coordinates.
	 * eg to access the coordinates on Locatable A, type A.locationData().coordinates();
	 *
	 * @return the LocationData object for this Locatable
	 */
	public LocationData locationData();

	/**
	 *
	 * @return true if the Locatable can change spatial coordinates over time, false otherwise
	 */
	public boolean mobile();

}
