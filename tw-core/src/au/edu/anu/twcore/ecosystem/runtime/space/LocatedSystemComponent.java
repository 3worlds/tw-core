package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import fr.cnrs.iees.identity.IdentityScope;

/**
 * 
 * @author Jacques Gignoux - 13 mars 2020
 *
 */
public class LocatedSystemComponent
		implements Located<SystemComponent,Location> {

	private SystemComponent component;
	private Location location;

	public LocatedSystemComponent(SystemComponent sc, Location loc) {
		super();
		component = sc;
		location = loc;
	}

	public LocatedSystemComponent(SystemComponent sc) {
		super();
		component = sc;
		location = null;
	}

	@Override
	public String id() {
		return component.id();
	}

	@Override
	public IdentityScope scope() {
		return component.scope();
	}

	@Override
	public Location location() {
		return location;
	}

	@Override
	public void location(Location location) {
		this.location = location;		
	}
	
	@Override
	public SystemComponent item() {
		return component;
	}

}
