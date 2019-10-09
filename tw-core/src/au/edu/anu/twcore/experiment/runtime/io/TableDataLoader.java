package au.edu.anu.twcore.experiment.runtime.io;

import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import au.edu.anu.twcore.experiment.runtime.MultipleDataLoader;
import fr.cnrs.iees.graph.Node;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.utils.Logging;

/**
 * <p>Abstract ancestor for data loader dealing with data organized in tables (eg csv 
 * or odt data loaders). This data loader assumes that specific variables must appear
 * in a table format depending in the file they come from:</p>
 * <ol>
 * <li>a species identifier (property idSpecies)</li>
 * <li>a stage identifier (property idStage)</li>
 * <li>a SystemComponent (or individual) identifier (property idComponent)</li>
 * <li>a SystemRelation (or relation) identifier (property idRelation) [NOT YET IMPLEMENTED in simulator]</li>
 * <li>a series of dimension variables (property idDims)</li>
 * </ol>
 * <p>All other variables are assumed to be read as such (ie, they play no specific role
 * in the file scanning. The data file is assumed to contain the variable names 
 * (eg as column headers in a spreadsheet format - or row headers, wy not !)</p>
 * 
 * <p>Conditions apply on the abovelisted properties:</p>
 * <ul>
 * <li>(1) must be present, all other ones are optional.</li>
 * <li>if (3) is present then (2) must be present</li>
 * <li>all properties are names (Strings) except idDims which must be a StringTable</li>
 * </ul>
 * <p>Data organisation rules:</p>
 * <ol>
 * <li>if only (1) is present, an instance of TwData is produced per species</li>
 * <li>if (1) and (2) are present, an instance of TwData is produced per stage[in]species </li>
 * <li>if (1), (2) and (3) are present, an instance of TwData is produced per systemComponent[in]stage[in]species</li>
 * </ol>
 * <p>Uses {@link PropertyDataLoader} to read data chunks</p>
 * <p>results are returned in a map of String * TwData. Since more than one variable is
 * given to identify the TwData, the string is a concatenation of the names separated by a separator</p>
 * 
 * @author Jacques Gignoux - 31 mai 2017
 *
 */
