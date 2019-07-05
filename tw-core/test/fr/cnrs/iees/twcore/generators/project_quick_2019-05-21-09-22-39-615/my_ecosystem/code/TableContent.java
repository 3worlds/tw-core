package my_ecosystem.code;

import au.edu.anu.twcore.data.runtime.TwData;
import java.util.HashSet;
import fr.cnrs.iees.properties.SimplePropertyList;
import java.util.Set;
import au.edu.anu.rscs.aot.collections.tables.Table;

/*
*                    *** 3Worlds - A software for the simulation of ecosystems ***
*                    *                                                           *
*                    *        by:  Jacques Gignoux - jacques.gignoux@upmc.fr     *
*                    *             Ian D. Davies   - ian.davies@anu.edu.au       *
*                    *             Shayne R. Flint - shayne.flint@anu.edu.au     *
*                    *                                                           *
*                    *         http:// ???                                       *
*                    *                                                           *
*                    *************************************************************
* Class TableContent
* Model "my_ecosystem" -  - Fri Jul 05 16:13:08 CEST 2019
* CAUTION: generated code - do not modify
*/

public class TableContent extends TwData {

	private int a;
	private String b;

	public TableContent() {
		super();
	}

	@Override
	public String propertyToString(String v0) {
		if (v0.equals("a")) return String.valueOf(a);
		if (v0.equals("b")) return String.valueOf(b);
		return null;
	}

	@Override
	public Class<?> getPropertyClass(String v0) {
		if (v0.equals("a")) return int.class;
		if (v0.equals("b")) return String.class;
		return null;
	}

	@Override
	public TwData clear() {
		a = 0;
		b = "";
		return this;
	}

	public void a(int v0) {
		if (!isReadOnly()) a = v0;
	}

	@Override
	public boolean hasProperty(String v0) {
		if (v0.equals("a")) return true;
		if (v0.equals("b")) return true;
		return false;
	}

	@Override
	public Set<String> getKeysAsSet() {
		Set<String> result = new HashSet<String>();
		result.add("a");
		result.add("b");
		return result;
	}

	@Override
	public Object getPropertyValue(String v0) {
		if (v0.equals("a")) return a;
		if (v0.equals("b")) return b;
		return null;
	}

	@Override
	public int size() {
		return 2;
	}

	public void b(String v0) {
		if (!isReadOnly()) b = v0;
	}

	@Override
	protected TwData cloneStructure() {
		TableContent result = new TableContent();
		return result;
	}

	@Override
	public TwData setProperty(String v0, Object v1) {
		if (v0.equals("a")) a = (int) v1;
		if (v0.equals("b")) b = (String) v1;
		return this;
	}

	@Override
	public TwData clone() {
		TableContent clone = (TableContent) cloneStructure();
		clone.a = a;
		clone.b = b;
		return clone;
	}

	public int a() {
		return a;
	}

	public String b() {
		return b;
	}

	@Override
	public String[] getKeysAsArray() {
		String[] result = {"a","b"};
		return result;
	}

}

