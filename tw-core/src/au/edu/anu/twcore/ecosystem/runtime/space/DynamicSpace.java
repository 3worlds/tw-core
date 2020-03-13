package au.edu.anu.twcore.ecosystem.runtime.space;

import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.ecosystem.runtime.containers.DynamicContainer;
import au.edu.anu.twcore.ecosystem.runtime.containers.ResettableContainer;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SingleDataTrackerHolder;
import au.edu.anu.twcore.ecosystem.runtime.tracking.SpaceDataTracker;
import au.edu.anu.twcore.rngFactory.RngHolder;
import fr.cnrs.iees.identity.Identity;
import fr.ens.biologie.generic.Resettable;

/**
 * The type of Space used in 3Worlds
 * @author Jacques Gignoux - 13 mars 2020
 *
 * @param <T>
 */
public interface DynamicSpace<I extends Identity,T extends Located<I,Location>> 
	extends Space<I>, 
			DynamicContainer<T>, 
			ResettableContainer<T>,
			RngHolder,
			SingleDataTrackerHolder<Metadata>,
			Resettable {

	// default: no tracking assumed
	@Override
	default SpaceDataTracker dataTracker() {
		return null;
	}

	// default: no tracking assumed
	@Override
	default Metadata metadata() {
		return null;
	}

}
