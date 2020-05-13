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

import fr.cnrs.iees.OmugiClassLoader;
import fr.cnrs.iees.graph.Direction;
import fr.cnrs.iees.graph.GraphFactory;
import fr.cnrs.iees.graph.impl.TreeGraphNode;
import fr.cnrs.iees.identity.Identity;
import fr.cnrs.iees.properties.SimplePropertyList;
import fr.cnrs.iees.properties.impl.ExtendablePropertyListImpl;
import fr.ens.biologie.generic.LimitedEdition;
import fr.ens.biologie.generic.Sealable;

import static fr.cnrs.iees.twcore.constants.ConfigurationPropertyNames.*;
import static fr.cnrs.iees.twcore.constants.ConfigurationEdgeLabels.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import au.edu.anu.twcore.InitialisableNode;
import au.edu.anu.twcore.data.runtime.TwData;
import au.edu.anu.twcore.ecosystem.runtime.init.SecondaryParametersInitialiser;
import au.edu.anu.twcore.ecosystem.runtime.system.ComponentContainer;
import au.edu.anu.twcore.ecosystem.structure.Category;

/**
 * Class matching the "initialiser" node label in the 3Worlds configuration tree.
 * Has no properties except the automatically generated userClassName property
*
 * @author Jacques Gignoux - 4 juin 2019
 *
 */
@Deprecated
public class Initialiser
		extends InitialisableNode
		implements LimitedEdition<SecondaryParametersInitialiser>, Sealable {

	private boolean sealed = false;
	private Map<Integer,SecondaryParametersInitialiser> functions = new HashMap<>();
	private Constructor<? extends SecondaryParametersInitialiser> fConstructor = null;
	private Set<Category> categories = new HashSet<Category>();

	// default constructor
	public Initialiser(Identity id, SimplePropertyList props, GraphFactory gfactory) {
		super(id, props, gfactory);
	}

	// constructor with no properties
	public Initialiser(Identity id, GraphFactory gfactory) {
		super(id, new ExtendablePropertyListImpl(), gfactory);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		if (!sealed) {
			super.initialise();
			Collection<Category> cats = (Collection<Category>) get(edges(Direction.OUT),
				selectOneOrMany(hasTheLabel(E_APPLIESTO.label())),
				edgeListEndNodes());
			categories.addAll(cats);
			// this is once code has been generated and edited by the user
			String className = (String) properties().getPropertyValue(P_INITIALISERCLASS.key());
			if (className!=null) {
				// instantiate the user code based function
				// we need a URL classLoader here: Class.forName("nameofclass", true, new URLClassLoader(urlarrayofextrajarsordirs));
				//https://community.oracle.com/thread/4011800
				ClassLoader classLoader = OmugiClassLoader.getJarClassLoader();
				Class<? extends SecondaryParametersInitialiser> functionClass;
				try {
					functionClass = (Class<? extends SecondaryParametersInitialiser>) Class.forName(className,true,classLoader);
					fConstructor = functionClass.getConstructor();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		sealed = true;
	}

	@Override
	public int initRank() {
//		return N_INITIALISER.initRank();
		return -1;
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

	@Override
	public SecondaryParametersInitialiser getInstance(int id) {
		if (!sealed)
			initialise();
		if (!functions.containsKey(id)) {
			SecondaryParametersInitialiser initialiser = null;
			try {
				initialiser = fConstructor.newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			functions.put(id, initialiser);
		}
		return functions.get(id);
	}

	public Set<Category> categories() {
		if (!sealed)
			initialise();
		return categories;
	}

	// call this from Group or Component makeContainer methods to effectively set the secondary parameters
	@SuppressWarnings("unchecked")
	public static void computeSecondaryParameters(TreeGraphNode groupNode,
			ComponentContainer container,
			int index) {
		TreeGraphNode parent = (TreeGraphNode) groupNode.getParent();
		while ((parent!=null) & !(parent instanceof SimulatorNode))
			parent = (TreeGraphNode) parent.getParent();
		if (parent!=null)
			if (parent instanceof SimulatorNode) {
				List<Initialiser> inits = (List<Initialiser>) get(parent.getChildren(),
					selectZeroOrMany(hasTheLabel("")));
//					selectZeroOrMany(hasTheLabel(N_INITIALISER.label())));
				for (Initialiser init:inits)
					if (init.categories().equals(container.containerCategorized().categories())) {
						SecondaryParametersInitialiser spi = init.getInstance(index);
						TwData gpar = container.parameters();
						TwData lcpar = null;
						TwData ecopar = null;
						if (container.parentContainer()!=null) {
							if (container.parentContainer().parentContainer()==null) {
								if (container.containerCategorized() instanceof LifeCycle) {
									lcpar = container.parameters();
									gpar = null;
								}
								ecopar = container.parentContainer().parameters();
							}
							else {
								lcpar = container.parentContainer().parameters();
								ecopar = container.parentContainer().parentContainer().parameters();
							}
						}
						spi.setSecondaryParameters(gpar,lcpar,ecopar);
				}
		}

	}

}
