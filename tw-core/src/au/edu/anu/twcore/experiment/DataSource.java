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
package au.edu.anu.twcore.experiment;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.FieldNode;
import au.edu.anu.twcore.data.TableNode;
import au.edu.anu.twcore.experiment.runtime.MultipleDataLoader;
import au.edu.anu.twcore.experiment.runtime.io.BOMWeatherLoader;
import au.edu.anu.twcore.experiment.runtime.io.CsvFileLoader;
import au.edu.anu.twcore.experiment.runtime.io.OdfFileLoader;
import au.edu.anu.twcore.root.World;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.graph.impl.TreeGraphDataNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.DataElementType;
import fr.cnrs.iees.twcore.constants.FileType;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Logging;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Class mapping the dataSource node in the specification tree.
 * 
 * @author Jacques Gignoux - 10 juil. 2019
 *
 */
public class DataSource 
		extends InitialisableNode 
		implements Singleton<MultipleDataLoader<SimplePropertyList>>, Sealable {
	
	private static Logger log = Logging.getLogger(DataSource.class);
	
	/** the data stream which will be read by this DataLoader */
	private InputStream input = null;
	
	private boolean sealed = false;
	
	private MultipleDataLoader<SimplePropertyList> dataLoader = null;
	
	/** a map of all properties found in the model graph, with an instance of each prop type
	 * to clone real properties from */
	private Map<String,Object> propTemplates = new HashMap<>();

	public DataSource(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataSource(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		// construct the list of all properties (fields, tables) defined in the model
		// ok because all must have a unique name by construct
		TreeNode root = World.getRoot(this);
		Collection<TreeGraphDataNode> props = (Collection<TreeGraphDataNode>) get(root,
			childTree(),
			selectZeroOrMany(orQuery(hasTheLabel(N_FIELD.label()),hasTheLabel(N_TABLE.label()))) );
		for (TreeGraphDataNode tn:props) {
			Object value = null;
			String propName = tn.id();
			if (tn instanceof FieldNode) {
				DataElementType type = (DataElementType) tn.properties().getPropertyValue(P_FIELD_TYPE.key());
				value = ValidPropertyTypes.getDefaultValue(type.className());
				// case of records declared within tables
				if (tn.getParent().getParent() instanceof TableNode)
					propName = tn.getParent().getParent().id()+":"+tn.id();
				else
					propName = tn.id();
			}
			else if (tn instanceof TableNode) {
				propName = tn.id();
				value = ((TableNode)tn).templateInstance();
			}
			propTemplates.put(propName,value);
		}
		// load data from file (once for all simulators)
		InputStream ips = null;
		FileType ft = (FileType) properties().getPropertyValue(P_DATASOURCE_FILE.key());
    	File file = ft.getFile();
    	String name = ft.getRelativePath();
    	if (file!=null)
			try {
				ips = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				// NB normally this has been checked before by the queries.
				// reaching here means a big problem (eg file moved while running...)
				e.printStackTrace();
			}
		else
    		ips = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    	if (ips == null) {
    		log.severe("Unable to initialise data source "+id()+" - data file "+ file +" not found");
    	}
    	else {
    		input = ips;
     		log.info("Data source "+id()+" initialised");
    	}
    	String loaderclass = (String) properties().getPropertyValue(P_DATASOURCE_SUBCLASS.key());
		String idsp = (String) properties().getPropertyValue(P_DATASOURCE_IDLC.key());
		String idst = (String) properties().getPropertyValue(P_DATASOURCE_IDGROUP.key());
		String idsc = (String) properties().getPropertyValue(P_DATASOURCE_IDCOMPONENT.key());
		String idsr = (String) properties().getPropertyValue(P_DATASOURCE_IDRELATION.key());
		String idmd = (String) properties().getPropertyValue(P_DATASOURCE_IDVAR.key());
		StringTable dimlist = (StringTable) properties().getPropertyValue(P_DATASOURCE_DIM.key());
		String[] idDims = null;
		if (dimlist!=null) {
			idDims = new String[dimlist.size()];
			for (int i=0; i<dimlist.size(); i++)
				idDims[i] = dimlist.getWithFlatIndex(i);
		}
		StringTable readList = (StringTable) properties().getPropertyValue(P_DATASOURCE_READ.key());
		Set<String> columnsToRead = new HashSet<>();
		if (readList!=null)
			for (int i=0; i<readList.size(); i++)
				columnsToRead.add(readList.getWithFlatIndex(i));
		// TODO: when a column header is for a table, then a dim property MUST (of proper dimensions) be present
		// tricky to fix!
    	if (loaderclass.contains(CsvFileLoader.class.getSimpleName())) {
    		String sep = (String) properties().getPropertyValue(P_DATASOURCE_SEP.key());
    		if (sep==null)
    			sep = CsvFileLoader.defaultCsvSeparator;
    		dataLoader = new CsvFileLoader(idsp,idst,idsc,idsr,idmd,idDims,columnsToRead,propTemplates,input,sep);
    	}
    	else if (loaderclass.contains(OdfFileLoader.class.getSimpleName())) {
    		String sheet = null;
    		if (properties().hasProperty(P_DATASOURCE_SHEET.key()))
    			sheet = (String) properties().getPropertyValue(P_DATASOURCE_SHEET.key());
    		dataLoader = new OdfFileLoader(idsp,idst,idsc,idsr,idmd,idDims,columnsToRead,propTemplates,input,sheet);
    	}
    	else if (loaderclass.contains(BOMWeatherLoader.class.getSimpleName()))
    		dataLoader = new BOMWeatherLoader();
	}

	@Override
	public int initRank() {
		return N_DATASOURCE.initRank();
	}

	@Override
	public MultipleDataLoader<SimplePropertyList> getInstance() {
		if (!sealed)
			initialise();
		return dataLoader;
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
