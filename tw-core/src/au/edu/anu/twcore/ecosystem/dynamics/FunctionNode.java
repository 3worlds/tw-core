/**************************************************************************
 *  TW-CORE - 3Worlds Core classes and methods                            *
 *                                                                        *
 *  Copyright 2018: Shayne Flint, Jacques Gignoux & Ian D. Davies         *
 *       shayne.flint@anu.edu.au                                          * 
 *       jacques.gignoux@upmc.fr                                          *
 *       ian.davies@anu.edu.au                                            * 
 *                                                                        *
 *  TW-CORE is a library of the principle components required by 3W       *
 *                                                                        *
 **************************************************************************                                       
 *  This file is part of TW-CORE (3Worlds Core).                          *
 *                                                                        *
 *  TW-CORE is free software: you can redistribute it and/or modify       *
 *  it under the terms of the GNU General Public License as published by  *
 *  the Free Software Foundation, either version 3 of the License, or     *
 *  (at your option) any later version.                                   *
 *                                                                        *
 *  TW-CORE is distributed in the hope that it will be useful,            *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *  GNU General Public License for more details.                          *                         
 *                                                                        *
 *  You should have received a copy of the GNU General Public License     *
 *  along with TW-CORE.                                                   *
 *  If not, see <https://www.gnu.org/licenses/gpl.html>                   *
 *                                                                        *
 **************************************************************************/
package au.edu.anu.twcore.ecosystem.dynamics;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.ecosystem.runtime.TwFunction;
import au.edu.anu.twcore.exceptions.TwcoreException;
import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.TreeNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.Sealable;
import fr.ens.biologie.generic.Singleton;
import static fr.cnrs.iees.twcore.constants.ConfigurationNodeLabels.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;

import java.lang.reflect.Constructor;
/**
 * Class matching the "ecosystem/dynamics/timeLine/timeModel/process/function" node label in the 
 * 3Worlds configuration tree. Has the user class name property or a way to generate this class
 * 
 * @author Jacques Gignoux - 7 juin 2019
 *
 */
public class FunctionNode 
		extends InitialisableNode 
		implements Singleton<TwFunction>, Sealable {

	private boolean sealed = false;
	private TwFunction function = null;
	
	public FunctionNode(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	public FunctionNode(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		super.initialise();
		sealed = false;
		// this is once code has been generated and edited by the user
		String className = (String) properties().getPropertyValue(P_FUNCTIONCLASS.key());
		if (className!=null) {
			// instantiate the user code based function
			// we need a URL classLoader here: Class.forName("nameofclass", true, new URLClassLoader(urlarrayofextrajarsordirs));
			//https://community.oracle.com/thread/4011800
			ClassLoader classLoader = OmugiClassLoader.getClassLoader();
			Class<? extends TwFunction> functionClass;
			try {
				functionClass = (Class<? extends TwFunction>) Class.forName(className,false,classLoader);
				Constructor<? extends TwFunction> nodeConstructor = functionClass.getConstructor();
				function = nodeConstructor.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// add the consequences of the function, if any
			// if my parent is a function, I am a consequence of it
			if (getParent() instanceof FunctionNode) {
				FunctionNode parent = (FunctionNode) getParent();
				if (parent.isSealed())
					parent.getInstance().addConsequence(function);
			}
			// if my children are functions, they are consequences of me
			else for (TreeNode n:getChildren()) 
				if (n instanceof FunctionNode){
					FunctionNode csq = (FunctionNode) n;
					if (csq.isSealed())
						function.addConsequence(csq.getInstance());
			}
		}
		sealed = true;
	}

	@Override
	public int initRank() {
		return N_FUNCTION.initRank();
	}

	@Override
	public TwFunction getInstance() {
		if (sealed)
			return function;
		else
			throw new TwcoreException("attempt to access uninitialised data");
	}

	@Override
	public Sealable seal() {
		sealed = true;
		return this;
	}

	@Override
	public boolean isSealed() {
		return sealed;
	}

}
