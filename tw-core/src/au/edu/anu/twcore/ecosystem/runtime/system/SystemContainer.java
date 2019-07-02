package au.edu.anu.twcore.ecosystem.runtime.system;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.exceptions.TwcoreException;

/**
 * A container for SystemComponents 
 * @author Jacques Gignoux - 2 juil. 2019
 *
 */
public class SystemContainer extends CategorizedContainer<SystemComponent> {
	
	public SystemContainer(Categorized<SystemComponent> cats, 
			String proposedId, 
			SystemContainer parent,
			TwData parameters,
			TwData variables) {
		super(cats,proposedId,parent,parameters,variables);
	}

	@Override
	public SystemComponent newInstance() {
		if (categoryInfo() instanceof SystemFactory)
			return ((SystemFactory)categoryInfo()).newInstance();
		throw new TwcoreException("SystemContainer "+id()+" cannot instantiate SystemComponents");
	}

	@Override
	public final SystemComponent clone(SystemComponent item) {
		SystemComponent result = newInstance();
		result.properties().setProperties(item.properties());
		return result;
	}

}
