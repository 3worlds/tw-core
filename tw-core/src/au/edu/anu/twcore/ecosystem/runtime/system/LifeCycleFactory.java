package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Set;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleFactory extends ElementFactory<LifeCycleComponent> {

	public LifeCycleFactory(Set<Category> categories,
			TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit) {
		super(categories, auto, drv, dec, ltc, setinit, true);
		// TODO Auto-generated constructor stub
	}

	@Override
	public LifeCycleComponent newInstance() {
		// TODO Auto-generated method stub
		return super.newInstance();
	}

}
