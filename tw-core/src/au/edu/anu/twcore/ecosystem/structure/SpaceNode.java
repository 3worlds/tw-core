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
package au.edu.anu.twcore.ecosystem.structure;

import java.util.*;

import fr.cnrs.iees.omugi.collections.tables.StringTable;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.RngNode;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.SpaceData;
import au.edu.anu.twcore.ecosystem.runtime.space.FlatSurface;
import au.edu.anu.twcore.ecosystem.runtime.space.ObserverDynamicSpace;
import au.edu.anu.twcore.ecosystem.runtime.space.SquareGrid;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SingleDataTrackerHolder;
import au.edu.anu.twcore.ui.runtime.DataReceiver;
import fr.cnrs.iees.omugi.graph.*;
import fr.cnrs.iees.omugi.identity.Identity;
import fr.cnrs.iees.omugi.properties.*;
import fr.cnrs.iees.omugi.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.rvgrid.rendezvous.GridNode;
import fr.cnrs.iees.twcore.constants.BorderListType;
import fr.cnrs.iees.twcore.constants.BorderType;
import fr.cnrs.iees.twcore.constants.EdgeEffectCorrection;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.cnrs.iees.uit.space.Box;
import fr.cnrs.iees.omhtk.*;
import fr.cnrs.iees.omhtk.utils.Interval;

