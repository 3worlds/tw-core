/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/

package au.edu.anu.twcore.rngFactory;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import au.edu.anu.omhtk.rng.Pcg32;
import au.edu.anu.omhtk.rng.RandomSeeds;
import au.edu.anu.omhtk.rng.XSRandom;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;
import fr.ens.biologie.generic.utils.Logging;

/**
 * Author Ian Davies
 *
 * Date Dec 5, 2018
 */
/**
 * <p>
 * Simple random number stream factory that manages resetting seeds in various
 * ways.
 * </p>
 * 
 * <p>
 * Usage: Model developers create a stream as:
 * </p>
 * {@code RandomFactory.makeRandom("test1", 0, ResetType.ONRUNSTART, SeedSource.SECURE, new [Pcg32()||XSRandom()||Random()]);}<br/>
 * <p>
 * Then use it as:
 * </p>
 * 
 * {@code Random rns = RandomFactory.getRandom("test1");}<br/>
 * 
 * {@code rns.nextDouble();} etc
 * </p>
 * 
 * <p>
 * This system contains a table of 1000 random numbers that have been generated
 * from atmospheric noise. This can be used as seeds by the given index if
 * SeedSource.TABLE is used. Alternatively, seeds can also be generated by
 * SecureRandom algorithm.
 * </p>
 * 
 * 
 */
public class RngFactory {

	private static Random seedGenerator = new SecureRandom();
	private static Logger log = Logging.getLogger(RngFactory.class);

	public final static class Generator {
		private RngResetType resetType;
		private long seed;
		private Random rng;
		private String id;

		private Generator(String id, long seed, RngResetType resetType, Random rng) {
			this.id = id;
			this.rng = rng;
			this.resetType = resetType;
			this.seed = seed;
			reset();
			log.info("Created random number stream" + toString());
		}

		private void reset() {
			rng.setSeed(seed);
		}

//		// not needed
//		public void resetRun() {
//			if (resetType == RngResetType.onRunStart) {
//				log.info("reset on run start '" + id + "'.");
//				reset();
//			}
//		}
//
//		// not needed
//		public void resetExperiment() {
//			if (resetType == RngResetType.onExperimentStart) {
//				log.info("reset on exp start '" + id + "'.");
//				reset();
//			}
//		}
//
		/** for saving to an initial state file */
		public long getState() {
			long state = rng.nextLong();
			// return to prev state.
			rng.setSeed(state);
			return state;
		}

		/** for reading from an initial state file */
		public void setState(long state) {
			rng.setSeed(state);
		}

		public String id() {
			return id;
		}

		public Random getRandom() {
			return rng;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("[class: ");
			sb.append(rng.getClass().getSimpleName());
			sb.append("; id: '");
			sb.append(id);
			sb.append("'; Seed: ");
			sb.append(seed);
			sb.append("; Reset: ");
			sb.append(resetType.name());
			sb.append("]");
			return sb.toString();

		}
	}

	private static Map<String, Generator> rngs = new HashMap<>();

	/**
	 * 
	 * There are 3 random number generators available. Note that SecureRandom cannot
	 * be used as it has no means of resetting the seed; a feature this factory
	 * relies upon. The currently available generators are:
	 * <ol>
	 * <li>Java.util.Random - medium speed, poor quality;</li>
	 * 
	 * <li>au.edu.anu.fses.rng.XSRandom - very fast (76% faster than
	 * Java.util.Random), medium quality</li>
	 * 
	 * <li>au.edu.anu.fses.rng.Pcg32 - fast (56% faster than Java.util.Random) and
	 * good quality</li>
	 * </ol>
	 * 
	 * The choice is really between 3 (faster) & 4 (better quality).
	 * 
	 * @param name      unique name
	 * @param seedIndex index into array[0..999] of naturally generated random
	 *                  numbers to act as seeds for resetting.
	 * @param resetType type of reset method (NEVER, ONRUNSTART, ONEXPERIMENTSTART)
	 * 
	 * @param source    Method of creating the random number seed (TABLE, SECURE,
	 *                  ZERO)
	 * @param rns       random number generator.
	 */
	public static Generator newInstance(String name, int seedIndex, RngResetType resetType, RngSeedSourceType source,
			RngAlgType algType) {
		Random rns = null;
		switch (algType) {
		case Pcg32: {
			rns = new Pcg32();
			break;
		}
		case XSRandom: {
			rns = new XSRandom();
			break;
		}
		default:
			rns = new Random();
		}

		if (rns instanceof SecureRandom)
			throw new TwcoreException("SecureRandom algorithm is not supported.");
		if (source.equals(RngSeedSourceType.table))
			if (seedIndex < 0 || seedIndex >= RandomSeeds.nSeeds())
				throw new TwcoreException(
						"SeedIndex is out of range [0.." + (RandomSeeds.nSeeds() - 1) + "] found: " + seedIndex);
		if (rngs.containsKey(name))
			throw new TwcoreException("Attempt to create duplicate random number generetor [" + name + "]");
		long seed;
		if (source.equals(RngSeedSourceType.table))
			seed = RandomSeeds.getSeed(seedIndex);
		else if (source.equals(RngSeedSourceType.secure))
			seed = seedGenerator.nextLong();
		else if (rns instanceof XSRandom)
			seed = 1L; // NB Cannot be set to zero!
		else
			seed = 0L;
//		log.info("Creating random stream [" + name + "; Seed: " + seed);
		Generator rng = new Generator(name, seed, resetType, rns);
		rngs.put(name, rng);
		return rng;
	}

	public static Generator find(String name) {
		return rngs.get(name);
	}

	public static void resetRun() {
		rngs.forEach((n, r) -> {
			if (r.resetType == RngResetType.onRunStart) {
				log.info("reset on run start '" + r.id + "'.");
				r.reset();
			}
		});
	}

	public static void resetExperiment() {
		rngs.forEach((n, r) -> {
			if (r.resetType == RngResetType.onExperimentStart) {
				log.info("reset on experiment start '" + r.id + "'.");
				r.reset();
			}
		});
	}
}
