package au.edu.anu.twcore.ecosystem.runtime.space;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.cnrs.iees.twcore.constants.BorderType;

/**
 * Testing distance computation with edge effects
 * 
 * @author Jacques Gignoux - 4 sept. 2020
 *
 */
class FlatSurfaceTest {
		 
	private BorderType[][] bt = {{BorderType.wrap,BorderType.wrap},{BorderType.wrap,BorderType.wrap}};
	private BorderType[][] bt2 = {{BorderType.sticky,BorderType.sticky},{BorderType.sticky,BorderType.sticky}};
	private BorderType[][] bt3 = {{BorderType.wrap,BorderType.oblivion},{BorderType.wrap,BorderType.sticky}};
	private FlatSurface flat = new FlatSurface(0,20,20,50,0.01,"m",bt,null,"FS");
	private FlatSurface flat2 = new FlatSurface(0,20,20,50,0.01,"m",bt2,null,"FS");
	private FlatSurface flat3 = new FlatSurface(0,20,20,50,0.01,"m",bt3,null,"FS");
	private double[] A = {5,30};
	private double[] B = {10,35};
	private double[] C = {15,20};
	private double[] D = {0,25};
	private double[] E = {20,50};
	private double[] F = {25,15};
	private double[] G = {12,52};
	
	@BeforeEach
	private void init() {
//		flat = new FlatSurface(0,20,20,50,0.01,"m",bt,null,"FS");
	}

	@Test
	final void testSquaredEuclidianDistance() {
		assertEquals(flat.squaredEuclidianDistance(A,B),50);
		assertEquals(flat.squaredEuclidianDistance(A,C),200);
		assertEquals(flat.squaredEuclidianDistance(A,D),50);
		assertEquals(flat.squaredEuclidianDistance(B,C),250);
		assertEquals(flat.squaredEuclidianDistance(D,C),50);
		assertEquals(flat.squaredEuclidianDistance(A,E),125);
		
		assertEquals(flat2.squaredEuclidianDistance(A,B),50);
		assertEquals(flat2.squaredEuclidianDistance(D,C),250);
		assertEquals(flat2.squaredEuclidianDistance(A,E),625);
		
		assertEquals(flat3.squaredEuclidianDistance(A,B),50);
		assertEquals(flat3.squaredEuclidianDistance(D,C),50);
		assertEquals(flat3.squaredEuclidianDistance(A,E),425);
	}

	@Test
	final void testFixLocation() {
		double[] loc = flat.fixLocation(A);
		assertEquals(loc[0],A[0]);
		assertEquals(loc[1],A[1]);
		
		loc = flat.fixLocation(F);
		assertEquals(loc[0],5);
		assertEquals(loc[1],45);
		loc = flat2.fixLocation(F);
		assertEquals(loc[0],20);
		assertEquals(loc[1],20);
		loc = flat3.fixLocation(F);
		assertEquals(loc,null);
		
		loc = flat.fixLocation(G);
		assertEquals(loc[0],12);
		assertEquals(loc[1],22);
		loc = flat2.fixLocation(G);
		assertEquals(loc[0],12);
		assertEquals(loc[1],50);
		loc = flat3.fixLocation(G);
		assertEquals(loc[0],12);
		assertEquals(loc[1],50);
	}
	

}
