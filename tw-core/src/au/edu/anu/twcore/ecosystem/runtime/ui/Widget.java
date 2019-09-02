package au.edu.anu.twcore.ecosystem.runtime.ui;

import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.Resettable;

/**
 * @author Ian Davies
 *
 * @date 2 Sep 2019
 */
public interface Widget extends Resettable{

	public void setProperties(SimplePropertyList properties);
	public Object getUserInterfaceContainer();
	public Object getMenuContainer();


}
