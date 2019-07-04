package au.edu.anu.twcore.project;

/**
 * 3Worlds projects' directory structure - these dirs appear under
 * ProjectPaths.PROJECT_FILES/[ModelName]/
 * 
 * @author Jacques Gignoux	- 23/4/2015
 *
 */
@Deprecated // use TWPaths instead.
public interface ThreeWorldsProjectPaths {
	
	/** the directory for all generated code */
	public static final String THREE_WORLDS_CODE = "code";
	/** the directory for all user-specific data (eg csv files and others stuff) */
	public static final String THREE_WORLDS_DATA = "data";
	
	/** the package where default input files can be found - to use with Resource.getInputStream(name,package) */	
	public static final String THREE_WORLDS_DEFAULTS = "fr.ens.biologie.threeWorlds.resources.defaults";

}