import static au.edu.anu.qgraph.queries.CoreQueries.*;
import static au.edu.anu.qgraph.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 *
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class SpaceNode extends InitialisableNode
		implements LimitedEdition<ObserverDynamicSpace>, Sealable {

	private boolean sealed = false;
	private Map<Integer, ObserverDynamicSpace> spaces = new HashMap<>();
	private SpaceType stype = null;
	private EdgeEffectCorrection eecorr = null;
	private StringTable borderList = null;// Order x,y,(z)... pairs : left,right, bottom, top
	private Box obsWindow = null;
	private double guardWidth = 0.0;
	// the name of coordinates relative to this space (eg "<this.id()>.x")
	private Set<String> coordNames = new HashSet<>();
	private String units = "arbitrary units";
	private double precision = 0.0;
	private RngNode rngNode = null;
	private boolean attachDataTrackerToSpace = false;

	public SpaceNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public SpaceNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		stype = (SpaceType) properties().getPropertyValue(P_SPACETYPE.key());
		// eecorr = (EdgeEffectCorrection)
		// properties().getPropertyValue(P_SPACE_EDGEEFFECTS.key());
		// if (properties().hasProperty(P_SPACE_BORDERTYPE.key()))

		// actually a (BorderListType) - descendant of StringTable 1D otherwise.
		// BorderListType exists so the MM property sheet can decide on which property
		// editor to use based on its class.
		borderList = (BorderListType) properties().getPropertyValue(P_SPACE_BORDERTYPE.key());
		eecorr = BorderListType.getEdgeEffectCorrection((BorderListType) borderList);
		if (properties().hasProperty(P_SPACE_PREC.key()))
			precision = (double) properties().getPropertyValue(P_SPACE_PREC.key());
		if (properties().hasProperty(P_SPACE_UNITS.key()))
			units = (String) properties().getPropertyValue(P_SPACE_UNITS.key());
		if (properties().hasProperty(P_SPACE_GUARDAREA.key()))
			guardWidth = (double) properties().getPropertyValue(P_SPACE_GUARDAREA.key());
		if (properties().hasProperty(P_SPACE_OBSWINDOW.key()))
			obsWindow = (Box) properties().getPropertyValue(P_SPACE_OBSWINDOW.key());
		rngNode = (RngNode) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_USERNG.label())), endNode());
		// if at least one widget is listening to this space, add a datatracker to space
		List<Edge> l = (List<Edge>) get(edges(Direction.IN),
			selectZeroOrMany(hasTheLabel(E_TRACKSPACE.label())));
		attachDataTrackerToSpace = !l.isEmpty();
		seal();
	}

	@Override
	public int initRank() {
		return N_SPACE.initRank();
	}

	private BorderType[][] getBorders() {
		// 1st dimension of borderTypes = 2 (lower/upper border)
		// 2nd dimension of borderTypes = ndim (number of dimensions of space)

		// WATCH OUT! We now have some conflict between IDD and JG! I have made
		// borderTypes a 1 D stringTable (BorderListType)
		// I won't go any further with this for fear of trashing where this connects
		// with the uit
//		int ndim = borderTypes.getDimensioners()[1].getLength();
		int ndim = borderList.size() / 2;
		BorderType[][] result = new BorderType[2][ndim];
		// I'm assuming that borderList values are in pairs. So for 2d left,right,
		// bottom, top.
		// However, y axis in the GUI increases DOWN while Cartesian it goes up so top
		// and bottom may need to be reversed somewhere, probably just in the
		// borderListType property editor or maybe its taken care of in the rendering.
		// Wait and see.
		for (int i = 0; i < ndim; i++) {
			int x1 = i * 2;
			int x2 = x1 + 1;
//			result[0][i] = BorderType.valueOf(borderList.getByInt(0, i));
//			result[1][i] = BorderType.valueOf(borderList.getByInt(1, i));
			result[0][i] = BorderType.valueOf(borderList.getWithFlatIndex(x1));
			result[1][i] = BorderType.valueOf(borderList.getWithFlatIndex(x2));
		}
		return result;
	}

	private ObserverDynamicSpace makeSpace(int id) {
		ObserverDynamicSpace result = null;
		SpaceDataTracker dt = null;
		if (attachDataTrackerToSpace) {
			// weird: bug fix: attach time metadata to data tracker ???
			// space <- structure <- system -> dynamics -> timeline
			ReadOnlyDataHolder timeLine = (ReadOnlyDataHolder) get(getParent().getParent().getChildren(),
					selectOne(hasTheLabel(N_DYNAMICS.label())), children(), selectOne(hasTheLabel(N_TIMELINE.label())));
			ExtendablePropertyList l = new ExtendablePropertyListImpl();
			l.addProperties(timeLine.properties());
			l.addProperties(properties());
//			dt = new SpaceDataTracker(id,properties());
			dt = new SpaceDataTracker(id, l);
		}
		BorderType[][] borders = eecorr.borderTypes(stype.dimensions());
		if (borders == null)
			borders = getBorders();
		switch (stype) {
		case continuousFlatSurface:
			Interval xlim = (Interval) properties().getPropertyValue(P_SPACE_XLIM.key());
			Interval ylim = (Interval) properties().getPropertyValue(P_SPACE_YLIM.key());
			result = new FlatSurface(xlim.inf(), xlim.sup(), ylim.inf(), ylim.sup(), precision, units, borders,
					obsWindow, guardWidth, dt, id(),id);
			break;
		case linearNetwork:
			break;
		case squareGrid:
			double cellSize = (double) properties().getPropertyValue(P_SPACE_CELLSIZE.key());
			int nx = (int) properties().getPropertyValue(P_SPACE_NX.key());
			int ny = nx;
			if (properties().hasProperty("ny"))
				ny = (int) properties().getPropertyValue(P_SPACE_NY.key());
			result = new SquareGrid(cellSize, nx, ny, units, borders, obsWindow, guardWidth, dt, id(),id);
			break;
		case topographicSurface:
			break;
		default:
			break;
		}
		if (rngNode != null)
			result.setRng(rngNode.getInstance(id));
		else
			result.setRng(result.defaultRng(id));
		return result;
	}

	@Override
	public ObserverDynamicSpace getInstance(int id) {
		if (!sealed)
			initialise();
		if (!spaces.containsKey(id))
			spaces.put(id, makeSpace(id));
		return spaces.get(id);
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

	public Iterable<String> coordinateNames() {
		if (!sealed)
			initialise();
		return coordNames;
	}

	public void attachSpaceWidget(DataReceiver<SpaceData, Metadata> widget) {
		for (ObserverDynamicSpace sp : spaces.values())
			if (sp instanceof SingleDataTrackerHolder) {
				SpaceDataTracker dts = sp.dataTracker();
				if (dts != null) {
					dts.addObserver(widget);
//					dts.sendMetadata(dts.getInstance());
					dts.sendMetadataTo((GridNode) widget, dts.getInstance());
				}
			}
	}

}
