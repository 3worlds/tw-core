package au.edu.anu.twcore.ecosystem.runtime.tracking;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import au.edu.anu.rscs.aot.collections.tables.Table;
import au.edu.anu.twcore.data.runtime.DataLabel;
import au.edu.anu.twcore.data.runtime.Metadata;
import au.edu.anu.twcore.data.runtime.OutputXYData;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Population;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedComponent;
import au.edu.anu.twcore.ecosystem.runtime.system.CategorizedContainer;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.twcore.constants.SamplingMode;
import fr.cnrs.iees.twcore.constants.SimulatorStatus;

/**
 * A data tracker for simple XY plots.
 * Always tracks only 1 single component
 *
 * @author J. Gignoux - 15 mai 2020
 *
 */
public class DataTrackerXY extends AbstractDataTracker<OutputXYData, Metadata> {

	private CategorizedComponent trackedComponent = null;
	private long currentTime = 0L;
	private DataLabel currentItem = null;
	private CategorizedContainer<CategorizedComponent> trackedContainer = null;
	private SamplingMode trackMode;
	private Metadata metadata = null;
	// TODO: replace these with data labels for diving into the TwData tree
	// but this is already done in the DataTracker0D...
	private String xPropName = null;
	private String yPropName = null;

	public DataTrackerXY(int simulatorId,
			SamplingMode selection,
			CategorizedContainer<CategorizedComponent> trackedGroup,
			List<CategorizedComponent> trackedComponents,
			Collection<String> track,
			ReadOnlyPropertyList fieldMetadata) {
		super(DataMessageTypes.XY,simulatorId);
		senderId = simulatorId;
		if (trackedComponents!=null)
			if (!trackedComponents.isEmpty())
				trackedComponent = trackedComponents.get(0);
		trackedContainer = trackedGroup;
		trackMode = selection;
		// Assuming here that fieldMetadata only contains 2 properties
		metadata = new Metadata(senderId,fieldMetadata);
		// the properties are sorted in alphabetical order: first is x, second is y
		SortedSet<String> names = new TreeSet<>();
		names.addAll(metadata.properties().getKeysAsSet());
		int i=0;
		for (String name:names)
			if (name.contains(".hlabel")){
			if (i==0)
				xPropName = name.substring(0,name.indexOf('.'));
			else if (i==1)
				yPropName = name.substring(0,name.indexOf('.'));
			else
				break;
			i++;
		}

	}

	@Override
	public void recordTime(long time) {
		currentTime = time;
	}

	@Override
	public void recordItem(String... labels) {
		if (currentItem==null)
			currentItem = new DataLabel(labels);
	}

	// AT the moment this only works with fields, no tables.
	private void getRecValue(int depth, TwData root, OutputXYData xyd) {
		if (root.hasProperty(xPropName)) {
			Object next = root.getPropertyValue(xPropName);
			if (next instanceof Table) {
				// TODO !!
			}
			else {
				if (next instanceof Double)
					xyd.setX((double) next);
				else if (next instanceof Float)
					xyd.setX((Float)next);
				else if (next instanceof Integer)
					xyd.setX((int)next);
				else if (next instanceof Long)
					xyd.setX((long)next);
				else if (next instanceof Boolean)
					xyd.setX((boolean)next);
				else if (next instanceof Short)
					xyd.setX((short)next);
				else if (next instanceof Byte)
					xyd.setX((byte)next);
			}
		}
		if (root.hasProperty(yPropName)) {
			Object next = root.getPropertyValue(yPropName);
			if (next instanceof Table) {
				// TODO !!
			}
			else {
				if (next instanceof Double)
					xyd.setY((double) next);
				else if (next instanceof Float)
					xyd.setY((Float)next);
				else if (next instanceof Integer)
					xyd.setY((int)next);
				else if (next instanceof Long)
					xyd.setY((long)next);
				else if (next instanceof Boolean)
					xyd.setY((boolean)next);
				else if (next instanceof Short)
					xyd.setY((short)next);
				else if (next instanceof Byte)
					xyd.setY((byte)next);
			}
		}
	}

	@Override
	public void record(SimulatorStatus status, TwData... props) {
		if (hasObservers()) {
			OutputXYData xyd = new OutputXYData(status,senderId,metadata.type());
			xyd.setTime(currentTime);
			xyd.setItemLabel(currentItem);
			for (TwData data:props)
				if (data!=null) {
					getRecValue(0,data,xyd);
			}
			sendData(xyd);
		}
	}

	@Override
	public boolean isTracked(CategorizedComponent sc) {
		return (trackedComponent==sc);
	}

	@Override
	public void updateTrackList() {
		if (!trackedComponent.isPermanent()) {
			if (trackedContainer==null) {
				trackedComponent = null;
				currentItem = null;
			}
			else if (!trackedContainer.contains(trackedComponent)) {
				trackedComponent = null;
				currentItem = null;
				switch (trackMode) {
					case FIRST:
						for (CategorizedComponent cc:trackedContainer.items()) {
							trackedComponent = cc;
							break;
						}
						break;
					case RANDOM:
						int max = ((Population)trackedContainer.hierarchicalView().autoVar()).count();
						int stop = rng.nextInt(max);
						int i=0;
						for (CategorizedComponent cc:trackedContainer.items()) {
							trackedComponent = cc;
							if (i==stop)
								break;
							i++;
						}
						break;
					case LAST:
						for (CategorizedComponent cc:trackedContainer.items())
							trackedComponent = cc;
						break;
				}
				// NB: trackedComponent may sill be null here
			}
		}
	}

	@Override
	public Metadata getInstance() {
		return metadata;
	}

}
