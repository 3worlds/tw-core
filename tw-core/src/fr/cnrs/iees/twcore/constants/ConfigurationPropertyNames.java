package fr.cnrs.iees.twcore.constants;

/**
 * 
 * @author Jacques Gignoux - 31 mai 2019
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
	;

	private final String pname;
	
	private ConfigurationPropertyNames(String pname) {
		this.pname = pname;
	}
	
	public String key() {
		return pname;
	}
}
