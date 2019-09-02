package fr.cnrs.iees.twcore.constants;

/**
 * Set of constants for initialisation order.
 * Sets the base values for chains of dependencies.
 * 
 * @author Jacques Gignoux - 2 sept. 2019
 *
 */
interface InitialisationRanks {

	static final int DIMBASE = 0;
	static final int TIMEBASE = 0;
	static final int ECOBASE = 0;
	static final int SIMBASE = 0;
	static final int CATEGORYBASE = 0;
	// UI must be initialised only after all other runtime classes are initialised
	static final int UIBASE = 1000;
}
