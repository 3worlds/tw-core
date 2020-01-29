package au.edu.anu.twcore.ecosystem.structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.edu.anu.twcore.DefaultStrings;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.space.FlatSurface;
import au.edu.anu.twcore.ecosystem.runtime.space.Space;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.SpaceType;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.utils.Interval;
import fr.ens.biologie.generic.utils.NameUtils;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

/**
 * 
 * @author Jacques Gignoux - 28 janv. 2020
 *
 */
public class SpaceNode 
		extends InitialisableNode 
		implements LimitedEdition<Space>, Sealable {
	
	private boolean sealed = false;
	private Map<Integer,Space> spaces = new HashMap<>();
	private SpaceType stype = null;
	// the name of coordinates relative to this space (eg "<this.id()>.x")
	private Set<String> coordNames = new HashSet<>();
	private String xname = "x";
	private String yname = "y";
	private String zname = "z";
	
	public SpaceNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}
	
	public SpaceNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}
	
	private String makeCoordinateName(String name) {
		return id()+NameUtils.initialUpperCase(name);
	}
	
	@Override
	public void initialise() {
		super.initialise();
		stype = (SpaceType) properties().getPropertyValue(P_SPACETYPE.key());
		if (properties().hasProperty(P_SPACE_XNAME.key()))
			xname = (String)properties().getPropertyValue(P_SPACE_XNAME.key());
		if (properties().hasProperty(P_SPACE_YNAME.key()))
			yname = (String)properties().getPropertyValue(P_SPACE_YNAME.key());
		if (properties().hasProperty(P_SPACE_ZNAME.key()))
			zname = (String)properties().getPropertyValue(P_SPACE_ZNAME.key());
		switch (stype) {
			case continuousFlatSurface:
				coordNames.clear();
				coordNames.add(makeCoordinateName(xname));
				coordNames.add(makeCoordinateName(yname));
				break;
			case linearNetwork:
				break;
			case squareGrid:
				break;
			case topographicSurface:
				coordNames.clear();
				coordNames.add(makeCoordinateName(xname));
				coordNames.add(makeCoordinateName(yname));
				coordNames.add(makeCoordinateName(zname));
				break;
			default:
				break;		
		}
		seal();
	}

	@Override
	public int initRank() {
		return N_SPACE.initRank();
	}
	
	private Space makeSpace(int id) {
		Space result = null;
		switch (stype) {
			case continuousFlatSurface:
				Interval xlim = (Interval) properties().getPropertyValue(P_SPACE_XLIM.key());
				Interval ylim = (Interval) properties().getPropertyValue(P_SPACE_YLIM.key());
				result = new FlatSurface(xlim.inf(),xlim.sup(),ylim.inf(),ylim.sup(),xname,yname);
				break;
			case linearNetwork:
				break;
			case squareGrid:
				break;
			case topographicSurface:
				break;
			default:
				break;		
		}
		return result;
	}

	@Override
	public Space getInstance(int id) {
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
