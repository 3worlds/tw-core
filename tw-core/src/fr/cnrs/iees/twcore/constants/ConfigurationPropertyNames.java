package fr.cnrs.iees.twcore.constants;

/**
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 * NB at the moment I only listed the property names I needed in the code. More will probably
 * come in later.
 *
 */
public enum ConfigurationPropertyNames {
	
	P_DIMENSIONER_SIZE 			("size"),
	P_FIELD_TYPE 				("type"),
	P_DESIGN_TYPE				("type"),
	P_DESIGN_FILE				("file"),
	P_TREATMENT_REPLICATES		("replicates"),
	P_MODELCHANGE_PARAMETER		("parameter"),
	P_MODELCHANGE_REPLACEWITH	("replaceWith"),
	P_TIMEPERIOD_START			("start"),
	P_TIMEPERIOD_END			("end"),
	P_TIMELINE_SCALE			("scale"),
	P_TIMELINE_SHORTTU			("shortestTimeUnit"),
	P_TIMELINE_LONGTU			("longestTimeUnit"),
	P_TIMELINE_TIMEORIGIN		("timeOrigin"),
	P_COMPONENT_LIFESPAN		("lifeSpan"),
	P_PARAMETERCLASS			("parameterClass"),
	P_DRIVERCLASS				("driverClass"), 
	P_DECORATORCLASS			("decoratorClass"), 
	P_DYNAMIC					("dynamic"),
	P_FUNCTIONTYPE				("type"),
	P_FUNCTIONCLASS				("userClassName"), 
	;

	private final String pname;
	
	private ConfigurationPropertyNames(String pname) {
		this.pname = pname;
	}
	
	public String key() {
		return pname;
	}
}
