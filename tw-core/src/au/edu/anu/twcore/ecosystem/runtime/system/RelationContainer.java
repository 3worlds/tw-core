package au.edu.anu.twcore.ecosystem.runtime.system;

import java.util.HashSet;
import java.util.Set;

import au.edu.anu.twcore.ecosystem.runtime.Categorized;
import au.edu.anu.twcore.ecosystem.runtime.Related;
import au.edu.anu.twcore.ecosystem.runtime.containers.DynamicContainer;
import au.edu.anu.twcore.ecosystem.structure.RelationType;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.Edge;
import fr.ens.biologie.generic.Resettable;
import fr.ens.biologie.generic.utils.Duple;

/**
 * Management of relations (ie delayed addition and removal). this is NOT a container, i.e. relations
 * are not stored here, they are stored by the SystemComponent edge lists.
 * 
 * @author Jacques Gignoux - 16 janv. 2020
 *
 */
public class RelationContainer 
		implements DynamicContainer<SystemRelation>, Resettable, Related<SystemComponent>  {

	//
	private RelationType relationType = null;
	// the list of system component pairs to later relate
	private Set<Duple<SystemComponent,SystemComponent>> relationsToAdd = new HashSet<>();
	private Set<Duple<SystemComponent,SystemComponent>> relationsToRemove = new HashSet<>();

	public RelationContainer(RelationType rel) {
		super(); // since they are different local scopes it may work...
		relationType = rel;
	}
	
	@Override
	public void postProcess() {
		relationsToAdd.clear();
		relationsToRemove.clear();
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
	public void removeItem(SystemRelation relation) {
		relationsToRemove.add(new Duple<SystemComponent,SystemComponent>
			((SystemComponent)relation.startNode(),(SystemComponent)relation.endNode()));
	}
	
	@Override
	public void effectChanges() {
		// delete all old relations
		for (Duple<SystemComponent,SystemComponent> dup : relationsToRemove)
			for (Edge e:dup.getFirst().edges(Direction.OUT))
				if (e.endNode().equals(dup.getSecond()))
					e.disconnect();
		relationsToRemove.clear();
		// establish all new relations
		for (Duple<SystemComponent,SystemComponent> item : relationsToAdd)
			item.getFirst().relateTo(item.getSecond(),relationType.id());
		relationsToAdd.clear();
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
