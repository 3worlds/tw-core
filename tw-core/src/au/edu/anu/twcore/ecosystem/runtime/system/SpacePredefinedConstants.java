package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import au.edu.anu.rscs.aot.collections.tables.DoubleTable;
import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

/**
 * Automatic variables for spaces
 *
 * @author J. Gignoux - 10 juil. 2020
 *
 */
public class SpacePredefinedConstants extends TwData {

	// constants
	/** space dimension (>=1)*/
	private int dimension = 0;
	/** Space bounding box (rectangle)*/
	private Box limits;
	// FLAW HERE: replace with DoubleTable !!!
	private double[] lowerLimit = null;
	private double[] upperLimit = null;
	/** absolute precision */
	private double precision;
	/** Space measurement units */
	private String units;
	/** absolute location of this space in the SpaceOrganiser -
	 * must be a box with dim = the greatest number of dims of any space */
	private Box absoluteLimits;

	private static String[] keyArray = {"dimension","lowerLimit","upperLimit","precision","units"};
	private static Set<String> keySet = new HashSet<String>(Arrays.asList(keyArray));

	public SpacePredefinedConstants() {
		super();
	}

	@Override
	public TwData setProperty(String key, Object value) {
		if (key.equals("dimension")) {
			dimension = (int) value;
			if (dimension!=lowerLimit.length)
				lowerLimit = null;
			if (dimension!=upperLimit.length)
				upperLimit = null;
			if ((dimension!=0)&&(lowerLimit!=null)&&(upperLimit!=null))
				limits = Box.boundingBox(Point.newPoint(lowerLimit), Point.newPoint(upperLimit));
		}
		if (key.equals("lowerLimit")) {
			lowerLimit = (double[]) value;
			if (dimension!=lowerLimit.length)
				dimension = lowerLimit.length;
			if (upperLimit.length!=lowerLimit.length)
				upperLimit = null;
			if ((dimension!=0)&&(lowerLimit!=null)&&(upperLimit!=null))
				limits = Box.boundingBox(Point.newPoint(lowerLimit), Point.newPoint(upperLimit));
		}
		if (key.equals("upperLimit")) {
			upperLimit = (double[]) value;
			if (dimension!=upperLimit.length)
				dimension = upperLimit.length;
			if (upperLimit.length!=lowerLimit.length)
				lowerLimit = null;
			if ((dimension!=0)&&(lowerLimit!=null)&&(upperLimit!=null))
				limits = Box.boundingBox(Point.newPoint(lowerLimit), Point.newPoint(upperLimit));
		}
		if (key.equals("precision")) precision = (double) value;
		if (key.equals("units")) units = (String) value;
		return this;
	}

	@Override
	public Object getPropertyValue(String key) {
		if (key.equals("dimension")) return dimension;
		if (key.equals("lowerLimit")) return lowerLimit;
		if (key.equals("upperLimit")) return upperLimit;
		if (key.equals("precision")) return precision;
		if (key.equals("units")) return units;
		return null;
	}

	@Override
	public boolean hasProperty(String key) {
		return keySet.contains(key);
	}

	@Override
	public String propertyToString(String key) {
		if (key.equals("dimension")) return String.valueOf(dimension);
		if (key.equals("lowerLimit")) return lowerLimit.toString();
		if (key.equals("upperLimit")) return upperLimit.toString();
		if (key.equals("precision")) return String.valueOf(precision);
		if (key.equals("units")) return units;
		return null;
	}

	@Override
	public Class<?> getPropertyClass(String key) {
		if (key.equals("dimension")) return Integer.class;
		if (key.equals("lowerLimit")) return DoubleTable.class;
		if (key.equals("upperLimit")) return DoubleTable.class;
		if (key.equals("precision")) return Double.class;
		if (key.equals("units")) return String.class;
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
		return new SpacePredefinedConstants();
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
