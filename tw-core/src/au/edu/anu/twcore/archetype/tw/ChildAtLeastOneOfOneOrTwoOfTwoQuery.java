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
package au.edu.anu.twcore.archetype.tw;

import java.util.List;

import au.edu.anu.rscs.aot.collections.tables.StringTable;
import au.edu.anu.rscs.aot.queries.QueryAdaptor;
import au.edu.anu.rscs.aot.queries.Queryable;
import fr.cnrs.iees.graph.TreeNode;
import static au.edu.anu.rscs.aot.queries.CoreQueries.*;
import static au.edu.anu.rscs.aot.queries.base.SequenceQuery.get;

/**
 * @author Ian Davies
 *
 * @date 27 Sep 2019
 * 
 *       input is either a Tab or a Container.
 * 
 *       Constraint: Either 1 or 2 of nodeLabel1 or just 2 nodelabel2 - for tabs
 *       and containers in the UI
 * 
 *       Cannot have just one container(node label2)
 *       Cannot have nothing
 *       Cannot have more that 2 in total
 * 
 * 
 *       1) 1 widget OR
 * 
 *       3) 1 widget and 1 container OR
 * 
 *       4) 2 widgets OR
 * 
 *       5) 2 containers
 */
// Great name!
public class ChildAtLeastOneOfOneOrTwoOfTwoQuery extends QueryAdaptor {
	private final String widgetLabel;
	private final String containerLabel;

	public ChildAtLeastOneOfOneOrTwoOfTwoQuery(String nodeLabel1, String nodeLabel2) {
		super();
		this.widgetLabel = nodeLabel1;
		this.containerLabel = nodeLabel2;
	}

	public ChildAtLeastOneOfOneOrTwoOfTwoQuery(StringTable table) {
		super();
		widgetLabel = table.getWithFlatIndex(0);
		containerLabel = table.getWithFlatIndex(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Queryable submit(Object input) {
		initInput(input);
		TreeNode localItem = (TreeNode) input;
		List<TreeNode> widgets = (List<TreeNode>) get(localItem.getChildren(), selectZeroOrMany(hasTheLabel(widgetLabel)));
		List<TreeNode> containers= (List<TreeNode>) get(localItem.getChildren(), selectZeroOrMany(hasTheLabel(containerLabel)));

		switch (widgets.size()) {
		case 0:{
			if (containers.isEmpty())
				errorMsg = "'"+localItem.toShortString()+"' must have '"+widgetLabel+"' or '"+containerLabel+"' child node.";
			else if (containers.size()==1)
				errorMsg = "'"+localItem.toShortString()+"' must have '"+widgetLabel+"' or additional '"+containerLabel+"' child node.";			
			return this;
		}
		case 1:{
//			if (containers.size()>1)
//				
//				ok = false;
//			// Remove a container OR add widget
		
			break;
		}
		case 2:{
//			if (!containers.isEmpty())
//				ok = false;
//			// remove all containers
			break;
		}
		default :{
//			ok = false;
			// remove n widgets
		}
		}
		return this;
	}
}
