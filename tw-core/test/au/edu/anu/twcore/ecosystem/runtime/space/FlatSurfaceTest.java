package au.edu.anu.twcore.ecosystem.runtime.space;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import fr.cnrs.iees.twcore.constants.BorderType;
import fr.cnrs.iees.uit.space.Point;

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
	private double[] E = {20,50};
	private double[] F = {25,15};
	private double[] G = {12,52};
	
	@BeforeEach
	private void init() {
//		flat = new FlatSurface(0,20,20,50,0.01,"m",bt,null,"FS");
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

	@Test
	final void testOtherClosestLocation() {
		assertEquals(flat.fixOtherLocation(Point.newPoint(A),Point.newPoint(B)).coordinate(0),10);
		assertEquals(flat.fixOtherLocation(Point.newPoint(A),Point.newPoint(B)).coordinate(1),35);
		assertEquals(flat.fixOtherLocation(Point.newPoint(A),Point.newPoint(E)).coordinate(0),0);
		assertEquals(flat.fixOtherLocation(Point.newPoint(A),Point.newPoint(E)).coordinate(1),20);
		
		assertEquals(flat2.fixOtherLocation(Point.newPoint(A),Point.newPoint(E)).coordinate(0),20);
		assertEquals(flat2.fixOtherLocation(Point.newPoint(A),Point.newPoint(E)).coordinate(1),50);
		
		assertEquals(flat3.fixOtherLocation(Point.newPoint(A),Point.newPoint(E)).coordinate(0),0);
		assertEquals(flat3.fixOtherLocation(Point.newPoint(A),Point.newPoint(E)).coordinate(1),50);
	}

}
