package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * Ancestor class for data messages where the variables originate from a particular entity with
 * a unique label (e.g. a SystemComponent instance).
 * 
 * @author Jacques Gignoux - 16 oct. 2019
 *
 */
public abstract class LabelledItemData extends TimeData {
	
	private DataLabel itemLabel = null;

	public LabelledItemData(SimulatorStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}
	
	public DataLabel itemLabel() {
		return itemLabel;
	}
	
	public void setItemLabel(String... labels) {
		itemLabel = new DataLabel(labels);
	}

	public void setItemLabel(DataLabel labels) {
		itemLabel = labels;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append(" item=").append(itemLabel.toString());
		return sb.toString();
	}

}
