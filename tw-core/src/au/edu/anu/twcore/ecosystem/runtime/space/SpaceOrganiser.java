package au.edu.anu.twcore.ecosystem.runtime.space;

import java.util.HashMap;
import java.util.Map;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.ens.biologie.generic.Sealable;

/**
 * The class that knows how spaces are positioned relative to each other (equivalent for Spaces of the
 * TimeLine for Timers).
 *
 * @author J. Gignoux - 10 juil. 2020
 *
 */
public class SpaceOrganiser implements Sealable {

	private Map<String,Space<SystemComponent>> spaces = new HashMap<>();
	private boolean sealed = false;

	// TODO: overlaps between spaces, intersections, projections, topolgical assemblage...
	// store geometric transformations from one space to the next

	public SpaceOrganiser() {
		super();
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

	/**
	 * Finds a SystemComponent in the named space, returns null if not found.
	 *
	 * @param item
	 * @param inSpace
	 * @return
	 */
	public Location whereIs(SystemComponent item, String inSpace) {
		Space<SystemComponent> sp = spaces.get(inSpace);
		if (sp!=null)
			return sp.locationOf(item);
		return null;
	}

	public Space<SystemComponent> space(String name) {
		return spaces.get(name);
	}

}
