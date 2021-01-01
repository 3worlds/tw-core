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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import au.edu.anu.twcore.rngFactory.RngFactory.Generator;
import fr.cnrs.iees.twcore.constants.RngAlgType;
import fr.cnrs.iees.twcore.constants.RngResetType;
import fr.cnrs.iees.twcore.constants.RngSeedSourceType;

class RngFactoryTest {

	private final static int trials = 100_000_000;


	private long timing(Random rng) {
		long s = System.nanoTime();
		double sum = 0;
		for (int i = 0; i < trials; i++)
			sum += rng.nextDouble();
		long e = System.nanoTime();
		double mean = sum / (double) trials;
		System.out.println(rng.getClass().getSimpleName()+ " mean ="+mean);
		assertTrue(mean > 0.4999);
		assertTrue(mean < 0.5001);
		return e - s;
	}

	private void checkRange(Random rng) {
		double min = Double.MAX_VALUE;
		double max = -min;
		for (int i = 0; i < trials; i++) {
			double v = rng.nextDouble();
			min = Math.min(min, v);
			max = Math.max(max, v);
		}
		assertTrue(min >= 0.0);
		assertTrue(max < 1.0);
	}

	@Test
	void test() {

		Generator rngJava = RngFactory.newInstance("Random", 0, RngResetType.ONRUNSTART, RngSeedSourceType.CONSTANT, RngAlgType.JAVA);
		Generator rngXSRandom = RngFactory.newInstance("XSRandom", 0, RngResetType.ONRUNSTART, RngSeedSourceType.CONSTANT, RngAlgType.XSRANDOM);
		Generator rngPCG = RngFactory.newInstance("PCGRandom", 0, RngResetType.ONRUNSTART, RngSeedSourceType.CONSTANT,RngAlgType.PCG32);
		
		Generator crap = RngFactory.newInstance("CRAP", 0, RngResetType.NEVER, RngSeedSourceType.PSEUDO, RngAlgType.JAVA);
		System.out.println(crap.getRandom().nextDouble());
				

		Random random = rngJava.getRandom();
		Random xsRandom = rngXSRandom.getRandom();
		Random pcgRandom = rngPCG.getRandom();

//		rngJava.resetRun();
//		rngXSRandom.resetRun();
//		rngPCG.resetRun();
		RngFactory.resetRun();
		double v1 = random.nextDouble();
		double v2 = xsRandom.nextDouble();
		double v3 = pcgRandom.nextDouble();
		
		RngFactory.resetRun();
//		rngJava.resetRun();
//		rngXSRandom.resetRun();
//		rngPCG.resetRun();
		assertEquals(v1, random.nextDouble());
		assertEquals(v2, xsRandom.nextDouble());
		assertEquals(v3, pcgRandom.nextDouble());

		System.out.println("Range check Random");
		checkRange(random);
		System.out.println("Range check XSRandom");
		checkRange(xsRandom);
		System.out.println("Range check PCGRandom");
		checkRange(pcgRandom);

//		rngJava.resetRun();
//		rngXSRandom.resetRun();
//		rngPCG.resetRun();
		RngFactory.resetRun();

		System.out.println("Time trial Random");
		double t1 = timing(random);
		System.out.println("Time trial XSRandom");
		double t2 = timing(xsRandom);
		System.out.println("Time trial PCGRandom");
		double t3 = timing(pcgRandom);
		System.out.println(("xsRandom is: " + (1 - t2 / t1) * 100) + " % faster than Java.util.Random");
		System.out.println(("pcgRandom is: " + (1 - t3 / t1) * 100) + " % faster than Java.util.Random");
	}


}
