package au.edu.anu.twcore.data.runtime;

import fr.cnrs.iees.twcore.constants.DataTrackerStatus;

/**
 * @author Ian Davies
 *
 * @date 19 Sep 2019
 */
public class TimeData extends OutputData {
	private long time;

	public TimeData(DataTrackerStatus status, int senderId, int metaDataType) {
		super(status, senderId, metaDataType);
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long time() {
		return time;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("time=").append(time);
		return sb.toString();
	}

}
