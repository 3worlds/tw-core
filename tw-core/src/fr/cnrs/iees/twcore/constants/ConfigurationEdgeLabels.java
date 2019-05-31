package fr.cnrs.iees.twcore.constants;

import au.edu.anu.twcore.data.SizedByEdge;

public enum ConfigurationEdgeLabels {


	// TODO: all 'Object.class' must be replaced by a valid Edge descendant
	//=========================================================================
	//							| label				| 	class				
	//-------------------------------------------------------------------------
	E_SIZEDBY ("sizedBy",	SizedByEdge.class),
	;
	//=========================================================================
	private final String label;
	private final Class<?> type;
	
	private ConfigurationEdgeLabels(String label,Class<?> type) {
		this.label = label;
		this.type = type;
	}
	
	public String label() {
		return label;
	}
	
	public Class<?> type() {
		return type;
	}

}
