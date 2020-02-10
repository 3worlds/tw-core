package au.edu.anu.twcore.ecosystem.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.RngNode;
import au.edu.anu.twcore.ecosystem.runtime.space.FlatSurface;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import au.edu.anu.twcore.ecosystem.runtime.space.SquareGrid;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.utils.Interval;

import static au.edu.anu.rscs.aot.queries.CoreQueries.endNode;
import static au.edu.anu.rscs.aot.queries.CoreQueries.hasTheLabel;
import static au.edu.anu.rscs.aot.queries.CoreQueries.selectZeroOrOne;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.E_USERNG;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class SpaceNode 
		extends InitialisableNode 
		implements LimitedEdition<Space<SystemComponent>>, Sealable {
	
	private boolean sealed = false;
	private Map<Integer,Space<SystemComponent>> spaces = new HashMap<>();
	private SpaceType stype = null;
	// the name of coordinates relative to this space (eg "<this.id()>.x")
	private Set<String> coordNames = new HashSet<>();
	private String units = "arbitrary units";
	private double precision = 0.0;
	private RngNode rngNode = null;
	
	public SpaceNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SpaceNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}
	
	@Override
	public void initialise() {
		super.initialise();
		stype = (SpaceType) properties().getPropertyValue(P_SPACETYPE.key());
		if (properties().hasProperty(P_SPACE_PREC.key()))
			precision = (double)properties().getPropertyValue(P_SPACE_PREC.key());
		if (properties().hasProperty(P_SPACE_UNITS.key()))
			units = (String)properties().getPropertyValue(P_SPACE_UNITS.key());
		rngNode = (RngNode) get(edges(Direction.OUT),
			selectZeroOrOne(hasTheLabel(E_USERNG.label())),
			endNode());
		seal();
	}

	@Override
	public int initRank() {
		return N_SPACE.initRank();
	}
	
	private Space<SystemComponent> makeSpace(int id) {
		Space<SystemComponent> result = null;
		switch (stype) {
			case continuousFlatSurface:
				Interval xlim = (Interval) properties().getPropertyValue(P_SPACE_XLIM.key());
				Interval ylim = (Interval) properties().getPropertyValue(P_SPACE_YLIM.key());
				result = new FlatSurface(xlim.inf(),xlim.sup(),ylim.inf(),ylim.sup(),precision,units);
				break;
			case linearNetwork:
				break;
			case squareGrid:
				double cellSize = (double) properties().getPropertyValue(P_SPACE_CELLSIZE.key());
				int nx = (int) properties().getPropertyValue(P_SPACE_NX.key());
				int ny = nx;
				if (properties().hasProperty("ny"))
					ny = (int) properties().getPropertyValue(P_SPACE_NY.key());
				result = new SquareGrid(cellSize,nx,ny,precision,units);
				break;
			case topographicSurface:
				break;
			default:
				break;		
		}
		if (rngNode!=null)
			result.setRng(rngNode.getInstance(id));
		else
			result.setRng(result.defaultRng(id));
		return result;
	}

	@Override
	public Space<SystemComponent> getInstance(int id) {
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
	
}