public abstract class TableDataLoader 
		implements MultipleDataLoader<SimplePropertyList> {
	
	private Logger log = Logging.getLogger(TableDataLoader.class);
	
	/** the separator used to build hierarchical identifiers eg species:stage:individual*/
	private static String SEP = ":";
	/** the list of variable names as found in the file */
	private String[] headers = null;
	/** the list of variable names to read from the file, if provided*/
	private Set<String> columnsToRead = new HashSet<String>();
	/** the table of raw data as extracted from the file */
	private String[/*line*/][/*column*/] rawData;
	/** the identifier column names, sorted by target (species, stage, component, relation, variable) */
	private Map<String,Integer> idcols = new HashMap<String,Integer>();
	
	private int[] dimCols = null;
	

	public TableDataLoader (String idsp,String idst,String idsc,String idsr,String idmd) {
		// list of identifier columns to read, properly ordered
//		String idsp = (String) getPropertyValue("idSpecies");
//		String idst = (String) getPropertyValue("idStage");
//		String idsc = (String) getPropertyValue("idComponent");
//		String idsr = (String) getPropertyValue("idRelation");
//		String idmd = (String) getPropertyValue("idVariable");
		// dimension columns, if any
//		List<Node> dimlist = (List<Node>) get(getEdges(Direction.OUT), 
//		    edgeListEndNodes(), 
//		    selectZeroOrMany(hasTheLabel("dim")));
//		List<Node> dimlist = (List<Node>) get(this,children(), 
//			    selectZeroOrMany(hasTheLabel("dim")));
//		String[] idDims = new String[dimlist.size()];
//		for (Node dim:dimlist) {
//			// assumption here: the name of the 'dim' AotNode is an integer giving its
//			// rank in the dimension table - starts at 1, not 0 !
//			int i = Integer.valueOf(dim.getName());
//			idDims[i-1] = (String)dim.getPropertyValue("col");
//		}
//		// if a list of variable names is given, then only these ones will be read
//		List<Node> readlist = (List<Node>) get(getEdges(Direction.OUT),
//			edgeListEndNodes(),
//			selectZeroOrMany(hasTheLabel("read")));
//		for (AotNode var:readlist)
//			columnsToRead.add((String)var.getPropertyValue("name"));
//		// now read the file (in descendants)
//		rawData = loadFromFile();
//		if (rawData==null) {
//			log.error("No data could be read from resource "+input);
//			return this;
//		}
//		// by definition, in a table the header line is the first line:
//		headers = rawData[0];
//		// record the indexes of dimensions in proper order
//		dimCols = new int[idDims.length];
//		for (int i=0; i<headers.length; i++)
//			for (int j=0; j<idDims.length; j++)
//				if (idDims[j].equals(headers[i]))
//					dimCols[j] = i;
//		// if no names were given, it means all variables (excluding dims) are 
//		// to be read except the identifiers which deserve a special treatment
//		boolean fillIt = columnsToRead.isEmpty();
//		for (int i=0; i<headers.length; i++) {
//			String var = headers[i];
//			boolean dimFound = false;
//			for (int j=0; j<idDims.length; j++)
//				if (idDims[j].equals(var)) 
//					dimFound = true;	
//			if (!dimFound)
//				if (var.equals(idsp)) idcols.put("idSpecies",i);
//				else if (var.equals(idst)) idcols.put("idStage",i);
//				else if (var.equals(idsc)) idcols.put("idComponent",i);
//				else if (var.equals(idsr)) idcols.put("idRelation",i);
//				else if (var.equals(idmd)) idcols.put("idVariable",i);
//				else if (fillIt) columnsToRead.add(var);
//		}
	}
	
	protected abstract String[/*line*/][/*column*/] loadFromFile();
	
	private String uniqueId(String[] dataLine) {
		String id="";
		if (idcols.containsKey("idSpecies"))
			id = dataLine[idcols.get("idSpecies")];
		if (idcols.containsKey("idStage"))
			id += SEP+dataLine[idcols.get("idStage")];
		if (idcols.containsKey("idComponent"))
			id += SEP+dataLine[idcols.get("idComponent")];
		if (idcols.containsKey("idRelation"))
			id += SEP+dataLine[idcols.get("idRelation")];
		if (idcols.containsKey("idVariable"))
			id += SEP+dataLine[idcols.get("idVariable")];
		if (id.startsWith(SEP))
			id = id.substring(1);
		if (id.endsWith(SEP))
			id = id.substring(0,id.length()-1);
		return id;
	}
	
	@Override
	public final void load(Map<String, SimplePropertyList> result, SimplePropertyList dataModel) {
		LinkedList<String[]> dataChunk = new LinkedList<String[]>();
		// get item unique id
		// start at 1 because rawData[0] is the headers line
		String id = uniqueId(rawData[1]);
		String previousId = id;
		SimplePropertyList data = null;
		for (int i=1; i<rawData.length; i++) {
			id = uniqueId(rawData[i]);
			if (i==rawData.length-1) dataChunk.add(rawData[i]);
			if ((!id.equals(previousId))|(i==rawData.length-1)) {
		    	data = find(previousId,result,dataModel);
		    	String[][] ss = new String[dataChunk.size()][];
				PropertyDataLoader dataLoader = new PropertyDataLoader(
					dataChunk.toArray(ss),
				    headers,
				    columnsToRead,
				    dimCols);
				dataLoader.load(data);
				result.put(id,data);
				dataChunk.clear();
			}
			dataChunk.add(rawData[i]);
			previousId = id;
		}
	}

    /** utility for descendants */
    @SuppressWarnings("unchecked")
	protected SimplePropertyList find(String id,
			Map<String,SimplePropertyList> list,
			SimplePropertyList dataModel) {
    	SimplePropertyList data = null;
    	if (list.containsKey(id))
    		data = list.get(id);
    	else {
    		data = (SimplePropertyList) dataModel.clone();
    		list.put(id, data);
    	}
    	return data;
    }

    /**
     * Results are returned in a map with String identifiers.
     * These identifiers may be compound (eg species:stage:individual).
     * This methods extract the hierarchical levels from the identifier name
     * @param ids
     * @return
     */
	public static final String[] extractIds(String ids) {
		return ids.split(SEP);
	}
	
    /**
     * Results are returned in a map with String identifiers.
     * These identifiers may be compound (eg species:stage:individual).
     * This methods produces a compound identifier from a list of Strings given to it
     * @param ids
     * @return
     */
	public static final String collateIds(String... ids) {
		String s = ids[0];
		for (int i=1; i<ids.length; i++)
			s+= SEP+ids[i];
		return s;
	}

	public final String[] headers() {
		return headers;
	}
	
}
