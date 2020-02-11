package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.Random;

import au.edu.anu.twcore.rngFactory.RngFactory;
import fr.cnrs.iees.twcore.constants.EdgeEffects;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.cnrs.iees.uit.space.Box;

/**
 * 
 * @author Jacques Gignoux - 30 janv. 2020
 *
 */
public abstract class SpaceAdapter<T extends Located> implements Space<T> {
	
	// the minimal relative precision of locations is 1E-5
	// ie points apart from less than this relative distance are considered to have the same location
	// it is relative to largest space size (diagonal)
	private static final double minimalPrecision = 0.00001;
	private Random rng = null;

	@Override
	public Random rng() {
		return rng;
	}

	@Override
	public void setRng(Random arng) {
		if (rng==null)
			rng = arng;
	}

	private Box limits;
	// absolute precision
	private double precision;
	private String units;
	private EdgeEffects correction;
	
	Random jitterRNG = RngFactory.newInstance("SpaceJitterRNG", 0, RngResetType.never, 
			RngSeedSourceType.secure,RngAlgType.Pcg32).getRandom();

	public SpaceAdapter(Box box, double prec, String units, EdgeEffects ee) {
		super();
		limits = box;
// precision based on diagonal, but this is probably unexpected for users		
//		double boxdiag = Math.sqrt(limits.sideLength(0)*limits.sideLength(0)
//			+limits.sideLength(1)*limits.sideLength(1));
//		precision = Math.max(prec,minimalPrecision)*boxdiag;
//	precision based on shortest side of plot
		precision = Math.max(prec,minimalPrecision)*Math.min(limits.sideLength(0),limits.sideLength(1));
		this.units = units;
		correction = ee;
	}

	@Override
	public final Box boundingBox() {
		return limits;
	}

	@Override
	public final double precision() {
		return precision;
	}

	@Override
	public final String units() {
		return units;
	}
	
	@Override
	public final EdgeEffects edgeEffectCorrection() {
		return correction;
	}


}
