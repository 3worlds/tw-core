package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.biology.ChangeCategoryDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.CreateOtherDecisionFunction;
import au.edu.anu.twcore.ecosystem.runtime.biology.SetInitialStateFunction;
import au.edu.anu.twcore.ecosystem.structure.Category;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.ens.biologie.generic.utils.Duple;

/**
 *
 * @author J. Gignoux - 8 déc. 2020
 *
 */
public class LifeCycleFactory extends ElementFactory<LifeCycleComponent> {

//	private static Logger log = Logging.getLogger(LifeCycleFactory.class);

	private String lifeCycleName = null;
	private String lifeCycleTypeName = null;
	private Map<CreateOtherDecisionFunction,Duple<String,String>> produceNodes;
	private Map<ChangeCategoryDecisionFunction,Duple<String,String>> recruitNodes;
	private SortedSet<Category> stageCategories = null;

	public LifeCycleFactory(Set<Category> categories,
			TwData auto, TwData drv, TwData dec, TwData ltc,
			SetInitialStateFunction setinit,
			String name, ComponentContainer parent,
			Map<CreateOtherDecisionFunction,Duple<String,String>> produceNodes,
			Map<ChangeCategoryDecisionFunction,Duple<String,String>> recruitNodes,
			int simulatorId,SortedSet<Category> stageCategories) {
		super(categories, auto, drv, dec, ltc, setinit, true, simulatorId);
		this.parentContainer = parent;
		lifeCycleTypeName = name;
		this.produceNodes = produceNodes;
		this.recruitNodes = recruitNodes;
		this.stageCategories = stageCategories;
	}

	/**
	 * This MUST be called before newInstance() in order for the correct name to be used.
	 * Otherwise the LifeCycleType name is used to generate a group
	 * @param name
	 */
	public void setName(String name) {
		lifeCycleName = name;
	}

	@Override
	public LifeCycleComponent newInstance() {
		LifeCycleComponent lifeCycle = null;
		ComponentContainer container = null;
		if (lifeCycleName!=null) {
			container = new ComponentContainer(lifeCycleName,parentContainer,null,simId);
//			if (!ComponentContainer.containerScope.contains(lifeCycleName))
//				container = new ComponentContainer(lifeCycleName,parent,null,simId);
//			else { // groupName already in use
//				container = new ComponentContainer(lifeCycleTypeName,parent,null,simId);
//				String s = container.id();
//				log.warning(()->"LifeCycle container couldnt be created with name '"+lifeCycleName
//					+"' - name '" + s + "' used instead.");
//			}
		}
		else
			container = new ComponentContainer(lifeCycleTypeName,parentContainer,null,simId);
		autoVarTemplate = new ContainerData(container);
		SimplePropertyList props = new SystemComponentPropertyListImpl(autoVarTemplate,
		driverTemplate,decoratorTemplate,lifetimeConstantTemplate,2,propertyMap);
		lifeCycle = (LifeCycleComponent) SCfactory.get(simId).makeNode(LifeCycleComponent.class,container.id(),props);
		lifeCycle.setCategorized(this);
		container.setData(lifeCycle);
		lifeCycle.setContent(container);
		lifeCycle.connectParent(parentContainer.descriptors());
		return lifeCycle;
	}

	@Override
	public LifeCycleComponent newInstance(ComponentContainer parentContainer) {
		return newInstance();
	}

	protected String fromCategories(CreateOtherDecisionFunction func) {
		return produceNodes.get(func).getFirst();
	}

	protected String toCategories(CreateOtherDecisionFunction func) {
		return produceNodes.get(func).getSecond();
	}

	protected String fromCategories(ChangeCategoryDecisionFunction func) {
		return recruitNodes.get(func).getFirst();
	}

	protected String toCategories(ChangeCategoryDecisionFunction func) {
		return recruitNodes.get(func).getSecond();
	}

	protected Set<Category> stageCategories() {
		return stageCategories;
	}
}
