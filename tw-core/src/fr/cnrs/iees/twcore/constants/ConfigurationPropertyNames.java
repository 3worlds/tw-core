package fr.cnrs.iees.twcore.constants;

/**
 * 
 * @author Jacques Gignoux - 31 mai 2019
 *
 */
public enum ConfigurationPropertyNames {
	
	P_DIMENSIONER_SIZE 			("size"),
	P_FIELD_TYPE 				("type"),
	;

	private final String pname;
	
	private ConfigurationPropertyNames(String pname) {
		this.pname = pname;
	}
	
	public String key() {
		return pname;
	}
}
