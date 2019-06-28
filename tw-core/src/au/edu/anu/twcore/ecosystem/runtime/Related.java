package au.edu.anu.twcore.ecosystem.runtime;

/**
 * To be associated to relation instances
 * 
 * @author Jacques Gignoux - 28 juin 2019
 *
 */
public interface Related {
	
	public Categorized from();
	
	public Categorized to();

}
