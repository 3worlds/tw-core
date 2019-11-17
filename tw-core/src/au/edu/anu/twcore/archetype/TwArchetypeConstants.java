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

package au.edu.anu.twcore.archetype;

/**
 * @author Ian Davies
 *
 * @date 8 Aug 2019
 */
/*
 * See about naming conventions as this develops. Refactoring is easy. For the
 * moment I just prefix these with twa (tw archetype) to distinguish from aa (archetype
 * archetype contants cf:
 */
public interface TwArchetypeConstants {
	public final static String twaName = "name";
	public final static String twaSubclass = "subclass";
	public final static String twaValues = "values";
	public final static String twaParameters = "parameters";
	public final static String twaNodeLabel1 = "nodeLabel1";
	public final static String twaNodeLabel2 = "nodeLabel2";
	public final static String twaEdgeLabel1 = "edgeLabel1";
	public final static String twaEdgeLabel2 = "edgeLabel2";
	public final static String twaConditions = "conditions";

}
