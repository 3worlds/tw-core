package au.edu.anu.twcore.ecosystem.runtime.init;

import au.edu.anu.twcore.data.runtime.TwData;
import fr.cnrs.iees.twcore.constants.TimeUnits;

/**
 * Ancestor class for user-defined setting of secondary parameters
 * TODO: improve this implementation - (1) there are no stage or species
 * (2) Initialisers could actually be attached to SystemComponent, LifeCycle or Ecosystem
 * (3) Initialisers should have access to the parameter sets of the systems they are
 * nested in (eg SystemComponent to LifeCycle or Ecosystem, LifeCycle to Ecosystem)
 * 
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
public abstract class SecondaryParametersInitialiser {

    public abstract void setSecondaryParameters(TwData speciesParameters,
    	TwData stageParameters,
    	long timeOrigin,
    	TimeUnits timeUnit); 
	
}
