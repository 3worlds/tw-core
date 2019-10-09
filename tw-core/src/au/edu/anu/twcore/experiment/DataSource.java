package au.edu.anu.twcore.experiment;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.experiment.runtime.MultipleDataLoader;
import au.edu.anu.twcore.experiment.runtime.io.BOMWeatherLoader;
import au.edu.anu.twcore.experiment.runtime.io.CsvFileLoader;
import au.edu.anu.twcore.experiment.runtime.io.OdfFileLoader;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.cnrs.iees.twcore.constants.FileType;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import fr.ens.biologie.generic.utils.Logging;

import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
	protected InputStream input = null;
	
	private boolean sealed = false;
	
	private MultipleDataLoader<SimplePropertyList> dataLoader = null;

	public DataSource(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public DataSource(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@Override
	public void initialise() {
		super.initialise();
		InputStream ips = null;
		FileType ft = (FileType) properties().getPropertyValue(P_DATASOURCE_FILE.key());
    	File file = ft.getFile();
    	String name = ft.getRelativePath();
    	// TODO: CHECK ALL THIS !!!
    	if (file!=null)
			try {
				ips = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
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
    	if (loaderclass.contains(CsvFileLoader.class.getSimpleName()))
    		dataLoader = new CsvFileLoader();
    	else if (loaderclass.contains(OdfFileLoader.class.getSimpleName()))
    		dataLoader = new OdfFileLoader();
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
