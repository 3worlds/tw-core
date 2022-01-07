package au.edu.anu.twcore.ecosystem.runtime.system;

/**
 * Static methods to find container in the arena > life cycle > group hierarchy
 * 
 * @author Jacques Gignoux - 7 janv. 2022
 *
 */
public class ContainerUtils {
	
	// to prevent instantiation
	private ContainerUtils() {}
	
	public static ComponentContainer getArenaContainer(ArenaComponent arena) {
		return (ComponentContainer)arena.content();
	}

	public static ComponentContainer getGroupContainer() {
		return null;
	}
	
	public static ComponentContainer getLifeCycleContainer(ArenaComponent arena, String lcId) {
		if (arena.content().contains(lcId))
			return (ComponentContainer) arena.content().findContainer(lcId);
		else {
			
		}
		return null;
	}
	
}
