package my_ecosystem.code;

import au.edu.anu.twcore.data.runtime.TwData;
import java.util.HashSet;
import my_ecosystem.code.Ty;
import fr.cnrs.iees.properties.SimplePropertyList;
import java.util.Set;
import au.edu.anu.rscs.aot.collections.tables.Dimensioner;
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
* Class MyOtherRec
* Model "my_ecosystem" -  - Fri Jul 05 16:13:08 CEST 2019
* CAUTION: generated code - do not modify
*/

public class MyOtherRec extends TwData {

	private Ty ty;

	public MyOtherRec() {
		super();
		ty = new Ty();
	}

	@Override
	public String propertyToString(String v0) {
		if (v0.equals("ty")) return ty.toString();
		return null;
	}

	public Ty ty() {
		return ty;
	}

	@Override
	public Set<String> getKeysAsSet() {
		Set<String> result = new HashSet<String>();
		result.add("ty");
		return result;
	}

	@Override
	public Object getPropertyValue(String v0) {
		if (v0.equals("ty")) return ty;
		return null;
	}

	@Override
	public Class<?> getPropertyClass(String v0) {
		if (v0.equals("ty")) return Ty.class;
		return null;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	protected TwData cloneStructure() {
		MyOtherRec result = new MyOtherRec();
		return result;
	}

	@Override
	public TwData setProperty(String v0, Object v1) {
		if (v0.equals("ty")) ty.copy((Table)v1);
		return this;
	}

	@Override
	public TwData clone() {
		MyOtherRec clone = (MyOtherRec) cloneStructure();
		clone.ty = (Ty) ty.clone();
		return clone;
	}

	@Override
	public TwData clear() {
		ty.clear();
		return this;
	}

	@Override
	public boolean hasProperty(String v0) {
		if (v0.equals("ty")) return true;
		return false;
	}

	@Override
	public String[] getKeysAsArray() {
		String[] result = {"ty"};
		return result;
	}

}

