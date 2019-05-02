package au.edu.anu.twcore.archetype.tw;

import fr.cnrs.iees.identity.Identity;
import au.edu.anu.rscs.aot.queries.Query;

public class NameStartsWithUpperCaseQuery extends Query {

	@Override
	public Query process(Object input) { // input is a node - actually anything with an id() will work
		defaultProcess(input);
		String localItem = ((Identity) input).id();
		char c = localItem.charAt(0);
		char upper = Character.toUpperCase(c);
		if (c==upper) satisfied = true;
		return this;
	}

	public String toString() {
//		return "[" + this.getClass().getSimpleName() + ", satisfied=" + satisfied 
//			+ ", labels = " + labels+ "]";
		return "[" + stateString() + " Name must start with an upper case character]";
	}

}
