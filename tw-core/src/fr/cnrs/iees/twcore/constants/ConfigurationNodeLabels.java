package fr.cnrs.iees.twcore.constants;

/**
 * Mapping of 3w tree node labels to class types (for use in a factory)
 * 
 * @author Jacques Gignoux - 23 mai 2019
 *
 */
public enum ConfigurationNodeLabels {

	// TODO: all 'Object.class' must be replaced by a valid Node descendant
	//========================================================================================
	//							| label				| 	class
	//----------------------------------------------------------------------------------------
	N_ROOT 						("3worlds",				Object.class),
		N_DATADEFINITION 		("dataDefinition", 		Object.class),
			N_DIMENSIONER 		("dimensioner",			Object.class),
			N_TABLE 			("table",				Object.class),
			N_RECORD 			("record",				Object.class),
			N_FIELD 			("field",				Object.class),
		N_SYSTEM 				("system",				Object.class), // World
			N_DYNAMICS 			("dynamics",			Object.class), // Simulator ?
				N_TIMELINE 		("timeLine",			Object.class), // TimeLine
				N_TIMEMODEL		("timeModel",			Object.class), // TimeModel
				N_EVENTQUEUE	("eventQueue",			Object.class), // EventQueue
				N_PROCESS 		("process",				Object.class), // ProcessNode
				N_FUNCTION 		("function",			Object.class),
				N_LIFECYCLE 	("lifeCycle",			Object.class), // LifeCycle
				N_RECRUIT 		("recruit",				Object.class),
				N_PRODUCE 		("produce",				Object.class),
				N_STOPPINGCONDITION("stoppingCondition",Object.class),
			N_STRUCTURE 		("structure",			Object.class),
				N_CATEGORYSET 	("categorySet",			Object.class),
				N_CATEGORY 		("category",			Object.class), // Category
				N_COMPONENT 	("component",			Object.class), // SystemFactory
				N_REALISEDCOMPONENT("realisedComponent",Object.class), // ComplexSystem ?
				N_INITIALISER 	("initialiser",			Object.class),
				N_RELATIONTYPE 	("relationType",		Object.class),
		N_EXPERIMENT 			("experiment",			Object.class),
			N_DESIGN 			("design",				Object.class),
			N_TREATMENT 		("treatment",			Object.class),
			N_TIMEPERIOD 		("timePeriod",			Object.class),
			N_MODELCHANGE 		("modelChange",			Object.class),
			N_DATAIO 			("dataIO",				Object.class),
			N_PARAMETERVALUES 	("parameterValues",		Object.class),
			N_INITIALSTATE 		("initialState",		Object.class),
		N_UI 					("userInterface",		Object.class),
			N_UITOP 			("top",					Object.class),
			N_UIBOTTOM 			("bottom",				Object.class),
			N_UICENTER 			("center",				Object.class),
			N_UIWIDGET 			("widget",				Object.class), // GridNode + ElementUserInterface
	;
	//========================================================================================
	private final String label;
	private final Class<?> type;
	
	private ConfigurationNodeLabels(String label, Class<?> type) {
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
