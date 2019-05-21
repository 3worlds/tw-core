package au.edu.anu.twcore.archetype.tw;

import au.edu.anu.rscs.aot.collections.tables.ObjectTable;
import au.edu.anu.rscs.aot.queries.Query;
import fr.cnrs.iees.graph.ReadOnlyDataHolder;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.twcore.constants.TimeScaleType;
import fr.cnrs.iees.twcore.constants.TimeUnits;
import static fr.cnrs.iees.twcore.constants.ThreeWorldsGraphReference.*;

/**
 * A Query to check that minimal time unit and maximal time unit are consistent
 * 
 * @author gignoux
 *
 */
public class TimeIntervalValidityQuery extends Query {

	private TimeScaleType refScale;
	private TimeUnits modelMax;
	private TimeUnits modelMin;
	private TimeUnits minTU;
	private TimeUnits maxTU;

	private String pscale;
	private String pmin;
	private String pmax;
	private boolean timeModelRangeError;

	public TimeIntervalValidityQuery(ObjectTable<?> parameters) {
		super();
		pmin = (String) parameters.getWithFlatIndex(0); // name of the minimal time unit property
		pmax = (String) parameters.getWithFlatIndex(1); // name of the maximal time unit property
		pscale = (String) parameters.getWithFlatIndex(2); // name of the time scale property
	}

	@SuppressWarnings("unchecked")
	@Override
	public Query process(Object input) {
		// input is a Timeline - can't be generalised
		defaultProcess(input);
		ReadOnlyDataHolder timeLine = (ReadOnlyDataHolder) input;
		TreeNode timeLineNode = (TreeNode) input;
		refScale = (TimeScaleType) timeLine.properties().getPropertyValue(pscale);
		minTU = (TimeUnits) timeLine.properties().getProperty(pmin).getValue();
		maxTU = (TimeUnits) timeLine.properties().getProperty(pmax).getValue();
		modelMax = TimeUnits.MICROSECOND;
		modelMin = TimeUnits.MILLENNIUM;
		timeModelRangeError = false;

		// If there is no refScale property we should crash.
		if (refScale.equals(TimeScaleType.MONO_UNIT))
			satisfied = (minTU == maxTU);
		else
			satisfied = (minTU.compareTo(maxTU) <= 0);
		if (satisfied) {
			Iterable<ReadOnlyDataHolder> timeModels = (Iterable<ReadOnlyDataHolder>) timeLineNode.getChildren();
			for (ReadOnlyDataHolder timeModel : timeModels) {
				TimeUnits tu = (TimeUnits) timeModel.properties().getPropertyValue(P_TIMEUNIT.toString());
				if (tu.compareTo(modelMin) < 0)
					modelMin = tu;
				if (tu.compareTo(modelMax) > 0)
					modelMax = tu;
			}
			if (!modelMin.equals(minTU)) {
				timeModelRangeError = true;
				satisfied = false;
			} else if (!modelMax.equals(maxTU)) {
				timeModelRangeError = true;
				satisfied = false;
			}
		}
		return this;
	}

	public String toString() {
		if (timeModelRangeError)
			return ": Time models collectively must span the whole range of possible values of the time line, i.e. from "+minTU + " to "+maxTU;
		else if (refScale.equals(TimeScaleType.MONO_UNIT))
			return ": For " + TimeScaleType.MONO_UNIT + ", " + pmin + " must be equal to " + pmax;
		else
			return ": " + pmin + " must be shorter than or equal to " + pmax;
	}

}
