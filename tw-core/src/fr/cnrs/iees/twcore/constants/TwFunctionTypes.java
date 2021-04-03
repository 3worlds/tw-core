/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *                    *** 3Worlds - A software for the simulation of ecosystems ***
 *                    *                                                           *
 *                    *        by:  Jacques Gignoux - jacques.gignoux@upmc.fr     *
 *                    *             Ian D. Davies   - ian.davies@anu.edu.au       *
 *                    *             Shayne R. Flint - shayne.flint@anu.edu.au     *
 *                    *                                                           *
 *                    *         http:// ???                                       *
 *                    *                                                           *
 *                    *************************************************************
 * CAUTION: generated code - do not modify
 * generated by CentralResourceGenerator on Fri Apr 02 16:45:34 CEST 2021
*/
package fr.cnrs.iees.twcore.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import fr.cnrs.iees.io.parsing.ValidPropertyTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;
import fr.cnrs.iees.twcore.generators.process.TwFunctionArguments;
import static fr.cnrs.iees.twcore.generators.process.TwFunctionArguments.*;


public enum TwFunctionTypes {

// ChangeState: change the state, i.e. the values of the descriptors of a system component
	ChangeState ("change the state, i.e. the values of the descriptors of a system component",
		"changeState",
		"void",
		""),

// ChangeCategoryDecision: change category of a system component according to life cycle (has no effect if no life cycle is specified)
	ChangeCategoryDecision ("change category of a system component according to life cycle (has no effect if no life cycle is specified)",
		"changeCategory",
		"String",
		"the new category of the recruited <em>focal</em> system component"),

// CreateOtherDecision: create another system component, of the same categories if no life cycle is present, otherwise as specified by the life cycle
	CreateOtherDecision ("create another system component, of the same categories if no life cycle is present, otherwise as specified by the life cycle",
		"nNew",
		"double",
		"the number of new system components to create. The integral part is used as a number of new components, the decimal part as a probability to create an extra component."),

// DeleteDecision: delete self
	DeleteDecision ("delete self",
		"delete",
		"boolean",
		"true if the current <em>focal</em> system component is to be deleted."),

// ChangeOtherState: _focal_ changes the state of _other_
	ChangeOtherState ("_focal_ changes the state of _other_",
		"changeOtherState",
		"void",
		""),

// RelateToDecision: _focal_ establishes a new relation to _other_
	RelateToDecision ("_focal_ establishes a new relation to _other_",
		"relateTo",
		"boolean",
		"true if a new relation is to be set between <em>focal</em> and  <em>other</em>"),

// MaintainRelationDecision: decision to maintain or remove an existing relation
	MaintainRelationDecision ("decision to maintain or remove an existing relation",
		"maintainRelation",
		"boolean",
		"true if the existing relation between <em>focal</em> and <em>other</em> is to be kept"),

// ChangeRelationState: change the state of a relation, i.e. possibly both the state of _focal_ and _other_ at the same time
	ChangeRelationState ("change the state of a relation, i.e. possibly both the state of _focal_ and _other_ at the same time",
		"changeRelationState",
		"void",
		""),

// SetInitialState: sets the initial state of a newly created SystemComponent
	SetInitialState ("sets the initial state of a newly created SystemComponent",
		"setInitialState",
		"void",
		""),

// SetOtherInitialState: sets the initial state of a newly created SystemComponent given a parent component
	SetOtherInitialState ("sets the initial state of a newly created SystemComponent given a parent component",
		"setOtherInitialState",
		"void",
		"");
	
	private final String description;
	private final String method;
	private final String returnType;
	private final String returnJavaDoc;

	private TwFunctionTypes(String description, String method, String returnType, String returnJavaDoc) {
		this.description = description;
		this.method = method;
		this.returnType = returnType;
		this.returnJavaDoc = returnJavaDoc;
	}

	public String description() {
		return description;
	}

	public String method() {
		return method;
	}

	public String returnType() {
		return returnType;
	}

	public String returnJavaDoc() {
		return returnJavaDoc;
	}

	public static String[] toStrings() {
		String[] result = new String[TwFunctionTypes.values().length];
		for (TwFunctionTypes s: TwFunctionTypes.values())
			result[s.ordinal()] = s.name();
		Arrays.sort(result);
		return result;
	}

