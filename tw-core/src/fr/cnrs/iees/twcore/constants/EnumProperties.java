package fr.cnrs.iees.twcore.constants;

/**
 * A class to initialise user-defined properties - must be called before any use of ValidProperties
 * is attempted
 * @author Jacques Gignoux - 8 août 2019
 *
 */
public class EnumProperties {

	private EnumProperties() {}
	
	// these references trigger the static block intialisation of all these classes,
	// which record them in ValidPropertyTypes
	// this method must be called early in application setup
	// TODO: a cleaner coding by scanning the directory, finding all the class names
	// and invoking any method but this seems non trivial
	// Its possible that compiler optimisation will prevent calling these statements - check.
	public static void recordEnums() {
		DataElementType.defaultValue();
		DateTimeType.defaultValue();
		ExperimentDesignType.defaultValue();
		FileType.defaultValue();
		Grouping.defaultValue();
		LifespanType.defaultValue();
		SamplingMode.defaultValue();
		SnippetLocation.defaultValue();
		StatisticalAggregates.defaultValue();
//		StatisticalAggregatesSet.
//		TabLayoutTypes.defaultValue();
		TimeScaleType.defaultValue();
		TimeUnits.defaultValue();
		TwFunctionTypes.defaultValue();
//		UIContainers.defaultValue();
		
	}

}
