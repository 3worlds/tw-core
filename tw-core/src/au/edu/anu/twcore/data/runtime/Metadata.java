/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.data.runtime;

import au.edu.anu.rscs.aot.graph.property.Property;
import fr.cnrs.iees.properties.ExtendablePropertyList;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;

/**
 * <p>
 * A class to send <em>metadata</em> from {@linkplain DataTracker}s to
 * {@linkplain DataReceiver}s. Metadata are "data about the data", i.e.
 * information that can be useful to the receiver to process the data (e.g.
 * measurement units, label, range of values...). A DataTracker will send the
 * metadata prior to any data message, assuming its information applies to all
 * those it will send thereafter.
 * </p>
 * <p>
 * In the general case, any data structure can be used as metadata, as long as
 * the data receiver knows how to handle it. DataTrackers implement the
 * {@link Singleton}&lt;{@code M}&gt; interface for metadata, i.e. they have a
 * {@code getInstance()} method which will return a metadata instance of class
 * {@code M}.
 * </p>
 * <p>
 * In this implementation, we store all metadata in an
 * {@linkplain ExtendablePropertyList}. As a result, further properties can be
 * added to the metadata after construction -- this to handle complex cases
 * where metadata come from diverse origins.
 * </p>
 * <p>
 * Typical use case for this class:
 * </p>
 * 
 * <pre>
 * Metadata meta = myDataTracker.getInstance();
 * meta.addProperties(MyAdditionalPropertyList);
 * ...
 * myDataTracker.sendMetadata(meta);
 * ...
 * myDataTracker.sendData(...);
 * </pre>
 * <p>
 * NOTE: although this class is a descendant of {@linkplain OutputData}, its
 * simulator {@code status} field is set to {@code null}, as this is meaningless
 * for metadata.
 * </p>
 * 
 * @author Jacques Gignoux - 10 sept. 2019
 *
 */
public class Metadata extends OutputData {
// If this had a time field (inherited from OutputData) it would be the startTime of the TimeLine (consistent but redundant)
	private static int N_INSTANCES = 0;

	private ExtendablePropertyList properties = null;

	/**
	 * Constructor for a metadata record. The receiver is supposed to know what to
	 * do with the metadata sent. The constructor works out a unique integer id
	 * linked to this instance of Metadata, which enables to match future output
	 * data records to this metadata instance. This id is accessible through the
	 * type() method.
	 * 
	 * @param status     the status of the DataTracker
	 * @param senderId   the id of the DataTracker
	 * @param properties all the metadata as a property list
	 */
	public Metadata(int senderId, ReadOnlyPropertyList properties) {
		// NB status is meaningless for metadata
		super(null, senderId, N_INSTANCES++);
		// make a copy of properties
		this.properties = new ExtendablePropertyListImpl(properties.getKeysAsSet());
		for (String key : properties.getKeysAsSet())
			this.properties.setProperty(key, properties.getPropertyValue(key));
	}

	/**
	 * Accessor to the metadata as properties (cf. {@linkplain Property}).
	 * 
	 * @return the property list, read-only
	 */
	public ReadOnlyPropertyList properties() {
		return properties;
	}

	/**
	 * This method adds all the properties of the argument to the metadata property
	 * list.
	 * 
	 * @param props {@linkplain PropertyList}s to add to the metadata
	 */
	public void addProperties(ReadOnlyPropertyList... props) {
		for (ReadOnlyPropertyList pl : props)
			properties.addProperties(pl);
	}

	/**
	 * This method adds all the properties of the argument to the metadata property
	 * list.
	 * 
	 * @param props {@linkplain Property}(ies) to add to the metadata
	 */
	public void addProperties(Property... props) {
		for (Property p : props)
			properties.addProperty(p);
	}

	/** Extend the properites with a single key:value pair */
	public void addProperty(String key, Object value) {
		properties.addProperty(key, value);
	}

}