	public static Set<String> keySet() {
		Set<String> result = new HashSet<String>();
		for (TwFunctionTypes e: TwFunctionTypes.values())
			result.add(e.toString());
		return result;
	}

	public static TwFunctionTypes defaultValue() {
		return ChangeState;
	}

	static {
		ValidPropertyTypes.recordPropertyType(TwFunctionTypes.class.getSimpleName(), 
		TwFunctionTypes.class.getName(),defaultValue());
	}

    /**
     * read-only Function arguments to call the TwFunction descendant from Process
     * eg in Process.executeFunction(): userFunction.changeState(...)
     *
     * @return
     */
    public Set<TwFunctionArguments> readOnlyArguments() {
        switch (this) {
        case SetInitialState:
            return EnumSet.of(arena,lifeCycle,group,focal,space);
        case ChangeState:
        case ChangeCategoryDecision:
        case CreateOtherDecision:
        case DeleteDecision:
            return EnumSet.of(t,dt,arena,lifeCycle,group,focal,space);
        case SetOtherInitialState:
        case ChangeOtherState:
        case MaintainRelationDecision:
        case RelateToDecision:
        case ChangeRelationState:
            return EnumSet.of(t,dt,arena,lifeCycle,group,focal,otherLifeCycle,otherGroup,other,space);
        default:
            return EnumSet.noneOf(TwFunctionArguments.class);
        }
    }

    /**
     * Arguments to call the &lt;UserModel&gt; interface from the TwFunction descendant.
     * eg in MyFunction.changeState(...): UserModel.userfunk(...)
     * @return
     */
    public Set<TwFunctionArguments> localArguments() {
        switch (this) {
        case ChangeCategoryDecision:
        return EnumSet.of(random,decider,selector,recruit);
        case DeleteDecision:
        case CreateOtherDecision:
        case MaintainRelationDecision:
        case RelateToDecision:
            return EnumSet.of(random,decider);
        case ChangeState:
        case SetInitialState:
        case ChangeOtherState:
        case ChangeRelationState:
        case SetOtherInitialState:
            return EnumSet.of(random);
        default:
            return EnumSet.noneOf(TwFunctionArguments.class);
        }
    }

    /** writeable arguments ?*/
    // Question here: should we allow components to modify decorators of their context
    // (ie arena, group, lifecycle)? This would be handy to perform statistics on them,
    // but this may also be source of a lot of mess - wait and see
    public List<String> innerVars() {
        List<String> result = new ArrayList<>();
        switch (this) {
        case ChangeOtherState:
            result.add("otherDrv");
            result.add("otherDec");
            result.add("limits");
//            result.add("focalLoc");
//            result.add("otherLoc");
        break;
        case ChangeRelationState:
            result.add("focalDrv");
            result.add("focalDec");
            result.add("otherDrv");
            result.add("otherDec");
            result.add("limits");
//            result.add("focalLoc");
//            result.add("otherLoc");
        break;
        case ChangeState:
            result.add("focalDrv");
            result.add("focalDec");
            result.add("limits");
//            result.add("focalLoc");
        break;
        case SetInitialState:
            result.add("focalDrv");
            result.add("focalCnt");
        break;
        case SetOtherInitialState:
            result.add("otherDrv");
            result.add("otherCnt");
            result.add("limits");
//            result.add("focalLoc");
        break;
        case MaintainRelationDecision:
        case RelateToDecision:
            result.add("limits");
//            result.add("focalLoc");
//            result.add("otherLoc");
        default:
            result.add("limits");
//            result.add("focalLoc");
        break;
        }
        return result;
    }

//    public Set<TwFunctionArguments> writeableArguments() {
//        switch (this) {
//        case ChangeRelationState:
//            return EnumSet.of(nextFocalLoc,nextOtherLoc);
//        case ChangeOtherState:
//        case SetOtherInitialState:
//            return EnumSet.of(nextOtherLoc);
//        case ChangeState:
//        case SetInitialState:
//            return EnumSet.of(nextFocalLoc);
//        default:
//            return EnumSet.noneOf(TwFunctionArguments.class);
//        }
//    }

}

