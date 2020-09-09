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
package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.Collection;

import fr.cnrs.iees.graph.Edge;
import fr.cnrs.iees.graph.Graph;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.uit.space.Point;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public interface Space<T> {

	/**
	 * Every space is contained within a n-dim bounding box. This function returns
	 * the bounding box (useful for drawing the space).
	 * 
	 * @return the space bounding box
	 */
	public Box boundingBox();
	
	/**
	 * part of the space that is considered to compute statistics and outputs - enables to
	 * have a safety zone around the central space to reduce edge effects. NB in most
	 * cases it's = boundingBox().
	 * 
	 * @return
	 */
	public Box observationWindow();
	
//	/**
//	 * Every space has an edge effect correction method attached to it.
//	 * 
//	 * @return
//	 */
//	public EdgeEffects edgeEffectCorrection();
	
	/**
	 * Spaces can be 1,2,3,n-dimensional. This function returns their dimension.
	 * 
	 * @return the space dimension
	 */
	public int ndim();
	
	/**
	 * A space can be represented as a graph of SystemComponents and SystemRelations. This
	 * function returns such a graph. Useful for space complexity comparisons.
	 * 
	 * @return
	 */
	public Graph<? extends Node, ? extends Edge> asGraph();
	
	/**
	 * A drawable object representing this space
	 * 
	 * @return
	 */
	public TwShape asShape();
	
	/**
	 * Locates a system component within this space. This will trigger a call to 
	 * SystemComponent.initialLocation()
	 * 
	 * @param focal the system to add
	 *
	 */
	public Location locate(T focal, double...location);
	
	public Location locate(T focal, Point location);
	
	public Location locate(T focal, Location location);
	
//	/**
//	 * Sets the location of unclearable items - these locations are not clearable through the 
//	 * clear() method and will be kept forever.
//	 * 
//	 * @param focal
//	 * @param location
//	 */
//	public void locateUnclearable(T focal, double...location);
	
	/**
	 * Finds the location of an item in this space
	 * 
	 * @param focal
	 * @return
	 */
//	public Point locationOf(T focal);
	public Location locationOf(T focal);
	
	/**
	 * Removes the system component focal from this space.
	 * 
	 * @param focal the system to remove
	 * 
	 */
	public void unlocate(T focal);

	public void unlocate(Collection<T> focal);

	/**
	 * ABSOLUTE precision (in space distance units), ie distance below which locations are considered
	 * identical.
	 * NB precision is used to assess if two points are at the same location
	 * 
	 * @return the precision of location coordinates
	 */
	public double precision();
	
	/**
	 * 
	 * @return the measurement unit of locations
	 */
	public String units();
	
	/**
	 * 
	 * @return the SpaceType matching this particular descendant
	 */
	public default SpaceType type() {
		for (SpaceType st:SpaceType.values())
			if (st.className().equals(this.getClass().getName()))
				return st;
		return null;
	}
	
	/**
	 * gets all the items located at the shortest distance from the focal item, excluding itself.
	 * It allows for items having the same location.
	 * 
	 * @param item
	 * @return
	 */
	public Iterable<T> getNearestItems(T item);
	
	/**
	 * gets all items within a distance of the focal item.
	 * 
	 * @param item
	 * @param distance
	 * @return
	 */
	public Iterable<T> getItemsWithin(T item, double distance);

	public default double[] defaultLocation() {
		Point c = boundingBox().centre();
		double[] coords = new double[c.dim()];
		for (int i=0; i<coords.length; i++)
			coords[i] = c.coordinate(i);
		return coords;
	}

	/**
	 * clears all items EXCEPT those that were located with locateUnclearable
	 */
	public void clear();
	
	/**
	 * return a location from x coordinates
	 * @param x
	 * @return
	 */
	public Location makeLocation(double...x);
	
	/**
	 * return a location from a point
	 * @param point
	 * @return
	 */
	public Location makeLocation(Point point);
	
	/**
	 * A method to check that a series of numbers matches an (usually already existing) location
	 * @param reference the reference location to compare to
	 * @param candidate the candidate coordinates to compare to the reference
	 * @return true if candidate coordinates are not different from reference location coordinates
	 */
	public boolean equalLocation(Location reference, double[] candidate);
	
	/**
	 * check and fix location of proposed point as per edge effect correction
	 * 
	 * @param location
	 * @return
	 */
	public double[] fixLocation(double[] location);
	
	/**
	 * fix location of other relative to focal according to edge effect correction
	 * 
	 * @param focal
	 * @param other
	 * @return
	 */
	public Point fixOtherLocation(Point focal, Point other);

	
}
