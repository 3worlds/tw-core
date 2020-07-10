package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.twcore.constants.EdgeEffects;
import fr.cnrs.iees.uit.space.Box;

/**
 * Automatic variables for spaces
 *
 * @author J. Gignoux - 10 juil. 2020
 *
 */
public class SpaceData extends TwData {

	// constants
	/** space dimension (>=1)*/
	private int dimension;
	/** Space bounding box (rectangle)*/
	private Box limits;
	/** absolute precision */
	private double precision;
	/** Space measurement units */
	private String units;
	/** type of edge-effect correction */
	private EdgeEffects correction;
	/** absolute location of this space in the SpaceOrganiser -
	 * must be a box with dim = the greatest number of dims of any space */
	private Box absoluteLimits;

	private static String[] keyArray = {"dimension","limits","precision","units","correction","absoluteLimits"};
	private static Set<String> keySet = new HashSet<String>(Arrays.asList(keyArray));

	public SpaceData() {
		super();
	}

	@Override
	public TwData setProperty(String key, Object value) {
		if (key.equals("dimension")) dimension = (int) value;
		if (key.equals("limits")) limits = (Box) value;
		if (key.equals("precision")) precision = (double) value;
		if (key.equals("units")) units = (String) value;
		if (key.equals("correction")) correction = (EdgeEffects) value;
		if (key.equals("absoluteLimits")) absoluteLimits = (Box) value;
		return this;
	}

	@Override
	public Object getPropertyValue(String key) {
		if (key.equals("dimension")) return dimension;
		if (key.equals("limits")) return limits;
		if (key.equals("precision")) return precision;
		if (key.equals("units")) return units;
		if (key.equals("correction")) return correction;
		if (key.equals("absoluteLimits")) return absoluteLimits;
		return null;
	}

	@Override
	public boolean hasProperty(String key) {
		return keySet.contains(key);
	}

	@Override
	public String propertyToString(String key) {
		if (key.equals("dimension")) return String.valueOf(dimension);
		if (key.equals("limits")) return limits.toString();
		if (key.equals("precision")) return String.valueOf(precision);
		if (key.equals("units")) return units;
		if (key.equals("correction")) return correction.toString();
		if (key.equals("absoluteLimits")) return absoluteLimits.toString();
		return null;
	}

	@Override
	public Class<?> getPropertyClass(String key) {
		if (key.equals("dimension")) return Integer.class;
		if (key.equals("limits")) return Box.class;
		if (key.equals("precision")) return Double.class;
		if (key.equals("units")) return String.class;
		if (key.equals("correction")) return EdgeEffects.class;
		if (key.equals("absoluteLimits")) return Box.class;
		return null;
	}

	@Override
	public Set<String> getKeysAsSet() {
		return keySet;
	}

	@Override
	public String[] getKeysAsArray() {
		return keyArray;
	}

	@Override
	protected TwData cloneStructure() {
		return new SpaceData();
	}

	@Override
	public TwData clear() {
		// do nothing
		return this;
	}

	@Override
	public int size() {
		return keyArray.length;
	}

	public int dimension() {
		return dimension;
	}

	public Box limits() {
		return limits;
	}

	public double precision() {
		return precision;
	}

	public String units() {
		return units;
	}

	public EdgeEffects correction() {
		return correction;
	}

	public Box getAbsoluteLimits() {
		return absoluteLimits;
	}

	public void setAbsoluteLimits(Box absoluteLimits) {
		this.absoluteLimits = absoluteLimits;
	}

	@Override
	public TwData clone() {
		return cloneStructure().setProperties(this);
	}

}
