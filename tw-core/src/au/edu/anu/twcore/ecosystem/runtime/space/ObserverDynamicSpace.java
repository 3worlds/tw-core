package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.ecosystem.runtime.system.DynamicGraphObserver;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.SystemRelation;

/**
 *
 * @author J. Gignoux - 18 févr. 2021
 *
 */
public interface ObserverDynamicSpace
		extends DynamicSpace<SystemComponent>,
			DynamicGraphObserver<SystemComponent,SystemRelation> {
}
