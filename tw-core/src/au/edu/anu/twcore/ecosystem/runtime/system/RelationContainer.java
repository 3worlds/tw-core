package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.properties.ReadOnlyPropertyList;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.utils.Duple;

/**
 * Storage and management of relations
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public class RelationContainer 
		extends AbstractPopulationContainer<SystemRelation>
		implements Resettable, Related<SystemComponent>  {

	//
	private RelationType relationType = null;
	// items contained at this level (owned)
	private Map<String,SystemRelation> relations = new HashMap<>();
	// the list of system component pairs to later relate
	private Set<Duple<SystemComponent,SystemComponent>> relationsToAdd = new HashSet<>();
	private Set<String> relationsToRemove = new HashSet<>();

	public RelationContainer(RelationType rel) {
		super(rel.id()); // since they are different local scopes it may work...
		relationType = rel;
	}
	
	@Override
	public void reset() {
		clearItems();
	}
	
	@Override
	public ReadOnlyPropertyList populationData() {
		return populationData;
	}
	
	@Override
	public void addItem(SystemRelation item) {
		throw new TwcoreException("Relations cannot be directly added to relation container "
			+ "- must be a pair of Components");
	}
	
	// use this instead of the previous
	public void addItem(SystemComponent from, SystemComponent to) {
		relationsToAdd.add(new Duple<>(from,to));
	}
	
	@Override
	public void removeItem(String id) {
		relationsToRemove.add(id);
	}
	
	@Override
	public SystemRelation item(String id) {
		return relations.get(id);
	}
	
	@Override
	public Iterable<SystemRelation> items() {
		return relations.values();
	}
	
	// NB it is recommended to do this cleanup AFTER the SystemContainers because
	// removing SystemComponents will leave free-floating edges.
	@Override
	public void effectChanges() {
		for (String id : relationsToRemove) {
			SystemRelation sr = relations.remove(id);
			if (sr != null) {
				sr.disconnect();
				populationData.count--;
				populationData.nRemoved++;
			}
		}
		relationsToRemove.clear();
		for (Duple<SystemComponent,SystemComponent> item : relationsToAdd) {
			SystemRelation sr = item.getFirst().relateTo(item.getSecond(),relationType.id());
			if (relations.put(sr.id(),sr) == null) {
				populationData.count++;
				populationData.nAdded++;
			}
		}
		relationsToAdd.clear();
	}
	
	@Override
	public boolean contains(SystemRelation item) {
		return relations.containsValue(item);
	}
	
	@Override
	public boolean contains(String item) {
		return relations.containsKey(item);
	}
	
	@Override
	public void clearItems() {
		relations.clear();
		relationsToAdd.clear();
		relationsToRemove.clear();
		resetCounters();
	}
	
	@Override
	public void resetCounters() {
		super.resetCounters();
		populationData.count = relations.size();
	}

	@Override
	public Categorized<SystemComponent> from() {
		return relationType.from();
	}

	@Override
	public Categorized<SystemComponent> to() {
		return relationType.to();
	}

}
